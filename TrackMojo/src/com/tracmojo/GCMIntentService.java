package com.tracmojo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.tracmojo.pushnotification.PushUtils;
import com.tracmojo.ui.DashboardActivity;
import com.tracmojo.util.AppSession;
import com.tracmojo.util.Util;

public class GCMIntentService extends GCMBaseIntentService {

    private String title, type, eventCode, trac_relation;
    private int groupPosition;
    public SharedPreferences prefs;


    public GCMIntentService() {
        super(PushUtils.GCMSenderId);
    }

    @Override
    protected void onError(Context context, String regId) {
        Util.showErrorLog(getClass().getSimpleName(), "error registration id : " + regId);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Util.showErrorLog(TAG, "Message Received");
            dumpIntent(intent);
        if(!TextUtils.isEmpty(title)){
            generateNotification(context, title);
            handleMessage(context, intent);
        }
    }

    @Override
    protected void onRegistered(Context context, String regId) {
        handleRegistration(context, regId);
    }

    @Override
    protected void onUnregistered(Context context, String regId) {
        Util.showErrorLog(TAG, "Device unregistered");
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            GCMRegistrar.unregister(context);
        } else {
            Util.showErrorLog(TAG, "Ignoring unregister callback");
        }
    }

    private void handleMessage(Context context, Intent intent) {
    }

    private void handleRegistration(Context context, String regId) {
        Util.showErrorLog(getClass().getSimpleName(), "Handling Registration" + regId);
        /*SharedPreferences prefs = new WeddingEventSession(context).getPreferences();
		prefs.edit().putString("registrationID", regId).commit();*/
    }

    private void generateNotification(Context context, String message) {
        int icon = R.mipmap.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);

        Intent notificationIntent = new Intent(context, DashboardActivity.class);

        notificationIntent.putExtra("message",""+message);
        notificationIntent.putExtra("isForNotification",true);
        if(type!=null && !TextUtils.isEmpty(type)){
            if(type.equalsIgnoreCase("contactuser")){
                notificationIntent.putExtra("type","contactuser");
            } else if(type.equalsIgnoreCase("follow_invitation") || type.equalsIgnoreCase("participate_invitation")){
                notificationIntent.putExtra("type","follow_invitation");
            } else if(type.equalsIgnoreCase("respondtracinvitation")){
                notificationIntent.putExtra("type","respondtracinvitation");
            }  else if(type.equalsIgnoreCase("trac_changed")){
                notificationIntent.putExtra("type","trac_changed");
            } else if(type.equalsIgnoreCase("trac_deleted")){
                notificationIntent.putExtra("type","trac_deleted");
            } else if(type.equalsIgnoreCase("tracrated")){
                notificationIntent.putExtra("type","tracrated");
            } else {
                notificationIntent.putExtra("type","general");
            }
        }
        notificationIntent.putExtra("groupPosition",groupPosition);

        AppSession session = new AppSession(context);
        prefs = session.getPreferences();
        prefs.edit().putInt("groupPosition",groupPosition).apply();
        prefs.edit().putInt("groupPositionEdit",groupPosition).apply();

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        //notification.setLatestEventInfo(context, context.getString(R.string.app_name), message, intent);

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        notificationManager.notify((int) System.currentTimeMillis(), notification);
    }

    public void dumpIntent(Intent i) {

        Bundle bundle = i.getExtras();
        if (bundle != null) {
            try {
                Log.e("Intent---", "Dumping Intent start : " + bundle.toString());
                title = bundle.getString("title");
                type = bundle.getString("type");
                trac_relation = bundle.getString("trac_relation");
                if(!TextUtils.isEmpty(trac_relation)){
                    if(trac_relation.equalsIgnoreCase("p") || trac_relation.equalsIgnoreCase("g") || trac_relation.equalsIgnoreCase("group")){
                        groupPosition = 1;
                    }else if(trac_relation.equalsIgnoreCase("f")){
                        groupPosition = 2;
                    }else if(trac_relation.equalsIgnoreCase("personal")){
                        groupPosition = 0;
                    }
                }

                Log.e("title", "" + title);
                Log.e("type", "" + type);
                Log.e("trac_relation", "" + trac_relation);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.e("Intent---", "Dumping Intent end");
        }
    }
}
