package com.thunderbird.chennai.fapapp.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thunderbird.chennai.fapapp.Model.EqpModel;
import com.thunderbird.chennai.fapapp.R;
import com.thunderbird.chennai.fapapp.Utility.EditTextK;
import com.thunderbird.chennai.fapapp.Utility.Textclass;
import com.thunderbird.chennai.fapapp.Utility.TypefaceTextView;
import com.thunderbird.chennai.fapapp.Utility.Utility;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.ParseException;


public class EventFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    // TODO: Rename parameter arguments, choose names that match
    RecyclerView recyclerView;
    Utility utility;
    private SharedPreferences sharedpreferences;
    private String uId;
    private SharedPreferences.Editor editor;
    private String screen;
    ListView listviewSession;
    SessionListAdapter sessionListAdapter;
    ArrayList<EqpModel> sessionList;
    private int sPosition;
    String ueb_id,ep_id;
    private AlertDialog alertDialog;
    CheckBox chkMorning1;
    CheckBox chkAfternoon1;
    CheckBox chkEvening1;
    private String sessionString1="";
    private TypefaceTextView tvSelectDate;
    private TypefaceTextView btnDone;
    private EditTextK edtEvent;
    TextView txtMessage;
    public EventFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_event, container, false);
        listviewSession = (ListView) view.findViewById(R.id.listview);
        utility = new Utility(getActivity());
        txtMessage=(TextView)view.findViewById(R.id.txtMessage);
        txtMessage.setVisibility(View.GONE);
        sharedpreferences = getActivity().getSharedPreferences("myprefrence", Context.MODE_PRIVATE);

        editor = sharedpreferences.edit();


        Bundle b = getArguments();
        if (b != null) {
            Log.e("screen",""+b.getString("screen"));
            screen = b.getString("screen");

        }

        if (screen.equalsIgnoreCase("profile")) {
            uId = sharedpreferences.getString("u_id", "");
        } else {
            uId = b.getString("u_id");
        }


        if (utility.isNetworkAvailable()) {
            getBookedSessions();
        } else {
            Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();

        }

        listviewSession.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sPosition=position;
                ueb_id=sessionList.get(position).getUebId();
                ep_id=sessionList.get(position).getEpId();
                SessionAction(position);
            }
        });

        return view;
    }

    private void getBookedSessions() {
        sessionListAdapter = new SessionListAdapter(getActivity());
        sessionList = new ArrayList<EqpModel>();


        String url = Textclass.baseurl + "get_user_all_equipment_block.php?api_key=ZmFw&u_id=" + uId + "";
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


              /*  {"Get_data":[{"ueb_id":"15","u_id":"7","ep_id":"2","ue_id":"14","b_date":"2016-12-27",
              "b_session":"Evening","b_created_date":"2016-12-27 03:42:21"}],"status":1,"message":"Get_data"}*/

                String str = new String(responseBody);
                Log.d("Success", statusCode + "" + str);
                try {
                    JSONObject json = new JSONObject(str);

                    int status = json.getInt("status");
                    if (status == 1) {
                        txtMessage.setVisibility(View.GONE);
                        JSONArray jsonArray = json.getJSONArray("Get_data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            EqpModel model = new EqpModel();
                            //  model.setu(jsonObject1.getString("u_id"));
                            model.setUebId(jsonObject1.getString("ueb_id"));
                            model.setEpId(jsonObject1.getString("ep_id"));
                            model.setEpName(jsonObject1.getString("ep_name"));
                            model.setBlockedSessionDate(jsonObject1.getString("b_date"));
                            model.setSession(jsonObject1.getString("b_session"));
                            model.setEventDetail(jsonObject1.getString("event_details"));
                            model.setUserEquipId(jsonObject1.getString("ue_id"));
                            sessionList.add(model);
                        }

                        listviewSession.setAdapter(sessionListAdapter);


                        //  Toast.makeText(getActivity(), getString(R.string.successfully_added), Toast.LENGTH_SHORT).show();

                    } else if (status == 0) {
                        txtMessage.setVisibility(View.VISIBLE);


                    } else {
                        Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_SHORT).show();
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

        private void DeleteBookSession(String id) {



            String url = Textclass.baseurl + "delete_user_equipment.php?api_key=ZmFw&ueb_id=" + id +"";
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
                    Log.d("Success", statusCode + "" + responseBody);

              /*  {"Get_data":[{"ueb_id":"15","u_id":"7","ep_id":"2","ue_id":"14","b_date":"2016-12-27",
              "b_session":"Evening","b_created_date":"2016-12-27 03:42:21"}],"status":1,"message":"Get_data"}*/

                    String str = new String(responseBody);
                    try {
                        JSONObject json = new JSONObject(str);

                        int status = json.getInt("status");
                        if (status == 1) {

                            sessionList.remove(sPosition);
                            sessionListAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), "Delete Successfully", Toast.LENGTH_SHORT).show();
//                            getBookedSessions();

                        } else if (status == 0) {
                            Toast.makeText(getActivity(),  getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        tvSelectDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
    }



    public class SessionListAdapter extends BaseAdapter {


        private final Context context;

        private int col;
        private ViewHolder holder;

        SessionListAdapter(Context context) {

            this.context = context;

        }

        @Override
        public int getCount() {

            return sessionList.size();
        }

        @Override
        public Object getItem(int position) {

            //   return 10;
            return sessionList.get(position);

        }

        @Override
        public long getItemId(int position) {

            // return 10;
            return sessionList.indexOf(getItem(position));
        }

        /**
         * Viewholder class
         */
        public class ViewHolder {
            TypefaceTextView tvSession,tv_eq_name;
            ImageView imgDeleteSession;
            TypefaceTextView tvSessionDate;
            TypefaceTextView tvEvent,tv_date,tv_year;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater mInflater;
            if (convertView == null) {

            holder= new ViewHolder();
                mInflater = (LayoutInflater) context
                        .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.custom_design_event, null);
                holder.tvSession = (TypefaceTextView) convertView.findViewById(R.id.tv_session);
                holder.tv_date = (TypefaceTextView) convertView.findViewById(R.id.tv_date);
                holder.tv_year = (TypefaceTextView) convertView.findViewById(R.id.tv_year);
                holder.tvEvent = (TypefaceTextView) convertView.findViewById(R.id.tv_event);
                holder.tv_eq_name = (TypefaceTextView) convertView.findViewById(R.id.tv_ep_name);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();


            }
            String[] date=utility.convertDateTime1(sessionList.get(position).getBlockedSessionDate()).split("-");
            holder.tvSession.setText(sessionList.get(position).getSession());
            holder.tv_year.setText(date[2]);
            holder.tv_date.setText(date[0]+"/"+date[1]);
            holder.tvEvent.setText(sessionList.get(position).getEventDetail());
            holder.tv_eq_name.setText(sessionList.get(position).getEpName());
            return convertView;

        }


    }

    public void SessionAction(int position) {
        final android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.dialog_contact_me, null);
        RelativeLayout relBookSession = (RelativeLayout) subView.findViewById(R.id.rel_email);
        RelativeLayout relDeleteBookSession = (RelativeLayout) subView.findViewById(R.id.rel_phone);
        TypefaceTextView tvDisplayEmail = (TypefaceTextView) subView.findViewById(R.id.tv_display_email);
        TypefaceTextView tvDisplayPhone = (TypefaceTextView) subView.findViewById(R.id.tv_display_phone);
        TypefaceTextView tvEmail = (TypefaceTextView) subView.findViewById(R.id.tv_email);
        TypefaceTextView tvPhone = (TypefaceTextView) subView.findViewById(R.id.tv_phone);
        tvEmail.setText("Edit Session");
        tvPhone.setText("Delete Session");

        tvDisplayEmail.setVisibility(View.INVISIBLE);
        tvDisplayPhone.setVisibility(View.INVISIBLE);
        relBookSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionBookDialog();
                alertDialog.dismiss();

            }
        });

        relDeleteBookSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DeleteBookSession(ueb_id);
                alertDialog.dismiss();


            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setView(subView);

        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void sessionBookDialog() {

        Calendar now = Calendar.getInstance();
        final DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setAccentColor(getResources().getColor(R.color.colorAccent));

        dpd.setMinDate(now);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.update_session_dialog, null);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setView(subView);
        alertDialog.setCancelable(true);
        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvSelectDate = (TypefaceTextView) subView.findViewById(R.id.tv_select_date);
        btnDone = (TypefaceTextView) subView.findViewById(R.id.btn_done);
        //   relSession=(RelativeLayout)subView.findViewById(R.id.rel_session);
        chkMorning1 = (CheckBox) subView.findViewById(R.id.chk_morning);
        chkAfternoon1 = (CheckBox) subView.findViewById(R.id.chk_afternoon);
        chkEvening1 = (CheckBox) subView.findViewById(R.id.chk_evening);
        edtEvent = (EditTextK) subView.findViewById(R.id.edt_event);
        edtEvent.setText(sessionList.get(sPosition).getEventDetail().toString());
        tvSelectDate.setText(parseConvertDate(sessionList.get(sPosition).getBlockedSessionDate().toString()));
        if(sessionList.get(sPosition).getSession().toString().equalsIgnoreCase("Morning")){
        chkMorning1.setChecked(true);
        }else if(sessionList.get(sPosition).getSession().toString().equalsIgnoreCase("Afternoon")){
            chkAfternoon1.setChecked(true);
        }else {
            chkEvening1.setChecked(true);
        }
        chkMorning1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    chkEvening1.setChecked(false);
                    chkAfternoon1.setChecked(false);
                }
            }
        });
        chkAfternoon1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    chkMorning1.setChecked(false);
                    chkEvening1.setChecked(false);
                }
            }
        });
        chkEvening1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    chkMorning1.setChecked(false);
                    chkAfternoon1.setChecked(false);
                }
            }
        });
        tvSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpd.show(getActivity().getFragmentManager(), "Start date1");
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tvSelectDate.getText().equals("Start Date")){
                    Toast.makeText(getActivity(),"Select Start Date",Toast.LENGTH_SHORT).show();
                }else if(!chkMorning1.isChecked() &&!chkAfternoon1.isChecked()  && !chkEvening1.isChecked()){
                    Toast.makeText(getActivity(),"Select Session Time",Toast.LENGTH_SHORT).show();
                }else if(edtEvent.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Enter event detail", Toast.LENGTH_SHORT).show();
                }else {
                    sessionString1 = "";
                    if (chkMorning1.isChecked()) {
                        sessionString1 = sessionString1 + "Morning" + ",";
                    }

                    if (chkAfternoon1.isChecked()) {
                        sessionString1 = sessionString1 + "Afternoon" + ",";
                    }

                    if (chkEvening1.isChecked()) {
                        sessionString1 = sessionString1 + "Evening" + ",";
                    }

                    if (sessionString1.endsWith(",")) {
                        sessionString1 = sessionString1.substring(0, sessionString1.length() - 1);

                    }


                    updateSessionService(tvSelectDate.getText().toString(),sessionString1,edtEvent.getText().toString(),alertDialog);
                }
            }
        });

    }

    public String parseDate(String time) {
        String inputPattern = "dd-MM-yyyy";
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return str;
    }


    public String parseConvertDate(String time) {
        String inputPattern ="yyyy-MM-dd";
        String outputPattern = "dd-MM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    private void updateSessionService(String date1,  String sessionString1,String details, final AlertDialog alertDialog) {
        String url = Textclass.baseurl + "update_session.php";

        RequestParams requestParams = new RequestParams();
        requestParams.add("api_key", "ZmFw");
        requestParams.add("u_id", uId);
        requestParams.add("ep_id",sessionList.get(sPosition).getEpId());
        requestParams.add("ue_id",sessionList.get(sPosition).getUserEquipId());
        requestParams.add("b_session", sessionString1);
        requestParams.add("ueb_id", sessionList.get(sPosition).getUebId());
        requestParams.add("b_date",parseDate(date1) );
        requestParams.add("event_details", details);

        Log.e("sesstion1",requestParams.toString());
//        Log.e("sesstion2",sessionString2);
//        Log.e("date1",date1);
//        Log.e("date2",date2);
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(getActivity(), url, requestParams, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                utility.showProgressDialog();

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"


                alertDialog.dismiss();
                String str = new String(responseBody);
                Log.d("Success", statusCode + "" + str);
                try {
                    JSONObject json = new JSONObject(str);
                    int status = json.getInt("status");
                    if (status == 1) {
                        getBookedSessions();
                        Toast.makeText(getActivity(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else if (status == 0) {
                        Toast.makeText(getActivity(), getString(R.string.server_problem), Toast.LENGTH_SHORT).show();
                    } else if(status==2){
                        Toast.makeText(getActivity(), "Equiment already booked", Toast.LENGTH_SHORT).show();
                    }
                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
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
