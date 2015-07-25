package com.zohaltech.app.mobiledatamonitor.dal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zohaltech.app.mobiledatamonitor.classes.Helper;
import com.zohaltech.app.mobiledatamonitor.entities.DailyTrafficHistory;
import com.zohaltech.app.mobiledatamonitor.entities.UsageLog;

import java.util.ArrayList;

public class UsageLogs {

    static final String TableName    = "UsageLogs";
    static final String Id           = "Id";
    static final String TrafficBytes = "TrafficBytes";
    static final String LogDateTime  = "LogDateTime";

    static final String CreateTable = "CREATE TABLE " + TableName + " (" +
                                      Id + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                      TrafficBytes + " BIGINT NOT NULL, " +
                                      LogDateTime + " CHAR(19) );";

    static final String DropTable = "Drop Table If Exists " + TableName;

    private static ArrayList<UsageLog> select(String whereClause, String[] selectionArgs) {
        ArrayList<UsageLog> logList = new ArrayList<>();
        DataAccess da = new DataAccess();
        SQLiteDatabase db = da.getReadableDB();
        Cursor cursor = null;

        try {
            String query = "Select * From " + TableName + " " + whereClause;
            cursor = db.rawQuery(query, selectionArgs);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    UsageLog log = new UsageLog(cursor.getInt(cursor.getColumnIndex(Id)),
                                                cursor.getLong(cursor.getColumnIndex(TrafficBytes)),
                                                cursor.getString(cursor.getColumnIndex(LogDateTime)));
                    logList.add(log);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (db != null && db.isOpen())
                db.close();
        }
        return logList;
    }

    private static String getMaxDateTime() {
        String maxLogDate = null;
        DataAccess da = new DataAccess();
        SQLiteDatabase db = da.getReadableDB();
        Cursor cursor = null;
        try {
            String query = "SELECT MAX(" + LogDateTime + ") MaxLogDate FROM " + TableName;
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    maxLogDate = cursor.getString(cursor.getColumnIndex("MaxLogDate"));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (db != null && db.isOpen())
                db.close();
        }
        return maxLogDate;
    }

    private static void integrateSumUsedTrafficUsagePerHourInDate(String date) {
        DataAccess da = new DataAccess();
        SQLiteDatabase db = da.getReadableDB();
        Cursor cursor = null;
        try {
            String query = "SELECT SUBSTR(LogDateTime,11,3) hourPeriod,SUM(TrafficBytes) SumTraffic FROM UsageLogs " +
                           "WHERE  SUBSTR(LogDateTime,1,10)='" + date + "' GROUP BY  SUBSTR(LogDateTime,11,3) ;";
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long sum = cursor.getLong(cursor.getColumnIndex("SumTraffic"));
                    String hour = cursor.getString(cursor.getColumnIndex("hourPeriod"));
                    String beginningDateTime = date + " " + hour + ":00:00";
                    int endingHour = Integer.parseInt(hour.substring(1)) + 1;
                    String endingDateTime = date + " " + endingHour + ":00:00";

                    DailyTrafficHistory history = new DailyTrafficHistory(sum,
                                                                          beginningDateTime,
                                                                          endingDateTime);
                    DailyTrafficHistories.insert(history);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (db != null && db.isOpen())
                db.close();
        }
    }

    public static ArrayList<UsageLog> select() {
        return select("", null);
    }

    public static long insert(UsageLog usageLog) {
        ContentValues values = new ContentValues();
        String maxDateStr = getMaxDateTime().substring(0, 10);
        String strCurrentDateTime = Helper.getCurrentDateTime();
        String strCurrentDate = strCurrentDateTime.substring(0, 10);

        if (maxDateStr != null && strCurrentDate.compareTo(maxDateStr) > 0) {
            integrateSumUsedTrafficUsagePerHourInDate(maxDateStr);
        }

        values.put(TrafficBytes, usageLog.getTrafficBytes());
        values.put(LogDateTime, strCurrentDateTime);

        DataAccess da = new DataAccess();
        return da.insert(TableName, values);
    }

    public static long update(UsageLog usageLog) {
        ContentValues values = new ContentValues();

        values.put(TrafficBytes, usageLog.getTrafficBytes());
        values.put(LogDateTime, usageLog.getLogDateTime());

        DataAccess da = new DataAccess();
        return da.update(TableName, values, Id + " =? ", new String[]{String.valueOf(usageLog.getId())});
    }

    public static long delete(UsageLog usageLog) {
        DataAccess db = new DataAccess();
        return db.delete(TableName, Id + " =? ", new String[]{String.valueOf(usageLog.getId())});
    }
}