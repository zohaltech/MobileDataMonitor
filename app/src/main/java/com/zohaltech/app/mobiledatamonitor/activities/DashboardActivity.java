package com.zohaltech.app.mobiledatamonitor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zohaltech.app.mobiledatamonitor.R;
import com.zohaltech.app.mobiledatamonitor.adapters.UsagePagerAdapter;
import com.zohaltech.app.mobiledatamonitor.classes.App;

import widgets.MyToast;
import widgets.MyViewPagerIndicator;

public class DashboardActivity extends EnhancedActivity {

    public static final String DASHBOARD_PAGE_INDEX = "DASHBOARD_PAGE_INDEX";

    ViewPager pagerUsages;
    MyViewPagerIndicator indicator;
    Button    btnPackageManagement;
    Button    btnPurchasePackage;
    Button    btnUsageReport;
    Button    btnPackagesHistory;

    long        startTime;
    UsagePagerAdapter usagePagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //AlarmHandler.start(App.context);
        startTime = System.currentTimeMillis() - 5000;

        pagerUsages = (ViewPager) findViewById(R.id.pagerUsages);
        indicator = (MyViewPagerIndicator) findViewById(R.id.indicator);
        btnPackageManagement = (Button) findViewById(R.id.btnPackageManagement);
        btnPurchasePackage = (Button) findViewById(R.id.btnPurchasePackage);
        btnUsageReport = (Button) findViewById(R.id.btnUsageReport);
        btnPackagesHistory = (Button) findViewById(R.id.btnPackagesHistory);

        pagerUsages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                indicator.setPercent(positionOffset);
                indicator.setCurrentPage(position);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 2) {
                    int pageIndex = pagerUsages.getCurrentItem();
                    if (pageIndex == 0) {
                        //usagePagerAdapter.startAnimation0();
                        usagePagerAdapter.notifyDataSetChanged();
                    } else
                    if (pageIndex == 1) {
                        usagePagerAdapter.startAnimation1();
                    }
                    //else if (pageIndex == 2) {
                    //    //usagePagerAdapter.startAnimation2();
                    //}
                }
            }
        });

        indicator.setIndicatorsCount(3);

        btnPackageManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(App.currentActivity, PackageManagementActivity.class);
                startActivity(myIntent);
            }
        });

        btnPurchasePackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(App.currentActivity, PackagesActivity.class);
                startActivity(intent);
            }
        });

        btnUsageReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(App.currentActivity, DailyTrafficMonitorActivity.class);
                startActivity(intent);
            }
        });

        btnPackagesHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(App.currentActivity, PackagesHistoryActivity.class);
                startActivity(intent);
            }
        });

        usagePagerAdapter = new UsagePagerAdapter();
        pagerUsages.setAdapter(usagePagerAdapter);
        pagerUsages.setCurrentItem(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        usagePagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - startTime) > 2000) {
            startTime = System.currentTimeMillis();
            MyToast.show(getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT);
            //Toast.makeText(App.context, getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }
}
