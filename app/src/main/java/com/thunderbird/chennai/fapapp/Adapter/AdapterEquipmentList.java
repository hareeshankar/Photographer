package com.thunderbird.chennai.fapapp.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thunderbird.chennai.fapapp.Model.Equipment;
import com.thunderbird.chennai.fapapp.Model.Equipment;
import com.thunderbird.chennai.fapapp.R;

import java.util.ArrayList;

/**
 * Created by piyush on 3/21/2017.
 */

public class AdapterEquipmentList extends RecyclerView.Adapter<AdapterEquipmentList.ViewHolder> {

    ProgressDialog pDialog;
    ArrayList<Equipment> EquipmentList;
    Context context;
    public AdapterEquipmentList(Context context, ArrayList<Equipment> EquipmentList) {
        this.EquipmentList = EquipmentList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

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

        final Equipment Equipment = EquipmentList.get(position);
        holder.txt_name.setText(Equipment.getEp_name());
    }

    @Override
    public int getItemCount() {
        return EquipmentList.size();
    }
}