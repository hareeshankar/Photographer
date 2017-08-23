package com.thunderbird.chennai.fapapp.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.thunderbird.chennai.fapapp.Activities.ProfileActivitys;
import com.thunderbird.chennai.fapapp.Carousel.CarouselLinearLayout;
import com.thunderbird.chennai.fapapp.Carousel.ItemFragment;
import com.thunderbird.chennai.fapapp.R;
import com.thunderbird.chennai.fapapp.Model.PhotoModel;
import com.thunderbird.chennai.fapapp.Utility.Textclass;
import com.thunderbird.chennai.fapapp.Utility.TypefaceTextView;
import com.thunderbird.chennai.fapapp.Utility.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Darshan on 12/5/2016.
 */

public class PhotosFragment extends Fragment {

    private View v;
    private GridView gridview;
    private Utility utility;
    private SharedPreferences sharedpreferences;
    private String uId;
    private SharedPreferences.Editor editor;
    private ArrayList<PhotoModel> userPhotoList;
    private View imgProfileImage;
    private String path;
    private String userUpdatePhotoId = "";
    private String screen;
    private AlertDialog alertDialog;
    public final static int LOOPS = 1000;
    public CustomPagerAdapter adapter;
    //   public ViewPager pager;
    public static int count = 20; //ViewPager items size
    /**
     * You shouldn't define first page = 0.
     * Let define firstpage = 'number viewpager size' to make endless carousel
     */
    public static int FIRST_PAGE = 10;

    /**
     * Hold a reference to the current animator, so that it can be canceled mid-way.
     */
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private int mPosition;
    PhotosAdapter photosAdapter;
    TextView btn_status;
    TextView txtMessage;
    String lastUploadtimeStamp;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_photos, null, false);
        gridview = (GridView) v.findViewById(R.id.gridview);
        btn_status=(TextView) v.findViewById(R.id.btn_status);

        txtMessage=(TextView)v.findViewById(R.id.txtMessage);
        txtMessage.setVisibility(View.GONE);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        utility = new Utility(getActivity());
        sharedpreferences = getActivity().getSharedPreferences("myprefrence", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        Bundle b = getArguments();
        if (b != null) {
            Log.e("screen",""+b.getString("screen"));
            screen = b.getString("screen");


        }

        if (screen.equalsIgnoreCase("profile")) {
            uId = sharedpreferences.getString("u_id", "");
            btn_status.setVisibility(View.VISIBLE);
        } else {
            btn_status.setVisibility(View.GONE);
            uId = b.getString("u_id");
        }
        if (utility.isNetworkAvailable()) {
            getUserPhotosService();
        } else {
            Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
        }
    }

    public void openCameraAndImagepicker() {

        ImagePicker.create(getActivity())
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            // do your logic ....
            path = images.get(0).getPath();
            if (userUpdatePhotoId.equalsIgnoreCase("")) {
                addUserPhotoService();
            } else {
                updateUserPhotoService();
            }

        }

    }

    public void alertDialog() {
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity()).setMessage("Are you sure you want to add a photo?").setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                openCameraAndImagepicker();

            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(true);

        alertDialog.show();
    }

    public void getUserPhotosService() {

        userPhotoList = new ArrayList<PhotoModel>();

        String url = Textclass.baseurl + "get_user_photo.php?api_key=ZmFw&u_id=" + uId;
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(getActivity(), url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                utility.showProgressDialog();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.e("Success", statusCode + "" + responseBody);

                String str = new String(responseBody);
                Log.e("Success", statusCode + "" + str);
                try {
                    JSONObject json = new JSONObject(str);
                    int status = json.getInt("status");

                    if (screen.equalsIgnoreCase("profile")) {
                        PhotoModel model = new PhotoModel();
                        model.setUserPhotoId("null");
                        model.setUserPhoto("null");
                        model.setUserPhotoCreatedDate("null");
                        userPhotoList.add(model);
                    }

                    if (status == 1) {
                        txtMessage.setVisibility(View.GONE);
                        JSONArray jsonArray = json.getJSONArray("Get_data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            PhotoModel model1 = new PhotoModel();
                            model1.setUserPhotoId(jsonObject1.getString("up_id"));
                            model1.setUserPhoto(jsonObject1.getString("photos"));
                            model1.setUserPhotoCreatedDate(jsonObject1.getString("up_created_date"));
                            lastUploadtimeStamp="Last photo upload was on "+jsonObject1.getString("up_created_date");
                            userPhotoList.add(model1);
                        }
                        adapter = new CustomPagerAdapter(getActivity());
                        ProfileActivitys.pager.setPageTransformer(true, new ZoomOutPageTransformer());
                        ProfileActivitys.pager.setAdapter(adapter);
                        ProfileActivitys.pager.setCurrentItem(0);

                        btn_status.setText(lastUploadtimeStamp);

                    } else if (status == 0) {
                        txtMessage.setVisibility(View.VISIBLE);
                        PhotosAdapter photosAdapter = new PhotosAdapter(getActivity(), userPhotoList);
                        gridview.setAdapter(photosAdapter);


                    } else {
                        Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                     photosAdapter = new PhotosAdapter(getActivity(), userPhotoList);
                    gridview.setAdapter(photosAdapter);

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    utility.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                utility.closeProgressDialog();
            }
        });
    }


    private void DeletePhoto() {



        String url = Textclass.baseurl + "delete_photo.php?api_key=ZmFw&up_id=" + userUpdatePhotoId +"";
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(getActivity(), url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                utility.showProgressDialog();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.d("Success", statusCode + "" + responseBody);

              /*  {"Get_data":[{"ueb_id":"15","u_id":"7","ep_id":"2","ue_id":"14","b_date":"2016-12-27",
              "b_session":"Evening","b_created_date":"2016-12-27 03:42:21"}],"status":1,"message":"Get_data"}*/

                String str = new String(responseBody);
                try {
                    JSONObject json = new JSONObject(str);

                    int status = json.getInt("status");
                    if (status == 1) {

                        userPhotoList.remove(mPosition);
                        photosAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Delete Successfully", Toast.LENGTH_SHORT).show();
//                            getBookedSessions();

                    } else if (status == 0) {
                        Toast.makeText(getActivity(),  getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    utility.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                utility.closeProgressDialog();
            }
        });

    }

    public void addUserPhotoService() {
        String url = Textclass.baseurl1 + "add_photo.php";

        RequestParams requestParams = new RequestParams();
        requestParams.add("api_key", "ZmFw");
        requestParams.add("u_id", uId);
        File imageFile = new File(path);

       Log.e("size",""+imageFile.length());
        int size= (int) (imageFile.length()/1000);
        if(size>512){
            Toast.makeText(getActivity(),"Sorry select another image, image size less then 512kb",Toast.LENGTH_LONG).show();
            return;
        }
        if (imageFile != null && imageFile.exists()) {
            try {
                requestParams.put("photos", imageFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            requestParams.put("photos", "");
        }
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(getActivity(), url, requestParams, new AsyncHttpResponseHandler() {

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
                Log.e("Success", statusCode + "" + str);

                try {
                    JSONObject json = new JSONObject(str);
                    int status = json.getInt("status");
                    if (status == 1) {

                        Toast.makeText(getActivity(), getString(R.string.successfully_added), Toast.LENGTH_SHORT).show();
                        getUserPhotosService();
                    } else if (status == 0) {
                        Toast.makeText(getActivity(), getString(R.string.server_problem), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    utility.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                utility.closeProgressDialog();
            }
        });
    }

    public void updateUserPhotoService() {
        String url = Textclass.baseurl1 + "update_photo.php";

        RequestParams requestParams = new RequestParams();
        requestParams.add("api_key", "ZmFw");
        requestParams.add("u_id", uId);
        requestParams.add("update_id", userUpdatePhotoId);
        File imageFile = new File(path);
        int size= (int) (imageFile.length()/1000);
        if(size>512){
            Toast.makeText(getActivity(),"Sorry select another image, image size less then 512kb",Toast.LENGTH_LONG).show();
            return;
        }
        if (imageFile != null && imageFile.exists()) {
            try {
                requestParams.put("photos", imageFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            requestParams.put("photos", "");
        }
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(getActivity(), url, requestParams, new AsyncHttpResponseHandler() {

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
                        userUpdatePhotoId = "";
                        Toast.makeText(getActivity(), getString(R.string.successfully_added), Toast.LENGTH_SHORT).show();
                        getUserPhotosService();
                    } else if (status == 0) {
                        Toast.makeText(getActivity(), getString(R.string.server_problem), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    userUpdatePhotoId = "";
                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    utility.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", statusCode + "" + responseBody);
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                utility.closeProgressDialog();
            }
        });
    }

    public class PhotosAdapter extends BaseAdapter {


        private final Context context;
        private final ArrayList<PhotoModel> mUserPhotoList;
        private ViewHolder holder;

        PhotosAdapter(Context context, ArrayList<PhotoModel> userPhotoList) {

            this.context = context;
            mUserPhotoList = userPhotoList;
        }

        @Override
        public int getCount() {

            return userPhotoList.size();
        }

        @Override
        public Object getItem(int position) {

//            return position;
            return mUserPhotoList.get(position);

        }

        @Override
        public long getItemId(int position) {

//            return position;
            return mUserPhotoList.indexOf(getItem(position));
        }

        /**
         * Viewholder class
         */
        public class ViewHolder {
            ImageView imgUserPhoto;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater mInflater;
            if (convertView == null) {

                holder = new ViewHolder();
                mInflater = (LayoutInflater) context
                        .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.custom_photos, null);
                holder.imgUserPhoto = (ImageView) convertView.findViewById(R.id.user_photo);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (screen.equalsIgnoreCase("profile")) {

                if(position==0){
                    holder.imgUserPhoto.setPadding(20,20,20,20);

                }else
                if (position < mUserPhotoList.size()) {
                    Glide.with(getActivity()).load(Textclass.photos + mUserPhotoList.get(position).getUserPhoto()).override(100, 100).placeholder(getResources().getDrawable(R.drawable.avtar)).into(holder.imgUserPhoto);
                }
                holder.imgUserPhoto.setPadding(0,0,0,0);
            }else {
                if (position < mUserPhotoList.size()) {
                    Glide.with(getActivity()).load(Textclass.photos + mUserPhotoList.get(position).getUserPhoto()).override(100, 100).placeholder(getResources().getDrawable(R.drawable.avtar)).into(holder.imgUserPhoto);
                }
                holder.imgUserPhoto.setPadding(0,0,0,0);
            }

            if (screen.equalsIgnoreCase("profile")) {
                holder.imgUserPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mPosition = position;
                        if(mPosition!=0){
                            if (userPhotoList.size() > mPosition) {
                                zoomImageFromThumb(view);
                            }
                        }else {
                            alertDialog();
                        }

                    }
                });

                holder.imgUserPhoto.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mPosition = position;


                        if (mPosition == 0) {
                            alertDialog();
                        } else if (mPosition != 0) {
                            userUpdatePhotoId = userPhotoList.get(mPosition).getUserPhotoId();
                            selectSession();
                        }

                        return false;
                    }
                });


            } else {
                holder.imgUserPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (userPhotoList.size() != 0) {

                            ProfileActivitys.pager.setCurrentItem(position);
                            ProfileActivitys.pager.setVisibility(View.VISIBLE);
                        }




                    }
                });


            }

            return convertView;

        }


    }

    public void selectSession() {
        final android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.dialog_contact_me, null);
        RelativeLayout relBookSession = (RelativeLayout) subView.findViewById(R.id.rel_email);
        RelativeLayout relDeleteBookSession = (RelativeLayout) subView.findViewById(R.id.rel_phone);
        RelativeLayout relViewSession = (RelativeLayout) subView.findViewById(R.id.rel_address);
//        relViewSession.setVisibility(View.VISIBLE);

        TypefaceTextView tvDisplayEmail = (TypefaceTextView) subView.findViewById(R.id.tv_display_email);
        TypefaceTextView tvDisplayPhone = (TypefaceTextView) subView.findViewById(R.id.tv_display_phone);
        TypefaceTextView tvEmail = (TypefaceTextView) subView.findViewById(R.id.tv_email);
        TypefaceTextView tvPhone = (TypefaceTextView) subView.findViewById(R.id.tv_phone);
        TypefaceTextView tvAddress = (TypefaceTextView) subView.findViewById(R.id.tv_address);
        tvEmail.setText("Update photo");
        tvPhone.setText("Delete photo");

        tvDisplayEmail.setVisibility(View.INVISIBLE);
        tvDisplayPhone.setVisibility(View.INVISIBLE);
        relBookSession.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        openCameraAndImagepicker();
                    }
                });

        relDeleteBookSession.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                alertDialog.dismiss();
                DeletePhoto();


            }

        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setView(subView);

        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    public class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }


        @Override
        public Object instantiateItem(ViewGroup collection, final int position) {


            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.custom_pager, collection, false);
            ImageView imgUserPhoto = (ImageView) layout.findViewById(R.id.user_photo);
            if (position < userPhotoList.size()) {
                Glide.with(getActivity()).load(Textclass.photos + userPhotoList.get(position).getUserPhoto()).placeholder(getResources().getDrawable(R.drawable.avtar)).into(imgUserPhoto);
            }


            collection.addView(layout);

            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {


            if (userPhotoList != null) {
                return userPhotoList.size();
            } else {
                return 1;
            }

        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


    }

    public class CarouselPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        public final static float BIG_SCALE = 1.0f;
        public final static float SMALL_SCALE = 0.7f;
        public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
        private PhotosFragment context;
        private FragmentManager fragmentManager;
        private float scale;

        public CarouselPagerAdapter(PhotosFragment context, FragmentManager fm) {
            super(fm);
            this.fragmentManager = fm;
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            // make the first pager bigger than others
            try {
                if (position == FIRST_PAGE)
                    scale = BIG_SCALE;
                else
                    scale = SMALL_SCALE;

                position = position % count;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return ItemFragment.newInstance(getActivity(), userPhotoList, position, scale);
        }

        @Override
        public int getCount() {
            int count1 = 0;
            try {
                count1 = count * LOOPS;
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            return count1;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            try {
                if (positionOffset >= 0f && positionOffset <= 1f) {
                    CarouselLinearLayout cur = getRootView(position);
                    CarouselLinearLayout next = getRootView(position + 1);

                    cur.setScaleBoth(BIG_SCALE - DIFF_SCALE * positionOffset);
                    next.setScaleBoth(SMALL_SCALE + DIFF_SCALE * positionOffset);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @SuppressWarnings("ConstantConditions")
        private CarouselLinearLayout getRootView(int position) {
            return (CarouselLinearLayout) fragmentManager.findFragmentByTag(this.getFragmentTag(position))
                    .getView().findViewById(R.id.root_container);
        }

        private String getFragmentTag(int position) {
            return "android:switcher:" + ProfileActivitys.pager.getId() + ":" + position;
        }
    }




    private void zoomImageFromThumb(final View thumbView) {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }


        Glide.with(this)
                .load(Textclass.photos + userPhotoList.get(mPosition).getUserPhoto()).thumbnail(0.1f).placeholder(R.drawable.avtar1)
                .into(ProfileActivitys.expandedImageView);

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
        getActivity().findViewById(R.id.activity_profile).getGlobalVisibleRect(finalBounds, globalOffset);
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
        ProfileActivitys.expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
        // the zoomed-in view (the default is the center of the view).
        ProfileActivitys.expandedImageView.setPivotX(0f);
        ProfileActivitys.expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(ProfileActivitys.expandedImageView, View.X, startBounds.left,
                        finalBounds.left))
                .with(ObjectAnimator.ofFloat(ProfileActivitys.expandedImageView, View.Y, startBounds.top,
                        finalBounds.top))
                .with(ObjectAnimator.ofFloat(ProfileActivitys.expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(ProfileActivitys.expandedImageView, View.SCALE_Y, startScale, 1f));
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
        ProfileActivitys.expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel, back to their
                // original values.
                AnimatorSet set = new AnimatorSet();
                set
                        .play(ObjectAnimator.ofFloat(ProfileActivitys.expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(ProfileActivitys.expandedImageView, View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(ProfileActivitys.expandedImageView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(ProfileActivitys.expandedImageView, View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        ProfileActivitys.expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        ProfileActivitys.expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }


    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }




}
