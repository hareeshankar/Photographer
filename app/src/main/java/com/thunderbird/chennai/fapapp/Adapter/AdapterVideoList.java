package com.thunderbird.chennai.fapapp.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.thunderbird.chennai.fapapp.Model.Video;
import com.thunderbird.chennai.fapapp.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by piyush on 3/21/2017.
 */

public class AdapterVideoList extends RecyclerView.Adapter<AdapterVideoList.ViewHolder> {

    ProgressDialog pDialog;
    ArrayList<Video> VideoList;
    Context context;


    public AdapterVideoList(Context context, ArrayList<Video> VideoList) {
        this.VideoList = VideoList;
        this.context = context;

    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txt_des,txt_name;
        LinearLayout main_layout;

        public ViewHolder(View v) {
            super(v);
            main_layout=(LinearLayout)v.findViewById(R.id.main_layout);
            txt_name = (TextView) v.findViewById(R.id.txt_name);
            txt_des = (TextView) v.findViewById(R.id.txt_des);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_design_videolist, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Video Video = VideoList.get(position);
        holder.txt_name.setText(Video.getV_name());
        holder.txt_des.setText(Video.getV_dec());

    }

    @Override
    public int getItemCount() {
        return VideoList.size();
    }

}