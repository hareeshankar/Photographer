package com.thunderbird.chennai.fapapp.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import com.thunderbird.chennai.fapapp.Model.Model;
import com.thunderbird.chennai.fapapp.R;
import com.thunderbird.chennai.fapapp.Utility.Textclass;
import com.thunderbird.chennai.fapapp.Utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class FeedbackActivity extends AppCompatActivity {
    private ResideMenu resideMenu;
    private ResideMenuItem itemProfile;
    private ResideMenuItem itemSearch;
    private ResideMenuItem itemFilter;
    private ResideMenuItem itemInfo;
    private ResideMenuItem itemTAndC;
    private ResideMenuItem itemExit;
    private ResideMenuItem itemTHowItsWork;
    private ResideMenuItem itemContactUs;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private ImageView navBar;
    private Utility utility;
    EditText  txtEmail, txtFeddback;

    Button btn_Clear, btnSend;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        utility = new Utility(this);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtFeddback = (EditText) findViewById(R.id.txtFeddback);
        btn_Clear = (Button) findViewById(R.id.btnClear);

        btnSend = (Button) findViewById(R.id.btnSend);
        sharedpreferences = getSharedPreferences("myprefrence", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        navBar = (ImageView) findViewById(R.id.nav_bar);
        navBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
        navigationDrawer();

        btn_Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtEmail.setText("");
                txtFeddback.setText("");
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (utility.isNetworkAvailable()) {

                    if (!txtEmail.getText().toString().matches(emailPattern)) {
                        Toast.makeText(FeedbackActivity.this,"Enter valid Email Address",Toast.LENGTH_SHORT).show();
                    } else if (txtFeddback.length() <= 20) {
                        Toast.makeText(FeedbackActivity.this,"Enter Must be 30 digit Feedback..!",Toast.LENGTH_SHORT).show();

                    } else {
                        submite();
                    }
                }else {
                    Toast.makeText(FeedbackActivity.this, getString(R.string.internet_message), Toast.LENGTH_SHORT).show();

                }

            }
        });
    }


    private void navigationDrawer() {
        // attach to current activity;
        resideMenu = new ResideMenu(this);

        // Fix RESideMenu bottom layout bug.
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        if (hasBackKey || hasHomeKey) { // There's a navigation bar.
            resideMenu.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Resources resources = getResources();
                    int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
                    int paddingBottom = resideMenu.getPaddingBottom();
                    paddingBottom += resources.getDimensionPixelSize(resourceId);;
                    resideMenu.setPadding(resideMenu.getPaddingLeft(), resideMenu.getPaddingTop(), resideMenu.getPaddingRight(), paddingBottom);
                }
            }, 300);
        }


        // resideMenu.setBackgroundColor(Color.parseColor("#034478"));
        resideMenu.setBackground(R.drawable.bg1);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        resideMenu.setShadowVisible(false);
        resideMenu.attachToActivity(this);

        // create menu items;
        itemProfile = new ResideMenuItem(this, R.drawable.profile, "Profile");
        resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_LEFT);
        itemSearch = new ResideMenuItem(this, R.drawable.search_white, "Search");
        resideMenu.addMenuItem(itemSearch, ResideMenu.DIRECTION_LEFT);
        itemFilter = new ResideMenuItem(this, R.drawable.filter, "Filter");
        resideMenu.addMenuItem(itemFilter, ResideMenu.DIRECTION_LEFT);
        itemInfo = new ResideMenuItem(this, R.drawable.info, "Information");
        resideMenu.addMenuItem(itemInfo, ResideMenu.DIRECTION_LEFT);
        itemTAndC = new ResideMenuItem(this, R.drawable.tandc, "Terms & Conditions");
        resideMenu.addMenuItem(itemTAndC, ResideMenu.DIRECTION_LEFT);

        itemTHowItsWork = new ResideMenuItem(this, R.drawable.exit, "How it Works");
        resideMenu.addMenuItem(itemTHowItsWork, ResideMenu.DIRECTION_LEFT);

        itemContactUs = new ResideMenuItem(this, R.drawable.exit, "Contact Us");
        resideMenu.addMenuItem(itemContactUs, ResideMenu.DIRECTION_LEFT);
//
        itemExit = new ResideMenuItem(this, R.drawable.exit, "Exit");
        resideMenu.addMenuItem(itemExit, ResideMenu.DIRECTION_LEFT);

        itemProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FeedbackActivity.this, ProfileActivitys.class);
                intent.putExtra("screen", "profile");
                startActivity(intent);
                finish();
            }
        });
        itemTHowItsWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=WkrT5om6IQw")));
            }
        });

        itemSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FeedbackActivity.this, PhotographerListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        itemFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(FeedbackActivity.this, FilterActivity.class);
                startActivity(intent);
                finish();

            }
        });

        itemInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FeedbackActivity.this, InfoActivity.class);
                startActivity(intent);
                finish();
            }
        });

        itemContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedbackActivity.this, FeedbackActivity.class);
                startActivity(intent);
                finish();
            }
        });
        itemTAndC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.closeMenu();

            }
        });
        itemTHowItsWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        itemContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        itemExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(FeedbackActivity.this).setMessage("Are you sure you want to logout?").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(FeedbackActivity.this, FeedbackActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(true);

                alertDialog.show();


            }
        });


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        getExitDialog();

    }

    public void getExitDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this).setMessage("Press ok to exit the app.").setPositiveButton("ok", new DialogInterface.OnClickListener() {
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


    private void submite() {

        String url = Textclass.baseurl + "feedback.php?api_key=ZmFw&u_id=" + sharedpreferences.getString("u_id", "") + "&u_email=" + txtEmail.getText().toString().trim()+ "&u_feedback=" + txtFeddback.getText().toString().trim();
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(FeedbackActivity.this, url, new AsyncHttpResponseHandler() {

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
                    if (str != null) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(str);
                            Integer success = jsonObject.getInt("success");
                            if(success==1){
                                Toast.makeText(FeedbackActivity.this,"Thanks you give value able feedback",Toast.LENGTH_SHORT).show();

                                Log.e("lenght", "" + jsonObject.length());
                                txtEmail.setText("");
                                txtFeddback.setText("");
                            }else {
                                Toast.makeText(FeedbackActivity.this,"Invalid Parametes, Please try again",Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Toast.makeText(FeedbackActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    utility.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(FeedbackActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                utility.closeProgressDialog();
            }
        });

    }

}
