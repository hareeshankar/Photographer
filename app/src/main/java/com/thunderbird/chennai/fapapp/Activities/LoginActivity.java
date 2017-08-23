package com.thunderbird.chennai.fapapp.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.thunderbird.chennai.fapapp.R;
import com.thunderbird.chennai.fapapp.Utility.EditTextK;
import com.thunderbird.chennai.fapapp.Model.Model;
import com.thunderbird.chennai.fapapp.Utility.Textclass;
import com.thunderbird.chennai.fapapp.Utility.TypefaceTextView;
import com.thunderbird.chennai.fapapp.Utility.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private TypefaceTextView tvSignUp;
    private TypefaceTextView btnLogin;
    private Utility utility;
    private EditTextK edtEmail;
    private EditTextK edtPassword;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private TypefaceTextView tvForgotPassword;
    private EditTextK edtForgot;
    private DialogInterface mDialog;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static LoginActivity LoginX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginX = this;
        sharedpreferences = getApplicationContext().getSharedPreferences("myprefrence", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        utility = new Utility(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        tvSignUp = (TypefaceTextView) findViewById(R.id.tv_sign_up);
        btnLogin = (TypefaceTextView) findViewById(R.id.btn_login);
        edtEmail = (EditTextK) findViewById(R.id.edt_email);
        edtPassword = (EditTextK) findViewById(R.id.edt_password);
        tvForgotPassword = (TypefaceTextView) findViewById(R.id.tv_forgot_password);

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (utility.isNetworkAvailable()) {
                    if (edtEmail.getText().toString().trim().equalsIgnoreCase("")) {
                        Toast.makeText(LoginActivity.this, getString(R.string.email), Toast.LENGTH_SHORT).show();
                    } else if (edtPassword.getText().toString().trim().equalsIgnoreCase("")) {
                        Toast.makeText(LoginActivity.this, getString(R.string.password), Toast.LENGTH_SHORT).show();
                    } else {
                        loginService();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.internet_message), Toast.LENGTH_SHORT).show();

                }


            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistationOne.class);
                startActivity(intent);
               // finish();
            }
        });
    }

    /**
     * alert dialog
     */
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_forgot_password, null);

        edtForgot = (EditTextK) v.findViewById(R.id.edt_forgot);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)
                .setCancelable(false)
                // Add action buttons
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        mDialog = dialog;
                        if (utility.isNetworkAvailable()) {
                            if(edtForgot.getText().toString().trim().equalsIgnoreCase("")){
                                Toast.makeText(LoginActivity.this, getString(R.string.email), Toast.LENGTH_SHORT).show();
                            }else if (!edtForgot.getText().toString().trim().matches(emailPattern)) {
                                Toast.makeText(LoginActivity.this, getString(R.string.valid_email), Toast.LENGTH_SHORT).show();
                            }else{
                                forgotPasswordService();
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }


    private void forgotPasswordService() {
        String url = Textclass.baseurl + "forgot_password.php?api_key=ZmFw&u_email=" + edtForgot.getText().toString();

        AsyncHttpClient asyncHttpClint = new AsyncHttpClient();
        asyncHttpClint.get(LoginActivity.this, url, new AsyncHttpResponseHandler() {

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
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(str);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        Toast.makeText(LoginActivity.this, getString(R.string.password_sent), Toast.LENGTH_SHORT).show();
                    } else if (status == 0) {
                        Toast.makeText(LoginActivity.this, getString(R.string.email_does_not_exists), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                    mDialog.dismiss();
                    utility.closeProgressDialog();
                } catch (JSONException e) {
                    utility.closeProgressDialog();
                    Toast.makeText(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                utility.closeProgressDialog();
                Toast.makeText(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void loginService() {

        String url = Textclass.baseurl + "get_user_login.php?api_key=ZmFw&u_email=" + edtEmail.getText().toString().trim() + "&u_password=" + edtPassword.getText().toString().trim();
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(LoginActivity.this, url, new AsyncHttpResponseHandler() {

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
                            Model model = new Model();
                            model.setuId(jsonObject1.getString("u_id"));
                            model.setFirstName(jsonObject1.getString("fname"));
                            model.setLastName(jsonObject1.getString("lname"));
                            model.setPhone(jsonObject1.getString("u_phone"));
                            model.setProfilePic(jsonObject1.getString("profile_pic"));
                            model.setState(jsonObject1.getString("u_state"));
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

                        Intent intent = new Intent(LoginActivity.this, PhotographerListActivity.class);
                        startActivity(intent);
                        finish();

                    } else if (status == 0) {
                        Toast.makeText(LoginActivity.this, getString(R.string.server_problem), Toast.LENGTH_SHORT).show();

                    } else if(status==2){
                        Toast.makeText(LoginActivity.this, getString(R.string.email_id_or_password_does_not_exists), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    utility.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                utility.closeProgressDialog();
            }
        });

    }


}


