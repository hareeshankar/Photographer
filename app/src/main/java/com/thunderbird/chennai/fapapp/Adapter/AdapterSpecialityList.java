package com.thunderbird.chennai.fapapp.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thunderbird.chennai.fapapp.Model.Speciality;
import com.thunderbird.chennai.fapapp.Model.Speciality;
import com.thunderbird.chennai.fapapp.R;

import java.util.ArrayList;

/**
 * Created by piyush on 3/21/2017.
 */

public class AdapterSpecialityList extends RecyclerView.Adapter<AdapterSpecialityList.ViewHolder> {

    ProgressDialog pDialog;
    ArrayList<Speciality> SpecialityList;
    Context context;


    public AdapterSpecialityList(Context context, ArrayList<Speciality> SpecialityList) {
        this.SpecialityList = SpecialityList;
        this.context = context;

    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txt_des,txt_name;
        LinearLayout main_layout;

        public ViewHolder(View v) {
            super(v);
            main_layout=(LinearLayout)v.findViewById(R.id.main_layout);
            txt_name = (TextView) v.findViewById(R.id.txt_name);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_design_quipment, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Speciality Speciality = SpecialityList.get(position);
        holder.txt_name.setText(Speciality.getSp_name());



    }

    @Override
    public int getItemCount() {
        return SpecialityList.size();
    }



}