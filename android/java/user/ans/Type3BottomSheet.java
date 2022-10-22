package com.name.social_helper_r_p.user.ans;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.connections.URLS;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.name.social_helper_r_p.user.ProfileViewFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Type3BottomSheet extends BottomSheetDialogFragment {
    TextView name;
    TextView title;
    TextView description;
    TextView date;
    TextView category;
    TextView quantity;

    Button intreasted;

    Fetch fetch = new Fetch();
    URLS urls = new URLS();

    SharedPreferences preferences;
    Bundle bundle;

    ImageView poster;

    ImageView profile;

    Type3BottomSheetInterface fragmentInterface;

    boolean want = false;

    CardView chat;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.type3_bottom, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        title = v.findViewById(R.id.title);
        description = v.findViewById(R.id.description);
        date = v.findViewById(R.id.date);
        name = v.findViewById(R.id.name);
        profile = v.findViewById(R.id.profile);
        chat = v.findViewById(R.id.chat);

        quantity = v.findViewById(R.id.quantity);
        category = v.findViewById(R.id.category);

        intreasted = v.findViewById(R.id.intreasted);

        poster = v.findViewById(R.id.poster);
        bundle = getArguments();

        intreasted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread send = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Request request = new Request(urls.URL(), urls.vote3(), "POST","POST");
                        RequestData data = new RequestData();
                        data.setData(new Data("id", bundle.getString("id")));
                        data.setData(new Data("token", preferences.getString("TOKEN", "")));
                        data.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));

                        Return response = fetch.fetch(request, data);

                        switch (response.getType()){
                            case 0:
                                ((Activity) getContext()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        want = !want;
                                        if(want==true){
                                            intreasted.setText("Nie interesuje mnie już to wydarzenie");
                                        }else{
                                            intreasted.setText("Daj znać że interesuje Ciebie to wydarzenie");

                                        }
                                    }
                                });

                                break;
                        }
                    }
                });
                send.start();
            }
        });
        Thread get = new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request(urls.URL(), urls.getAnn(), "POST", "POST");
                RequestData requestData = new RequestData();
                requestData.setData(new Data("id", bundle.getString("id")));
                requestData.setData(new Data("type", "3"));
                requestData.setData(new Data("token", preferences.getString("TOKEN", "")));
                requestData.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));
                Return response = fetch.fetch(request, requestData);
                System.out.println(response.getType());
                switch (response.getType()){
                    case 0:
                        JSONObject data = (JSONObject) response.getObject();
                        JSONObject user = null;
                        try {
                            user = data.getJSONObject("user");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JSONObject finalUser = user;
                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void run() {
                                profile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle data = new Bundle();
                                        try {
                                            data.putString("id", finalUser.getString("_id"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        ProfileViewFragment profileViewFragment = new ProfileViewFragment();
                                        profileViewFragment.setArguments(data);
                                        profileViewFragment.show(getChildFragmentManager(), "d");
                                    }
                                });
                                try {
                                    System.out.println(data);
                                    JSONObject announcement = data.getJSONObject("announcements");
                                    JSONObject user = data.getJSONObject("user");

                                    chat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                fragmentInterface.goToChat(finalUser.getString("_id"),finalUser.getString("name"),finalUser.getString("surname"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    title.setText(announcement.getString("name").trim().equals("") ? "Brak nazwy" : announcement.getString("name"));
                                    description.setText(announcement.getString("description").trim().equals("") ? "Brak opisu " : announcement.getString("description"));
                                    System.out.println("DSYR");
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:ss");
                                    String d1 ="Data: ";
                                    try {
                                        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(announcement.getLong("startDate")/1000, 0, ZoneOffset.UTC);
                                        d1+=formatter.format(dateTime);
                                    }catch (Exception e){

                                    }
                                    try {
                                        LocalDateTime dateTime2 = LocalDateTime.ofEpochSecond(announcement.getLong("endDate")/1000, 0, ZoneOffset.UTC);
                                        d1+=("-"+formatter.format(dateTime2));
                                    }catch (Exception e){

                                    }
                                    want = data.getBoolean("want");
                                    if(want==true){
                                        intreasted.setText("Nie interesuje mnie już to wydarzenie");
                                    }else{
                                        intreasted.setText("Daj znać że interesuje Ciebie to wydarzenie");

                                    }
                                    date.setText( d1);
                                    name.setText(user.getString("name")+" "+user.getString("surname"));
                                    quantity.setText(announcement.getJSONArray("declared").getJSONObject(0).getLong("id")+" osób chce wziąść udział");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        Bitmap photo = null;
                        try {
                            photo = fetch.getImageFromURL(urls.URL()+urls.getPoster()+"/"+bundle.getString("id"), "POST");
                            Bitmap finalPhoto = photo;
                            ((Activity) getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    poster.setImageBitmap(finalPhoto);
                                }
                            });
                        }catch (Exception e){

                        }


                        try {
                            name.setText(user.getString("name")+" "+user.getString("surname"));
                            photo = fetch.getImageFromURL(urls.URL()+urls.getProfileImage()+user.getString("_id"),"POST");

                            Bitmap finalPhoto1 = photo;
                            ((Activity)getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    profile.setImageBitmap(finalPhoto1);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        break;
                }
            }
        });

        get.start();
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentInterface = (Type3BottomSheetInterface) context;
    }

    public interface Type3BottomSheetInterface{
        public void goToChat(String id, String name, String surname);
    }
}
