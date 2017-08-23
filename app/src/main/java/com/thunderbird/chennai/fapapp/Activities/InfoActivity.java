package com.thunderbird.chennai.fapapp.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.thunderbird.chennai.fapapp.R;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

public class InfoActivity extends AppCompatActivity {

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
    private WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        sharedpreferences = getSharedPreferences("myprefrence", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        navBar = (ImageView) findViewById(R.id.nav_bar);
        webView = (WebView) findViewById(R.id.webview);

        navBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
      //  webView.loadUrl("http://www.google.com");
        webView.loadUrl("file:///android_asset/info.html");
        navigationDrawer();

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


        itemContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoActivity.this, FeedbackActivity.class);
                startActivity(intent);
                finish();
            }
        });
        itemProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InfoActivity.this, ProfileActivitys.class);
                intent.putExtra("screen", "profile");
                startActivity(intent);
                finish();
            }
        });

        itemSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InfoActivity.this, PhotographerListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        itemFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(InfoActivity.this, FilterActivity.class);
                startActivity(intent);
                finish();

            }
        });

        itemInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.closeMenu();

            }
        });

        itemTAndC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InfoActivity.this, TandCActivity.class);
                startActivity(intent);
                finish();
            }
        });

        itemExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(InfoActivity.this).setMessage("Are you sure you want to logout?").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(InfoActivity.this, LoginActivity.class);
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

        itemTHowItsWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=WkrT5om6IQw")));
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
}
