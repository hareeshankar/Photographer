package com.thunderbird.chennai.fapapp.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.thunderbird.chennai.fapapp.Adapter.AdapterSpecialityList;
import com.thunderbird.chennai.fapapp.Model.SpModel;
import com.thunderbird.chennai.fapapp.Model.Speciality;
import com.thunderbird.chennai.fapapp.R;
import com.thunderbird.chennai.fapapp.Utility.Textclass;
import com.thunderbird.chennai.fapapp.Utility.TypefaceTextView;
import com.thunderbird.chennai.fapapp.Utility.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Darshan on 12/5/2016.
 */

public class SpecialityFragment extends Fragment{

    private View v;
    private GridView gridview;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private Utility utility;
    private String uId;
    private ArrayList<Speciality> userSpecialityList;
    String specialityID;
    private String multiSelectedId;
    private String screen;
    RecyclerView recyclerView;
    TextView txtMessage;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_grid_speciality, null, false);
        gridview=(GridView)v.findViewById(R.id.gridview);
        recyclerView=(RecyclerView)v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        txtMessage=(TextView)v.findViewById(R.id.txtMessage);
        txtMessage.setVisibility(View.GONE);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        utility=new Utility(getActivity());
        sharedpreferences = getActivity().getSharedPreferences("myprefrence", Context.MODE_PRIVATE);
        editor=sharedpreferences.edit();

        Bundle b=getArguments();
        if(b!=null){
            Log.e("screen",""+b.getString("screen"));
            screen=b.getString("screen");
            uId=b.getString("u_id");
        }

        if(screen.equalsIgnoreCase("profile")){
            uId=sharedpreferences.getString("u_id","");
            Log.e("screen",""+uId);
        }else{

            uId=b.getString("u_id");

        }

        if(utility.isNetworkAvailable()){
            getUserSpecialityService();
        }else{
            Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();

        }



    }






    public void getUserSpecialityService(){


        userSpecialityList=new ArrayList<Speciality>();
        Log.e("uid",""+uId);
        String url = Textclass.baseurl + "get_new_user_speciality.php?api_key=ZmFw&u_id="+uId;
        Log.e("uid",""+url);
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(getActivity(), url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                utility.showProgressDialog();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"

                multiSelectedId="";
                String str = new String(responseBody);
                Log.d("Success", statusCode + "" + str);
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    Integer success = jsonObject.getInt("success");
                    Log.e("lenght", "" + jsonObject.length());
                    if (success == 0) {
                        txtMessage.setVisibility(View.VISIBLE);

                    } else {
                        txtMessage.setVisibility(View.GONE);
                        for (int i = 1; i <= success; i++) {
                            JSONObject jsonObject1 = jsonObject.getJSONObject(String.valueOf(i));
                            Speciality model = new Speciality();
                            model.setSp_id(jsonObject1.getString("sp_id"));
                            model.setSp_name(jsonObject1.getString("sp_name"));

                            userSpecialityList.add(model);
                        }

                        AdapterSpecialityList specialityAdapter = new AdapterSpecialityList(getActivity(), userSpecialityList);
                        recyclerView.setAdapter(specialityAdapter);
                    }


                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    utility.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                utility.closeProgressDialog();
            }
        });
    }



}
