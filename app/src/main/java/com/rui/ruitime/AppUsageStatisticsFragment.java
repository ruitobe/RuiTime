/*
* Copyright 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.rui.ruitime;

/**
 * Created by rhuang on 12/8/15.
 * Modified the original source code: AppUsageStatisticsFragment.java
 */

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.rui.ruitime.Model.*;
import com.rui.ruitime.adapters.*;


/**
 * Fragment that demonstrates how to use App Usage Statistics API.
 */
public class AppUsageStatisticsFragment extends Fragment {

    private static final String TAG = AppUsageStatisticsFragment.class.getSimpleName();

    //VisibleForTesting for variables below
    UsageStatsManager mUsageStatsManager;
    AppUsageListAdapter mUsageListAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    Button mOpenUsageSettingButton;
    Spinner mTimeSpanSpinner;
    Spinner mSortSpinner;

    int itemPosOfTimeSpanSpinner = 0;
    int itemPosOfSortSpinner = 0;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment {@link AppUsageStatisticsFragment}.
     */
    public static AppUsageStatisticsFragment newInstance() {
        AppUsageStatisticsFragment fragment = new AppUsageStatisticsFragment();
        return fragment;
    }

    public AppUsageStatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUsageStatsManager = (UsageStatsManager) getActivity()
                .getSystemService(Context.USAGE_STATS_SERVICE); //Context.USAGE_STATS_SERVICE
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        mUsageListAdapter = new AppUsageListAdapter();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_app_usage);
        mLayoutManager = mRecyclerView.getLayoutManager();
        mRecyclerView.scrollToPosition(0);
        mRecyclerView.setAdapter(mUsageListAdapter);
        mOpenUsageSettingButton = (Button) rootView.findViewById(R.id.button_open_usage_setting);

        mTimeSpanSpinner = (Spinner) rootView.findViewById(R.id.spinner_time_span);
        SpinnerAdapter timeSpanSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.action_list, android.R.layout.simple_spinner_dropdown_item);
        mTimeSpanSpinner.setAdapter(timeSpanSpinnerAdapter);


        final String[] intervalStrs = getResources().getStringArray(R.array.action_list);
        final String[] criteriaStrs = getResources().getStringArray(R.array.sort_by_list);

        mTimeSpanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                itemPosOfTimeSpanSpinner = position;
                StatsUsageInterval statsUsageInterval = StatsUsageInterval
                        .getValue(intervalStrs[position]);
                if (statsUsageInterval != null) {
                    List<UsageStats> usageStatsList =
                            getUsageStatistics(statsUsageInterval.mInterval);
                    //Collections.sort(usageStatsList, new LastTimeLaunchedComparatorDesc());
                    if(criteriaStrs[itemPosOfSortSpinner].equals("Total duration"))
                    {
                        Collections.sort(usageStatsList, new TotalDurationUsedComparatorDesc());
                        updateAppsList(usageStatsList);
                    }
                    else if(criteriaStrs[itemPosOfSortSpinner].equals("Alphabetical"))
                    {
                        // TODO: Implement it later
                        System.out.println("Alphabetical");
                        updateAppsList(usageStatsList);
                    }

                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSortSpinner = (Spinner) rootView.findViewById(R.id.spinner_sort);
        SpinnerAdapter SortSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sort_by_list, android.R.layout.simple_spinner_dropdown_item);

        mSortSpinner.setAdapter(SortSpinnerAdapter);



        mSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the usageStatsList according to StatsUsageInterval selection first
                // Then according to the criteria, handle the list sorting and update the list

                itemPosOfSortSpinner = position;

                StatsUsageInterval statsUsageInterval = StatsUsageInterval
                        .getValue(intervalStrs[itemPosOfTimeSpanSpinner]);

                if (statsUsageInterval != null) {

                    List<UsageStats> usageStatsList = getUsageStatistics(statsUsageInterval.mInterval);

                    if(criteriaStrs[position].equals("Total duration"))
                    {
                        Collections.sort(usageStatsList, new TotalDurationUsedComparatorDesc());
                        updateAppsList(usageStatsList);
                    }
                    else if(criteriaStrs[position].equals("Alphabetical"))
                    {
                        // TODO: Implement it later
                        System.out.println("Alphabetical");
                        //updateAppsList(usageStatsList);
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Returns the {@link #mRecyclerView} including the time span specified by the
     * intervalType argument.
     *
     * @param intervalType The time interval by which the stats are aggregated.
     *                     Corresponding to the value of {@link UsageStatsManager}.
     *                     E.g. {@link UsageStatsManager#INTERVAL_DAILY}, {@link
     *                     UsageStatsManager#INTERVAL_WEEKLY},
     *
     * @return A list of {@link android.app.usage.UsageStats}.
     */
    public List<UsageStats> getUsageStatistics(int intervalType) {
        // Get the app statistics since one year ago from the current time.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);

        List<UsageStats> queryUsageStats = mUsageStatsManager
                .queryUsageStats(intervalType, cal.getTimeInMillis(),
                        System.currentTimeMillis());

        if (queryUsageStats.size() == 0) {
            Log.i(TAG, "The user may not allow the access to apps usage. ");
            Toast.makeText(getActivity(),
                    getString(R.string.explanation_access_to_appusage_is_not_enabled),
                    Toast.LENGTH_LONG).show();
            mOpenUsageSettingButton.setVisibility(View.VISIBLE);
            mOpenUsageSettingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }
            });
        }
        return queryUsageStats;
    }

    /**
     * Updates the {@link #mRecyclerView} with the list of {@link UsageStats} passed as an argument.
     *
     * @param usageStatsList A list of {@link UsageStats} from which update the
     *                       {@link #mRecyclerView}.
     */
    //VisibleForTesting
    void updateAppsList(List<UsageStats> usageStatsList) {
        List<CustomAppUsageStats> customUsageStatsList = new ArrayList<>();
        PackageManager packageManager =  getActivity().getPackageManager();

        for (int i = 0; i < usageStatsList.size(); i++) {
            CustomAppUsageStats customUsageStats = new CustomAppUsageStats();
            ApplicationInfo appInfo = new ApplicationInfo();
            customUsageStats.usageStats = usageStatsList.get(i);
            try {
                Drawable appIcon = getActivity().getPackageManager()
                        .getApplicationIcon(customUsageStats.usageStats.getPackageName());
                customUsageStats.appIcon = appIcon;


            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, String.format("App Icon is not found for %s",
                        customUsageStats.usageStats.getPackageName()));
                customUsageStats.appIcon = getActivity()
                        .getDrawable(R.drawable.ic_default_app_launcher);
            }

            try {
                appInfo = packageManager.getApplicationInfo(customUsageStats.usageStats.getPackageName(), 0);

            } catch (PackageManager.NameNotFoundException e)
            {
                Log.w(TAG, String.format("App Name is not interpreted for %s",
                        customUsageStats.usageStats.getPackageName()));
            }
            customUsageStats.appName = (String) (appInfo != null ? packageManager.getApplicationLabel(appInfo) : "Unknown");
            customUsageStatsList.add(customUsageStats);
        }
        mUsageListAdapter.setCustomUsageStatsList(customUsageStatsList);
        mUsageListAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
    }

    /**
     * The {@link Comparator} to sort a collection of {@link UsageStats} sorted by the timestamp
     * last time the app was used in the descendant order.
     */
    private static class LastTimeLaunchedComparatorDesc implements Comparator<UsageStats> {

        @Override
        public int compare(UsageStats left, UsageStats right) {

            return Long.compare(right.getLastTimeUsed(), left.getLastTimeUsed());
        }
    }

    /**
     * The {@link Comparator} to sort a collection of {@link UsageStats} sorted by the total time spent
     * the most time the app was used in the descendant order.
     */

    private static class TotalDurationUsedComparatorDesc implements Comparator<UsageStats> {

        @Override
        public int compare(UsageStats left, UsageStats right) {

            return Long.compare(right.getTotalTimeInForeground(), left.getTotalTimeInForeground());
        }
    }

    /**
     * Enum represents the intervals for {@link android.app.usage.UsageStatsManager} so that
     * values for intervals can be found by a String representation.
     *
     */
    //VisibleForTesting
    static enum StatsUsageInterval {
        DAILY("Daily", UsageStatsManager.INTERVAL_DAILY),
        WEEKLY("Weekly", UsageStatsManager.INTERVAL_WEEKLY),
        MONTHLY("Monthly", UsageStatsManager.INTERVAL_MONTHLY),
        YEARLY("Yearly", UsageStatsManager.INTERVAL_YEARLY);

        private int mInterval;
        private String mStringRepresentation;

        StatsUsageInterval(String stringRepresentation, int interval) {
            mStringRepresentation = stringRepresentation;
            mInterval = interval;
        }

        static StatsUsageInterval getValue(String stringRepresentation) {
            for (StatsUsageInterval statsUsageInterval : values()) {
                if (statsUsageInterval.mStringRepresentation.equals(stringRepresentation)) {
                    return statsUsageInterval;
                }
            }
            return null;
        }
    }


}
