package com.thunderbird.chennai.fapapp.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thunderbird.chennai.fapapp.Adapter.AdapterEquipmentList;
import com.thunderbird.chennai.fapapp.Model.EqpModel;

import com.thunderbird.chennai.fapapp.Model.Equipment;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.ParseException;

import static java.lang.Integer.parseInt;

/**
 * Created by Darshan on 12/5/2016.
 */

public class EquipmentFragments extends android.support.v4.app.Fragment implements BillingProcessor.IBillingHandler, DatePickerDialog.OnDateSetListener {
    private View v;
    private GridView gridview;
    private Utility utility;
    private SharedPreferences sharedpreferences;
    private String uId;
    private SharedPreferences.Editor editor;
    private String screen;
    private ArrayList<Equipment> userEquipmentList;
    private ArrayList<String> userEquipmentNameList;
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
    private TypefaceTextView tvEndDate;
    String dateRangeList;
    private CheckBox chkMorning1;
    private CheckBox chkAfternoon1;
    private CheckBox chkEvening1;
    private CheckBox chkMorning2;
    private CheckBox chkAfternoon2;
    private CheckBox chkEvening2;
    private String sessionString1="";
    private String sessionString2="";
    private String endDate;
    private EditTextK edtEvent;
    private String mEquipmentId;
    Button btn_add_equipments;
    EditText edtSelectSpeciality;
    ArrayAdapter<String> adapterOrder;
    AdapterEquipmentList adapterEquipmentList;
    Spinner spinnerSelectEquipment;
    List<String> spinnerEquipmentList = new ArrayList<String>();
    List<String> spinnerEquipmentIdList = new ArrayList<String>();
    List<String> spinnerSpecialityList = new ArrayList<String>();
    List<String> selectedSpecialityList = new ArrayList<String>();
    CharSequence[] reader_options;
    boolean[] reader_selections;
    String selectedSpecialityId;
    String ep_id,ue_id,sp_id;
    TypefaceTextView btn_add;
    RecyclerView recyclerView;
    TextView txtMessage;
    private SessionListAdapter sessionListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_grid_equipments, null, false);
        userEquipmentList = new ArrayList<Equipment>();
        userEquipmentNameList= new ArrayList<String>();
        btn_add_equipments = (Button) v.findViewById(R.id.btnA_ddEquipments);
        recyclerView=(RecyclerView)v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        txtMessage=(TextView)v.findViewById(R.id.txtMessage);
        txtMessage.setVisibility(View.GONE);
        utility = new Utility(getActivity());
        sharedpreferences = getActivity().getSharedPreferences("myprefrence", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        Bundle b = getArguments();
        if (b != null) {
            Log.e("screen", "" + b.getString("screen"));
            screen = b.getString("screen");
        }
        if (screen.equalsIgnoreCase("profile")) {
            btn_add_equipments.setVisibility(View.VISIBLE);
            uId = sharedpreferences.getString("u_id", "");
            Log.e("check",""+screen.equalsIgnoreCase("profile"));
        } else {
            btn_add_equipments.setVisibility(View.GONE);
            uId = b.getString("u_id");
        }

        adapterEquipmentList = new AdapterEquipmentList(getActivity(), userEquipmentList);
        recyclerView.setAdapter(adapterEquipmentList);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),
                recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                //Values are passing to activity & to fragment as well
                sPosition=position;
                ep_id=userEquipmentList.get(position).getEp_id();
                sp_id=userEquipmentList.get(position).getSp_id().toString();
                ue_id=userEquipmentList.get(position).getUe_id();
                if (screen.equalsIgnoreCase("profile")) {
                    selectSession();
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bp = new BillingProcessor(getActivity(), Textclass.base64, this);

        if (utility.isNetworkAvailable()) {
            getUserEquipmentService();
        } else {
            Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();

        }
        btn_add_equipments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEquipmentsDilogs();
            }
        });


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
            }

        }
    }



    private void addEquipmentsDilogs() {
        getEquipment();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.custom_design_add_equipment, null);
        TypefaceTextView btn_add = (TypefaceTextView) subView.findViewById(R.id.btn_add);
        alertDialog = alertDialogBuilder.create();
        alertDialog.setView(subView);
        alertDialog.setCancelable(true);
        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        spinnerEquipmentList.clear();
        spinnerEquipmentList.add("Select Equipments");
        spinnerSelectEquipment = (Spinner) subView.findViewById(R.id.spinnerSelectEquipment);
        edtSelectSpeciality = (EditText) subView.findViewById(R.id.edtSelectSpeciality);
        btn_add = (TypefaceTextView) subView.findViewById(R.id.btn_add);
        adapterOrder = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item, spinnerEquipmentList);
        adapterOrder.setDropDownViewResource(R.layout.spinner_item_item);
        spinnerSelectEquipment.setAdapter(adapterOrder);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinnerSelectEquipment.getSelectedItemPosition()==0){
                    Toast.makeText(getActivity(),"Select Equipment",Toast.LENGTH_SHORT).show();
                }else if(edtSelectSpeciality.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getActivity(),"Select Speciality",Toast.LENGTH_SHORT).show();
                }else {
                    transactionId=" ";
                    orderId=" ";
                    ep_id=spinnerEquipmentIdList.get(spinnerSelectEquipment.getSelectedItemPosition()-1);
                    addUserEquipment();
                }
            }
        });

        edtSelectSpeciality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateDialog();
            }
        });
        spinnerSelectEquipment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    getSpeciality(spinnerEquipmentIdList.get(spinnerSelectEquipment.getSelectedItemPosition()-1));
                    edtSelectSpeciality.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //   tvSessionShow = (TypefaceTextView)subView. findViewById(R.id.tv_session_show);



    }

    private void getSpeciality(String ep_ids) {

        String url = Textclass.baseurl + "get_spaciality.php?api_key=ZmFw?&ep_id="+ep_ids;
        Log.e("check",""+url);
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


                String str = new String(responseBody);
                Log.e("Success", statusCode + "" + str);
                try {
                    JSONObject json = new JSONObject(str);
                    int status = json.getInt("status");
                    if (status == 1) {

                        spinnerSpecialityList.clear();
                        JSONArray jsonArray = json.getJSONArray("Get_data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            spinnerSpecialityList.add(jsonObject1.getString("sp_id") + "-" + jsonObject1.getString("sp_name"));
//                            spinnerSpecialityIdList.add(jsonObject1.getString("sp_id"));

                        }

                        reader_options = new CharSequence[spinnerSpecialityList.size()];
                        reader_selections = new boolean[reader_options.length];
                        for (int i = 0; i < spinnerSpecialityList.size(); i++) {
                            reader_options[i] = spinnerSpecialityList.get(i).substring(2,spinnerSpecialityList.get(i).length());

                        }


                    } else {
                        reader_options = new CharSequence[0];
                        reader_selections = new boolean[0];
                        Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Log.e("Exception", "" + e);
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    utility.closeProgressDialog();
                    reader_options = new CharSequence[0];
                    reader_selections = new boolean[0];
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                utility.closeProgressDialog();
                reader_options = new CharSequence[0];
                reader_selections = new boolean[0];
            }
        });
    }


    /**
     * book_event
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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.book_equipment_session_dialog, null);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setView(subView);
        alertDialog.setCancelable(true);
        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //   spSession = (Spinner)subView. findViewById(R.id.sp_session);
        //   tvSessionShow = (TypefaceTextView)subView. findViewById(R.id.tv_session_show);
        tvSelectDate = (TypefaceTextView) subView.findViewById(R.id.tv_select_date);
        tvEndDate = (TypefaceTextView) subView.findViewById(R.id.tv_end_date);
        btnDone = (TypefaceTextView) subView.findViewById(R.id.btn_done);
        //   relSession=(RelativeLayout)subView.findViewById(R.id.rel_session);
        chkMorning1 = (CheckBox) subView.findViewById(R.id.chk_morning1);
        chkAfternoon1 = (CheckBox) subView.findViewById(R.id.chk_afternoon1);
        chkEvening1 = (CheckBox) subView.findViewById(R.id.chk_evening1);
        chkMorning2 = (CheckBox) subView.findViewById(R.id.chk_morning2);
        chkAfternoon2 = (CheckBox) subView.findViewById(R.id.chk_afternoon2);
        chkEvening2 = (CheckBox) subView.findViewById(R.id.chk_evening2);
        edtEvent = (EditTextK) subView.findViewById(R.id.edt_event);
        tvSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpd.show(getActivity().getFragmentManager(), "Start date1");
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tvSelectDate.getText().equals("Date1")){
                    Toast.makeText(getActivity(),"Select Date1",Toast.LENGTH_SHORT).show();
                }else if(!chkMorning1.isChecked() && !chkMorning2.isChecked() && !chkAfternoon1.isChecked() && !chkAfternoon2.isChecked() && !chkEvening1.isChecked() && !chkEvening2.isChecked()){
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
                    sessionString2 = "";
                    if (chkMorning2.isChecked()) {
                        sessionString2 = sessionString2 + "Morning" + ",";
                    }

                    if (chkAfternoon2.isChecked()) {
                        sessionString2 = sessionString2 + "Afternoon" + ",";
                    }

                    if (chkEvening2.isChecked()) {
                        sessionString2 = sessionString2 + "Evening" + ",";
                    }

                    if (sessionString2.endsWith(",")) {
                        sessionString2 = sessionString2.substring(0, sessionString2.length() - 1);
                    }

                    bookSessionService(tvSelectDate.getText().toString(),tvEndDate.getText().toString(),sessionString1,sessionString2,edtEvent.getText().toString(),alertDialog);
                }
            }
        });

    }


        //getequipment
        public void getUserEquipmentService() {
        multiSelectedId = " ";
        multiSelectedTransactionId = " ";

        String url = Textclass.baseurl + "get_new_user_equipment.php?api_key=ZmFw&u_id=" + uId;
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

                String str = new String(responseBody);
                Log.d("Success", statusCode + "" + str);
                try {
                    userEquipmentList.clear();
                    userEquipmentNameList.clear();
                    JSONObject jsonObject = new JSONObject(str);
                    Integer success = jsonObject.getInt("success");
                    Log.e("lenght", "" + jsonObject.length());
                    if (success == 0) {
                        txtMessage.setVisibility(View.VISIBLE);
                    } else {

                        txtMessage.setVisibility(View.GONE);
                        for (int i = 1; i <= success; i++) {
                            Log.e("i", "" + i);
                            JSONObject obj = jsonObject.getJSONObject(String.valueOf(i));
                            Log.e("check",obj.getString("ep_name").toString());
                            Equipment equipment=new Equipment();
                            equipment.setEp_id(obj.getString("ep_id").toString());

                            if(userEquipmentNameList.contains(obj.getString("ep_name").toString())){
                                int occurrences = Collections.frequency(userEquipmentNameList, obj.getString("ep_name").toString());
                                Log.e("occurrences",""+occurrences);
                                if(occurrences==1){
                                    Log.e("check",""+occurrences);
                                    equipment.setEp_name(obj.getString("ep_name").toString()+" No."+occurrences);
                                }else {
                                    Log.e("check",""+occurrences+1);
                                    equipment.setEp_name(obj.getString("ep_name").toString()+" No."+occurrences);
                                }

                            }else {
                                Log.e("check","none");
                                equipment.setEp_name(obj.getString("ep_name").toString());
                            }
                            userEquipmentNameList.add(obj.getString("ep_name").toString());

                            equipment.setUe_id(obj.getString("ue_id").toString());
                            if(obj.getString("sp_ids").toString().equalsIgnoreCase("") || obj.getString("sp_ids")==null){
                                equipment.setSp_id("");
                            }else {
                                equipment.setSp_id(obj.getString("sp_ids").toString());
                            }

                            userEquipmentList.add(equipment);
                            adapterEquipmentList.notifyDataSetChanged();
                        }
                    }

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Log.e("expection",""+e);
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

            tvSelectDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            Date today = new Date();
            SimpleDateFormat formattedDate = new SimpleDateFormat("dd-MM-yyyy");
            Calendar c = Calendar.getInstance();
            c.set(year,(monthOfYear),dayOfMonth);
            c.add(Calendar.DAY_OF_MONTH, 1);
            // number of days to add
            String tomorrow = (String)(formattedDate.format(c.getTime()));
            tvEndDate.setText(tomorrow);
            System.out.println("Tomorrows date is " + tomorrow);


    }


    private void bookSessionService(String date1, String date2, String sessionString1, String sessionString2, String details, final AlertDialog alertDialog) {
        String url = Textclass.baseurl + "add_equipment_book.php";

        RequestParams requestParams = new RequestParams();
        requestParams.add("api_key", "ZmFw");
        requestParams.add("u_id", uId);
        requestParams.add("ep_id",ep_id);
        requestParams.add("ue_id", ue_id);
        if(sessionString1.equalsIgnoreCase("") || sessionString1.toString().isEmpty()){
            requestParams.add("b_date1","null");
            requestParams.add("b_session1", "null");
        }else {
            requestParams.add("b_date1",parseDate(date1) );
            requestParams.add("b_session1", sessionString1);
        }

        if(sessionString2.equalsIgnoreCase("") || sessionString2.toString().isEmpty()){
            requestParams.add("b_date2","null");
            requestParams.add("b_session2", "null");
        }else {
            requestParams.add("b_date2",parseDate(date2) );
            requestParams.add("b_session2", sessionString2);
        }


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
                Log.d("Success", statusCode + "" + responseBody);

            alertDialog.dismiss();
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



    private void DeleteBookEquipment() {

        sessionList = new ArrayList<EqpModel>();
        if(sp_id.equalsIgnoreCase("")){
         sp_id="null";
        }
        String url = Textclass.baseurl + "delete_equipment.php?api_key=ZmFw&ue_id=" + ue_id +"&sp_id=" + sp_id +"&u_id=" + uId +"";
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

                        userEquipmentList.remove(sPosition);
                        adapterEquipmentList.notifyDataSetChanged();
                          Toast.makeText(getActivity(), "Delete Successfully", Toast.LENGTH_SHORT).show();
//                        getUserEquipmentService();


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
    public void addUserEquipment() {
        transactionId="null";
        orderId ="null";
        String url = Textclass.baseurl + "add_user_equipment.php?api_key=ZmFw&u_id=" + uId + "&ep_id=" + ep_id + "&sp_id=" + selectedSpecialityId + "&transaction_id=" + transactionId + "&orderid=" + orderId;
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

                        if (utility.isNetworkAvailable()) {
                            getUserEquipmentService();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();

                        }
                    } else if (status == 0) {

                        Toast.makeText(getActivity(), getString(R.string.server_problem), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                    alertDialog.dismiss();
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
                alertDialog.dismiss();
                utility.closeProgressDialog();
            }
        });
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


    public void getEquipment() {

        String url = Textclass.baseurl + "get_equipment.php?api_key=ZmFw&u_id=" + uId;
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
                        spinnerEquipmentIdList.clear();
                        spinnerEquipmentIdList.clear();
                        JSONArray jsonArray = json.getJSONArray("Get_data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            spinnerEquipmentList.add(jsonObject1.getString("ep_name"));
                            spinnerEquipmentIdList.add(jsonObject1.getString("ep_id"));

                        }
                        spinnerSelectEquipment.setAdapter(adapterOrder);

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


    public void onCreateDialog() {


        // list_des.clear();

        new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
                .setTitle("Select Speciality")

                .setMultiChoiceItems(
                        reader_options,
                        reader_selections,
                        new DialogSelectionClickHandler(reader_options,
                                reader_selections))
                .setPositiveButton("OK", new DialogButtonClickHandler())
                .create()
                .show();


    }

        public class DialogSelectionClickHandler implements
            DialogInterface.OnMultiChoiceClickListener {

        public DialogSelectionClickHandler(CharSequence[] reader_options,
                                           boolean[] reader_selections) {
            // TODO Auto-generated constructor stub
        }

        public void onClick(DialogInterface dialog, int clicked,
                            boolean selected) {
            // Log.e("ME", shift_options[clicked] + " selected: " +
            // selected);

        }

    }


        public class DialogButtonClickHandler implements
            DialogInterface.OnClickListener {


        // ViewHolderItem holder1;

        public DialogButtonClickHandler() {
            // TODO Auto-generated constructor stub


            // holder1 = (ViewHolderItem) v.getTag();
        }

        public void onClick(DialogInterface dialog, int clicked) {
            switch (clicked) {
                case DialogInterface.BUTTON_POSITIVE:
                    //List<String> list_des = new ArrayList<String>();
                    String str = "";
                    selectedSpecialityId = "";
                    selectedSpecialityId = "";
                    selectedSpecialityList.clear();

                    for (int i = 0; i < reader_options.length; i++) {


                        if (reader_selections[i] == true) {

                            String abc = reader_options[i].toString(); //
                            selectedSpecialityList.add("" + i);
                            str = str + "\n" + abc;

                        }

                    }

                    if (str.equalsIgnoreCase("")) { //
                        edtSelectSpeciality.setText("");
                    } else {

                    }

                    Log.e("section_rules1", "" + str);

                    edtSelectSpeciality.setText(str);
                    for (int j = 0; j < selectedSpecialityList.size(); j++) {
                        int pos = parseInt(selectedSpecialityList.get(j));
                        String str1 = spinnerSpecialityList.get(pos);
                        Log.e("searated", "" + str1.toString());
                        String[] separated = str1.split("-");
                        Log.e("searated", "" + separated[0]);
                        selectedSpecialityId += separated[0].toString() + ",";
                    }
                    if (selectedSpecialityList.size() != 0) {
                        selectedSpecialityId = selectedSpecialityId.substring(0, selectedSpecialityId.length() - 1);
                    }
//                    selectedSpecialityId = selectedSpecialityId.replace(selectedSpecialityId.substring(selectedSpecialityId.length()-1), "");
                    Log.e("selectedSpeciality", "" + str);
                    Log.e("selectedSpecialityId", "" + selectedSpecialityId);
                    break;

            }
        }
    }

    public void selectSession() {
        final android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.dialog_contact_me, null);
        RelativeLayout relBookSession = (RelativeLayout) subView.findViewById(R.id.rel_email);
        RelativeLayout relDeleteBookSession = (RelativeLayout) subView.findViewById(R.id.rel_phone);
        RelativeLayout relViewSession = (RelativeLayout) subView.findViewById(R.id.rel_address);
        relViewSession.setVisibility(View.VISIBLE);

        TypefaceTextView tvDisplayEmail = (TypefaceTextView) subView.findViewById(R.id.tv_display_email);
        TypefaceTextView tvDisplayPhone = (TypefaceTextView) subView.findViewById(R.id.tv_display_phone);
        TypefaceTextView tvEmail = (TypefaceTextView) subView.findViewById(R.id.tv_email);
        TypefaceTextView tvPhone = (TypefaceTextView) subView.findViewById(R.id.tv_phone);
        TypefaceTextView tvAddress = (TypefaceTextView) subView.findViewById(R.id.tv_address);
        tvEmail.setText("Book session");
        tvPhone.setText("Delete equipment");
        tvAddress.setText("View session");
        relViewSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                sessionListDialog();
            }
        });
        tvDisplayEmail.setVisibility(View.INVISIBLE);
        tvDisplayPhone.setVisibility(View.INVISIBLE);
        relBookSession.setOnClickListener(

                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                sessionBookDialog();
            }
        });

        relDeleteBookSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.support.v7.app.AlertDialog.Builder alertDialog1 = new android.support.v7.app.AlertDialog.Builder(getActivity()).setMessage("Are you sure delete all equipment and speciality?").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        DeleteBookEquipment();
                        alertDialog.dismiss();

                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(true);

                alertDialog1.show();



            }

        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setView(subView);

        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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

    private void getBookedSessions() {

        sessionList = new ArrayList<EqpModel>();

        String url = Textclass.baseurl + "get_user_equipment_block.php?api_key=ZmFw&u_id=" + uId + "&ep_id=" + ep_id;
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

    public static interface ClickListener{
        public void onClick(View view,int position);
        public void onLongClick(View view,int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){

            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public class SessionListAdapter extends BaseAdapter {


        private final Context context;

        private int col;
        private SessionListAdapter.ViewHolder holder;

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

                holder = new SessionListAdapter.ViewHolder();
                mInflater = (LayoutInflater) context
                        .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.custom_list_session, null);
                holder.tvSession = (TypefaceTextView) convertView.findViewById(R.id.tv_session);
                holder.imgDeleteSession = (ImageView) convertView.findViewById(R.id.img_delete_session);
                holder.tvSessionDate = (TypefaceTextView) convertView.findViewById(R.id.tv_session_date);
                holder.tvEvent = (TypefaceTextView) convertView.findViewById(R.id.tv_event);
                convertView.setTag(holder);

            } else {
                holder = (SessionListAdapter.ViewHolder) convertView.getTag();


            }
            holder.tvSession.setText(sessionList.get(position).getSession());
            holder.tvSessionDate.setText(utility.convertDateTime1(sessionList.get(position).getBlockedSessionDate()));
            holder.tvEvent.setText(sessionList.get(position).getEventDetail());

//            if (screen.equalsIgnoreCase("profile")) {
//            } else {
//                holder.imgDeleteSession.setVisibility(View.GONE);
//            }


            //  Glide.with(getActivity()).load("http://ntechnosoft.com/2016/Projects/fap/images/equipment/"+userEquipmentList.get(position).getEpIcon()).into(holder.imgCustomCandid);

            return convertView;

        }


    }

}
