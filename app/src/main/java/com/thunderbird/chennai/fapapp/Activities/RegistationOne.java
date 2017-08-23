package com.thunderbird.chennai.fapapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.thunderbird.chennai.fapapp.R;
import com.thunderbird.chennai.fapapp.Utility.EditTextK;
import com.thunderbird.chennai.fapapp.Utility.Textclass;
import com.thunderbird.chennai.fapapp.Utility.TypefaceTextView;
import com.thunderbird.chennai.fapapp.Utility.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegistationOne extends AppCompatActivity {

    private TypefaceTextView btnNext;

    public static Activity RegisterOneX;
    private TypefaceTextView tvBack;
    private CircleImageView imgProfileImage;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private EditTextK edtFirstName;
    private EditTextK edtLastName;
    private EditTextK edtEmail;
    private EditTextK edtPassword;
    private EditTextK edtConfirmPassword;
    private String imagePath="";
    private Utility utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registation_one);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //   getWindow().setBackgroundDrawableResource(R.drawable.bg1);
        utility = new Utility(this);

        RegisterOneX = this;
        btnNext = (TypefaceTextView) findViewById(R.id.btn_next);
        tvBack = (TypefaceTextView) findViewById(R.id.btn_back);
        imgProfileImage = (CircleImageView) findViewById(R.id.profile_image);
        edtFirstName = (EditTextK) findViewById(R.id.edt_first_name);
        edtLastName = (EditTextK) findViewById(R.id.edt_last_name);
        edtEmail = (EditTextK) findViewById(R.id.edt_email);
        edtPassword = (EditTextK) findViewById(R.id.edt_password);
        edtConfirmPassword = (EditTextK) findViewById(R.id.edt_confirm_password);

        imgProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.create(RegistationOne.this)
                        .folderMode(true) // folder mode (false by default)
                        //    .folderTitle("Folder") // folder selection title
                        .imageTitle("Tap to select") // image selection title
                        .single() // single mode
                        //    .multi() // multi mode (default mode)
                        //    .limit(10) // max images can be selected (999 by default)
                        .showCamera(true) // show camera or not (true by default)
                        .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                        //      .origin(images) // original selected images, used in multi mode
                        .start(1); // start image picker activity with request code
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtFirstName.getText().toString().trim().equals("")) {
                    Toast.makeText(RegistationOne.this, getString(R.string.first_name), Toast.LENGTH_SHORT).show();
                } else if (edtLastName.getText().toString().trim().equals("")) {
                    Toast.makeText(RegistationOne.this, getString(R.string.last_name), Toast.LENGTH_SHORT).show();
                } else if (edtEmail.getText().toString().trim().equals("")) {
                    Toast.makeText(RegistationOne.this, getString(R.string.email), Toast.LENGTH_SHORT).show();
                } else if (edtPassword.getText().toString().trim().equals("")) {
                    Toast.makeText(RegistationOne.this, getString(R.string.password), Toast.LENGTH_SHORT).show();
                } else if (!edtEmail.getText().toString().trim().matches(emailPattern)) {
                    Toast.makeText(RegistationOne.this, getString(R.string.valid_email), Toast.LENGTH_SHORT).show();
                }else if (edtConfirmPassword.getText().toString().trim().equals("")) {
                    Toast.makeText(RegistationOne.this, getString(R.string.confirm_password), Toast.LENGTH_SHORT).show();
                } else if (!edtConfirmPassword.getText().toString().trim().equalsIgnoreCase(edtPassword.getText().toString().trim())) {
                    Toast.makeText(RegistationOne.this, getString(R.string.password_mismatch), Toast.LENGTH_SHORT).show();
                }  else if (imagePath.equalsIgnoreCase("")) {
                    Toast.makeText(RegistationOne.this, "Enter profile pic", Toast.LENGTH_SHORT).show();
                } else {
                    checkUserService();
                }
                //  finish();
            }
        });

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    private void checkUserService() {

        String url = Textclass.baseurl + "check_user.php?api_key=ZmFw&login_type=app&fname=" + edtFirstName.getText().toString().trim() + "&lname=" + edtLastName.getText().toString().trim() + "&u_email=" + edtEmail.getText().toString().trim() + "&u_password=" + edtPassword.getText().toString().trim();
        //  String url = Textclass.baseurl + "insert_register_data.php";

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(RegistationOne.this, url, new AsyncHttpResponseHandler() {


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

                        Intent intent = new Intent(RegistationOne.this, RegistrationTwo.class);
                        intent.putExtra("first_name", edtFirstName.getText().toString().trim());
                        intent.putExtra("last_name", edtLastName.getText().toString().trim());
                        intent.putExtra("email", edtEmail.getText().toString().trim());
                        intent.putExtra("login_type", "app");
                        intent.putExtra("u_password", edtPassword.getText().toString().trim());
                        intent.putExtra("confirm_password", edtConfirmPassword.getText().toString().trim());
                        intent.putExtra("img_path", imagePath);
                        startActivity(intent);

                    } else if (status == 123) {
                        Toast.makeText(RegistationOne.this, getString(R.string.email_already_exists), Toast.LENGTH_SHORT).show();
                    } else if (status == 0) {
                        Toast.makeText(RegistationOne.this, getString(R.string.server_problem), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegistationOne.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Toast.makeText(RegistationOne.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    utility.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(RegistationOne.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                utility.closeProgressDialog();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            // do your logic ....
            imagePath = images.get(0).getPath();


            Glide.with(this).load(new File(imagePath)).into(imgProfileImage);

        }
    }
}
