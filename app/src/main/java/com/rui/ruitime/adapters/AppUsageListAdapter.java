/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.rui.ruitime.adapters;

/**
 * Created by rhuang on 12/8/15.
 * Modified the original source code: UsageListAdapter.java
 */

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.WindowManager;
import android.content.Context;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.rui.ruitime.Model.CustomAppUsageStats;
import com.rui.ruitime.R;
import com.rui.ruitime.activity.MainActivity;

import org.w3c.dom.Text;

/**
 * Provide views to RecyclerView with the directory entries.
 */
public class AppUsageListAdapter extends RecyclerView.Adapter<AppUsageListAdapter.ViewHolder> {


    private List<CustomAppUsageStats> mCustomUsageStatsList = new ArrayList<>();
    private DateFormat mDateFormat = new SimpleDateFormat();

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mName;

        private final TextView mLastTimeUsed;
        private final TextView mTotalDurationUsed;
        //private final TextView mTime;
        private final ImageView mAppIcon;
        private final ProgressBar mTimeBar;

        public ViewHolder(View v) {
            super(v);
            mName = (TextView) v.findViewById(R.id.textview_name);
            mLastTimeUsed = (TextView) v.findViewById(R.id.textview_last_time_used);
            mTotalDurationUsed = (TextView) v.findViewById(R.id.textview_total_duration_used);
            //mTime = (TextView) v.findViewById(R.id.time);
            mAppIcon = (ImageView) v.findViewById(R.id.app_icon);
            mTimeBar = (ProgressBar) v.findViewById(R.id.timeBar);
        }

        public TextView getLastTimeUsed() {
            return mLastTimeUsed;
        }
        public TextView getTotalDurationUsed() {
            return mTotalDurationUsed;
        }
        //public TextView getTime() { return mTime; }
        public TextView getName() {
            return mName;
        }

        public ImageView getAppIcon() {
            return mAppIcon;
        }

        public View getTimeBar() { return mTimeBar; }
    }

    public AppUsageListAdapter() {
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        final View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.usage_row_main, viewGroup, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {


        if(mCustomUsageStatsList.get(position).appName.equals("Unknown")) {
            // We use the package name instead of app name
            viewHolder.getName().setText(mCustomUsageStatsList.get(position).usageStats.getPackageName());
        }
        else {
            viewHolder.getName().setText(mCustomUsageStatsList.get(position).appName);
        }


        int[] statsDuration = {0, 0, 0};
        long lastTimeUsed = mCustomUsageStatsList.get(position).usageStats.getLastTimeUsed();
        long totalDurationUsed = (long) (mCustomUsageStatsList.get(position).usageStats.getTotalTimeInForeground() / 1000.0D);

        statsDuration[0] = (int)(totalDurationUsed / 3600);
        statsDuration[1] = (int)((totalDurationUsed - statsDuration[0] * 3600) / 60);
        statsDuration[2] = (int)((totalDurationUsed - statsDuration[0] * 3600 - statsDuration[1] * 60));

        float secondsRatio = (float) totalDurationUsed / 7200;

        if (secondsRatio > 1) {
            secondsRatio = 1;
        }

        int timeBarColor;

        int timeBarWidth;
        int secondsColor = (Math.round(secondsRatio * 100));

        if (secondsColor >= 100)
        {
            timeBarColor = 0xFFFF4444;
        }
        else if (secondsColor >= 80 && secondsColor < 100) {

            timeBarColor = 0xFFFF4444;

        } else if (secondsColor >= 60 && secondsColor < 80) {

            timeBarColor = 0xFFFFBB33;

        } else if (secondsColor >= 40 && secondsColor < 60) {

            timeBarColor = 0xFFAA66CC;
        } else if (secondsColor >= 20 && secondsColor < 40) {

            timeBarColor = 0xFF74C353;
        } else {
            timeBarColor = 0xFF34B5E2;
        }

        timeBarWidth = (int)(100 * secondsRatio);

        viewHolder.getLastTimeUsed().setText(mDateFormat.format(new Date(lastTimeUsed)));
        viewHolder.getAppIcon().setImageDrawable(mCustomUsageStatsList.get(position).appIcon);

        // Customize the time bar width and color,
        // the width is calculate based on the time duration spent
        // the color is set to each bar
        //viewHolder.getTimeBar().setBackgroundColor(timeBarColor);
        ProgressBar bar = (ProgressBar) viewHolder.getTimeBar();
        bar.getProgressDrawable().setColorFilter(timeBarColor, PorterDuff.Mode.SRC_IN);
        bar.setScaleY(3F);
        bar.setProgress(timeBarWidth);
        String totalDurationString = ((statsDuration[0] > 0) ? statsDuration[0] + "h " : "") + ((statsDuration[1] > 0) ? statsDuration[1] + "m " : "") + ((statsDuration[2] > 0) ? statsDuration[2] + "s " : "1s");
        viewHolder.getTotalDurationUsed().setText(totalDurationString);
        //viewHolder.getTimeBar().getLayoutParams().width = 360;
    }

    @Override
    public int getItemCount() {
        return mCustomUsageStatsList.size();
    }


    public void setCustomUsageStatsList(List<CustomAppUsageStats> customUsageStats) {
        mCustomUsageStatsList = customUsageStats;
    }
}
