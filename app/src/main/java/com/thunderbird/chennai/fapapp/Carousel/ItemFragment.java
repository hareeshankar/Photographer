package com.thunderbird.chennai.fapapp.Carousel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.thunderbird.chennai.fapapp.Model.PhotoModel;
import com.thunderbird.chennai.fapapp.R;
import com.thunderbird.chennai.fapapp.Utility.Textclass;

import java.util.ArrayList;

public class ItemFragment extends Fragment {

    private static final String POSITON = "position";
    private static final String SCALE = "scale";
    private static final String DRAWABLE_RESOURE = "resource";
    private static ArrayList<PhotoModel> mUserPhotoList;

    private int screenWidth;
    private int screenHeight;

    /*private int[] imageArray = new int[]{R.drawable.image1, R.drawable.image2,
            R.drawable.image3, R.drawable.image4, R.drawable.image5,
            R.drawable.image6, R.drawable.image7, R.drawable.image8,
            R.drawable.image9, R.drawable.image10};*/

    public static Fragment newInstance(Activity context, ArrayList<PhotoModel> userPhotoList, int pos, float scale) {
        Bundle b = new Bundle();
        b.putInt(POSITON, pos);
        b.putFloat(SCALE, scale);
        mUserPhotoList=userPhotoList;


        return Fragment.instantiate(context, ItemFragment.class.getName(), b);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidthAndHeight();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        final int postion = this.getArguments().getInt(POSITON);
        float scale = this.getArguments().getFloat(SCALE);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWidth/2 , screenHeight/2 );
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_image, container, false);

     //   TextView textView = (TextView) linearLayout.findViewById(R.id.text);
        CarouselLinearLayout root = (CarouselLinearLayout) linearLayout.findViewById(R.id.root_container);
        ImageView imageView = (ImageView) linearLayout.findViewById(R.id.pagerImg);

     //   textView.setText("Carousel item: " + postion);
        imageView.setLayoutParams(layoutParams);
    //    imageView.setImageResource(mUserPhotoList.get(postion).getUserPhoto());
        if(postion>=mUserPhotoList.size()){
            Glide.with(getActivity()).load(R.drawable.avtar).into(imageView);

        }else{
            Glide.with(getActivity()).load(Textclass.photos + mUserPhotoList.get(postion).getUserPhoto()).placeholder(getResources().getDrawable(R.drawable.avtar)).into(imageView);
        }

        //handling click event
       /* imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageDetailsActivity.class);
                intent.putExtra(DRAWABLE_RESOURE, imageArray[postion]);
                startActivity(intent);
            }
        });*/

        root.setScaleBoth(scale);

        return linearLayout;
    }

    /**
     * Get device screen width and height
     */
    private void getWidthAndHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;
    }
}
