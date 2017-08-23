package com.thunderbird.chennai.fapapp.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bumptech.glide.Glide;
import com.thunderbird.chennai.fapapp.Fragments.EquipmentFragments;
import com.thunderbird.chennai.fapapp.Model.EqpModel;
import com.thunderbird.chennai.fapapp.Model.Model;
import com.thunderbird.chennai.fapapp.Model.SpModel;
import com.thunderbird.chennai.fapapp.R;
import com.thunderbird.chennai.fapapp.Utility.CSVFile;
import com.thunderbird.chennai.fapapp.Utility.GlobalVariables;
import com.thunderbird.chennai.fapapp.Utility.Textclass;
import com.thunderbird.chennai.fapapp.Utility.TypefaceTextView;
import com.thunderbird.chennai.fapapp.Utility.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

import static java.lang.Integer.parseInt;

public class FilterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, BillingProcessor.IBillingHandler {

    private ImageView navBar;
    private ResideMenu resideMenu;
    private ResideMenuItem itemProfile;
    private ResideMenuItem itemSearch;
    private ResideMenuItem itemFilter;
    private ResideMenuItem itemInfo;
    private ResideMenuItem itemTAndC;
    private ResideMenuItem itemExit;
    private ResideMenuItem itemTHowItsWork;
    private ResideMenuItem itemContactUs;
    private TypefaceTextView popUpDateTimePicker, popUpDateTimePicker2;
    private TypefaceTextView tvDate, tvDate2;
    private ArrayList<String> citiesList;
    private Switch switchCity;
    int flag;
    private Utility utility;
    private Switch switchState;
    private ImageView btnDropdown, btnDropdown2;
    private Spinner spSession, spSession2;
    private TypefaceTextView tvSessionShow, tvSessionShow2;
    private HorizontalScrollView hScrollSp;
    private HorizontalScrollView hScrollEquip;
    private LinearLayout topLinearLayoutSp;
    private LinearLayout topLinearLayoutEquip;
    //  private ArrayList<EqpModel> equipmentList;
    private ArrayList<SpModel> userSpecialityList;
    private String multiSelectedId;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private String uId;
    private ArrayList<EqpModel> userEquipmentList;
    private TypefaceTextView tvStateShow;
    private TypefaceTextView tvCityShow;
    private TypefaceTextView tvAreaShow;
    private RelativeLayout btnSearch;
    private ArrayList<Model> searchList;
    private int equipPosition;
    private TypefaceTextView tvNameRef;
    private RelativeLayout tvRelImageCandid1Ref;
    private BillingProcessor bp;
    private String transactionId;
    private String multiSelectedTransactionId;
    private String specialityID;
    private String speciality;
    private String equipment;
    private String transactionIds;
    private ArrayList<String> cityList;
    private TypefaceTextView resetFilter;
    private Switch switchArea;
    private ArrayList<String> areaNameList;
    private ArrayList<String> latitudeList;
    private ArrayList<String> longitudeList;
    private String lati;
    private String longi;
    private boolean sessionChecked, sessionChecked2;
    private String filterState;
    private String filterCity;
    private String filterArea;
    private String filterDate, filterDate2;
    private String filterSession, filterSession2;
    private String filterSpeciality;
    private String filterEquipment;
    private List<String[]> areaList;
    EditText edtSelectSpeciality, edtSelectEquipment;
    List<String> selectedSpecialityList = new ArrayList<String>();
    List<String> selectedEquipmentList = new ArrayList<String>();
    CharSequence[] reader_options, reader_options_equipment;
    boolean[] reader_selections, reader_selections_equipment;
    String selectedSpecialityId, selectedEquipmentId = "";
    List<String> spinnerSpecialityList = new ArrayList<String>();
    List<String> spinnerEquipmentList = new ArrayList<String>();
    private List<String[]> cityList2;

    //  private ArrayList<SpModel> specialityList;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        utility = new Utility(this);
        bp = new BillingProcessor(this, Textclass.base64, this);
        sharedpreferences = getSharedPreferences("myprefrence", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        uId = sharedpreferences.getString("u_id", "");
        lati = sharedpreferences.getString("lati", "");
        longi = sharedpreferences.getString("longi", "");
        filterState = sharedpreferences.getString("filter_state", "");
        filterCity = sharedpreferences.getString("filter_city", "");
        filterArea = sharedpreferences.getString("filter_area", "");
        filterDate = sharedpreferences.getString("filter_date", "");
        filterDate2 = sharedpreferences.getString("filter_date2", "");
        filterSession = sharedpreferences.getString("filter_session", "");
        filterSession2 = sharedpreferences.getString("filter_session2", "");
        filterSpeciality = sharedpreferences.getString("filter_speciality", "");
        filterEquipment = sharedpreferences.getString("filter_equipment", "");
        navBar = (ImageView) findViewById(R.id.nav_bar);
        popUpDateTimePicker = (TypefaceTextView) findViewById(R.id.pop_up_date);
        popUpDateTimePicker2 = (TypefaceTextView) findViewById(R.id.pop_up_date2);
        tvDate = (TypefaceTextView) findViewById(R.id.tv_date);
        tvDate2 = (TypefaceTextView) findViewById(R.id.tv_date2);
        switchCity = (Switch) findViewById(R.id.switch_city);
        switchState = (Switch) findViewById(R.id.switch_state);
        switchArea = (Switch) findViewById(R.id.switch_area);
        btnDropdown = (ImageView) findViewById(R.id.btn_down);
        btnDropdown2 = (ImageView) findViewById(R.id.btn_down2);
        tvStateShow = (TypefaceTextView) findViewById(R.id.tv_state_show);
        tvCityShow = (TypefaceTextView) findViewById(R.id.tv_city_show);
        tvAreaShow = (TypefaceTextView) findViewById(R.id.tv_area_show);
        resetFilter = (TypefaceTextView) findViewById(R.id.tv_reset_filter);
        citiesList = new ArrayList<String>();
        edtSelectSpeciality = (EditText) findViewById(R.id.edtSelectSpeciality);
        edtSelectEquipment = (EditText) findViewById(R.id.edtSelectEquipment);
        spSession = (Spinner) findViewById(R.id.sp_session);
        spSession2 = (Spinner) findViewById(R.id.sp_session2);
        tvSessionShow = (TypefaceTextView) findViewById(R.id.tv_session_show);
        tvSessionShow2 = (TypefaceTextView) findViewById(R.id.tv_session_show2);
        hScrollSp = (HorizontalScrollView) findViewById(R.id.h_scroll_sp);
        hScrollEquip = (HorizontalScrollView) findViewById(R.id.h_scroll_equip);
        btnSearch = (RelativeLayout) findViewById(R.id.btn_search);
        topLinearLayoutSp = new LinearLayout(this);
        topLinearLayoutSp.setBackgroundColor(Color.parseColor("#E9EDF2"));
        topLinearLayoutSp.setOrientation(LinearLayout.HORIZONTAL);
        topLinearLayoutEquip = new LinearLayout(this);
        topLinearLayoutEquip.setBackgroundColor(Color.parseColor("#E9EDF2"));
        topLinearLayoutEquip.setOrientation(LinearLayout.HORIZONTAL);

        if (utility.isNetworkAvailable()) {
            getEquipmentList();
        } else {
            Toast.makeText(FilterActivity.this, getString(R.string.internet_message), Toast.LENGTH_SHORT).show();

        }


        tvStateShow.setText(filterState);
        if (!filterState.equalsIgnoreCase("")) {
            switchState.setChecked(true);
        }
        tvCityShow.setText(filterCity);
        if (!filterCity.equalsIgnoreCase("")) {
            switchCity.setChecked(true);
        }
        tvAreaShow.setText(filterArea);
        if (!filterArea.equalsIgnoreCase("")) {
            switchArea.setChecked(true);
        }

        tvDate.setText(filterDate);
        tvDate2.setText(filterDate2);
        tvSessionShow.setText(filterSession);
        tvSessionShow2.setText(filterSession2);
        edtSelectSpeciality.setText(filterSpeciality);
        edtSelectEquipment.setText(filterEquipment);


        /*areaNameList.add("Adambakkam");
        areaNameList.add("Choolai");
        areaNameList.add("Padi");
        areaNameList.add("St.Thomas Mount");
        areaNameList.add("Red Hills");*/


        final ArrayList<String> sessionList = new ArrayList<>();
        sessionList.add("Select session1");
        sessionList.add("Morning");
        sessionList.add("Afternoon");
        sessionList.add("Evening");


        final ArrayList<String> sessionList2 = new ArrayList<>();
        sessionList2.add("Select session2");
        sessionList2.add("Morning");
        sessionList2.add("Afternoon");
        sessionList2.add("Evening");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sessionList);
        spSession.setAdapter(arrayAdapter);

        final ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sessionList2);
        spSession2.setAdapter(arrayAdapter2);


        Textclass.specialityList.clear();
        Textclass.equipmentList.clear();
        if (!filterSpeciality.equalsIgnoreCase("")) {
            String[] sList = filterSpeciality.split(",");
            Textclass.specialityList = new ArrayList<String>(Arrays.asList(sList));
        }

        if (!filterEquipment.equalsIgnoreCase("")) {
            String[] sList = filterEquipment.split(",");
            Textclass.equipmentList = new ArrayList<String>(Arrays.asList(sList));
        }


        edtSelectSpeciality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateD(1);
            }
        });

        edtSelectEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateD(2);
            }
        });

        resetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editor.putString("filter_state", "");
                editor.putString("filter_city", "");
                editor.putString("filter_area", "");
                editor.putString("filter_date", "");
                editor.putString("filter_date2", "");
                editor.putString("filter_session", "");
                editor.putString("filter_session2", "");
                editor.putString("filter_speciality", "");
                editor.putString("filter_equipment", "");
                editor.putString("filter_speciality_id", "");
                editor.putString("filter_equipment_id", "");
                editor.putString("filter_speciality_list", "");
                editor.putString("filter_equipment_list", "");
                editor.commit();
                // Textclass.stateName = "";
                // Textclass.cityName = "";
                // Textclass.areaName = "";
                //  Textclass.date = "";
                //  Textclass.session = "";
                tvStateShow.setText("");
                edtSelectSpeciality.setText("");
                edtSelectEquipment.setText("");
                tvCityShow.setText("");
                tvAreaShow.setText("");
                tvDate.setText("");
                tvDate2.setText("");
                tvSessionShow.setText("Select session1");
                tvSessionShow2.setText("Select session2");
                switchState.setChecked(false);
                switchCity.setChecked(false);
                switchArea.setChecked(false);

                if (utility.isNetworkAvailable()) {
                    Textclass.specialityList.clear();
                    Textclass.equipmentList.clear();
//                    hScrollSp.removeAllViews();
//                    hScrollEquip.removeAllViews();
//                    topLinearLayoutSp.removeAllViews();
//                    topLinearLayoutEquip.removeAllViews();

                } else {
                    Toast.makeText(FilterActivity.this, getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                }

            }
        });

        tvSessionShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spSession.performClick();
            }
        });

        tvSessionShow2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spSession2.performClick();
            }
        });

        spSession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    if (filterSession.equalsIgnoreCase("")) {
                        tvSessionShow.setText(sessionList.get(i));
                        sessionChecked = false;
                    } else {
                        tvSessionShow.setText(filterSession);
                    }
                } else {
                    tvSessionShow.setText(sessionList.get(i));
                    filterSession = sessionList.get(i);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spSession2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    if (filterSession2.equalsIgnoreCase("")) {
                        tvSessionShow2.setText(sessionList2.get(i));
                        sessionChecked2 = false;
                    } else {
                        tvSessionShow2.setText(filterSession2);
                    }
                } else {
                    tvSessionShow2.setText(sessionList2.get(i));
                    filterSession2 = sessionList2.get(i);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        tvSessionShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spSession.performClick();
            }
        });


        switchState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    stateDialog();
                } else {
                    tvStateShow.setText("");
                    tvCityShow.setText("");
                    tvAreaShow.setText("");
                    editor.putString("filter_state", "");
                    editor.putString("filter_city", "");
                    editor.putString("filter_area", "");
                    editor.commit();
                    //   Textclass.stateName = "";
                    //   Textclass.cityName = "";
                    //   Textclass.areaName = "";
                    switchCity.setChecked(false);
                    switchArea.setChecked(false);
                }

            }
        });

        switchArea.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (tvStateShow.getText().toString().equalsIgnoreCase("") || tvCityShow.getText().toString().equalsIgnoreCase("")) {
                    switchArea.setChecked(false);
                    return;
                }

                if (b) {
                    InputStream inputStream = null;
                    try {
                        inputStream = getAssets().open("areaLists.csv");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    CSVFile csvFile = new CSVFile(inputStream);
                    areaList = csvFile.read();


                    areaNameList = new ArrayList<String>();

                    for (int i = 0; i < areaList.size(); i++) {

                        if(areaList.get(i)[1].contains(tvCityShow.getText().toString())){
                            areaNameList.add(areaList.get(i)[2]);

                        }

                    }

                    areaDialog();
                } else {
                    tvAreaShow.setText("");
                    //  Textclass.areaName = "";
                }
            }
        });

        switchCity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (tvStateShow.getText().toString().equalsIgnoreCase("")) {
                    switchCity.setChecked(false);
                    return;
                }

                if (b) {
                    JSONArray obj = null;
                    try {
                        InputStream inputStream1 = null;
                        try {
                            inputStream1 = getAssets().open("areaLists.csv");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        CSVFile csvFile1 = new CSVFile(inputStream1);
                        cityList2 = csvFile1.read();
                        citiesList = new ArrayList<String>();

                        for (int i = 0; i < cityList2.size(); i++) {
                            if (i == 0) {
                                citiesList.add(cityList2.get(i)[1]);
                            }else if (!citiesList.contains(cityList2.get(i)[1])) {
                                    citiesList.add(cityList2.get(i)[1]);
                                }
                            }
                        cityDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    tvCityShow.setText("");
                    tvAreaShow.setText("");

                    editor.putString("filter_city", "");
                    editor.putString("filter_area", "");
                    editor.commit();

                    //  Textclass.cityName = "";
                    //  Textclass.areaName = "";
                    switchArea.setChecked(false);
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                speciality = "";
                equipment = "";
                transactionIds = "";

                if (tvStateShow.getText().toString().equalsIgnoreCase("") && tvCityShow.getText().toString().equalsIgnoreCase("") && tvAreaShow.getText().toString().trim().equalsIgnoreCase("") && tvSessionShow.getText().toString().equalsIgnoreCase("Select session1") && tvSessionShow2.getText().toString().equalsIgnoreCase("Select session2") && tvDate.getText().toString().equalsIgnoreCase("") && tvDate2.getText().toString().equalsIgnoreCase("") && speciality.equalsIgnoreCase("") && equipment.equalsIgnoreCase("")) {
                    Toast.makeText(FilterActivity.this, "Please enter any one field", Toast.LENGTH_SHORT).show();
                } else {

                    editor.putString("filter_session", tvSessionShow.getText().toString());
                    editor.putString("filter_session2", tvSessionShow2.getText().toString());
                    editor.putString("filter_speciality", edtSelectSpeciality.getText().toString());
                    editor.putString("filter_equipment", edtSelectEquipment.getText().toString());
                    editor.putString("filter_speciality_id", sharedpreferences.getString("filter_speciality_id", ""));
                    editor.putString("filter_equipment_id", sharedpreferences.getString("filter_equipment_id", ""));
                    editor.putString("filter_state", tvStateShow.getText().toString());
                    editor.putString("filter_area", tvAreaShow.getText().toString());
                    editor.putString("filter_city", tvCityShow.getText().toString());
                    editor.putString("filter_date", tvDate.getText().toString());
                    editor.putString("filter_date2", tvDate2.getText().toString());
                    editor.commit();
//
//                    Set<String> setSpecialityList = new HashSet<String>();
//                    setSpecialityList.addAll(selectedSpecialityList);
//                    editor.putStringSet("filter_speciality_list", setSpecialityList);
//
//                    Set<String> setEquipmentList = new HashSet<String>();
//                    setEquipmentList.addAll(selectedEquipmentList);
//                    editor.putStringSet("filter_equipment_list", setEquipmentList);
//                    editor.commit();


                    Intent intent = new Intent(FilterActivity.this, PhotographerListActivity.class);
                    intent.putExtra("u_state", tvStateShow.getText().toString().trim());
                    intent.putExtra("u_city", tvCityShow.getText().toString().trim());
                    intent.putExtra("u_area", tvAreaShow.getText().toString().trim());
                    if (tvSessionShow.getText().toString().equalsIgnoreCase("Select session1")) {
                        intent.putExtra("u_session", "");
                    } else {
                        intent.putExtra("u_session", tvSessionShow.getText().toString());
                    }

                    if (tvSessionShow2.getText().toString().equalsIgnoreCase("Select session2")) {
                        intent.putExtra("u_session2", "");
                    } else {
                        intent.putExtra("u_session2", tvSessionShow2.getText().toString());
                    }
                    intent.putExtra("u_date", tvDate.getText().toString().trim());
                    intent.putExtra("u_date2", tvDate2.getText().toString().trim());
                    intent.putExtra("u_speciality", sharedpreferences.getString("filter_speciality_id", ""));
                    intent.putExtra("u_equipment", sharedpreferences.getString("filter_equipment_id", ""));
                    startActivity(intent);
                    finish();
                }

            }
        });


        Calendar now = Calendar.getInstance();
        final DatePickerDialog dpd = DatePickerDialog.newInstance(
                FilterActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setAccentColor(getResources().getColor(R.color.colorAccent));

        navigationDrawer();

        navBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });


        popUpDateTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flag = 1;
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        popUpDateTimePicker2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 2;
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });


        //  final String[] namesArr = citiesList.toArray(new String[citiesList.size()]);
    }


    @Override
    protected void onStop() {
        super.onStop();
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
                    paddingBottom += resources.getDimensionPixelSize(resourceId);
                    ;
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

        itemContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterActivity.this, FeedbackActivity.class);
                startActivity(intent);
                finish();
            }
        });
        itemProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterActivity.this, ProfileActivitys.class);
                intent.putExtra("screen", "profile");
                startActivity(intent);
                finish();
            }
        });

        itemSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterActivity.this, PhotographerListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        itemFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.closeMenu();


            }
        });

        itemInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterActivity.this, InfoActivity.class);
                startActivity(intent);
                finish();
            }
        });

        itemTAndC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterActivity.this, InfoActivity.class);
                startActivity(intent);
                finish();
            }
        });

        itemExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(FilterActivity.this).setMessage("Are you sure you want to logout?").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(FilterActivity.this, LoginActivity.class);
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

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("cities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        if (flag == 1) {
            tvDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            filterDate = tvDate.getText().toString();
        } else {
            tvDate2.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            filterDate2 = tvDate2.getText().toString();
        }

    }


    public void cityDialog() {
        int position;
        if (filterCity.equalsIgnoreCase("")) {
            position = 0;
        } else {
            position = citiesList.indexOf(filterCity);
        }

        new AlertDialog.Builder(FilterActivity.this).setTitle("Select city")
                .setSingleChoiceItems(citiesList.toArray(new String[citiesList.size()]), position, null)

                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int whichButton) {

                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        filterCity = citiesList.get(selectedPosition);

                        tvCityShow.setText(citiesList.get(selectedPosition));
                        //      switchCity.setChecked(false);
                        dialog.dismiss();
                    }
                })
                .show();


    }

    private void areaDialog() {
        String[] stockArr = new String[areaNameList.size()];
        stockArr = areaNameList.toArray(stockArr);
        new AlertDialog.Builder(FilterActivity.this).setTitle("Select area")
                .setSingleChoiceItems(stockArr, 0, null)

                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        //  switchCity.setChecked(false);

                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        filterArea = areaNameList.get(selectedPosition);
                        tvAreaShow.setText(areaNameList.get(selectedPosition));
                        dialog.dismiss();
                    }
                })
                .show();
    }


    public void stateDialog() {
        new AlertDialog.Builder(FilterActivity.this).setTitle("Select state")
                .setSingleChoiceItems(new String[]{"Tamil nadu"}, 0, null)

                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        //  switchCity.setChecked(false);
                        filterState = "Tamil nadu";
                        //    Textclass.stateName = "Tamil nadu";

                        tvStateShow.setText("Tamil nadu");
                        //   switchState.setChecked(false);
                    }
                })
                .show();
    }


    private void getEquipmentService() {

        String url = Textclass.baseurl + "get_equipment.php?api_key=ZmFw&u_id=" + uId;
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(FilterActivity.this, url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                utility.showProgressDialog();

            }

            @Override
            public void onFinish() {
                super.onFinish();
                utility.closeProgressDialog();
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
                        spinnerEquipmentList.clear();
                        JSONArray jsonArray = json.getJSONArray("Get_data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            spinnerEquipmentList.add(jsonObject1.getString("ep_id") + "-" + jsonObject1.getString("ep_name"));
                        }
                        reader_options_equipment = new CharSequence[spinnerEquipmentList.size()];
                        reader_selections_equipment = new boolean[reader_options_equipment.length];
                        for (int i = 0; i < spinnerEquipmentList.size(); i++) {
                            reader_options_equipment[i] = spinnerEquipmentList.get(i);
                        }
                    } else {
                        reader_options_equipment = new CharSequence[0];
                        reader_selections_equipment = new boolean[0];
                        Toast.makeText(FilterActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Log.e("Exception", "" + e);
                    Toast.makeText(FilterActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    utility.closeProgressDialog();
                    reader_options_equipment = new CharSequence[0];
                    reader_selections_equipment = new boolean[0];
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(FilterActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                utility.closeProgressDialog();
                reader_options_equipment = new CharSequence[0];
                reader_selections_equipment = new boolean[0];
            }
        });
    }

    public void getUserEquipmentService() {

        userEquipmentList = new ArrayList<EqpModel>();

        String url = Textclass.baseurl + "get_user_equipment.php?api_key=ZmFw&u_id=" + uId;
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(FilterActivity.this, url, new AsyncHttpResponseHandler() {

            public RelativeLayout relImageCandid;

            @Override
            public void onStart() {
                super.onStart();
                //  utility.showProgressDialog();

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
                            if (model.getUserHave().equalsIgnoreCase("1") && model.getUserHave().equalsIgnoreCase("0")) {
                                multiSelectedId = multiSelectedId + "," + model.getEpId();
                                multiSelectedTransactionId = multiSelectedTransactionId + "," + model.getTransactionId();
                            }
                            userEquipmentList.add(model);
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            View child = getLayoutInflater().inflate(R.layout.gallary_custom, null);
                            ImageView imgCandid = (ImageView) child.findViewById(R.id.img_custom_candid);
                            TypefaceTextView tvName = (TypefaceTextView) child.findViewById(R.id.tv_name);
                            relImageCandid = (RelativeLayout) child.findViewById(R.id.rel_img_candid);
                            child.setTag(i);

                            if (userEquipmentList.get(i).getUserHave().equalsIgnoreCase("1") && userEquipmentList.get(i).getUserBlock().equalsIgnoreCase("0")) {
                                relImageCandid.setBackgroundColor(getResources().getColor(android.R.color.black));
                                tvName.setTextColor(getResources().getColor(R.color.colorAccent));
                            } else {
                                relImageCandid.setBackgroundColor(getResources().getColor(R.color.lightgrey));
                                tvName.setTextColor(getResources().getColor(android.R.color.black));
                            }
                            Glide.with(FilterActivity.this).load(Textclass.equipment + userEquipmentList.get(i).getEpIcon()).into(imgCandid);
                            tvName.setText(userEquipmentList.get(i).getEpName());

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
                                    if (userEquipmentList.get(equipPosition).getUserHave().equalsIgnoreCase("0") && userEquipmentList.get(equipPosition).getUserBlock().equalsIgnoreCase("0")) {

                                        if (utility.isNetworkAvailable()) {

                                            bp.purchase(FilterActivity.this, "android.test.purchased");
                                            //   bp.purchase(FilterActivity.this, "equipment_pid");
                                        } else {
                                            Toast.makeText(FilterActivity.this, getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });


                            topLinearLayoutEquip.addView(child);
                        }
                        hScrollEquip.addView(topLinearLayoutEquip);
                    } else if (status == 0) {
                        Toast.makeText(FilterActivity.this, getString(R.string.no_data), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(FilterActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Toast.makeText(FilterActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    utility.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(FilterActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

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
                    equipment = userEquipmentList.get(equipPosition).getEpId();
                    //  alertDialog.dismiss();
                    bp.consumePurchase(sku);
                    //  addSubscribedUser();
                    //  loadFragment();
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }
    }

    private void getEquipmentList() {

        String url = Textclass.baseurl + "get_equipment.php?api_key=ZmFw&u_id=" + uId;
        Log.e("check", "" + url);
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(FilterActivity.this, url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                utility.showProgressDialog();

            }

            @Override
            public void onFinish() {
                super.onFinish();
                utility.closeProgressDialog();

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

                        spinnerEquipmentList.clear();
                        JSONArray jsonArray = json.getJSONArray("Get_data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            spinnerEquipmentList.add(jsonObject1.getString("ep_id") + "-" + jsonObject1.getString("ep_name"));
//                            spinnerSpecialityIdList.add(jsonObject1.getString("sp_id"));

                        }

                        reader_options_equipment = new CharSequence[spinnerEquipmentList.size()];
                        reader_selections_equipment = new boolean[reader_options_equipment.length];

                        for (int i = 0; i < spinnerEquipmentList.size(); i++) {
                            reader_options_equipment[i] = spinnerEquipmentList.get(i);

                        }


                    } else {
                        reader_options_equipment = new CharSequence[0];
                        reader_selections_equipment = new boolean[0];
                        Toast.makeText(FilterActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Log.e("Exception", "" + e);
                    Toast.makeText(FilterActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    utility.closeProgressDialog();
                    reader_options_equipment = new CharSequence[0];
                    reader_selections_equipment = new boolean[0];
                }
                getSpecialityService();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(FilterActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                utility.closeProgressDialog();
                reader_options_equipment = new CharSequence[0];
                reader_selections_equipment = new boolean[0];
            }
        });
    }

    private void getSpecialityService() {

        String url = Textclass.baseurl + "get_spaciality_all.php?api_key=ZmFw";
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(FilterActivity.this, url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                utility.showProgressDialog();

            }

            @Override
            public void onFinish() {
                super.onFinish();
                utility.closeProgressDialog();

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
                            reader_options[i] = spinnerSpecialityList.get(i);

                        }


                    } else {
                        reader_options = new CharSequence[0];
                        reader_selections = new boolean[0];
                        Toast.makeText(FilterActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Log.e("Exception", "" + e);
                    Toast.makeText(FilterActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    utility.closeProgressDialog();
                    reader_options = new CharSequence[0];
                    reader_selections = new boolean[0];
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(FilterActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                utility.closeProgressDialog();
                reader_options = new CharSequence[0];
                reader_selections = new boolean[0];
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


    public void onCreateD(int val) {


        // list_des.clear();

        if (val == 1) {


            title = "Select Speciality";
            try {

                Set<String> set = sharedpreferences.getStringSet("filter_speciality_list", null);
                List<String> specialityList = new ArrayList<String>(set);

                for (int i = 0; i < specialityList.size(); i++) {
                    reader_selections[Integer.parseInt(specialityList.get(i))] = true;
                }
            } catch (Exception e) {

            }
            new AlertDialog.Builder(FilterActivity.this, R.style.MyAlertDialogStyle)
                    .setTitle(title)

                    .setMultiChoiceItems(
                            reader_options,
                            reader_selections,
                            new DialogSelectionClickHandler(reader_options,
                                    reader_selections))
                    .setPositiveButton("OK", new DialogButtonClickHandler(val))
                    .create()
                    .show();
        } else {
            title = "Select Equipments";

            try {

                Set<String> set = sharedpreferences.getStringSet("filter_equipment_list", null);
                List<String> equipmentList = new ArrayList<String>(set);

                for (int i = 0; i < equipmentList.size(); i++) {
                    reader_selections_equipment[Integer.parseInt(equipmentList.get(i))] = true;
                }
            } catch (Exception e) {

            }

            new AlertDialog.Builder(FilterActivity.this, R.style.MyAlertDialogStyle)
                    .setTitle(title)

                    .setMultiChoiceItems(
                            reader_options_equipment,
                            reader_selections_equipment,
                            new DialogSelectionClickHandler(reader_options_equipment,
                                    reader_selections_equipment))
                    .setPositiveButton("OK", new DialogButtonClickHandler(val))
                    .create()
                    .show();
        }


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
        int val;


        public DialogButtonClickHandler(int val1) {
            this.val = val1;
        }

        public void onClick(DialogInterface dialog, int clicked) {
            switch (clicked) {
                case DialogInterface.BUTTON_POSITIVE:
                    //List<String> list_des = new ArrayList<String>();
                    if (val == 1) {

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

                        Log.e("selectedSpeciality", "" + str);
                        Log.e("selectedSpecialityId", "" + selectedSpecialityId);
                        specialityID = selectedSpecialityId;

                        Set<String> setSpecialityList = new HashSet<String>();
                        setSpecialityList.addAll(selectedSpecialityList);
                        editor.putStringSet("filter_speciality_list", setSpecialityList);
                        editor.putString("filter_speciality_id", selectedSpecialityId);
                        editor.putString("filter_speciality", edtSelectSpeciality.getText().toString());
                        editor.commit();
                        break;
                    } else {

                        String str = "";
                        selectedEquipmentId = "";
                        selectedEquipmentId = "";
                        selectedEquipmentList.clear();

                        for (int i = 0; i < reader_options_equipment.length; i++) {


                            if (reader_selections_equipment[i] == true) {

                                String abc = reader_options_equipment[i].toString(); //
                                selectedEquipmentList.add("" + i);
                                str = str + "\n" + abc;
                            }

                        }


                        if (str.equalsIgnoreCase("")) { //
                            edtSelectEquipment.setText("");
                        } else {

                        }
                        GlobalVariables.setStr1(str);
                        Log.e("section_rules1", "" + str);
                        edtSelectEquipment.setText(str);
                        for (int j = 0; j < selectedEquipmentList.size(); j++) {
                            int pos = parseInt(selectedEquipmentList.get(j));
                            String str1 = spinnerEquipmentList.get(pos);
                            Log.e("searated", "" + str1.toString());
                            String[] separated = str1.split("-");
                            Log.e("searated", "" + separated[0]);
                            selectedEquipmentId += separated[0].toString() + ",";


                        }

                        if (selectedEquipmentList.size() != 0) {
                            selectedEquipmentId = selectedEquipmentId.substring(0, selectedEquipmentId.length() - 1);
                        }
//                    selectedSpecialityId = selectedSpecialityId.replace(selectedSpecialityId.substring(selectedSpecialityId.length()-1), "");
                        Log.e("selectedEquipmentId", "" + str);
                        Set<String> setEquipmentList = new HashSet<String>();
                        setEquipmentList.addAll(selectedEquipmentList);
                        editor.putStringSet("filter_equipment_list", setEquipmentList);
                        editor.putString("filter_equipment_id", selectedEquipmentId);
                        editor.putString("filter_equipment", edtSelectEquipment.getText().toString());
                        editor.commit();
                        Log.e("selectedEquipmentId", "" + selectedEquipmentId);
                        break;
                    }


            }
        }
    }
}
