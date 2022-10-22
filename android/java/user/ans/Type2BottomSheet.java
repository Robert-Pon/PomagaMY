package com.name.social_helper_r_p.user.ans;

import android.app.Activity;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.Arrays;
import java.util.List;

public class Type2BottomSheet extends BottomSheetDialogFragment {
    TextView title;
    TextView description;
    TextView quantity_1;

    ProgressBar quantity;

    TextView name;

    Fetch fetch = new Fetch();
    URLS urls = new URLS();

    Bundle bundle;

    SharedPreferences preferences;
    ImageView profile;

    String main_category;

    String secondCategory;

    TextView categories;

    String categoiresS;

    CardView chat;

    Type2BottomSheetInterface fragmentInterface;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.type2_bottom, container, false);

        title = v.findViewById(R.id.title);
        description = v.findViewById(R.id.description);
        quantity_1 = v.findViewById(R.id.article);
        name = v.findViewById(R.id.name);
        chat = v.findViewById(R.id.chat);

        quantity = v.findViewById(R.id.quantity);
        profile = v.findViewById(R.id.profile);
        categories = v.findViewById(R.id.category);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        bundle = getArguments();
        Thread get = new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request(urls.URL(), urls.getAnn(), "POST", "POST");
                RequestData requestData = new RequestData();
                requestData.setData(new Data("id", bundle.getString("id")));
                requestData.setData(new Data("type", "2"));
                requestData.setData(new Data("token", preferences.getString("TOKEN", "")));
                requestData.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));
                Return response = fetch.fetch(request, requestData);
                System.out.println(response.getType());
                switch (response.getType()){
                    case 0:
                        JSONObject data = (JSONObject) response.getObject();
                        JSONObject announcement = null;
                        try {
                            announcement = data.getJSONObject("announcements");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONObject user = null;
                        try {
                            user = data.getJSONObject("user");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONObject finalAnnouncement = announcement;
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
                                    title.setText(finalAnnouncement.getString("title").trim().equals("") ? "Brak nazwy" : finalAnnouncement.getString("title"));
                                    description.setText(finalAnnouncement.getString("description").trim().equals("") ? "Brak opisu " : finalAnnouncement.getString("description"));
                                    quantity_1.setText(finalAnnouncement.getString("get")+"/"+ finalAnnouncement.getString("quantity")+"  ");
                                    name.setText(finalUser.getString("name")+" "+ finalUser.getString("surname"));
                                    quantity.setMax(finalAnnouncement.getInt("quantity"));
                                    quantity.setProgress(finalAnnouncement.getInt("get"));

                                    List<String> ids = Arrays.asList(getResources().getStringArray(R.array.categoriesHumanIDS));

                                    for(int i = 0; i < ids.size(); i++){
                                        try {
                                            if(ids.get(i).equals(finalAnnouncement.getString("category"))){
                                                main_category = finalAnnouncement.getString("category");
                                                categoiresS = Arrays.asList(getResources().getStringArray(R.array.categoriesHuman)).get(i);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    List<String> secondAdapter = null;
                                    List<String> secondCata = null;
                                    switch (main_category){
                                        case "cH5":
                                            secondAdapter = Arrays.asList(getResources().getStringArray(R.array.categoriesAssortmentIDS));
                                            secondCata = Arrays.asList(getResources().getStringArray(R.array.categoriesAssortment));

                                            break;
                                        case "cH6":
                                            secondAdapter = Arrays.asList(getResources().getStringArray(R.array.categoriesAnimalsIDS));
                                            secondCata = Arrays.asList(getResources().getStringArray(R.array.categoriesAnimals));
                                            break;
                                        case "cH9":
                                            secondAdapter = Arrays.asList(getResources().getStringArray(R.array.categoriesLearningIDS));
                                            secondCata = Arrays.asList(getResources().getStringArray(R.array.categoriesLearning));
                                            break;
                                    }
                                if(secondAdapter!=null){
                                    for(int i = 0; i < secondAdapter.size(); i++){
                                        try {
                                            if(secondAdapter.get(i).equals(finalAnnouncement.getString("secondCategory"))){
                                                main_category = finalAnnouncement.getString("secondCategory");
                                                categoiresS +=(" • "+secondCata.get(i)) ;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }


                                    categories.setText(categoiresS);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        Bitmap photo = null;
                        try {
                            photo = fetch.getImageFromURL(urls.URL()+urls.getProfileImage()+user.getString("_id"),"POST");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Bitmap finalPhoto = photo;
                        ((Activity)getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                profile.setImageBitmap(finalPhoto);
                            }
                        });
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
        fragmentInterface = (Type2BottomSheetInterface) context;
    }

    public interface Type2BottomSheetInterface{
        public void goToChat(String id, String name, String surname);
    }
}
