package com.thunderbird.chennai.fapapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.thunderbird.chennai.fapapp.Model.Model;
import com.thunderbird.chennai.fapapp.R;
import com.thunderbird.chennai.fapapp.Utility.Textclass;
import com.thunderbird.chennai.fapapp.Utility.TypefaceTextView;
import com.thunderbird.chennai.fapapp.Utility.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;


public class RegistrationPayment extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private TypefaceTextView btnCheckout;
    private TypefaceTextView tvBack;
    private String fName;
    private String lName;
    private String email;
    private String password;
    private String loginType;
    private String imgPath;
    private String phone;
    private String companyName;
    private String address;
    private String state;
    private String area;
    private String latitude;
    private String longitude;
    private String country;
    private Utility utility;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private String city;
    private BillingProcessor bp;
    private ImageView imgPlan;
    private String transactionId,orderId;
    private boolean exists;
    //  public static Activity RegistrationPaymentX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_payment);

        utility = new Utility(this);
        transactionId = "";
        sharedpreferences = getApplicationContext().getSharedPreferences("myprefrence", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        bp = new BillingProcessor(this, Textclass.base64, this);

        //     RegistrationPaymentX=this;
        tvBack = (TypefaceTextView) findViewById(R.id.btn_back);
        btnCheckout = (TypefaceTextView) findViewById(R.id.btn_checkout);

        imgPlan = (ImageView) findViewById(R.id.img_plan);

        fName = getIntent().getStringExtra("first_name");
        lName = getIntent().getStringExtra("last_name");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        loginType = getIntent().getStringExtra("login_type");
        imgPath = getIntent().getStringExtra("img_path");
        phone = getIntent().getStringExtra("phone");
        companyName = getIntent().getStringExtra("company_name");
        address = getIntent().getStringExtra("address");
        state = getIntent().getStringExtra("state");
        city = getIntent().getStringExtra("city");
        area = getIntent().getStringExtra("area");
        country = getIntent().getStringExtra("country");
        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");

        //   compressImage();

        imgPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (utility.isNetworkAvailable()) {

                //    bp.subscribe(RegistrationPayment.this, "android.test.purchased");
                  //      if(!bp.isSubscribed("register_sid")) {
                            bp.subscribe(RegistrationPayment.this, "register_sid");

                  //      }else{
                  //          Toast.makeText(RegistrationPayment.this, "You are already", Toast.LENGTH_SHORT).show();
                  //      }

                    //  registerService();
                } else {
                    Toast.makeText(RegistrationPayment.this, getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (utility.isNetworkAvailable()) {

                   // registerService();
                  //  transactionId="test";
                    if (!transactionId.equalsIgnoreCase("") && !exists) {
                        registerService();
                    } else {
                        Toast.makeText(RegistrationPayment.this, getString(R.string.register_purchase), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(RegistrationPayment.this, getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
                    transactionId = jo.getString("purchaseToken");
                    if(jo.has("orderId")) {
                        orderId =jo.getString("orderId");
                    }
                    else {
                        orderId="";
                    }
                    //  transactionId=jo.getString("purchaseToken");

                    //  alertDialog.dismiss();
                  //  bp.consumePurchase(sku);
                    //  addSubscribedUser();
                    //  loadFragment();
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void registerService() {
        //  String url = Textclass.baseurl + "get_user_filter.php?api_key=ZmFw&u_id="+uId+"&u_state="+tvStateShow.getText().toString().trim()+"&u_city="+tvCityShow.getText().toString().trim()+"&u_area="+tvAreaShow.getText().toString().trim()+"&b_date="+utility.convertDateTime(tvDate.getText().toString().trim())+"&b_session="+tvSessionShow.getText().toString().trim()+"&sp_id="+speciality+"&ep_id=";
        String url = Textclass.baseurl + "insert_register_data.php";

        RequestParams requestParams = new RequestParams();
        requestParams.add("api_key", "ZmFw");
        requestParams.add("fname", fName);
        requestParams.add("lname", lName);
        requestParams.add("u_email", email);
        requestParams.add("u_phone",phone);
        requestParams.add("u_password", password);
        requestParams.add("u_company_name", companyName);
        requestParams.add("u_address", address);
        requestParams.add("u_state", state);
        requestParams.add("u_city", city);
        requestParams.add("u_area", area);
        requestParams.add("login_type", loginType);
        requestParams.add("u_country", country);
        requestParams.add("u_transaction_id", transactionId);
        requestParams.add("orderid",""+orderId);
        requestParams.add("u_lat", latitude);
        requestParams.add("u_long", longitude);

        File imageFile = null;

        if (imgPath == null) {
            imgPath = "";
        } else {
            imageFile = new File(imgPath);
        }

        if (imageFile != null && imageFile.exists()) {
            try {
                requestParams.put("profile_pic", imageFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            requestParams.put("profile_pic", "");
        }


        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(RegistrationPayment.this, url, requestParams, new AsyncHttpResponseHandler() {
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
                        transactionId = "";
                        exists=true;
                        JSONArray jsonArray = json.getJSONArray("Get_data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            Model model = new Model();
                            model.setuId(jsonObject1.getString("u_id"));
                            model.setFirstName(jsonObject1.getString("fname"));
                            model.setLastName(jsonObject1.getString("lname"));
                            model.setPhone(jsonObject1.getString("u_phone"));
                            model.setProfilePic(jsonObject1.getString("profile_pic"));
                            model.setState(jsonObject1.getString("u_state"));
                            model.setCity(jsonObject1.getString("u_city"));
                            model.setPassword(jsonObject1.getString("u_password1"));
                            model.setEmail(jsonObject1.getString("u_email"));
                            model.setCity(jsonObject1.getString("u_city"));
                            model.setPassword(jsonObject1.getString("u_password1"));
                            model.setArea(jsonObject1.getString("u_area"));
                            model.setAddress(jsonObject1.getString("u_address"));
                            model.setCountry(jsonObject1.getString("u_country"));
                            model.setCompanyName(jsonObject1.getString("u_company_name"));
                            model.setLati(jsonObject1.getString("u_lat"));
                            model.setLongi(jsonObject1.getString("u_long"));


                            editor.putString("u_id", model.getuId());
                            editor.putString("f_name", model.getFirstName());
                            editor.putString("l_name", model.getLastName());
                            editor.putString("profile_pic", model.getProfilePic());
                            editor.putString("state", model.getState());
                            editor.putString("city", model.getCity());
                            editor.putString("phone", model.getPhone());
                            editor.putString("password", model.getPassword());
                            editor.putString("email", model.getEmail());
                            editor.putString("area", model.getArea());
                            editor.putString("address", model.getAddress());
                            editor.putString("country", model.getCountry());
                            editor.putString("company_name",model.getCompanyName());
                            editor.putString("lati", model.getLati());
                            editor.putString("longi",model.getLongi());
                            editor.commit();
                        }

                        Intent intent = new Intent(RegistrationPayment.this, RegSelectEquip.class);
                        startActivity(intent);
                        finish();
                        RegistationOne.RegisterOneX.finish();
                        RegistrationTwo.RegistrationTwoX.finish();
                        LoginActivity.LoginX.finish();


                    } else if (status == 0) {
                        Toast.makeText(RegistrationPayment.this, getString(R.string.server_problem), Toast.LENGTH_SHORT).show();
                    } else if(status==10){
                     //   Toast.makeText(RegistrationPayment.this, getString(R.string.email_already_exists), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(RegistrationPayment.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Toast.makeText(RegistrationPayment.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    utility.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(RegistrationPayment.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                utility.closeProgressDialog();
            }
        });
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
      //  bp.consumePurchase(productId);
        //  bp.purchase(RegistrationPayment.this, productId);

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
}


