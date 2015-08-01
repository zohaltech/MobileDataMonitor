package com.zohaltech.app.mobiledatamonitor.classes;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.zohaltech.app.mobiledatamonitor.R;

import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class DataUsageService extends Service {


    private static final int     USAGE_LOG_INTERVAL         = 60;
    private static       long    tempReceivedBytes          = 0;
    private static       long    tempSentBytes              = 0;
    private static       boolean firstTime                  = true;
    private static       int     usageLogInterval           = 0;
    private static       long    tempUsage                  = 0;
    private static       long    currentDateSumTraffic      = 0;
    private static       String  strCurrentDateTotalTraffic = "0.00000 MB";
    //private Timer executorService;
    private ScheduledExecutorService executorService;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long currentReceivedBytes = android.net.TrafficStats.getMobileRxBytes();
            long currentSentBytes = android.net.TrafficStats.getMobileTxBytes();
            long receivedBytes = 0;
            long sentBytes = 0;

            if (currentReceivedBytes + currentSentBytes == 0) {
                //log("transfer = 0");
                firstTime = true;
            } else {
                if (firstTime) {
                    firstTime = false;
                    tempReceivedBytes = currentReceivedBytes;
                    App.preferences.edit().putLong("tempReceivedBytes", tempReceivedBytes).commit();
                    tempSentBytes = currentSentBytes;
                    App.preferences.edit().putLong("tempSentBytes", tempSentBytes).commit();
                }
                //////////receivedBytes = currentReceivedBytes - tempReceivedBytes;
                receivedBytes = currentReceivedBytes - App.preferences.getLong("tempReceivedBytes", 0);
                //log("receivedBytes = " + receivedBytes);
                //////////sentBytes = currentSentBytes - tempSentBytes;
                sentBytes = currentSentBytes - App.preferences.getLong("tempSentBytes", 0);
                ;
                //log("sentBytes = " + sentBytes);
                tempReceivedBytes = currentReceivedBytes;
                App.preferences.edit().putLong("tempReceivedBytes", tempReceivedBytes).commit();
                tempSentBytes = currentSentBytes;
                App.preferences.edit().putLong("tempSentBytes", tempSentBytes).commit();

                tempUsage = App.preferences.getLong("tempUsage", 0);

                tempUsage = tempUsage + receivedBytes + sentBytes;

                App.preferences.edit().putLong("tempUsage", tempUsage).commit();

                //log("tempUsage = " + tempUsage);

                //usageLogInterval++;
                //if (usageLogInterval == USAGE_LOG_INTERVAL) {
                //UsageLogs.insert(new UsageLog(tempUsage));
                //log(tempUsage + " inserted");
                //currentDateSumTraffic = UsageLogs.getCurrentDateSumTraffic();
                //log("currentDateSumTraffic = " + currentDateSumTraffic);
                //strCurrentDateTotalTraffic = String.format("%.2f MB", (float) currentDateSumTraffic / (1024 * 1024));
                //log("strCurrentDateTotalTraffic = " + strCurrentDateTotalTraffic);
                //usageLogInterval = 0;
                //tempUsage = 0;
                //}
                //strCurrentDateTotalTraffic = String.format("%.2f MB", (float) tempUsage / (1024 * 1024));

            }

            int iconId;
            long value = (receivedBytes + sentBytes) / 1024;
            //Random r = new Random();
            //int Low = 10;
            //int High = 1024 * 30;
            //int value = r.nextInt(High - Low) + Low;

            if (value < 1000) {
                iconId = App.context.getResources().getIdentifier("wkb" + String.format("%03d", value), "drawable", getPackageName());
            } else if (value >= 1000 && value <= 1024) {
                iconId = App.context.getResources().getIdentifier("wmb010", "drawable", getPackageName());
            } else if ((float) value / 1024 > 1 && (float) value / 1024 < 10) {
                BigDecimal decimal = Helper.round((float) value / 1024, 1);
                iconId = App.context.getResources().getIdentifier("wmb0" + decimal.toString().replace(".", ""), "drawable", getPackageName());
            } else if (value / 1024 >= 10 && value / 1024 <= 200) {
                value = (value / 1024) + 90;
                iconId = App.context.getResources().getIdentifier("wmb" + value, "drawable", getPackageName());
            } else if (value / 1024 > 200) {
                value = (value / 1024) + 90;
                iconId = App.context.getResources().getIdentifier("wmb" + value, "drawable", getPackageName());
            } else {
                iconId = R.drawable.wkb000;
            }

            String total = Helper.getTotalUsedTraffic(App.preferences.getLong("tempUsage", 0));

            NotificationHandler.displayNotification(App.context, iconId, String.format("Down: %s, Up: %s", Helper.getTransferRate(receivedBytes), Helper.getTransferRate(sentBytes))
                    , String.format("Total: %s MB", total));

            //log("Notification : receivedBytes = " + receivedBytes + ", sentBytes = " + sentBytes + ", total = " + strCurrentDateTotalTraffic);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(runnable, 0L, 1000L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
        executorService = null;
        NotificationHandler.cancelNotification(App.context);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
}