package com.thunderbird.chennai.fapapp.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bumptech.glide.Glide;
import com.loopj.android.http.RequestParams;
import com.thunderbird.chennai.fapapp.Model.EqpModel;
import com.thunderbird.chennai.fapapp.R;
import com.thunderbird.chennai.fapapp.Utility.EditTextK;
import com.thunderbird.chennai.fapapp.Utility.Textclass;
import com.thunderbird.chennai.fapapp.Utility.TypefaceTextView;
import com.thunderbird.chennai.fapapp.Utility.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Darshan on 12/5/2016.
 */

public class EquipmentFragment extends android.support.v4.app.Fragment implements BillingProcessor.IBillingHandler, DatePickerDialog.OnDateSetListener {

    private View v;
    private GridView gridview;
    private Utility utility;
    private SharedPreferences sharedpreferences;
    private String uId;
    private SharedPreferences.Editor editor;
    private ArrayList<EqpModel> userEquipmentList;
    private String screen;
    private String multiSelectedId;
    private String specialityID;
    private String transactionId;
    private String multiSelectedTransactionId, orderId;
    private BillingProcessor bp;
    private int mPosition;
    private String sku;
    private Spinner spSession;
    private TypefaceTextView tvSessionShow;
    private TypefaceTextView tvSelectDate;
    private TypefaceTextView btnDone;
    private RelativeLayout relSession;
    private AlertDialog alertDialog;
    private ArrayList<EqpModel> sessionList;
    private int sPosition;
    private ListView listviewSession;
    private String userId;
    private TypefaceTextView tvNoData;
    private SessionListAdapter sessionListAdapter;
    private TypefaceTextView tvEndDate;
    String dateRangeList;
    private CheckBox chkMorning;
    private CheckBox chkAfternoon;
    private CheckBox chkEvening;
    private String sessionString;
    private String endDate;
    private EditTextK edtEvent;
    private String mEquipmentId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        v = inflater.inflate(R.layout.fragment_grid_equipment, null, false);
        gridview = (GridView) v.findViewById(R.id.gridview);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        utility = new Utility(getActivity());
        sharedpreferences = getActivity().getSharedPreferences("myprefrence", Context.MODE_PRIVATE);

        editor = sharedpreferences.edit();

        bp = new BillingProcessor(getActivity(), Textclass.base64, this);

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
            getUserEquipmentService();
        } else {
            Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();

        }


        if (screen.equalsIgnoreCase("profile")) {
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    mPosition = i;
                    if (userEquipmentList.get(i).getUserHave().equalsIgnoreCase("0")) {

                        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity()).setMessage("Are you sure you want to add equipment?").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (utility.isNetworkAvailable()) {
                                    //  bp.purchase(getActivity(),"android.test.purchased");


                                    if (userEquipmentList.get(mPosition).getUserHave().equalsIgnoreCase("0")) {
                                        //free equipment code(temporary)
                                        if (userEquipmentList.get(mPosition).getTimeOf().equalsIgnoreCase("0")) {
                                            userEquipmentList.get(mPosition).setTransactionId("free");
                                            specialityID = userEquipmentList.get(mPosition).getEpId();
                                            transactionId = userEquipmentList.get(mPosition).getTransactionId();
                                            orderId = "free";
                                            if (multiSelectedId != null && multiSelectedId.length() > 0 && multiSelectedId.charAt(multiSelectedId.length() - 1) == ',') {
                                                multiSelectedId = multiSelectedId.substring(0, multiSelectedId.length() - 1);
                                                multiSelectedTransactionId = multiSelectedTransactionId.substring(0, multiSelectedTransactionId.length() - 1);
                                            }
                                            multiSelectedId = specialityID + multiSelectedId;
                                            multiSelectedTransactionId = transactionId + multiSelectedTransactionId;

                                            getTrialDialog();


                                        } else {
                                            //for paid equipment
                                            bp.purchase(getActivity(), "equipment_pid");
                                            //    bp.purchase(getActivity(),"android.test.purchased");
                                        }

                                    }

                                } else {
                                    Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(true);

                        alertDialog.show();
                    } else {
                        selectSession();
                    }
                }
            });
        } else {
            /*gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    if(!userEquipmentList.get(i).getUserHave().equalsIgnoreCase("0")){
                        mPosition=i;
                        sessionListDialog();
                    }

                }
            });*/
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 32459) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == getActivity().RESULT_OK) {
                try {

                    JSONObject jo = new JSONObject(purchaseData);
                    Log.e("json:", jo.toString());
                    sku = jo.getString("productId");
                    //   transactionId=jo.getString("orderId");
                    transactionId = jo.getString("purchaseToken");
                    userEquipmentList.get(mPosition).setTransactionId(transactionId);
                    specialityID = userEquipmentList.get(mPosition).getEpId();
                    transactionId = userEquipmentList.get(mPosition).getTransactionId();
                    if (multiSelectedId != null && multiSelectedId.length() > 0 && multiSelectedId.charAt(multiSelectedId.length() - 1) == ',') {
                        multiSelectedId = multiSelectedId.substring(0, multiSelectedId.length() - 1);
                        multiSelectedTransactionId = multiSelectedTransactionId.substring(0, multiSelectedTransactionId.length() - 1);
                    }
                    multiSelectedId = specialityID + multiSelectedId;
                    multiSelectedTransactionId = transactionId + multiSelectedTransactionId;
                    if (jo.has("orderId")) {
                        orderId = jo.getString("orderId");
                    } else {
                        orderId = "";
                    }

                    mEquipmentId = multiSelectedId;

                    addUserEquipment();

                    /*equipmentList.get(equipPosition).setTransactionId(transactionId);
                    tvRelImageCandid1Ref.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    tvNameRef.setTextColor(getResources().getColor(R.color.colorAccent));*/
                    //  alertDialog.dismiss();

                    //  addSubscribedUser();
                    //  loadFragment();
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * alert dialog
     */
    private void sessionListDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.session_list_dialog, null);

        listviewSession = (ListView) subView.findViewById(R.id.listview);
        tvNoData = (TypefaceTextView) subView.findViewById(R.id.tv_no_data);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setView(subView);
        alertDialog.setCancelable(true);
        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, sessionList);
        spSession.setAdapter(adapter);*/
        if (utility.isNetworkAvailable()) {
            getBookedSessions();
        } else {
            Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
        }


    }


    /**
     * alert dialog
     */
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

        final ArrayList<String> sessionList = new ArrayList<>();
        sessionList.add("Select session");
        sessionList.add("Morning");
        sessionList.add("Afternoon");
        sessionList.add("Evening");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.book_session_dialog, null);

        //   spSession = (Spinner)subView. findViewById(R.id.sp_session);
        //   tvSessionShow = (TypefaceTextView)subView. findViewById(R.id.tv_session_show);
        tvSelectDate = (TypefaceTextView) subView.findViewById(R.id.tv_select_date);
        tvEndDate = (TypefaceTextView) subView.findViewById(R.id.tv_end_date);
        btnDone = (TypefaceTextView) subView.findViewById(R.id.btn_done);
        //   relSession=(RelativeLayout)subView.findViewById(R.id.rel_session);
        chkMorning = (CheckBox) subView.findViewById(R.id.chk_morning);
        chkAfternoon = (CheckBox) subView.findViewById(R.id.chk_afternoon);
        chkEvening = (CheckBox) subView.findViewById(R.id.chk_evening);
        edtEvent = (EditTextK) subView.findViewById(R.id.edt_event);


        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setView(subView);
        alertDialog.setCancelable(true);
        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, sessionList);
        spSession.setAdapter(adapter);

        relSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spSession.performClick();
            }
        });

        spSession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvSessionShow.setText(sessionList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        btnDone.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                if (utility.isNetworkAvailable()) {

                    sessionString = "";
                    if (chkMorning.isChecked()) {
                        sessionString = sessionString + "Morning" + ",";
                    }

                    if (chkAfternoon.isChecked()) {
                        sessionString = sessionString + "Afternoon" + ",";
                    }

                    if (chkEvening.isChecked()) {
                        sessionString = sessionString + "Evening" + ",";
                    }

                    if (sessionString.endsWith(",")) {
                        sessionString = sessionString.substring(0, sessionString.length() - 1);

                    }


                    if (tvSelectDate.getText().toString().equalsIgnoreCase("Start Date")) {
                        Toast.makeText(getActivity(), "Enter start date", Toast.LENGTH_SHORT).show();
                    } else if (tvEndDate.getText().toString().equalsIgnoreCase("End Date")) {
                        Toast.makeText(getActivity(), "Enter end date", Toast.LENGTH_SHORT).show();
                    } else if (!(chkMorning.isChecked() || chkAfternoon.isChecked() || chkEvening.isChecked())) {
                        Toast.makeText(getActivity(), "Select session", Toast.LENGTH_SHORT).show();
                    } else if (edtEvent.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Enter event detail", Toast.LENGTH_SHORT).show();
                    } else {
                        //String date = "2014-11-25 14:30";
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        Date testDate = null;
                        Date testDate1 = null;
                        try {
                            testDate = sdf.parse(tvSelectDate.getText().toString());
                            testDate1 = sdf.parse(endDate);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        String dates = "";
                        //   List<String> dates = new ArrayList<String>();
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(testDate);

                        if (calendar.getTime().before(testDate1) || calendar.getTime().equals(testDate1)) {
                            dateRangeList = getDaysBetweenDates(tvSelectDate.getText().toString(), endDate);

                            if (dateRangeList.endsWith(",")) {
                                dateRangeList = dateRangeList.substring(0, dateRangeList.length() - 1);
                            }
                            bookSessionService();
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Enter valid date range", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                }


            }
        });

        tvSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dpd.show(getActivity().getFragmentManager(), "Start date");
            }
        });

        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dpd.show(getActivity().getFragmentManager(), "End date");
            }
        });


    }

    public String getDaysBetweenDates(String startdate, String enddate) {

        //String date = "2014-11-25 14:30";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date testDate = null;
        Date testDate1 = null;
        try {
            testDate = sdf.parse(startdate);
            testDate1 = sdf.parse(enddate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String dates = "";
        //   List<String> dates = new ArrayList<String>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(testDate);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        while (calendar.getTime().before(testDate1)) {
            Date result = calendar.getTime();
            dates = dates + formatter.format(result) + ",";
            // dates.add(formatter.format(result));
            calendar.add(Calendar.DATE, 1);
        }


        return dates;
    }

    public void selectSession() {
        final android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.dialog_contact_me, null);
        RelativeLayout relBookSession = (RelativeLayout) subView.findViewById(R.id.rel_email);
        RelativeLayout relDeleteBookSession = (RelativeLayout) subView.findViewById(R.id.rel_phone);
        TypefaceTextView tvDisplayEmail = (TypefaceTextView) subView.findViewById(R.id.tv_display_email);
        TypefaceTextView tvDisplayPhone = (TypefaceTextView) subView.findViewById(R.id.tv_display_phone);
        TypefaceTextView tvEmail = (TypefaceTextView) subView.findViewById(R.id.tv_email);
        TypefaceTextView tvPhone = (TypefaceTextView) subView.findViewById(R.id.tv_phone);
        tvEmail.setText("Book session");
        tvPhone.setText("View session");

        tvDisplayEmail.setVisibility(View.INVISIBLE);
        tvDisplayPhone.setVisibility(View.INVISIBLE);
        relBookSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                sessionBookDialog();
            }
        });

        relDeleteBookSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                sessionListDialog();


            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setView(subView);

        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void getBookedSessions() {

        sessionList = new ArrayList<EqpModel>();

        String url = Textclass.baseurl + "get_user_equipment_block.php?api_key=ZmFw&u_id=" + uId + "&ep_id=" + userEquipmentList.get(mPosition).getEpId();
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

                        JSONArray jsonArray = json.getJSONArray("Get_data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            EqpModel model = new EqpModel();
                            //  model.setu(jsonObject1.getString("u_id"));
                            model.setUebId(jsonObject1.getString("ueb_id"));
                            model.setEpId(jsonObject1.getString("ep_id"));
                            model.setBlockedSessionDate(jsonObject1.getString("b_date"));
                            model.setSession(jsonObject1.getString("b_session"));
                            model.setEventDetail(jsonObject1.getString("event_details"));
                            sessionList.add(model);
                        }
                        sessionListAdapter = new SessionListAdapter(getActivity());
                        listviewSession.setAdapter(sessionListAdapter);


                        //  Toast.makeText(getActivity(), getString(R.string.successfully_added), Toast.LENGTH_SHORT).show();

                    } else if (status == 0) {
                        //  Toast.makeText(getActivity(), getString(R.string.no_data), Toast.LENGTH_SHORT).show();
                        listviewSession.setVisibility(View.INVISIBLE);
                        tvNoData.setVisibility(View.VISIBLE);
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

    private void bookSessionService() {
        String url = Textclass.baseurl + "add_equipment_book.php";

        RequestParams requestParams = new RequestParams();
        requestParams.add("api_key", "ZmFw");
        requestParams.add("u_id", uId);
        requestParams.add("ep_id", userEquipmentList.get(mPosition).getEpId());
        requestParams.add("ue_id", userEquipmentList.get(mPosition).getUserEquipId());
        requestParams.add("b_date", dateRangeList);
        requestParams.add("b_session", sessionString);
        requestParams.add("event_details", edtEvent.getText().toString());

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
                Log.d("Success", statusCode + "" + responseBody);

                String str = new String(responseBody);
                try {
                    JSONObject json = new JSONObject(str);
                    int status = json.getInt("status");
                    if (status == 1) {
                        Toast.makeText(getActivity(), getString(R.string.successfully_added), Toast.LENGTH_SHORT).show();

                    } else if (status == 0) {
                        Toast.makeText(getActivity(), getString(R.string.server_problem), Toast.LENGTH_SHORT).show();

                    } else if (status == 2) {
                        Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_SHORT).show();
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

    public void getUserEquipmentService() {
        multiSelectedId = "";
        multiSelectedTransactionId = "";
        userEquipmentList = new ArrayList<EqpModel>();

        String url = Textclass.baseurl1 + "get_user_equipment.php?api_key=ZmFw&u_id=" + uId;
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

                String str = new String(responseBody);
                try {
                    JSONObject json = new JSONObject(str);
                    int status = json.getInt("status");
                    if (status == 1) {
                        JSONArray jsonArray = json.getJSONArray("Get_data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            EqpModel model = new EqpModel();
                            model.setUserEquipId(jsonObject1.getString("ue_id"));
                            model.setEpId(jsonObject1.getString("ep_id"));
                            model.setTransactionId(jsonObject1.getString("transaction_id"));
                            model.setUserEpCreatedDate(jsonObject1.getString("ue_created_date"));
                            model.setEpCreatedDate(jsonObject1.getString("ep_created_date"));
                            model.setEpName(jsonObject1.getString("ep_name"));
                            model.setEpIcon(jsonObject1.getString("ep_icon"));
                            model.setUserHave(jsonObject1.getString("userhave"));
                            model.setUserBlock(jsonObject1.getString("userblock"));
                            model.setTimeOf(jsonObject1.getString("time_of"));
                            //have user means user have purchase equipment
                            if (model.getUserHave().equalsIgnoreCase("1") /*&& model.getUserBlock().equalsIgnoreCase("0")*/) {

                                multiSelectedId = multiSelectedId + "," + model.getEpId();
                                multiSelectedTransactionId = multiSelectedTransactionId + "," + model.getTransactionId();
                                if (!screen.equalsIgnoreCase("profile")) {
                                    userEquipmentList.add(model);
                                }
                            }

                            if (screen.equalsIgnoreCase("profile")) {
                                userEquipmentList.add(model);
                            }
                            // userEquipmentList.add(model);dss
                        }

                        EquipmentAdapter equipmentAdapter = new EquipmentAdapter(getActivity(), userEquipmentList);
                        gridview.setAdapter(equipmentAdapter);

                    } else if (status == 0) {

                        //  Toast.makeText(getActivity(), getString(R.string.no_data), Toast.LENGTH_SHORT).show();

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
                Toast.makeText(getActivity(), getActivity().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                utility.closeProgressDialog();
            }
        });
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        bp.consumePurchase(productId);
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();

        super.onDestroy();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if (view.getTag().equalsIgnoreCase("Start date")) {
            tvSelectDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            tvEndDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            endDate = dayOfMonth + 1 + "-" + (monthOfYear + 1) + "-" + year;
        } else {
            tvEndDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            endDate = dayOfMonth + 1 + "-" + (monthOfYear + 1) + "-" + year;
        }

    }


    public class EquipmentAdapter extends BaseAdapter {


        private final Context context;
        private final ArrayList<EqpModel> mUserEquipmentList;
        private int col;
        private ViewHolder holder;

        EquipmentAdapter(Context context, ArrayList<EqpModel> userEquipmentList) {

            this.context = context;
            mUserEquipmentList = userEquipmentList;
        }

        @Override
        public int getCount() {

            return userEquipmentList.size();
        }

        @Override
        public Object getItem(int position) {

            //   return 10;
            return userEquipmentList.get(position);

        }

        @Override
        public long getItemId(int position) {

            // return 10;
            return userEquipmentList.indexOf(getItem(position));
        }

        /**
         * Viewholder class
         */
        public  class ViewHolder {
            TypefaceTextView tvName;
            ImageView imgCustomCandid;
            RelativeLayout relImageCandid;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater mInflater;
            if (convertView == null) {
                holder = new ViewHolder();
                mInflater = (LayoutInflater) context
                        .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.gallary_custom, null);
                holder.tvName = (TypefaceTextView) convertView.findViewById(R.id.tv_name);
                holder.imgCustomCandid = (ImageView) convertView.findViewById(R.id.img_custom_candid);
                holder.relImageCandid = (RelativeLayout) convertView.findViewById(R.id.rel_img_candid);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (userEquipmentList.get(position).getUserHave().equalsIgnoreCase("1")) {
                holder.relImageCandid.setBackgroundColor(getResources().getColor(android.R.color.black));
                holder.tvName.setTextColor(getResources().getColor(R.color.colorAccent));
            } else {
                holder.relImageCandid.setBackgroundColor(getResources().getColor(R.color.lightgrey));
                holder.tvName.setTextColor(getResources().getColor(android.R.color.black));
            }

            holder.tvName.setText(userEquipmentList.get(position).getEpName());
            Glide.with(getActivity()).load(Textclass.equipment + userEquipmentList.get(position).getEpIcon()).into(holder.imgCustomCandid);

            return convertView;

        }


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
            TypefaceTextView tvSession;
            ImageView imgDeleteSession;
            TypefaceTextView tvSessionDate;
            TypefaceTextView tvEvent;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater mInflater;
            if (convertView == null) {

                holder = new ViewHolder();
                mInflater = (LayoutInflater) context
                        .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.custom_list_session, null);
                holder.tvSession = (TypefaceTextView) convertView.findViewById(R.id.tv_session);
                holder.imgDeleteSession = (ImageView) convertView.findViewById(R.id.img_delete_session);
                holder.tvSessionDate = (TypefaceTextView) convertView.findViewById(R.id.tv_session_date);
                holder.tvEvent = (TypefaceTextView) convertView.findViewById(R.id.tv_event);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();


            }
            holder.tvSession.setText(sessionList.get(position).getSession());
            holder.tvSessionDate.setText(utility.convertDateTime1(sessionList.get(position).getBlockedSessionDate()));
            holder.tvEvent.setText(sessionList.get(position).getEventDetail());

            if (screen.equalsIgnoreCase("profile")) {
                holder.imgDeleteSession.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (utility.isNetworkAvailable()) {
                            sPosition = position;
                            getDeleteSessionDialog();
                            //  deleteUserEquipmentService();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            } else {
                holder.imgDeleteSession.setVisibility(View.GONE);
            }


            //  Glide.with(getActivity()).load("http://ntechnosoft.com/2016/Projects/fap/images/equipment/"+userEquipmentList.get(position).getEpIcon()).into(holder.imgCustomCandid);

            return convertView;

        }


    }

    private void deleteUserEquipmentService() {

        String url = Textclass.baseurl + "delete_user_equipment.php?api_key=ZmFw&ueb_id=" + sessionList.get(sPosition).getUebId();
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
                       /* sessionList=new ArrayList<>();
                        JSONArray jsonArray = json.getJSONArray("Get_data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            EqpModel model = new EqpModel();
                            //  model.setu(jsonObject1.getString("u_id"));
                            model.setUebId(jsonObject1.getString("ueb_id"));
                            model.setEpId(jsonObject1.getString("ep_id"));
                            model.setBlockedSessionDate(jsonObject1.getString("b_date"));
                            model.setSession(jsonObject1.getString("b_session"));
                            sessionList.add(model);
                        }
                        SessionListAdapter adapter=new SessionListAdapter(getActivity());
                        listviewSession.setAdapter(adapter);*/
                        sessionList.remove(sPosition);
                        sessionListAdapter.notifyDataSetChanged();

                        //  Toast.makeText(getActivity(), getString(R.string.successfully_added), Toast.LENGTH_SHORT).show();

                    } else if (status == 0) {
                        Toast.makeText(getActivity(), getString(R.string.no_data), Toast.LENGTH_SHORT).show();

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

    public void addUserEquipment() {

        String url = Textclass.baseurl + "add_user_equipment.php?api_key=ZmFw&u_id=" + uId + "&ep_id=" + multiSelectedId + "&transaction_id=" + transactionId + "&orderid=" + orderId;
        Log.e("url", url);
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
                multiSelectedId = "";
                String str = new String(responseBody);
                try {

                    JSONObject json = new JSONObject(str);
                    int status = json.getInt("status");
                    if (status == 1) {
                        // bp.consumePurchase(sku);
                        userEquipmentList = new ArrayList<EqpModel>();
                        JSONArray jsonArray = json.getJSONArray("Get_data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            EqpModel model = new EqpModel();
                            model.setUserEquipId(jsonObject1.getString("ue_id"));
                            model.setEpId(jsonObject1.getString("ep_id"));
                            model.setTransactionId(jsonObject1.getString("transaction_id"));
                            model.setUserEpCreatedDate(jsonObject1.getString("ue_created_date"));
                            model.setEpCreatedDate(jsonObject1.getString("ep_created_date"));
                            model.setEpName(jsonObject1.getString("ep_name"));
                            model.setEpIcon(jsonObject1.getString("ep_icon"));
                            model.setUserHave(jsonObject1.getString("userhave"));
                            model.setUserBlock(jsonObject1.getString("userblock"));
                            model.setTimeOf(jsonObject1.getString("time_of"));
                            if (model.getUserHave().equalsIgnoreCase("1") && model.getUserBlock().equalsIgnoreCase("0")) {

                                multiSelectedId = multiSelectedId + "," + model.getEpId();
                                multiSelectedTransactionId = multiSelectedTransactionId + "," + model.getTransactionId();
                            }

                            if (model.getEpId().equalsIgnoreCase(mEquipmentId)) {
                                if (model.getTimeOf().equalsIgnoreCase("0")) {

                                }
                            }

                            userEquipmentList.add(model);
                        }
                        EquipmentAdapter equipmentAdapter = new EquipmentAdapter(getActivity(), userEquipmentList);
                        gridview.setAdapter(equipmentAdapter);

                    } else if (status == 0) {
                        Toast.makeText(getActivity(), getString(R.string.server_problem), Toast.LENGTH_SHORT).show();

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

    public void getDeleteSessionDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity()).setMessage("Do you really want to delete session?").setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUserEquipmentService();
                //  super.onBackPressed();
            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(true);

        alertDialog.show();
    }

    public void getTrialDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity()).setMessage("You will get two months trial period for this equipment.").setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addUserEquipment();
                //  super.onBackPressed();
            }
        }).setCancelable(true);

        alertDialog.show();
    }

}
