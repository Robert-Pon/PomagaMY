package com.name.social_helper_r_p.user.ans;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.connections.URLS;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.name.social_helper_r_p.user.ImagesAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.name.social_helper_r_p.user.ProfileViewFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class Type4BottomSheet extends BottomSheetDialogFragment {
    TextView title;
    TextView category;
    TextView description;

    ImageView profile;
    TextView name;

    RecyclerView elements;

    SharedPreferences preferences;

    URLS urls = new URLS();
    Fetch fetch = new Fetch();

    Bundle bundle;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.type4_bottom, container, false);
        title = v.findViewById(R.id.title);
        category = v.findViewById(R.id.category);
        description = v.findViewById(R.id.description);
        elements = v.findViewById(R.id.elements);
        profile = v.findViewById(R.id.profile);
        name = v.findViewById(R.id.name);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        bundle = getArguments();
        Thread get = new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request(urls.URL(), urls.getAnn(), "POST", "POST");
                RequestData requestData = new RequestData();
                requestData.setData(new Data("id", bundle.getString("id")));
                requestData.setData(new Data("type", "4"));
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
                                    JSONObject announcements = data.getJSONObject("announcements");
                                    JSONArray photos= announcements.getJSONArray("photos");
                                    ImagesAdapter imagesAdapter = new ImagesAdapter(photos, bundle.getString("id"), "4", getContext());
                                    elements.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                                    elements.setAdapter(imagesAdapter);
                                    title.setText(announcements.getString("title").trim().equals("") ? "Brak nazwy" : announcements.getString("title"));
                                    description.setText(announcements.getString("description").trim().equals("") ? "Brak opisu " : announcements.getString("description"));

                                    List<String> ids = Arrays.asList(getResources().getStringArray(R.array.evCatIDS));

                                    for(int i = 0; i < ids.size(); i++){
                                        try {
                                            if(ids.get(i).equals(announcements.getString("category"))){
                                                category.setText(Arrays.asList(getResources().getStringArray(R.array.evCat)).get(i));
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        Bitmap photo = null;
                        try {
                            name.setText(user.getString("name")+" "+user.getString("surname"));
                            photo = fetch.getImageFromURL(urls.URL()+urls.getProfileImage()+user.getString("_id"),"POST");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    if(photo!=null){
                        Bitmap finalPhoto1 = photo;
                        ((Activity)getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                profile.setImageBitmap(finalPhoto1);
                            }
                        });
                    }


                        System.out.println(data);
                        break;
                }
            }
        });

        get.start();

        return v;
    }


}
