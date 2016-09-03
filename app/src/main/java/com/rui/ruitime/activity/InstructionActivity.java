package com.rui.ruitime.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Intent;

import com.rui.ruitime.R;
import com.viewpagerindicator.CirclePageIndicator;


/**
 * Created by rhuang on 12/7/15.
 */
public class InstructionActivity extends AppCompatActivity
{
    SectionsPagerAdapter rSectionsPagerAdapter;
    ViewPager rViewPager;
    CirclePageIndicator rIndicator;


    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fragmentManager) {

            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {

            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String SECTION_NUM_ARG = "section_num";

        public static PlaceholderFragment newInstance(int sectionNum) {
            PlaceholderFragment fragment = new PlaceholderFragment();

            Bundle args = new Bundle();
            args.putInt(SECTION_NUM_ARG, sectionNum);
            fragment.setArguments(args);

            return fragment;
        }

        public PlaceholderFragment() {


        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            Bundle bundle = getArguments();
            int page = bundle.getInt(SECTION_NUM_ARG);

            int layout = getResources().getIdentifier("instructions_page" + page, "layout", "com.rui.ruitime");
            View rootView = inflater.inflate(layout, container, false);

            if(page == 2) {
                Button btn3 = (Button)rootView.findViewById(com.rui.ruitime.R.id.btn2);
                btn3.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        AppCompatActivity activity = (AppCompatActivity) getContext();
                        activity.finish();
                    }
                });

            }
            return rootView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.rui.ruitime.R.layout.activity_instructions);

        rSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        rViewPager = (ViewPager)findViewById(com.rui.ruitime.R.id.pager);
        rViewPager.setAdapter(rSectionsPagerAdapter);

        rIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        rIndicator.setViewPager(rViewPager);

        final float density = getResources().getDisplayMetrics().density;
        rIndicator.setRadius(7 * density);
        rIndicator.setFillColor(0xFFFFFFFF);
        rIndicator.setPageColor(0xFF466066);
        rIndicator.setStrokeColor(0xFFFFFFFF);
        rIndicator.setStrokeWidth(2 * density);
        rIndicator.setSnap(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
