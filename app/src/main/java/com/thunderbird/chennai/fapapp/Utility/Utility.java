package com.thunderbird.chennai.fapapp.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.thunderbird.chennai.fapapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import dmax.dialog.SpotsDialog;

/**
 * Created by Darshan on 12/7/2016.
 */

public class Utility {


    private Activity mContext;
    private AlertDialog mDialog;

    public Utility(Activity context){
        mContext=context;
    }

    /**
     * checks if internet is available or not
     * @return
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showProgressDialog(){
        mDialog=new SpotsDialog(mContext, R.style.Custom);
        mDialog.show();
    }

    public void closeProgressDialog(){
        mDialog.dismiss();
    }

    public String convertDateTime(String date){
        //String date = "2014-11-25 14:30";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        //  SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
        return formatter.format(testDate);
    }



    public String convertDateTime1(String date){
        //String date = "2014-11-25 14:30";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        //  SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
        return formatter.format(testDate);
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open("cities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
