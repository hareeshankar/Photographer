package com.thunderbird.chennai.fapapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.thunderbird.chennai.fapapp.Activities.LoginActivity;
import com.thunderbird.chennai.fapapp.Activities.PhotographerListActivity;
import com.thunderbird.chennai.fapapp.Adapter.AdapterVideoList;
import com.thunderbird.chennai.fapapp.Model.EqpModel;
import com.thunderbird.chennai.fapapp.Model.Model;
import com.thunderbird.chennai.fapapp.Model.Video;
import com.thunderbird.chennai.fapapp.R;
import com.thunderbird.chennai.fapapp.Utility.EditTextK;
import com.thunderbird.chennai.fapapp.Utility.Textclass;
import com.thunderbird.chennai.fapapp.Utility.TypefaceTextView;
import com.thunderbird.chennai.fapapp.Utility.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class VideoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    RecyclerView recyclerView;
    Button btn_add;
    Utility utility;
    ArrayList<Video> videoList;
    private SharedPreferences sharedpreferences;
    private String screen;
    String uId;
    AdapterVideoList adapterVideoList;
    AlertDialog alertDialog;
    TextView txtMessage;
    String pos;
    String name,url,dec,v_id;
    public VideoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_video, container, false);
        videoList=new ArrayList<Video>();
        sharedpreferences = getActivity().getSharedPreferences("myprefrence", Context.MODE_PRIVATE);
        recyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        btn_add=(Button)view.findViewById(R.id.btn_add);
        txtMessage=(TextView)view.findViewById(R.id.txtMessage);
        txtMessage.setVisibility(View.GONE);
        Bundle b = getArguments();
        if (b != null) {
            Log.e("screen",""+b.getString("screen"));
            screen = b.getString("screen");

        }
        if (screen.equalsIgnoreCase("profile")) {
            uId = sharedpreferences.getString("u_id", "");


        } else {
            btn_add.setVisibility(View.INVISIBLE);
            uId = b.getString("u_id");
        }
        utility = new Utility(getActivity());
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addVideoDialog();
            }
        });
        adapterVideoList = new AdapterVideoList(getActivity(), videoList);
        recyclerView.setAdapter(adapterVideoList);
        getVideo();

        recyclerView.addOnItemTouchListener(new VideoFragment.RecyclerTouchListener(getActivity(),
                recyclerView, new EquipmentFragments.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                //Values are passing to activity & to fragment as well
                try {
                    getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoList.get(position).getV_link())));
                }catch (Exception e){
                    Toast.makeText(getActivity(), "Unable to play video invalid url", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                if (screen.equalsIgnoreCase("profile")) {
                    pos=""+position;
                    v_id=videoList.get(position).getV_id();
                    SessionAction(position);
                }
            }
        }));
        return  view;
    }
    public void SessionAction(final int position) {
        final android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.dialog_contact_me, null);
        RelativeLayout relBookSession = (RelativeLayout) subView.findViewById(R.id.rel_email);
        RelativeLayout relDeleteBookSession = (RelativeLayout) subView.findViewById(R.id.rel_phone);
        TypefaceTextView tvDisplayEmail = (TypefaceTextView) subView.findViewById(R.id.tv_display_email);
        TypefaceTextView tvDisplayPhone = (TypefaceTextView) subView.findViewById(R.id.tv_display_phone);
        TypefaceTextView tvEmail = (TypefaceTextView) subView.findViewById(R.id.tv_email);
        TypefaceTextView tvPhone = (TypefaceTextView) subView.findViewById(R.id.tv_phone);
        tvEmail.setText("Edit Video");
        tvPhone.setText("Delete Video");
        tvDisplayEmail.setVisibility(View.INVISIBLE);
        tvDisplayPhone.setVisibility(View.INVISIBLE);
        relBookSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                editVideoDialog(position);


            }
        });
        relDeleteBookSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                deleteVideo(position);

            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.setView(subView);
        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    public void deleteVideo(final int position) {
        String url = Textclass.baseurl + "delete_video.php?api_key=ZmFw&v_id=" + v_id +"";
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

                String str = new String(responseBody);
                try {
                    JSONObject json = new JSONObject(str);

                    int status = json.getInt("status");
                    if (status == 1) {

                        videoList.remove(position);
                        adapterVideoList.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Delete Successfully", Toast.LENGTH_SHORT).show();



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



    public void editVideoDialog(int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.custom_design_add_video, null);

        //   spSession = (Spinner)subView. findViewById(R.id.sp_session);
        //   tvSessionShow = (TypefaceTextView)subView. findViewById(R.id.tv_session_show);

        final EditTextK edtName = (EditTextK) subView.findViewById(R.id.edt_name);
        final EditTextK edtlink = (EditTextK) subView.findViewById(R.id.edt_link);
        final EditTextK edtdescription = (EditTextK) subView.findViewById(R.id.edt_descrioption);
        edtName.setText(videoList.get(position).getV_name());
        edtlink.setText(videoList.get(position).getV_link());
        edtdescription.setText(videoList.get(position).getV_dec());
        TypefaceTextView btn_add = (TypefaceTextView) subView.findViewById(R.id.btn_add);
        btn_add.setText("Update");
        alertDialog = alertDialogBuilder.create();
        alertDialog.setView(subView);
        alertDialog.setCancelable(true);
        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (utility.isNetworkAvailable()) {
                    if (edtName.getText().toString().trim().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Enter Video Name", Toast.LENGTH_SHORT).show();
                    } else if (edtdescription.getText().toString().trim().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Enter Video Descrioption", Toast.LENGTH_SHORT).show();
                    } else if (edtlink.getText().toString().trim().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Enter Video Link", Toast.LENGTH_SHORT).show();
                    } else {
                        editVideo(v_id,edtName.getText().toString(),edtdescription.getText().toString(),edtlink.getText().toString());
                    }

                } else {
                    Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void editVideo(String v_id,String name, String dec, String link) {
        alertDialog.dismiss();
        String url = Textclass.baseurl + "update_video.php?api_key=ZmFw&v_name=" + name + "&v_description=" + dec  + "&v_link=" + link  + "&u_id=" + uId+ "&v_id=" + v_id;
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

                String str = new String(responseBody);
                try {
                    Log.e("responseBody", "" + str);

                    JSONObject json = new JSONObject(str);
                    int status = json.getInt("status");
                    if (status == 1) {
                        Toast.makeText(getActivity(), "Video Update Successfully", Toast.LENGTH_SHORT).show();
                        utility.closeProgressDialog();
                        alertDialog.dismiss();
                        videoList.clear();
                        getVideo();
                    }else {
                        Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "" + e);

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


    private void addVideoDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.custom_design_add_video, null);
        //   spSession = (Spinner)subView. findViewById(R.id.sp_session);
        //   tvSessionShow = (TypefaceTextView)subView. findViewById(R.id.tv_session_show);
        final EditTextK edtName = (EditTextK) subView.findViewById(R.id.edt_name);
        final EditTextK edtlink = (EditTextK) subView.findViewById(R.id.edt_link);
        final EditTextK edtdescription = (EditTextK) subView.findViewById(R.id.edt_descrioption);
        TypefaceTextView btn_add = (TypefaceTextView) subView.findViewById(R.id.btn_add);
        alertDialog = alertDialogBuilder.create();
        alertDialog.setView(subView);
        alertDialog.setCancelable(true);
        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (utility.isNetworkAvailable()) {
                    if (edtName.getText().toString().trim().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Enter Video Name", Toast.LENGTH_SHORT).show();
                    } else if (edtdescription.getText().toString().trim().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Enter Video Descrioption", Toast.LENGTH_SHORT).show();
                    } else if (edtlink.getText().toString().trim().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Enter Video Link", Toast.LENGTH_SHORT).show();
                    } else {
                        addVideo(edtName.getText().toString(),edtdescription.getText().toString(),edtlink.getText().toString());
                    }

                } else {
                    Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void addVideo(String name, String dec, String link) {
        String url = Textclass.baseurl + "add_video.php?api_key=ZmFw&v_name=" + name + "&v_description=" + dec  + "&v_link=" + link  + "&u_id=" + uId;
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
                String str = new String(responseBody);
                try {
                    Log.e("responseBody", "" + str);
                    JSONObject json = new JSONObject(str);
                    int status = json.getInt("status");
                    if (status == 1) {
                        Toast.makeText(getActivity(), "Video Add Successfully", Toast.LENGTH_SHORT).show();
                        utility.closeProgressDialog();
                        alertDialog.dismiss();
                        getVideo();
                    }else {
                        Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                    utility.closeProgressDialog();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "" + e);
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

    private void getVideo() {
        videoList.clear();
        adapterVideoList.notifyDataSetChanged();
        String url = Textclass.baseurl + "get_video.php?api_key=ZmFw&u_id=" + uId;
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

                String str = new String(responseBody);
                Log.e("responseBody", "" + responseBody);

                try {
                    JSONObject json = new JSONObject(str);
                    int status = json.getInt("status");
                    if (status == 1) {
                        txtMessage.setVisibility(View.GONE);
                        JSONArray jsonArray = json.getJSONArray("Get_data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            Video model = new Video();
                            model.setU_id(jsonObject1.getString("u_id"));
                            model.setV_name(jsonObject1.getString("v_name"));
                            model.setV_id(jsonObject1.getString("v_id"));
                            model.setV_dec(jsonObject1.getString("v_description"));
                            model.setV_link(jsonObject1.getString("v_link"));
                            videoList.add(model);
                            adapterVideoList.notifyDataSetChanged();


                        }



                    } else if (status == 0) {
                        txtMessage.setVisibility(View.VISIBLE);

                    } else {
                        Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_SHORT).show();
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private EquipmentFragments.ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final EquipmentFragments.ClickListener clicklistener){

            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
