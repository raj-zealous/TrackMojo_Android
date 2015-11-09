package com.tracmojo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tracmojo.R;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;


public class Util {

    public static boolean canAddPersonalTrac;
    public static boolean canAddGroupTrac;
    public static boolean isAccountExpired;
    public static int participantLeftCount;

    public static boolean isAlert = true;
    private static boolean showLogs = true;
    public static int ImageCount = 0;
    public static int LoadLimit = 20;

    public static final String TAB_HOME = "HOME";
    public static final String TAB_SETTINGS = "SETTINGS";
    public static final String TAB_ADD_TRAC = "ADD_TRAC";
    public static final String TAB_EDIT_TRAC = "EDIT_TRAC";
    public static String KEY_isFirstTime = "isfirsttime";
    
    
    public static void setIsFirstTimepreference(Context ct, String ud) {
		// TODO Auto-generated method stub

		@SuppressWarnings({ "static-access", "deprecation" })
		SharedPreferences sp = ct.getSharedPreferences("isfirsttime",
				ct.MODE_WORLD_READABLE);
		SharedPreferences.Editor peditor = sp.edit();
		peditor.putString(KEY_isFirstTime, ud);

		peditor.commit();

	}
	
	public static String getIsFirstTimepreference(Context ct) {
		// TODO Auto-generated method stub
		@SuppressWarnings({ "static-access", "deprecation" })
		SharedPreferences sp = ct.getSharedPreferences("isfirsttime",
				ct.MODE_WORLD_READABLE);

		return sp.getString(KEY_isFirstTime, "true");

	}

    public static boolean checkConnection(Context activity) {
        ConnectivityManager conMan = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = conMan.getActiveNetworkInfo();

        final boolean connected = networkInfo != null
                && networkInfo.isAvailable() && networkInfo.isConnected();

        if (!connected) {
            showMessage(activity, activity.getString(R.string.please_check_internet_connection));
            return false;
        }
        return true;
    }

    public static boolean checkConnectionWithoutMessage(Context activity) {
        ConnectivityManager conMan = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = conMan.getActiveNetworkInfo();

        final boolean connected = networkInfo != null
                && networkInfo.isAvailable() && networkInfo.isConnected();

        if (!connected) {
            return false;
        }
        return true;
    }


    public static boolean checkConnection(Context activity, String message) {
        ConnectivityManager conMan = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = conMan.getActiveNetworkInfo();

        final boolean connected = networkInfo != null
                && networkInfo.isAvailable() && networkInfo.isConnected();

        if (!connected) {
            showMessage(activity, message);
            return false;
        }
        return true;
    }


    public static boolean isAlpha(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if (c != ' ' && !Character.isLetter(c)) {
                return false;
            }
        }

        return true;
    }

    public static void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle(title);


        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                })/*.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				})*/;

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void showMessage(Context con, String msg) {
        Toast.makeText(con, msg, Toast.LENGTH_SHORT).show();
    }


    public static boolean isEditTextEmpty(EditText et) {
        return TextUtils.isEmpty(et.getText().toString().trim());
    }

    /**
     * This method is used to clear shared preference fbFriendList
     *
     * @param context
     */

    /**
     * Method to show toast message
     *
     * @param context
     * @param message
     * @param duration
     */
    public final static void showToast(Context context, String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    /**
     * Method to show error log in logcat
     *
     * @param tag
     * @param message
     */
    public final static void showErrorLog(String tag, String message) {
        if (showLogs)
            Log.e(tag, message);
    }

    /**
     * Method to show debug log in logcat
     *
     * @param tag
     * @param message
     */
    public final static void showDebugLog(String tag, String message) {
        if (showLogs)
            Log.d(tag, message);
    }

    public static void makeCall(Context mContext, String phoneNo) {
        // TODO Auto-generated method stub
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNo));
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {

        }
    }


    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static String getTimeZoneOffSet() {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        Log.e("Time zone", "=" + (float) (tz.getRawOffset() / 1000l) / 3600);
        return "" + (float) (tz.getRawOffset() / 1000l) / 3600;
    }

    public static String getCurrentTimeZone() {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        Log.e("Time zone", "=" + tz.getID());
        return "" + tz.getID();
    }

    public static String getDateInDdMMFormat(long timestamp) {
        String strDate = "";
		/*Date date = new Date(timestamp*1000l);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM");
		strDate = sdf.format(date);*/
        return strDate;
    }

    public static String getDateInDdMMFormat(String sourceDate) {
        String strDate = "";
        SimpleDateFormat sdfSource = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfDestination = new SimpleDateFormat("dd-MM");
        Date date = null;
        try {
            date = sdfSource.parse(sourceDate);
            strDate = sdfDestination.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate;
    }

}


