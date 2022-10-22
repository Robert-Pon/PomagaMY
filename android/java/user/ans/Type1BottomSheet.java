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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.connections.URLS;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.name.social_helper_r_p.user.ProfileViewFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Type1BottomSheet extends BottomSheetDialogFragment {

    List<JSONObject> objects;

    RecyclerView items;

    TextView name;

    Fetch fetch = new Fetch();
    URLS urls = new URLS();

    Bundle bundle;

    SharedPreferences preferences;

    ImageView profile;
    List<String> ids = new ArrayList<>();

    Type1BottomSheetInterface fragmentInterface;

    CardView chat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.type1_bottom, container, false);

        bundle = getArguments();
        ids = Arrays.asList(getResources().getStringArray(R.array.categoriesHumanIDS));
        items = v.findViewById(R.id.items);
        name = v.findViewById(R.id.name);
        profile = v.findViewById(R.id.profile);
        chat = v.findViewById(R.id.chat);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());


        Thread get = new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request(urls.URL(), urls.getAnn(), "POST", "POST");
                RequestData requestData = new RequestData();
                requestData.setData(new Data("id", bundle.getString("id")));
                requestData.setData(new Data("type", "1"));
                requestData.setData(new Data("token", preferences.getString("TOKEN", "")));
                requestData.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));
                Return response = fetch.fetch(request, requestData);
                System.out.println(response.getType());
                switch (response.getType()){
                    case 0:
                        JSONObject data = (JSONObject) response.getObject();
                        try {
                            JSONObject announcement = data.getJSONObject("announcements");
                            JSONObject user = data.getJSONObject("user");
                            JSONObject innerData = announcement.getJSONObject("data");
                            JSONArray things = innerData.getJSONArray("things");
                            List<JSONObject> newThings = new ArrayList<>();
                            for(int i =0; i < things.length(); i++){
                                System.out.println(things.getJSONObject(i));
                                newThings.add(things.getJSONObject(i));
                            }
                            System.out.println(things.length());
                            ((Activity) getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    profile.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Bundle data = new Bundle();
                                            try {
                                                data.putString("id", user.getString("_id"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            ProfileViewFragment profileViewFragment = new ProfileViewFragment();
                                            profileViewFragment.setArguments(data);
                                            profileViewFragment.show(getChildFragmentManager(), "d");
                                        }
                                    });


                                    Type1Adapter adapter = new Type1Adapter(newThings);
                                    items.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                    items.setAdapter(adapter);
                                    chat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                fragmentInterface.goToChat(user.getString("_id"),user.getString("name"),user.getString("surname"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    try {
                                        name.setText(user.getString("name")+" "+user.getString("surname"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            Bitmap photo = fetch.getImageFromURL(urls.URL()+urls.getProfileImage()+user.getString("_id"),"POST");

                            ((Activity)getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    profile.setImageBitmap(photo);

                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });

        get.start();
        return v;
    }

    public class Type1Adapter extends RecyclerView.Adapter<Type1Adapter.Thing>{
        List<JSONObject> things;

        public Type1Adapter(List<JSONObject> things){
            this.things = things;
        }


        public class Thing extends RecyclerView.ViewHolder {
            TextView title;
            TextView description;
            TextView category;
            TextView quantity;
            ProgressBar quan;
            public Thing(@NonNull View itemView) {
                super(itemView);
            }
        }

        @NonNull
        @Override
        public Type1Adapter.Thing onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.thing_resources_v, parent, false);
            Thing thing = new Thing(v);
            thing.category = v.findViewById(R.id.category);
            thing.title = v.findViewById(R.id.title);
            thing.description = v.findViewById(R.id.description);
            thing.quantity = v.findViewById(R.id.article);
            thing.quan = v.findViewById(R.id.quantity);
            return thing;
        }

        @Override
        public void onBindViewHolder(@NonNull Type1Adapter.Thing holder, int position) {
            String main_category = "";
            String categoiresS = "";
            try {
                System.out.println(things.get(position));
                holder.title.setText(things.get(position).getString("title").trim().equals("") ? "Brak nazwy" : things.get(position).getString("title"));
                holder.description.setText(things.get(position).getString("description").trim().equals("") ? "Brak opisu " : things.get(position).getString("description"));
                holder.category.setText(things.get(position).getString("category")+" "+things.get(position).getString("secondCategory"));
                holder.quan.setMax(things.get(position).getInt("quantity"));
                holder.quantity.setText(things.get(position).getInt("get")+"/"+things.get(position).getInt("quantity")+"   ");
                holder.quan.setProgress(things.get(position).getInt("get"));


                for(int i = 0; i < ids.size(); i++){
                    try {
                        if(ids.get(i).equals(things.get(position).getString("category"))){
                            main_category = things.get(position).getString("category");
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
                            if(secondAdapter.get(i).equals(things.get(position).getString("secondCategory"))){
                                main_category = things.get(position).getString("secondCategory");
                                categoiresS +=(" • "+secondCata.get(i)) ;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                holder.category.setText(categoiresS);
            } catch (JSONException e) {
                holder.quan.setProgress(0);

                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return things.size();
        }


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentInterface = (Type1BottomSheetInterface) context;
    }

    public interface Type1BottomSheetInterface{
        public void goToChat(String id, String name, String surname);
    }

}
