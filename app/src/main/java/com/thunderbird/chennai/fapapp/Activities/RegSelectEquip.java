package com.thunderbird.chennai.fapapp.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bumptech.glide.Glide;
import com.thunderbird.chennai.fapapp.Model.EqpModel;
import com.thunderbird.chennai.fapapp.Model.SpModel;
import com.thunderbird.chennai.fapapp.R;
import com.thunderbird.chennai.fapapp.Utility.Textclass;
import com.thunderbird.chennai.fapapp.Utility.TypefaceTextView;
import com.thunderbird.chennai.fapapp.Utility.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class RegSelectEquip extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private TypefaceTextView btnFinish;
  //  private TypefaceTextView btnSkip;
    private TypefaceTextView tvBack;
    private Utility utility;
    private ArrayList<SpModel> specialityList;
    private LinearLayout topLinearLayoutSp;
    private LinearLayout topLinearLayoutEquip;
    private HorizontalScrollView hScrollSp;
    private HorizontalScrollView hScrollEquip;
    private ArrayList<EqpModel> equipmentList;
    private BillingProcessor bp;
    private int equipPosition;
    private String transactionId;
    public RelativeLayout relImageCandid;
    public TypefaceTextView tvName;
    public ImageView imgCandid;
    private TypefaceTextView tvNameRef;
    private RelativeLayout tvRelImageCandid1Ref;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private String uId;
    private String speciality;
    private String equipment;
    private String transactionIds;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_select_equip);
        bp = new BillingProcessor(this, Textclass.base64, this);
        utility = new Utility(this);

        sharedpreferences = getSharedPreferences("myprefrence", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        uId = sharedpreferences.getString("u_id", "");

        hScrollSp = (HorizontalScrollView) findViewById(R.id.h_scroll_sp);
        hScrollEquip = (HorizontalScrollView) findViewById(R.id.h_scroll_equip);
        topLinearLayoutSp = new LinearLayout(this);
        topLinearLayoutSp.setBackgroundColor(Color.parseColor("#E9EDF2"));
        topLinearLayoutSp.setOrientation(LinearLayout.HORIZONTAL);

        topLinearLayoutEquip = new LinearLayout(this);
        topLinearLayoutEquip.setBackgroundColor(Color.parseColor("#E9EDF2"));
        topLinearLayoutEquip.setOrientation(LinearLayout.HORIZONTAL);


        btnFinish = (TypefaceTextView) findViewById(R.id.btn_finish);
      //  btnSkip = (TypefaceTextView) findViewById(R.id.btn_skip);
        tvBack = (TypefaceTextView) findViewById(R.id.btn_back);

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        if (utility.isNetworkAvailable()) {
            getSpecialityService();
        } else {
            Toast.makeText(RegSelectEquip.this, getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
        }


        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                speciality = "";
                equipment = "";
                transactionIds = "";

                if (specialityList != null) {
                    for (int i = 0; i < specialityList.size(); i++) {
                        if (specialityList.get(i).getUserhave().equalsIgnoreCase("1")) {
                            if (i == (specialityList.size() - 1)) {
                                speciality = speciality + specialityList.get(i).getSpId();
                            } else {
                                speciality = speciality + specialityList.get(i).getSpId() + ",";
                            }
                        }
                    }
                }

                if (equipmentList != null) {
                    for (int i = 0; i < equipmentList.size(); i++) {
                        if (!equipmentList.get(i).getTransactionId().equalsIgnoreCase("")) {
                            if (i == (equipmentList.size() - 1)) {
                                equipment = equipment + equipmentList.get(i).getEpId();
                                transactionIds = transactionIds + equipmentList.get(i).getTransactionId();
                            } else {
                                equipment = equipment + equipmentList.get(i).getEpId() + ",";
                                transactionIds = transactionIds + equipmentList.get(i).getTransactionId() + ",";
                            }
                        }
                    }
                }

                if (utility.isNetworkAvailable()) {
                    if(speciality.equalsIgnoreCase("")){
                    //    Toast.makeText(RegSelectEquip.this, "Select speciality and equipment", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegSelectEquip.this, PhotographerListActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        addUserSpeciality();
                    }

                } else {
                    Toast.makeText(RegSelectEquip.this, getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                }


            }
        });

        /*btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegSelectEquip.this, PhotographerListActivity.class);
                startActivity(intent);
                finish();
            }
        });*/


    }


    private void getSpecialityService() {

        specialityList = new ArrayList<SpModel>();

        String url = Textclass.baseurl + "get_spaciality.php?api_key=ZmFw";
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(RegSelectEquip.this, url, new AsyncHttpResponseHandler() {


            public RelativeLayout relImageCandid;
            public TypefaceTextView tvName;
            public ImageView imgCandid;

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
                            SpModel model = new SpModel();
                            model.setSpId(jsonObject1.getString("sp_id"));
                            model.setSpName(jsonObject1.getString("sp_name"));
                            model.setIcon(jsonObject1.getString("sp_icon"));
                            model.setSpStatus(jsonObject1.getString("sp_status"));
                            model.setSpCreatedDate(jsonObject1.getString("sp_created_date"));
                            model.setUserhave("0");
                            specialityList.add(model);
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            View child = getLayoutInflater().inflate(R.layout.gallary_custom, null);
                            imgCandid = (ImageView) child.findViewById(R.id.img_custom_candid);
                            tvName = (TypefaceTextView) child.findViewById(R.id.tv_name);
                            relImageCandid = (RelativeLayout) child.findViewById(R.id.rel_img_candid);
                            child.setTag(i);

                           /* if (specialityList.get(i).getUserhave().equalsIgnoreCase("1")) {
                                relImageCandid.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                tvName.setTextColor(getResources().getColor(R.color.colorAccent));

                            } else {
                                relImageCandid.setBackgroundColor(getResources().getColor(R.color.lightgrey));
                                tvName.setTextColor(getResources().getColor(R.color.lightgrey));
                            }*/

                            Glide.with(RegSelectEquip.this).load(Textclass.speciality + specialityList.get(i).getIcon()).into(imgCandid);
                            tvName.setText(specialityList.get(i).getSpName());
                            final int finalI = i;
                            child.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int j = Integer.parseInt(view.getTag().toString());
                                    View v = topLinearLayoutSp.getChildAt(j);
                                    TypefaceTextView tvName1 = (TypefaceTextView) v.findViewById(R.id.tv_name);
                                    RelativeLayout relImageCandid1 = (RelativeLayout) v.findViewById(R.id.rel_img_candid);
                                    if (specialityList.get(j).getUserhave().equalsIgnoreCase("1")) {
                                        specialityList.get(j).setUserhave("0");
                                        relImageCandid1.setBackgroundColor(getResources().getColor(R.color.lightgrey));
                                        tvName1.setTextColor(getResources().getColor(android.R.color.black));
                                    } else {
                                        specialityList.get(j).setUserhave("1");
                                        relImageCandid1.setBackgroundColor(getResources().getColor(android.R.color.black));
                                        tvName1.setTextColor(getResources().getColor(R.color.colorAccent));
                                    }
                                }
                            });
                            topLinearLayoutSp.addView(child);
                        }
                        hScrollSp.addView(topLinearLayoutSp);
                        getEquipmentService();

                    } else if (status == 0) {
                        Toast.makeText(RegSelectEquip.this, getString(R.string.no_data), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(RegSelectEquip.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();


                    }

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Toast.makeText(RegSelectEquip.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    utility.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(RegSelectEquip.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                utility.closeProgressDialog();
            }
        });

    }

    private void getEquipmentService() {

        equipmentList = new ArrayList<EqpModel>();

        String url = Textclass.baseurl + "get_equipment.php?api_key=ZmFw";
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(RegSelectEquip.this, url, new AsyncHttpResponseHandler() {


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
                            model.setEpId(jsonObject1.getString("ep_id"));
                            model.setEpName(jsonObject1.getString("ep_name"));
                            model.setEpIcon(jsonObject1.getString("ep_icon"));
                            model.setEpStatus(jsonObject1.getString("ep_status"));
                            model.setEpCreatedDate(jsonObject1.getString("ep_created_date"));
                            model.setTransactionId("");
                            equipmentList.add(model);
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            View child = getLayoutInflater().inflate(R.layout.gallary_custom, null);
                            imgCandid = (ImageView) child.findViewById(R.id.img_custom_candid);
                            tvName = (TypefaceTextView) child.findViewById(R.id.tv_name);
                            relImageCandid = (RelativeLayout) child.findViewById(R.id.rel_img_candid);
                            child.setTag(i);

                            Glide.with(RegSelectEquip.this).load(Textclass.equipment + equipmentList.get(i).getEpIcon()).into(imgCandid);
                            tvName.setText(equipmentList.get(i).getEpName());

                            final int finalI = i;
                            child.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int j = Integer.parseInt(view.getTag().toString());
                                    View v = topLinearLayoutEquip.getChildAt(j);
                                    TypefaceTextView tvName1 = (TypefaceTextView) v.findViewById(R.id.tv_name);
                                    RelativeLayout relImageCandid1 = (RelativeLayout) v.findViewById(R.id.rel_img_candid);

                                    equipPosition = j;
                                    tvNameRef = tvName1;
                                    tvRelImageCandid1Ref = relImageCandid1;
                                    if (equipmentList.get(j).getTransactionId().equalsIgnoreCase("")) {

                                        if (utility.isNetworkAvailable()) {
                                          //  bp.purchase(RegSelectEquip.this, "android.test.purchased");

                                            //for paid equipment
                                         //   bp.purchase(RegSelectEquip.this,"equipment_pid");

                                            //free equipment code(temporary)
                                            equipmentList.get(equipPosition).setTransactionId("free");
                                            equipment=equipmentList.get(equipPosition).getEpId();
                                            orderId="";
                                            getTrialDialog();
                                           // addUserEquipment();


                                        } else {
                                            Toast.makeText(RegSelectEquip.this, getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });

                            topLinearLayoutEquip.addView(child);
                        }

                        hScrollEquip.addView(topLinearLayoutEquip);


                    } else if (status == 0) {
                        Toast.makeText(RegSelectEquip.this, getString(R.string.no_data), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(RegSelectEquip.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    }

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Toast.makeText(RegSelectEquip.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    utility.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(RegSelectEquip.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                utility.closeProgressDialog();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 32459) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {

                    JSONObject jo = new JSONObject(purchaseData);
                    Log.e("json:", jo.toString());
                    String sku = jo.getString("productId");
                    //   transactionId=jo.getString("orderId");
                    transactionId = jo.getString("purchaseToken");
                    equipmentList.get(equipPosition).setTransactionId(transactionId);
                    if(jo.has("orderId")) {
                        orderId =jo.getString("orderId");
                    }
                    else {
                        orderId="";
                    }

                    tvRelImageCandid1Ref.setBackgroundColor(getResources().getColor(android.R.color.black));
                    tvNameRef.setTextColor(getResources().getColor(R.color.colorAccent));
                    //  alertDialog.dismiss();
                  //  bp.consumePurchase(sku);

                 //   addUserEquipment();
                    //  addSubscribedUser();
                    //  loadFragment();
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }
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

    public void addUserSpeciality() {


        String url = Textclass.baseurl + "add_user_spaciality.php?api_key=ZmFw&u_id=" + uId + "&sp_id=" + speciality;
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(this, url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                utility.showProgressDialog();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.d("Success", statusCode + "" + responseBody);
                speciality = "";
                String str = new String(responseBody);
                try {

                    JSONObject json = new JSONObject(str);
                    int status = json.getInt("status");
                    if (status == 1) {
                      //  addUserEquipment();

                        Intent intent = new Intent(RegSelectEquip.this, PhotographerListActivity.class);
                        startActivity(intent);
                        finish();

                    } else if (status == 0) {
                        Toast.makeText(RegSelectEquip.this, getString(R.string.server_problem), Toast.LENGTH_SHORT).show();
                        utility.closeProgressDialog();
                    } else {
                        Toast.makeText(RegSelectEquip.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        utility.closeProgressDialog();
                    }


                } catch (Exception e) {
                    Toast.makeText(RegSelectEquip.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    utility.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(RegSelectEquip.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                utility.closeProgressDialog();
            }
        });
    }


    public void addUserEquipment() {

        String url = Textclass.baseurl + "add_user_equipment.php?api_key=ZmFw&u_id=" + uId + "&ep_id=" + equipment + "&transaction_id=free&orderid=free";
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(this, url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                //   utility.showProgressDialog();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.d("Success", statusCode + "" + responseBody);
                equipment = "";
                String str = new String(responseBody);
                try {

                    JSONObject json = new JSONObject(str);
                    int status = json.getInt("status");
                    if (status == 1) {
                        tvRelImageCandid1Ref.setBackgroundColor(getResources().getColor(android.R.color.black));
                        tvNameRef.setTextColor(getResources().getColor(R.color.colorAccent));
                        /*Intent intent = new Intent(RegSelectEquip.this, PhotographerListActivity.class);
                        startActivity(intent);
                        finish();*/

                    } else if (status == 0) {
                        Toast.makeText(RegSelectEquip.this, getString(R.string.server_problem), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(RegSelectEquip.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Toast.makeText(RegSelectEquip.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    utility.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(RegSelectEquip.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                utility.closeProgressDialog();
            }
        });
    }

    @Override
    public void onBackPressed() {

        getExitDialog();
        //super.onBackPressed();
    }

    public void getExitDialog(){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this).setMessage("Press ok to exit the app.").setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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

    public void getTrialDialog(){
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(this).setMessage("You will get two months trial period for this equipment.").setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addUserEquipment();
                //  super.onBackPressed();
            }
        }).setCancelable(true);

        alertDialog.show();
    }

}
