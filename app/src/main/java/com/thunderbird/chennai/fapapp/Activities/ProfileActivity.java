package com.thunderbird.chennai.fapapp.Activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thunderbird.chennai.fapapp.Fragments.EquipmentFragment;
import com.thunderbird.chennai.fapapp.Fragments.PhotosFragment;
import com.thunderbird.chennai.fapapp.Fragments.SpecialityFragment;
import com.thunderbird.chennai.fapapp.R;
import com.thunderbird.chennai.fapapp.Utility.CSVFile;
import com.thunderbird.chennai.fapapp.Utility.EditTextK;
import com.thunderbird.chennai.fapapp.Model.Model;
import com.thunderbird.chennai.fapapp.Utility.Textclass;
import com.thunderbird.chennai.fapapp.Utility.TypefaceTextView;
import com.thunderbird.chennai.fapapp.Utility.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

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
import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class ProfileActivity extends AppCompatActivity {
    private RelativeLayout relSpeciality;
    private RelativeLayout relEquipment;
    private RelativeLayout relPhotos;
    private View sBelow;
    private View eBelow;
    private View pBelow;
    private TypefaceTextView tvSpeciality;
    private TypefaceTextView tvEquipment;
    private TypefaceTextView tvPhotos;
    private Typeface typeRegular;
    private Typeface typeBold;
    private ImageView imgBlur;
    private TypefaceTextView tvBack;
    private ImageView btnMenu;
    private TypefaceTextView tvEdit;
    private ResideMenu resideMenu;
    private ResideMenuItem itemProfile;
    private ResideMenuItem itemSearch;
    private ResideMenuItem itemFilter;
    private ResideMenuItem itemInfo;
    private ResideMenuItem itemTAndC;
    private ResideMenuItem itemExit;
    private ResideMenuItem itemTHowItsWork;
    private ResideMenuItem itemContactUs;
    private PhotosFragment photosFragment;
    private String screen;
    private String uId;
    private TypefaceTextView tvName;
    private TypefaceTextView tvAddress;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private TypefaceTextView btnEdit;
    private android.support.v7.app.AlertDialog alertDialog;
    private EditTextK edtFirstName;
    private EditTextK tvLastName;
    private EditTextK tvPhoneName;
    private EditTextK edtPassword;
    private ImageView imgProfilePic;
    private TypefaceTextView btnDone;
    private Utility utility;
    private String imagePath;
    private File imageFile;
    private TypefaceTextView btnContactMe,btnVerify;
    private RelativeLayout relPhone;
    private RelativeLayout relEmail;
    private EquipmentFragment equipmentFragment;
    private String email;
    private String phone;
    private TypefaceTextView tvEmail;
    private TypefaceTextView tvPhone;

    /**
     * Hold a reference to the current animator, so that it can be canceled mid-way.
     */
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private String profileImage;
    public static ViewPager pager;
    private Spinner spState;
    private Spinner spCity;
    private Spinner spArea;
    private Spinner spCountry;
    private TypefaceTextView edtState;
    private TypefaceTextView edtCity;
    private TypefaceTextView edtArea;
    private EditTextK edtCompanyName;
    private EditTextK edtAddress;
    private TypefaceTextView edtCountry;
    private ArrayList<String> stateList;
    private ArrayList<String> countryList;
    private ArrayList<String> areaNameList;
    private ArrayList<String> latitudeList;
    private ArrayList<String> longitudeList;
    private int areaPosition;
    private ArrayList<String> citiesList;
    private ArrayList<String> cityList;
    private ImageView imgProfilePicDialog;
    private boolean stateFlag;
    private boolean cityFlag;
    private boolean areaFlag;
    private boolean countryFlag;
    public static ImageView expandedImageView;
    private List<String[]> areaList;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        utility = new Utility(this);

        sharedpreferences = getSharedPreferences("myprefrence", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        relSpeciality = (RelativeLayout) findViewById(R.id.rel_speciality);
        relEquipment = (RelativeLayout) findViewById(R.id.rel_equipment);
        relPhotos = (RelativeLayout) findViewById(R.id.rel_photos);
        tvSpeciality = (TypefaceTextView) findViewById(R.id.tv_speciality);
        tvEquipment = (TypefaceTextView) findViewById(R.id.tv_equipment);
        tvPhotos = (TypefaceTextView) findViewById(R.id.tv_photos);
        tvBack = (TypefaceTextView) findViewById(R.id.btn_back);
        btnMenu = (ImageView) findViewById(R.id.btn_menu);
        tvEdit = (TypefaceTextView) findViewById(R.id.tv_edit);
        sBelow = (View) findViewById(R.id.s_view);
        eBelow = (View) findViewById(R.id.e_below);
        pBelow = (View) findViewById(R.id.p_below);
        imgBlur = (ImageView) findViewById(R.id.img_blur);
        tvName = (TypefaceTextView) findViewById(R.id.tv_name);
        tvAddress = (TypefaceTextView) findViewById(R.id.tv_address);
        imgProfilePic = (ImageView) findViewById(R.id.profile_image);
        typeRegular = Typeface.createFromAsset(getAssets(), "fonts/OrkneyRegular.otf");
        typeBold = Typeface.createFromAsset(getAssets(), "fonts/OrkneyBold.otf");
        btnEdit = (TypefaceTextView) findViewById(R.id.tv_edit);
        btnContactMe = (TypefaceTextView) findViewById(R.id.btn_contact_me);
        btnVerify = (TypefaceTextView) findViewById(R.id.btn_verify);
        tvEmail = (TypefaceTextView) findViewById(R.id.tv_email);
        tvPhone = (TypefaceTextView) findViewById(R.id.tv_phone);
        pager = (ViewPager) findViewById(R.id.myviewpager);
        expandedImageView = (ImageView) findViewById(R.id.expanded_image);
        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        Intent intent = getIntent();
        screen = intent.getStringExtra("screen");
        viewPager = (ViewPager) findViewById(R.id.myviewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        if (screen.equalsIgnoreCase("profile")) {
            uId = sharedpreferences.getString("u_id", "");
            Glide.with(this).load(Textclass.profilePic + sharedpreferences.getString("profile_pic", "")).override(100, 100).centerCrop().placeholder(R.drawable.avtar1)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(imgProfilePic);
            //Glide.with(this).load(Textclass.profilePic + sharedpreferences.getString("profile_pic", "")).bitmapTransform(new CropCircleTransformation(ProfileActivity.this)).diskCacheStrategy(DiskCacheStrategy.ALL).override(100,100).centerCrop().placeholder(R.drawable.avtar).into(imgProfilePic);
            tvName.setText(sharedpreferences.getString("f_name", "") + " " + sharedpreferences.getString("l_name", ""));
            tvAddress.setText(sharedpreferences.getString("area", "") + ", " + sharedpreferences.getString("city", "") + ", " + sharedpreferences.getString("state", ""));
            btnMenu.setVisibility(View.VISIBLE);
            tvEdit.setVisibility(View.VISIBLE);
            tvBack.setVisibility(View.GONE);
            btnContactMe.setVisibility(View.GONE);
            tvEmail.setVisibility(View.VISIBLE);
            tvPhone.setVisibility(View.VISIBLE);
            tvEmail.setText(sharedpreferences.getString("email", ""));
            tvPhone.setText("+91" + sharedpreferences.getString("phone", ""));
            profileImage = sharedpreferences.getString("profile_pic", "");
            navigationDrawer();
            getUserPhotosService();
            imgProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    zoomImageFromThumb(imgProfilePic);
                }
            });
        } else {
            uId = intent.getStringExtra("u_id");
            email = intent.getStringExtra("email");
            phone = intent.getStringExtra("phone");
            profileImage = intent.getStringExtra("profile_pic");
            Glide.with(this)
                    .load(Textclass.profilePic + intent.getStringExtra("profile_pic")).override(100, 100).centerCrop().placeholder(R.drawable.avtar1)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(imgProfilePic);
            //     Glide.with(this).load(Textclass.profilePic + intent.getStringExtra("profile_pic")).bitmapTransform(new CropCircleTransformation(ProfileActivity.this)).diskCacheStrategy(DiskCacheStrategy.ALL).override(100,100).centerCrop().placeholder(R.drawable.avtar).into(imgProfilePic);
            Glide.with(this).load(Textclass.photos + intent.getStringExtra("background_image")).diskCacheStrategy(DiskCacheStrategy.ALL).override(100, 100).centerCrop().placeholder(R.drawable.avtar).into(imgBlur);
            tvName.setText(intent.getStringExtra("f_name") + " " + intent.getStringExtra("l_name"));
            tvAddress.setText(intent.getStringExtra("area") + ", " + intent.getStringExtra("city") + ", " + intent.getStringExtra("state"));
            btnMenu.setVisibility(View.GONE);
            tvEdit.setVisibility(View.GONE);
            tvBack.setVisibility(View.VISIBLE);
            btnContactMe.setVisibility(View.VISIBLE);
            if(intent.getStringExtra("verify_flag").equalsIgnoreCase("0")){
                btnVerify.setVisibility(View.GONE);
            }else {
                btnVerify.setVisibility(View.VISIBLE);
            }
            tvEmail.setVisibility(View.GONE);
            tvPhone.setVisibility(View.GONE);
            btnContactMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contactMeDialog();
                }
            });

            imgProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    zoomImageFromThumb(imgProfilePic);
                }
            });

        }
        itemTHowItsWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=WkrT5om6IQw")));
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog();
            }
        });


        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        relSpeciality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putString("screen", screen);
                b.putString("u_id", uId);
                eBelow.setVisibility(View.GONE);
                sBelow.setVisibility(View.VISIBLE);
                pBelow.setVisibility(View.GONE);
                tvEquipment.setTypeface(typeRegular);
                tvSpeciality.setTypeface(typeBold);
                tvPhotos.setTypeface(typeRegular);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SpecialityFragment resultListFragment = new SpecialityFragment();
                ft.replace(R.id.container, resultListFragment, "Speciality Fragment");
                resultListFragment.setArguments(b);
                ft.commit();
            }
        });

        relEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putString("screen", screen);
                b.putString("u_id", uId);
                eBelow.setVisibility(View.VISIBLE);
                sBelow.setVisibility(View.GONE);
                pBelow.setVisibility(View.GONE);
                tvEquipment.setTypeface(typeBold);
                tvSpeciality.setTypeface(typeRegular);
                tvPhotos.setTypeface(typeRegular);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                equipmentFragment = new EquipmentFragment();
                ft.replace(R.id.container, equipmentFragment, "Equipment Fragment");
                equipmentFragment.setArguments(b);
                ft.commit();
            }
        });

        relPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putString("screen", screen);
                b.putString("u_id", uId);
                eBelow.setVisibility(View.GONE);
                sBelow.setVisibility(View.GONE);
                pBelow.setVisibility(View.VISIBLE);
                tvEquipment.setTypeface(typeRegular);
                tvSpeciality.setTypeface(typeRegular);
                tvPhotos.setTypeface(typeBold);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                photosFragment = new PhotosFragment();
                ft.replace(R.id.container, photosFragment, "Photos Fragment");
                photosFragment.setArguments(b);
                ft.commit();
            }
        });

        Bundle b = new Bundle();
        b.putString("screen", screen);
        b.putString("u_id", uId);
        tvEquipment.setTypeface(typeRegular);
        tvSpeciality.setTypeface(typeBold);
        tvPhotos.setTypeface(typeRegular);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SpecialityFragment resultListFragment = new SpecialityFragment();
        ft.replace(R.id.container, resultListFragment, "Speciality Fragment");
        resultListFragment.setArguments(b);
        ft.commit();

    }

    public void getUserPhotosService() {

        String url = Textclass.baseurl + "get_user_photo.php?api_key=ZmFw&u_id=" + uId;
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(ProfileActivity.this, url, new AsyncHttpResponseHandler() {

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
                        Glide.with(ProfileActivity.this).load(Textclass.photos + jsonArray.getJSONObject(0).getString("photos")).diskCacheStrategy(DiskCacheStrategy.ALL).override(100, 100).centerCrop().placeholder(R.drawable.avtar).into(imgBlur);

                    } else if (status == 0) {
                     //   Toast.makeText(ProfileActivity.this, getString(R.string.no_data), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(ProfileActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    utility.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(ProfileActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                utility.closeProgressDialog();
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
        itemExit = new ResideMenuItem(this, R.drawable.exit, "Exit");
        resideMenu.addMenuItem(itemExit, ResideMenu.DIRECTION_LEFT);

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

                resideMenu.closeMenu();

            }
        });


        itemContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, FeedbackActivity.class);
                startActivity(intent);
                finish();
            }
        });
        itemSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, PhotographerListActivity.class);
                startActivity(intent);
                finish();
                resideMenu.closeMenu();
            }
        });

        itemFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProfileActivity.this, FilterActivity.class);
                startActivity(intent);
                finish();
                resideMenu.closeMenu();
            }
        });

        itemInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, InfoActivity.class);
                startActivity(intent);
                finish();
            }
        });

        itemTAndC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, TandCActivity.class);
                startActivity(intent);
                finish();
            }
        });

        itemExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(ProfileActivity.this).setMessage("Are you sure you want to logout?").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
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

    public void showEditDialog() {
        stateFlag = false;
        cityFlag = false;
        areaFlag = false;
        countryFlag = false;
        final android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.dialog_edit_profile, null);
        edtFirstName = (EditTextK) subView.findViewById(R.id.edt_first_name);
        tvLastName = (EditTextK) subView.findViewById(R.id.edt_last_name);
        tvPhoneName = (EditTextK) subView.findViewById(R.id.edt_phone);
        edtPassword = (EditTextK) subView.findViewById(R.id.edt_password);
        imgProfilePicDialog = (ImageView) subView.findViewById(R.id.profile_image);
        btnDone = (TypefaceTextView) subView.findViewById(R.id.btn_done);
        spState = (Spinner) subView.findViewById(R.id.sp_state);
        spCity = (Spinner) subView.findViewById(R.id.sp_city);
        spArea = (Spinner) subView.findViewById(R.id.sp_area);
        spCountry = (Spinner) subView.findViewById(R.id.sp_country);
        edtState = (TypefaceTextView) subView.findViewById(R.id.edt_state);
        edtCity = (TypefaceTextView) subView.findViewById(R.id.edt_city);
        edtArea = (TypefaceTextView) subView.findViewById(R.id.edt_area);
        edtCompanyName = (EditTextK) subView.findViewById(R.id.edt_company_name);
        edtAddress = (EditTextK) subView.findViewById(R.id.edt_address);
        edtCountry = (TypefaceTextView) subView.findViewById(R.id.edt_country);

        Glide.with(this).load(Textclass.profilePic + sharedpreferences.getString("profile_pic", "")).override(100, 100).centerCrop().bitmapTransform(new CropCircleTransformation(this)).placeholder(R.drawable.avtar1).into(imgProfilePicDialog);
        edtFirstName.setText(sharedpreferences.getString("f_name", ""));
        tvLastName.setText(sharedpreferences.getString("l_name", ""));
        tvPhoneName.setText(sharedpreferences.getString("phone", ""));
        edtPassword.setText(sharedpreferences.getString("password", ""));
        edtAddress.setText(sharedpreferences.getString("address", ""));
        edtArea.setText(sharedpreferences.getString("area", ""));
        edtCity.setText(sharedpreferences.getString("city", ""));
        edtCompanyName.setText(sharedpreferences.getString("company_name", ""));
        edtCountry.setText(sharedpreferences.getString("country", ""));
        edtState.setText(sharedpreferences.getString("state", ""));


        stateList = new ArrayList<>();
        stateList.add("Select State");
        stateList.add("Tamil nadu");
        countryList = new ArrayList<String>();
        countryList.add("Select Country");
        countryList.add("India");

        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("areaList.csv");

        } catch (IOException e) {
            e.printStackTrace();
        }
        CSVFile csvFile = new CSVFile(inputStream);
        areaList = csvFile.read();

        areaNameList = new ArrayList<String>();
        areaNameList.add("Select area");
        for(int i=0;i<areaList.size();i++){
            areaNameList.add(areaList.get(i)[0]);
        }
        /*areaNameList.add("Adambakkam");
        areaNameList.add("Choolai");
        areaNameList.add("Padi");
        areaNameList.add("St.Thomas Mount");
        areaNameList.add("Red Hills");*/

        latitudeList = new ArrayList<String>();
        latitudeList.add("12.99");
        latitudeList.add("12.99");
        latitudeList.add("13.089657");
        latitudeList.add("13.1037");
        latitudeList.add("13.005056");
        latitudeList.add("13.166667");


        longitudeList = new ArrayList<String>();
        longitudeList.add("80.2");
        longitudeList.add("80.2");
        longitudeList.add("80.267998");
        longitudeList.add("80.1947");
        longitudeList.add("80.193306");
        longitudeList.add("80.171528");


        ArrayAdapter<String> adapterArea = new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, areaNameList);
        spArea.setAdapter(adapterArea);
        spArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                areaPosition = i;

                if (!areaFlag) {
                    areaPosition = areaNameList.indexOf(edtArea.getText().toString().trim());
                    edtArea.setText(sharedpreferences.getString("area", ""));
                    areaFlag = true;
                } else {
                    edtArea.setText(areaNameList.get(i));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        edtArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtCity.getText().toString().equalsIgnoreCase("Chennai"))
                spArea.performClick();
            }
        });

        ArrayAdapter<String> adapterCountry = new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, countryList);
        spCountry.setAdapter(adapterCountry);
        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (!countryFlag) {
                    edtCountry.setText(sharedpreferences.getString("country", ""));
                    countryFlag = true;
                } else {
                    edtCountry.setText(countryList.get(i));
                }

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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, stateList);
        spState.setAdapter(adapter);
        spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (!stateFlag) {
                    edtState.setText(sharedpreferences.getString("state", ""));
                    stateFlag = true;
                } else {
                    edtState.setText(stateList.get(i));
                }
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

        edtCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spCity.performClick();
            }
        });


        citiesList = new ArrayList<String>();
        citiesList.add("Select city");

        JSONArray obj = null;
        try {
            //parsinng json from json file in assests

            // http://stackoverflow.com/questions/19945411/android-java-how-can-i-parse-a-local-json-file-from-assets-folder-into-a-listvi

            obj = new JSONArray(utility.loadJSONFromAsset());
            cityList = new ArrayList<String>();
            for (int j = 0; j < obj.length(); j++) {
                JSONObject json = obj.getJSONObject(j);
                if (json.getString("state").equalsIgnoreCase("Tamil Nadu")) {
                    if (json.getString("name").equalsIgnoreCase("Chennai")) {
                        citiesList.add(json.getString("name"));
                    } /*else {
                        cityList.add(json.getString("name"));
                    }*/
                }
            }
            Collections.sort(cityList);
            citiesList.addAll(cityList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, citiesList);
        spCity.setAdapter(adapterCity);
        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                if (!cityFlag) {
                    edtCity.setText(sharedpreferences.getString("city", ""));
                    cityFlag = true;
                } else {
                    edtCity.setText(citiesList.get(i));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (utility.isNetworkAvailable()) {
                    if (edtFirstName.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(ProfileActivity.this, "Enter first name", Toast.LENGTH_SHORT).show();
                    } else if (tvLastName.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(ProfileActivity.this, "Enter last name", Toast.LENGTH_SHORT).show();
                    } else if (tvPhoneName.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(ProfileActivity.this, "Enter phone", Toast.LENGTH_SHORT).show();
                    } else if (tvPhoneName.getText().toString().length() != 10) {
                        Toast.makeText(ProfileActivity.this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
                    } else if (edtCompanyName.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(ProfileActivity.this, "Enter company name", Toast.LENGTH_SHORT).show();
                    } else if (edtAddress.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(ProfileActivity.this, "Enter address", Toast.LENGTH_SHORT).show();
                    } else if (edtState.getText().toString().equalsIgnoreCase("Select State")) {
                        Toast.makeText(ProfileActivity.this, "Select State", Toast.LENGTH_SHORT).show();
                    } else if (edtCity.getText().toString().equalsIgnoreCase("Select city")) {
                        Toast.makeText(ProfileActivity.this, "Select city", Toast.LENGTH_SHORT).show();
                    } else if (edtArea.getText().toString().equalsIgnoreCase("Select area")) {
                        Toast.makeText(ProfileActivity.this, "Select area", Toast.LENGTH_SHORT).show();
                    } else if (edtCountry.getText().toString().equalsIgnoreCase("Select Country")) {
                        Toast.makeText(ProfileActivity.this, "Enter country", Toast.LENGTH_SHORT).show();
                    } else {
                        updateUserService();
                    }


                } else {
                    Toast.makeText(ProfileActivity.this, getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                }

            }
        });

        imgProfilePicDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.create(ProfileActivity.this)
                        .folderMode(true) // folder mode (false by default)
                        //    .folderTitle("Folder") // folder selection title
                        .imageTitle("Tap to select") // image selection title
                        .single() // single mode
                        //    .multi() // multi mode (default mode)
                        //    .limit(10) // max images can be selected (999 by default)
                        .showCamera(true) // show camera or not (true by default)
                        .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                        //      .origin(images) // original selected images, used in multi mode
                        .start(2); // start image picker activity with request code
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setView(subView);

        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void contactMeDialog() {


        final android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.dialog_contact_me, null);
        relEmail = (RelativeLayout) subView.findViewById(R.id.rel_email);
        relPhone = (RelativeLayout) subView.findViewById(R.id.rel_phone);
        TypefaceTextView tvDisplayEmail = (TypefaceTextView) subView.findViewById(R.id.tv_display_email);
        TypefaceTextView tvDisplayPhone = (TypefaceTextView) subView.findViewById(R.id.tv_display_phone);

        tvDisplayEmail.setText(email);
        tvDisplayPhone.setText("+91" + phone);

        relEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });

        relPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                    //     return;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                    }

                } else {
                    alertDialog.dismiss();
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                    if (ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(intent);
                }


            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setView(subView);

        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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

                alertDialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                if (ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(intent);

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
                alertDialog.dismiss();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }


    }

    public void updateUserService() {

        String url = Textclass.baseurl + "user_profile.php";
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.add("api_key", "ZmFw");
        requestParams.add("u_id", uId);
        requestParams.add("fname", edtFirstName.getText().toString().trim());
        requestParams.add("lname", tvLastName.getText().toString().trim());
        requestParams.add("u_password", edtPassword.getText().toString().trim());
        requestParams.add("u_phone", tvPhoneName.getText().toString().trim());
        requestParams.add("u_company_name", edtCompanyName.getText().toString().trim());
        requestParams.add("u_address", edtAddress.getText().toString().trim());
        requestParams.add("u_state", edtState.getText().toString().trim());
        requestParams.add("u_city", edtCity.getText().toString().trim());
        requestParams.add("u_area", edtArea.getText().toString().trim());
        requestParams.add("u_country", edtCountry.getText().toString().trim());
        /*requestParams.add("u_lat", latitudeList.get(areaPosition));
        requestParams.add("u_long", longitudeList.get(areaPosition));*/
        requestParams.add("u_lat",areaList.get(areaPosition-1)[1]);
        requestParams.add("u_long", areaList.get(areaPosition-1)[2]);


        //  File imageFile=new File(imagePath);

        if (imageFile != null && imageFile.exists()) {
            try {
                requestParams.put("profile_pic", imageFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            requestParams.put("profile_pic", "");
        }


        asyncHttpClient.post(ProfileActivity.this, url, requestParams, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                utility.showProgressDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.d("Success", statusCode + "" + responseBody);
                Toast.makeText(ProfileActivity.this, getString(R.string.update_successfully), Toast.LENGTH_SHORT).show();
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
                            //editor.putString("email", model.getEmail());
                            editor.putString("area", model.getArea());
                            editor.putString("address", model.getAddress());
                            editor.putString("country", model.getCountry());
                            editor.putString("company_name", model.getCompanyName());
                            editor.putString("lati", model.getLati());
                            editor.putString("longi", model.getLongi());
                            editor.commit();

                            if (imagePath != null) {
                                Glide.with(ProfileActivity.this).load(new File(imagePath)).centerCrop().bitmapTransform(new CropCircleTransformation(ProfileActivity.this)).placeholder(R.drawable.avtar1).into(imgProfilePic);
                            } else {
                                Glide.with(ProfileActivity.this).load(Textclass.profilePic + profileImage).centerCrop().bitmapTransform(new CropCircleTransformation(ProfileActivity.this)).placeholder(R.drawable.avtar1).into(imgProfilePic);
                            }
                            //   Glide.with(ProfileActivity.this).load(Textclass.profilePic + model.getProfilePic()).override(100, 100).centerCrop().bitmapTransform(new CropCircleTransformation(ProfileActivity.this)).placeholder(R.drawable.avtar).into(imgProfilePic);
                            tvName.setText(model.getFirstName() + " " + model.getLastName());
                            tvAddress.setText(model.getArea() + ", " + model.getCity() + ", " + model.getState());
                            tvPhone.setText("+91"+model.getPhone());
                        }
                    } else if (status == 0) {
                        Toast.makeText(ProfileActivity.this, getString(R.string.no_data), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(ProfileActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    utility.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(ProfileActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                utility.closeProgressDialog();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            // do your logic ....
            imagePath = images.get(0).getPath();
            imageFile = new File(imagePath);
            //    Glide.with(this).load(new File(imagePath)).into(imgProfilePic);
            Glide.with(this).load(new File(imagePath)).centerCrop().bitmapTransform(new CropCircleTransformation(this)).into(imgProfilePicDialog);

        } else if (resultCode == 0 && data == null) {
            super.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == 32459) {
            equipmentFragment.onActivityResult(requestCode, resultCode, data);
        } else {
            photosFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (screen.equalsIgnoreCase("profile")) {
            if (expandedImageView.getVisibility() == View.VISIBLE) {
                expandedImageView.performClick();
            } else {
                getExitDialog();
            }
        } else {
            if (pager.getVisibility() == View.VISIBLE) {
                pager.setVisibility(View.GONE);
            } else if (expandedImageView.getVisibility() == View.VISIBLE) {
                expandedImageView.performClick();
            } else {
                super.onBackPressed();
            }
        }
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

    private void zoomImageFromThumb(final View thumbView) {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        Glide.with(this)
                .load(Textclass.profilePic + profileImage).thumbnail(0.1f).placeholder(R.drawable.avtar1)
                .into(expandedImageView);

        // Calculate the starting and ending bounds for the zoomed-in image. This step
        // involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail, and the
        // final bounds are the global visible rectangle of the container view. Also
        // set the container view's offset as the origin for the bounds, since that's
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.activity_profile).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final bounds using the
        // "center crop" technique. This prevents undesirable stretching during the animation.
        // Also calculate the start scaling factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation begins,
        // it will position the zoomed-in view in the place of the thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
        // the zoomed-in view (the default is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left,
                        finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top,
                        finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down to the original bounds
        // and show the thumbnail instead of the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel, back to their
                // original values.
                AnimatorSet set = new AnimatorSet();
                set
                        .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

    private void loginService() {

        String url = Textclass.baseurl + "verify_user.php?api_key=ZmFw&u_id=" + sharedpreferences.getString("u_id", "") + "";
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(ProfileActivity.this, url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
//                utility.showProgressDialog();

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


                            editor.putString("verify_flag",jsonObject1.getString("verify_flag"));

                            editor.commit();
                            Log.e("vefify_flag",jsonObject1.getString("verify_flag"));
                            if(jsonObject1.getString("verify_flag").equalsIgnoreCase("0")){
                                btnVerify.setVisibility(View.GONE);
                            }else {
                                btnVerify.setVisibility(View.VISIBLE);
                            }



                        }


                    }

                } catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(ProfileActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();


            }
        });

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        SpecialityFragment specialityFragment=new SpecialityFragment();
        PhotosFragment photosFragment=new PhotosFragment();
        EquipmentFragment equipmentFragment=new EquipmentFragment();

        Bundle b = new Bundle();
        b.putString("screen", screen);
        b.putString("u_id", uId);
        specialityFragment.setArguments(b);
        photosFragment.setArguments(b);
        equipmentFragment.setArguments(b);
        adapter.addFragment(specialityFragment, "Speciality");
        adapter.addFragment(equipmentFragment, "Equipment");
        adapter.addFragment(photosFragment, "Phonto");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
