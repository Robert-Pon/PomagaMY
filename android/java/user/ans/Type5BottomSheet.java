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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class Type5BottomSheet extends BottomSheetDialogFragment {
    TextView title;
    TextView category;
    TextView description;

    RecyclerView elements;

    SharedPreferences preferences;

    URLS urls = new URLS();
    Fetch fetch = new Fetch();

    Bundle bundle;

    ProgressBar support;
    ProgressBar against;
    TextView againstTEXT;
    TextView supportTEXT;

    TextView name;
    ImageView profile;

    boolean myVote = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.type5_bottom, container, false);
        title = v.findViewById(R.id.title);
        category = v.findViewById(R.id.category);
        description = v.findViewById(R.id.description);
        elements = v.findViewById(R.id.elements);
        support = v.findViewById(R.id.support);
        against = v.findViewById(R.id.against);
        profile = v.findViewById(R.id.profile);
        againstTEXT = v.findViewById(R.id.againstTEXT);
        supportTEXT = v.findViewById(R.id.supportTEXT);
        name = v.findViewById(R.id.name);
        support.setMax(100);
        against.setMax(100);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!myVote){
                    VoteBottom voteBottom = new VoteBottom();
                    Bundle data = new Bundle();
                    data.putString("id", bundle.getString("id"));
                    data.putString("support", "true");
                    voteBottom.setArguments(data);
                    voteBottom.show(getChildFragmentManager(), "d");
                }else{
                    Toast.makeText(getContext(), "Wcześniej oddałeś już swój głos", Toast.LENGTH_LONG).show();
                }

            }
        });
        against.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!myVote){
                    VoteBottom voteBottom = new VoteBottom();
                    Bundle data = new Bundle();
                    data.putString("id", bundle.getString("id"));
                    data.putString("support", "false");
                    voteBottom.setArguments(data);
                    voteBottom.show(getChildFragmentManager(), "d");
                }else{
                    Toast.makeText(getContext(), "Wcześniej oddałeś już swój głos", Toast.LENGTH_LONG).show();
                }
            }
        });

        bundle = getArguments();
        Thread get = new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request(urls.URL(), urls.getAnn(), "POST", "POST");
                RequestData requestData = new RequestData();
                requestData.setData(new Data("id", bundle.getString("id")));
                requestData.setData(new Data("type", "5"));
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
                                    ImagesAdapter imagesAdapter = new ImagesAdapter(photos, bundle.getString("id"), "5", getContext());
                                    elements.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                                    elements.setAdapter(imagesAdapter);
                                    title.setText(announcements.getString("title").trim().equals("") ? "Brak nazwy" : announcements.getString("title"));
                                    description.setText(announcements.getString("description").trim().equals("") ? "Brak opisu " : announcements.getString("description"));
                                    category.setText(announcements.getString("category"));
                                    try {
                                        System.out.println(announcements.getBoolean("myVote"));
                                        myVote = true;
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        myVote = false;
                                    }
                                    if(announcements.getLong("votes")!=0){

                                        long percent = (announcements.getLong("support")/announcements.getLong("votes"))*100;
                                        support.setProgress((int) percent);
                                        against.setProgress((int) (100-percent));
                                        supportTEXT.setText("Jestem za ("+((int) percent)+"%)");
                                        againstTEXT.setText("Jestem przeciw ("+((int) 100-percent)+"%)");
                                    }else{
                                        supportTEXT.setText("Jestem za (0%)");
                                        againstTEXT.setText("Jestem przeciw (0%)");
                                        support.setProgress(0);
                                        against.setProgress(0);
                                    }
                                    List<String> ids = Arrays.asList(getResources().getStringArray(R.array.idCatIDS));

                                    for(int i = 0; i < ids.size(); i++){
                                        try {
                                            if(ids.get(i).equals(announcements.getString("category"))){
                                                category.setText(Arrays.asList(getResources().getStringArray(R.array.idCat)).get(i));
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
