package com.thunderbird.chennai.fapapp.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bumptech.glide.Glide;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.kobakei.ratethisapp.RateThisApp;
import com.thunderbird.chennai.fapapp.R;
import com.thunderbird.chennai.fapapp.Utility.EditTextK;
import com.thunderbird.chennai.fapapp.Model.Model;
import com.thunderbird.chennai.fapapp.Utility.Textclass;
import com.thunderbird.chennai.fapapp.Utility.TypefaceTextView;
import com.thunderbird.chennai.fapapp.Utility.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;
import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class PhotographerListActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private ResideMenu resideMenu;
    private ImageView navBar,nav_filter;
    private ResideMenuItem itemProfile;
    private ResideMenuItem itemSearch;
    private ResideMenuItem itemFilter;
    private ResideMenuItem itemInfo;
    private ResideMenuItem itemTAndC;
    private ResideMenuItem itemExit;
    private ResideMenuItem itemTHowItsWork;
    private ResideMenuItem itemContactUs;
    private Utility utility;
    private ArrayList<Model> photographerList;
    private ListView listview;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private String uId;
    private String state;
    private String city;
    private String area;
    private String session,session2;
    private String date,date2;
    private String speciality;
    private String equipment;
    private EditTextK edtSearch;
    private BillingProcessor bp;
    private AlertDialog.Builder ExtendSubscripDialog;
    private String lati;
    private String longi;
    protected static final String TAG = "PhotographerListAct";

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logout();
        }
    };

    /**
     * Constant used in the location settings dialog.
     */
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private Status status;
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private Bundle mBundle;
    private SpotsDialog mDialog;
    private PhotographerAdapter photographerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photographer_list);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        sharedpreferences = getSharedPreferences("myprefrence", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        uId = sharedpreferences.getString("u_id", "");
        lati = sharedpreferences.getString("lati", "");
        longi = sharedpreferences.getString("longi", "");
        listview = (ListView) findViewById(R.id.listview);
        navBar = (ImageView) findViewById(R.id.nav_bar);
        nav_filter = (ImageView) findViewById(R.id.nav_filter);
        edtSearch = (EditTextK) findViewById(R.id.edt_search);
        utility = new Utility(this);
        bp = new BillingProcessor(this, Textclass.base64, this);

        new AppUpdater(this)
                //.setUpdateFrom(UpdateFrom.GITHUB)
                //.setGitHubUserAndRepo("javiersantos", "AppUpdater")
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .setUpdateXML("https://play.google.com/store/apps/details?id=" + getPackageName())
                .setDisplay(com.github.javiersantos.appupdater.enums.Display.DIALOG)
                .showAppUpdated(false)
                .start();

          initialize();
    }


    private void Logout() {
        editor.clear();
        editor.commit();
        Intent intent = new Intent(PhotographerListActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void initialize() {
        if (getIntent().getStringExtra("u_state") != null) {
            if (utility.isNetworkAvailable()) {
                state = getIntent().getStringExtra("u_state");
                city = getIntent().getStringExtra("u_city");
                area = getIntent().getStringExtra("u_area");
                session = getIntent().getStringExtra("u_session");
                session2= getIntent().getStringExtra("u_session2");
                date = getIntent().getStringExtra("u_date");
                date2 = getIntent().getStringExtra("u_date2");
                speciality = getIntent().getStringExtra("u_speciality");
                equipment = getIntent().getStringExtra("u_equipment");
                getUserFilter();
            } else {
                Toast.makeText(PhotographerListActivity.this, getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
            }
        } else {
            edtSearch.setText("Chennai");
            if (utility.isNetworkAvailable()) {
                getPhotographerListService();
            } else {
                Toast.makeText(PhotographerListActivity.this, getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
            }
        }
        navigationDrawer();


        navBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });



        nav_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhotographerListActivity.this, FilterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(PhotographerListActivity.this, ProfileActivitys.class);
                intent.putExtra("u_id", photographerList.get(i).getuId());
                intent.putExtra("f_name", photographerList.get(i).getFirstName());
                intent.putExtra("l_name", photographerList.get(i).getLastName());
                intent.putExtra("city", photographerList.get(i).getCity());
                intent.putExtra("area", photographerList.get(i).getArea());
                intent.putExtra("state", photographerList.get(i).getState());
                intent.putExtra("profile_pic", photographerList.get(i).getProfilePic());
                intent.putExtra("background_image", photographerList.get(i).getBackgroundImage());
                intent.putExtra("email", photographerList.get(i).getEmail());
                intent.putExtra("phone", photographerList.get(i).getPhone());
                intent.putExtra("verify_flag", photographerList.get(i).getVerify_flag());
                intent.putExtra("screen", "other_profile");
                startActivity(intent);

                resideMenu.closeMenu();
            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (getIntent().getStringExtra("u_state") != null && photographerAdapter != null) {
                        photographerAdapter.filter(edtSearch.getText().toString().trim());
                        listview.invalidate();
                    } else {
                        getPhotographerListService();
                    }

                    return true;
                }
                return false;
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edtSearch.getText().toString().trim().equalsIgnoreCase("")) {

                    if (getIntent().getStringExtra("u_state") != null) {
                        photographerAdapter.filter(edtSearch.getText().toString().trim());
                        listview.invalidate();
                    } else {
                        getPhotographerListService();
                    }
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        // Monitor launch times and interval from installation
        RateThisApp.onStart(this);
        // If the criteria is satisfied, "Rate this app" dialog will be shown
        RateThisApp.showRateDialogIfNeeded(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter("test"));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        boolean showRationale1 = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            showRationale1 = shouldShowRequestPermissionRationale(permissions[0]);
        }


        if (requestCode == 1) {

            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                    ) {

                initialize();

            } else if (!showRationale1) {
                // user also CHECKED "never ask again"
                // you can either enable some fall back,
                // disable features of your app
                // or open another dialog explaining
                // again the permission and directing to
                // the app setting
                //   initialize();

                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 1);
            } else {
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
        Log.e("request code:", "" + requestCode);
        if (requestCode == 32459) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                Log.e("result code:", "" + requestCode);
                finish();
                Intent intent = new Intent(PhotographerListActivity.this, PhotographerListActivity.class);
                startActivity(intent);

            } else {
                editor.clear();
                editor.commit();
                Intent intent = new Intent(PhotographerListActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            switch (requestCode) {
                // Check for the integer request code originally supplied to startResolutionForResult().
                case REQUEST_CHECK_SETTINGS:
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            Log.i(TAG, "User agreed to make required location settings changes.");
                            break;
                        case Activity.RESULT_CANCELED:
                            try {
                                status.startResolutionForResult(PhotographerListActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                            Log.i(TAG, "User chose not to make required location settings changes.");
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //   mGoogleApiClient.disconnect();
    }

    private void navigationDrawer() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);


        //https://github.com/SpecialCyCi/AndroidResideMenu/issues/112
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
                    paddingBottom += resources.getDimensionPixelSize(resourceId);
                    ;
                    resideMenu.setPadding(resideMenu.getPaddingLeft(), resideMenu.getPaddingTop(), resideMenu.getPaddingRight(), paddingBottom);
                }
            }, 300);
        }

        //  resideMenu.setBackgroundColor(Color.parseColor("#034478"));
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

        itemExit = new ResideMenuItem(this, R.drawable.exit, "Exit");
        resideMenu.addMenuItem(itemExit, ResideMenu.DIRECTION_LEFT);

    itemTHowItsWork.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=WkrT5om6IQw")));
        }
    });
        itemProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // resideMenu.closeMenu();
                Intent intent = new Intent(PhotographerListActivity.this, ProfileActivitys.class);
                intent.putExtra("screen", "profile");
                startActivity(intent);
                finish();
                resideMenu.closeMenu();

            }
        });

        itemSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.closeMenu();
            }
        });

        itemFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PhotographerListActivity.this, FilterActivity.class);
                startActivity(intent);
                finish();

            }
        });

        itemInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotographerListActivity.this, InfoActivity.class);
                startActivity(intent);
                finish();
            }
        });
        itemContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhotographerListActivity.this, FeedbackActivity.class);
                startActivity(intent);
                finish();
            }
        });

        itemTAndC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotographerListActivity.this, TandCActivity.class);
                startActivity(intent);
                finish();
            }
        });

        itemExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(PhotographerListActivity.this).setMessage("Are you sure you want to logout?").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(PhotographerListActivity.this, LoginActivity.class);
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
    protected void onPause() {
        super.onPause();

        unregisterReceiver(broadcastReceiver);

    }

    public void closeKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public void getPhotographerListService() {
        photographerList = new ArrayList<Model>();
        String url = Textclass.baseurl + "get_user_search.php?api_key=ZmFw&u_id=" + uId + "&search_text=" + edtSearch.getText().toString();
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(PhotographerListActivity.this, url, new AsyncHttpResponseHandler() {


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
                            model.setProfilePic(jsonObject1.getString("profile_pic"));
                            model.setCity(jsonObject1.getString("u_city"));
                            model.setEmail(jsonObject1.getString("u_email"));
                            model.setPhone(jsonObject1.getString("u_phone"));
                            model.setArea(jsonObject1.getString("u_area"));
                            model.setState(jsonObject1.getString("u_state"));
//                            model.setGetDistance(jsonObject1.getString("distance"));
                            model.setCreatedDate(jsonObject1.getString("u_created_date"));
                            model.setBackgroundImage(jsonObject1.getString("user_bg_photos"));
                            model.setVerify_flag(jsonObject1.getString("verify_flag"));
                            photographerList.add(model);
                        }
                        photographerAdapter = new PhotographerAdapter(PhotographerListActivity.this, photographerList);

                        listview.setAdapter(photographerAdapter);

                    } else if (status == 0) {
                        Toast.makeText(PhotographerListActivity.this, getString(R.string.no_data), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(PhotographerListActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();


                    }

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Toast.makeText(PhotographerListActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    utility.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(PhotographerListActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                utility.closeProgressDialog();
            }
        });
    }


    public void getUserFilter() {

        photographerList = new ArrayList<Model>();
        if (date.equalsIgnoreCase("")) {
            date = "";
        } else {
            date = utility.convertDateTime(date);
        }
        if (date2.equalsIgnoreCase("")) {
            date2 = "";
        } else {
            date2 = utility.convertDateTime(date2);
        }
        String url = Textclass.baseurl + "get_user_filter1.php?api_key=ZmFw&u_id=" + uId + "&u_state=" + state + "&u_city=" + city + "&u_area=" + area + "&b_date=" + date + "&b_session=" + session + "&sp_id=" + speciality + "&ep_id=" + equipment + "&b_date1=" + date2 + "&b_session1=" + session2;
        Log.e("Url",url);
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(PhotographerListActivity.this, url, new AsyncHttpResponseHandler() {

            public TypefaceTextView tvName;
            public RelativeLayout relImageCandid;
            ImageView imgCandid;

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
                        JSONArray jsonArray = json.getJSONArray("Get_data");
                        if(jsonArray.length()==0){
                            Toast.makeText(PhotographerListActivity.this, getString(R.string.no_data), Toast.LENGTH_SHORT).show();
                            photographerAdapter = new PhotographerAdapter(PhotographerListActivity.this, photographerList);

                            listview.setAdapter(photographerAdapter);

                        }else {


                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            Model model = new Model();
                            model.setuId(jsonObject1.getString("u_id"));
                            model.setFirstName(jsonObject1.getString("fname"));
                            model.setLastName(jsonObject1.getString("lname"));
                            model.setProfilePic(jsonObject1.getString("profile_pic"));
                            model.setArea(jsonObject1.getString("u_area"));
                            model.setCity(jsonObject1.getString("u_city"));
                            model.setEmail(jsonObject1.getString("u_email"));
                            model.setPhone(jsonObject1.getString("u_phone"));
                            model.setState(jsonObject1.getString("u_state"));
                            model.setVerify_flag(jsonObject1.getString("verify_flag"));
                            model.setGetDistance(jsonObject1.getString("distance"));
                            model.setCreatedDate(jsonObject1.getString("u_created_date"));
                            model.setBackgroundImage(jsonObject1.getString("user_bg_photos"));
                            photographerList.add(model);
                        }

                        photographerAdapter = new PhotographerAdapter(PhotographerListActivity.this, photographerList);

                        listview.setAdapter(photographerAdapter);
                        }

                    } else if (status == 0) {
                        Toast.makeText(PhotographerListActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        photographerAdapter = new PhotographerAdapter(PhotographerListActivity.this, photographerList);
                        listview.setAdapter(photographerAdapter);


                    } else {
                        Toast.makeText(PhotographerListActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    utility.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("failure", statusCode + "" + responseBody);
                Toast.makeText(PhotographerListActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                utility.closeProgressDialog();
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

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        //   bp.consumePurchase("register_sid");
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

    /**
     * Adapter class
     */
    public class PhotographerAdapter extends BaseAdapter {


        private final ArrayList<Model> arraylist;
        Context context;
        List<Model> myMediaItemList;


        private ViewHolder holder;

        PhotographerAdapter(Context context, List<Model> rowItem) {
            this.context = context;
            myMediaItemList = rowItem;
            arraylist = new ArrayList<Model>();
            arraylist.addAll(myMediaItemList);

        }

        @Override
        public int getCount() {

            return myMediaItemList.size();
        }

        @Override
        public Object getItem(int position) {

            return myMediaItemList.get(position);

        }

        @Override
        public long getItemId(int position) {


            return myMediaItemList.indexOf(getItem(position));
        }

        /**
         * Viewholder class
         */
        public class ViewHolder {
            ImageView profileImage;
            TypefaceTextView tvName;
            TypefaceTextView tvAddress;
            TypefaceTextView tvKm;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater mInflater;
            if (convertView == null) {
                holder = new ViewHolder();
                mInflater = (LayoutInflater) context
                        .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.custom_list, null);
                holder.tvName = (TypefaceTextView) convertView.findViewById(R.id.tv_name);
                holder.tvAddress = (TypefaceTextView) convertView.findViewById(R.id.tv_address);
               // holder.tvKm = (TypefaceTextView) convertView.findViewById(R.id.tv_km);
                holder.profileImage = (ImageView) convertView.findViewById(R.id.profile_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

//            holder.tvKm.setText(new DecimalFormat("##").format(Double.parseDouble(myMediaItemList.get(position).getGetDistance())) + "km");
            holder.tvName.setText(myMediaItemList.get(position).getFirstName() + " " + myMediaItemList.get(position).getLastName());
            holder.tvAddress.setText(myMediaItemList.get(position).getArea() + ", " + myMediaItemList.get(position).getCity() + ", " + myMediaItemList.get(position).getState());


            Glide.with(PhotographerListActivity.this)
                    .load(Textclass.profilePic + myMediaItemList.get(position).getProfilePic()).override(100, 100).centerCrop().placeholder(R.drawable.avtar1)
                    .bitmapTransform(new CropCircleTransformation(PhotographerListActivity.this))
                    .into(holder.profileImage);
               return convertView;
        }

        public void filter(String charText) {

            charText = charText.toLowerCase(Locale.getDefault());

            myMediaItemList.clear();
            if (charText.length() == 0) {
                myMediaItemList.addAll(arraylist);

            } else {
                for (Model postDetail : arraylist) {
                    if (charText.length() != 0 && ((postDetail.getFirstName().toLowerCase(Locale.getDefault()).contains(charText) && postDetail.getLastName().toLowerCase(Locale.getDefault()).contains(charText)) || postDetail.getFirstName().toLowerCase(Locale.getDefault()).contains(charText) || postDetail.getLastName().toLowerCase(Locale.getDefault()).contains(charText) || postDetail.getState().toLowerCase(Locale.getDefault()).contains(charText) || postDetail.getCity().toLowerCase(Locale.getDefault()).contains(charText) || postDetail.getArea().toLowerCase(Locale.getDefault()).contains(charText) || postDetail.getState().toLowerCase(Locale.getDefault()).contains(charText))) {
                        myMediaItemList.add(postDetail);
                    }

                }
            }
            notifyDataSetChanged();
        }
    }


}
