package com.thunderbird.chennai.fapapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thunderbird.chennai.fapapp.Model.AreaModel;
import com.thunderbird.chennai.fapapp.Model.Model;
import com.thunderbird.chennai.fapapp.R;
import com.thunderbird.chennai.fapapp.Utility.CSVFile;
import com.thunderbird.chennai.fapapp.Utility.EditTextK;
import com.thunderbird.chennai.fapapp.Utility.Textclass;
import com.thunderbird.chennai.fapapp.Utility.TypefaceTextView;
import com.thunderbird.chennai.fapapp.Utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class RegistrationTwo extends AppCompatActivity {

    private TypefaceTextView btnNext;
    public static Activity RegistrationTwoX;
    private TypefaceTextView tvBack;
    private Spinner spState;
    private Spinner spArea;
    private TypefaceTextView edtState;
    private TypefaceTextView edtArea;
    private TypefaceTextView tvHello;
    private EditTextK edtPhone;
    private EditTextK edtCompanyName;
    private EditTextK edtAddress;
    private TypefaceTextView edtCountry;
    private String firstName;
    private String email;
    private String lastName;
    private String password;
    private String confirmPassword;
    private String imagePath;
    private String loginType;
    private TypefaceTextView edtCity;
    private ArrayList<String> stateList;
    private Spinner spCity;
    private ArrayList<String> citiesList;
    private Spinner spCountry;
    private ArrayList<String> countryList;
    private ArrayList<String> cityList;
    private ArrayList<AreaModel> areaList;
    private int areaPosition;
    private ArrayList<String> areaNameList;
    private ArrayList<String> latitudeList;
    private ArrayList<String> longitudeList;
    private Utility utility;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private List<String[]> areaList1;
    private List<String[]> cityList2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_two);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        sharedpreferences = getApplicationContext().getSharedPreferences("myprefrence", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        utility=new Utility(this);
        RegistrationTwoX=this;
        tvHello=(TypefaceTextView)findViewById(R.id.tv_hello);
        edtPhone=(EditTextK)findViewById(R.id.edt_phone_no);
        btnNext=(TypefaceTextView)findViewById(R.id.btn_next);
        tvBack=(TypefaceTextView)findViewById(R.id.btn_back);
        spState=(Spinner)findViewById(R.id.sp_state);
        spCity=(Spinner)findViewById(R.id.sp_city);
        spArea=(Spinner)findViewById(R.id.sp_area);
        spCountry=(Spinner)findViewById(R.id.sp_country);
        edtState=(TypefaceTextView)findViewById(R.id.edt_state);
        edtCity=(TypefaceTextView)findViewById(R.id.edt_city);
        edtArea=(TypefaceTextView)findViewById(R.id.edt_area);
        edtCompanyName=(EditTextK)findViewById(R.id.edt_company_name);
        edtAddress=(EditTextK)findViewById(R.id.edt_address);
        edtCountry=(TypefaceTextView)findViewById(R.id.edt_country);
       // edtCity=(TypefaceTextView)findViewById(R.id.edt_city);


        firstName=getIntent().getStringExtra("first_name");
        lastName=getIntent().getStringExtra("last_name");
        email=getIntent().getStringExtra("email");
        password=getIntent().getStringExtra("u_password");
        confirmPassword=getIntent().getStringExtra("confirm_password");
        imagePath=getIntent().getStringExtra("img_path");
        loginType=getIntent().getStringExtra("login_type");

        tvHello.setText("Hello "+firstName);

        stateList=new ArrayList<>();
        stateList.add("Tamil nadu");

        countryList=new ArrayList<String>();
        countryList.add("India");


        //

        citiesList=new ArrayList<String>();
        citiesList.add("Select city");
        ArrayAdapter<String> adapterCity=new ArrayAdapter<String>(RegistrationTwo.this,android.R.layout.simple_spinner_dropdown_item,citiesList);
        spCity.setAdapter(adapterCity);
        InputStream inputStream1 = null;
        try {
            inputStream1 = getAssets().open("areaLists.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        CSVFile csvFile1 = new CSVFile(inputStream1);
        cityList2 = csvFile1.read();
        for (int i = 0; i < cityList2.size(); i++) {
            if (i == 0) {
                citiesList.add(cityList2.get(i)[1]);
            }else if (!citiesList.contains(cityList2.get(i)[1])) {
                citiesList.add(cityList2.get(i)[1]);
            }
        }
        adapterCity.notifyDataSetChanged();

        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                edtCity.setText(citiesList.get(pos));
                if(spCity.getSelectedItemPosition()!=0){
                    //area
                    areaNameList=new ArrayList<String>();
                    areaNameList.add("Select area");
                    ArrayAdapter<String> adapterArea=new ArrayAdapter<String>(RegistrationTwo.this,android.R.layout.simple_spinner_dropdown_item,areaNameList);
                    spArea.setAdapter(adapterArea);

                    utility.showProgressDialog();
                    InputStream inputStream = null;
                    try {
                        inputStream = getAssets().open("areaLists.csv");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    CSVFile csvFile = new CSVFile(inputStream);
                    areaList1 = csvFile.read();
                    for (int i = 0; i < areaList1.size(); i++) {
                        if(areaList1.get(i)[1].contains(spCity.getSelectedItem().toString())){
                            areaNameList.add(areaList1.get(i)[2]);
                            adapterArea.notifyDataSetChanged();
                        }
                    }
                    Log.e("area",""+areaNameList.size());
                    utility.closeProgressDialog();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        edtCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spCity.performClick();
            }
        });



        spArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                areaPosition=i;
                edtArea.setText(areaNameList.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        edtArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spArea.performClick();
            }
        });



        ArrayAdapter<String> adapterCountry=new ArrayAdapter<String>(RegistrationTwo.this,android.R.layout.simple_spinner_dropdown_item,countryList);
        spCountry.setAdapter(adapterCountry);
        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                edtCountry.setText(countryList.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        edtCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spCountry.performClick();
            }
        });

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(RegistrationTwo.this,android.R.layout.simple_spinner_dropdown_item,stateList);
        spState.setAdapter(adapter);
        spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               edtState.setText(stateList.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        edtState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spState.performClick();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtPhone.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(RegistrationTwo.this,"Enter phone",Toast.LENGTH_SHORT).show();
                }else if(edtPhone.getText().toString().length()!=10){
                    Toast.makeText(RegistrationTwo.this,"Enter valid phone number",Toast.LENGTH_SHORT).show();
                }else if(edtCompanyName.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(RegistrationTwo.this,"Enter company name",Toast.LENGTH_SHORT).show();
                }else if(edtAddress.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(RegistrationTwo.this,"Enter address",Toast.LENGTH_SHORT).show();
                }else if(edtCity.getText().toString().equalsIgnoreCase("Select city")){
                    Toast.makeText(RegistrationTwo.this,"Enter city",Toast.LENGTH_SHORT).show();
                }else if(edtArea.getText().toString().equalsIgnoreCase("Select area")){
                    Toast.makeText(RegistrationTwo.this,"Enter area",Toast.LENGTH_SHORT).show();
                }else if(edtCountry.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(RegistrationTwo.this,"Enter country",Toast.LENGTH_SHORT).show();
                }else{

                    if(utility.isNetworkAvailable()){
                        registerService();
                    }else{
                        Toast.makeText(RegistrationTwo.this,getString(R.string.internet_message),Toast.LENGTH_SHORT).show();
                    }

//                    Intent intent=new Intent(RegistrationTwo.this,RegistrationPayment.class);
//                    intent.putExtra("first_name",firstName);
//                    intent.putExtra("last_name",lastName);
//                    intent.putExtra("email",email);
//                    intent.putExtra("password",password);
//                    intent.putExtra("confirm_password",confirmPassword);
//                    intent.putExtra("login_type",loginType);
//                    intent.putExtra("img_path",imagePath);
//                    intent.putExtra("phone",edtPhone.getText().toString().trim());
//                    intent.putExtra("company_name",edtCompanyName.getText().toString().trim());
//                    intent.putExtra("address",edtAddress.getText().toString().trim());
//                    intent.putExtra("state",edtState.getText().toString().trim());
//                    intent.putExtra("city",edtCity.getText().toString().trim());
//                    intent.putExtra("area",edtArea.getText().toString().trim());
//                    intent.putExtra("country",edtCountry.getText().toString().trim());
//                    intent.putExtra("latitude",areaList1.get(areaPosition-1)[1]);
//                    intent.putExtra("longitude",areaList1.get(areaPosition-1)[2]);
//                    startActivity(intent);
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

    public void registerService() {
        //  String url = Textclass.baseurl + "get_user_filter.php?api_key=ZmFw&u_id="+uId+"&u_state="+tvStateShow.getText().toString().trim()+"&u_city="+tvCityShow.getText().toString().trim()+"&u_area="+tvAreaShow.getText().toString().trim()+"&b_date="+utility.convertDateTime(tvDate.getText().toString().trim())+"&b_session="+tvSessionShow.getText().toString().trim()+"&sp_id="+speciality+"&ep_id=";
        String url = Textclass.baseurl1 + "insert_register_data.php";

        RequestParams requestParams = new RequestParams();
        requestParams.add("api_key", "ZmFw");
        requestParams.add("fname", firstName);
        requestParams.add("lname", lastName);
        requestParams.add("u_email", email);
        requestParams.add("u_phone",edtPhone.getText().toString().trim());
        requestParams.add("u_password", password);
        requestParams.add("u_company_name", edtCompanyName.getText().toString().trim());
        requestParams.add("u_address", edtAddress.getText().toString().trim());
        requestParams.add("u_state", edtState.getText().toString().trim());
        requestParams.add("u_city", edtCity.getText().toString().trim());
        requestParams.add("u_area", edtArea.getText().toString().trim());
        requestParams.add("login_type", loginType);
        requestParams.add("u_country", edtCountry.getText().toString().trim());
        requestParams.add("u_transaction_id", "free");
        requestParams.add("orderid","free");
        requestParams.add("u_lat","00.000000");
        requestParams.add("u_long", "00.000000");
        File imageFile = null;

        if (imagePath == null) {
            imagePath = "";
        } else {
            imageFile = new File(imagePath);
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
        asyncHttpClient.post(RegistrationTwo.this, url, requestParams, new AsyncHttpResponseHandler() {


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

                    Log.e("Success", statusCode + "" + str);
                    JSONObject json = new JSONObject(str);
                    int status = json.getInt("status");
                    if (status == 1) {

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

                        Intent intent = new Intent(RegistrationTwo.this, PhotographerListActivity.class);
                        startActivity(intent);
                        finish();
                        RegistationOne.RegisterOneX.finish();
                        LoginActivity.LoginX.finish();


                    } else if (status == 0) {
                        Toast.makeText(RegistrationTwo.this, json.getString("message"), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(RegistrationTwo.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Toast.makeText(RegistrationTwo.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    utility.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("failure", statusCode + "" + responseBody);
                Toast.makeText(RegistrationTwo.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                utility.closeProgressDialog();
            }
        });
    }

}
