package com.bluewavevision.tracmojo.database;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.bluewavevision.tracmojo.model.RateOption;
import com.bluewavevision.tracmojo.model.SyncTrac;
import com.bluewavevision.tracmojo.model.Trac;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class RemDBAdapter {

    private Context context = null;
    private RemDBHelper helper;
    private SQLiteDatabase db;

    public RemDBAdapter(Context ctx) {
        this.context = ctx;
    }

    public RemDBAdapter open() throws SQLException {
        helper = new RemDBHelper(context);
        db = helper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public long insertTrac(SyncTrac mSyncTrac) {

        ContentValues cv = new ContentValues();

        cv.put(RemDBHelper.ROWID, mSyncTrac.getId());
        cv.put(RemDBHelper.JSON_DATA, mSyncTrac.getJsonData());
        cv.put(RemDBHelper.TRAC_TYPE, mSyncTrac.getTracType());

        return db.insert(RemDBHelper.DATABASE_TABLE, null, cv);
    }


    public boolean deleteTrac(int rowId) {
        return db.delete(RemDBHelper.DATABASE_TABLE, RemDBHelper.ROWID + "=" + rowId, null) > 0;
    }


    public boolean deleteAll() {
        return db.delete(RemDBHelper.DATABASE_TABLE, null, null) > 0;
    }

    public boolean updateTrac(int rowId, SyncTrac mSyncTrac) {
        ContentValues cv = new ContentValues();

        cv.put(RemDBHelper.JSON_DATA, mSyncTrac.getJsonData());
        cv.put(RemDBHelper.TRAC_TYPE, mSyncTrac.getTracType());

        return db.update(RemDBHelper.DATABASE_TABLE, cv, RemDBHelper.ROWID + "=" + rowId, null) > 0;
    }

    public Cursor getAllData() {
        return db.query(RemDBHelper.DATABASE_TABLE, null, null, null, null, null, null);
    }

    public long insertTracIfNotExists(int id,boolean isDeleted,SyncTrac syncTrac){
        Cursor cursor = db.rawQuery("SELECT * FROM Trac where _id = "+id,null);
        if(cursor!=null && cursor.getCount()<1){
            if(!isDeleted)
                insertTrac(syncTrac);
        }else{
            if(isDeleted){
               deleteTrac(id);
            }else {
                updateTrac(id,syncTrac);
            }
        }
        return  0;
    }

    public ArrayList<Trac> getTracList(String type) throws JSONException {
        ArrayList<Trac> tempList = new ArrayList<Trac>();
        Cursor cursor = db.query(RemDBHelper.DATABASE_TABLE, null, RemDBHelper.TRAC_TYPE + "=?",
                new String[] { type }, null, null, RemDBHelper.ROWID + " DESC", null);

        if(cursor!=null){
            if (cursor.moveToFirst()) {
                do {
                    String jsonData = "" + cursor.getString(cursor.getColumnIndex(RemDBHelper.JSON_DATA));
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONObject jsonTrac = jsonObject.optJSONObject("Trac");
                    if(jsonTrac!=null){
                        tempList.add(getTracFromJson(jsonTrac));
                    }
                } while (cursor.moveToNext());
            }
        }

        return tempList;
    }

    public JSONObject getJsonTrac(int id) throws JSONException {
        JSONObject jsonTrac = null;

        Cursor cursor = db.query(RemDBHelper.DATABASE_TABLE, null, RemDBHelper.ROWID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if(cursor!=null){
            if (cursor.moveToFirst()) {
                do {
                    String jsonData = "" + cursor.getString(cursor.getColumnIndex(RemDBHelper.JSON_DATA));
                    JSONObject jsonObject = new JSONObject(jsonData);
                    jsonTrac = jsonObject.getJSONObject("Trac");

                } while (cursor.moveToNext());
            }
        }

        return jsonTrac;
    }

    public Trac getTracFromJson(JSONObject jsonTrac) throws JSONException {
        Trac trac = new Trac();
        int id = jsonTrac.getInt("id");
        String goal = "" + jsonTrac.getString("goal");
        if(TextUtils.isEmpty(goal)){
            goal = "" + jsonTrac.getString("idea_id_name");
        }
        String groupName = ""+jsonTrac.optString("group_name");
        String groupType = jsonTrac.optString("group_type");
        String myTrac = ""+jsonTrac.optString("my_trac");
        boolean isMyTrac;
        if (!TextUtils.isEmpty(myTrac) && myTrac.equalsIgnoreCase("y")) {
            isMyTrac = true;
        } else {
            isMyTrac = false;
        }

        String rate = jsonTrac.optString("rate");
        String notiOn = jsonTrac.optString("noti_on");
        boolean isNotiOn;
        if (!TextUtils.isEmpty(notiOn) && notiOn.equalsIgnoreCase("y")) {
            isNotiOn = true;
        } else {
            isNotiOn = false;
        }

        JSONObject jsonRateOptions = jsonTrac.optJSONObject("rate_option");
        RateOption rateOption = null;
        if(jsonRateOptions!=null){
            rateOption = new RateOption();
            rateOption.setOption1(""+jsonRateOptions.getString("option1"));
            rateOption.setOption2("" + jsonRateOptions.getString("option2"));
            rateOption.setOption3("" + jsonRateOptions.getString("option3"));
            rateOption.setOption4("" + jsonRateOptions.getString("option4"));
            rateOption.setOption5("" + jsonRateOptions.getString("option5"));
        }

        String rateFrequency = ""+jsonTrac.optString("rating_frequency");
        Log.e("rateFrequency", ":" + rateFrequency);
        String lastRated = ""+jsonTrac.optString("last_rated");
        String lastRate = ""+jsonTrac.optString("last_rate");

        String ownerName = "" + jsonTrac.optString("owner_name");

        trac.setId(id);
        trac.setGoal(goal);
        trac.setGroupName(groupName);
        trac.setGroupType(groupType);
        trac.setMyTrac(isMyTrac);
        trac.setRate(rate);
        trac.setNotificationOn(isNotiOn);
        trac.setRateOption(rateOption);
        trac.setRateFrequency(rateFrequency);
        trac.setLastRated(lastRated);
        trac.setLastRate(lastRate);
        trac.setOwnerName(ownerName);

        return trac;
    }

}
