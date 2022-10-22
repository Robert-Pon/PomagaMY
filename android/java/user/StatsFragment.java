package com.name.social_helper_r_p.user;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.connections.URLS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatsFragment extends Fragment {

    URLS urls = new URLS();
    Fetch fetch = new Fetch();

    Bundle bundle;
    SharedPreferences preferences;

    TextView quantity;
    TextView quantity_1;
    TextView announcements;

    GoogleMap googleMapOut = null;

    SupportMapFragment mapFragment;

    List<String> ids = new ArrayList<>();

    RecyclerView myHelp;

    RecyclerView articles;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.stats, container, false);

        ids = Arrays.asList(getResources().getStringArray(R.array.categoriesHumanIDS));

        myHelp = v.findViewById(R.id.last);
        articles = v.findViewById(R.id.articles);

        quantity = v.findViewById(R.id.quantity);
        quantity_1 = v.findViewById(R.id.article);
        announcements = v.findViewById(R.id.announcements);
        bundle = getArguments();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        articles.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                googleMapOut = googleMap;
                CameraPosition position = new CameraPosition.Builder().target(new LatLng(51.9189046, 19.1343786)).zoom(5).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));

                googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {

                    }
                });
            }
        });

        myHelp.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        articles.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        Thread get = new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request(urls.URL(), urls.getStats(), "POST", "POST");
                RequestData requestData = new RequestData();
                requestData.setData(new Data("token", preferences.getString("TOKEN", "")));
                requestData.setData(new Data("token_ID",  preferences.getString("TOKEN_ID", "")));

                Return response = fetch.fetch(request, requestData);

                switch (response.getType()){
                    case 0:
                        JSONObject data = (JSONObject) response.getObject();
                        try {
                            JSONArray stats = data.getJSONArray("stats");

                            ((Activity) getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        quantity.setText("Pomogłeś już "+data.getLong("hUsers")+" osobom");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        quantity_1.setText("Pomogłeś już "+stats.length()+" razy");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        announcements.setText("Wyraziłeś chęć pomocy "+data.getLong("myHL")+" razy");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            while (googleMapOut==null){
                                                try {
                                                    Thread.sleep(1000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            ((Activity) getContext()).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    for(int i = 0; i < stats.length(); i++){
                                                        LatLng position;
                                                        Marker marker = null;
                                                        try {
                                                            switch (stats.getJSONObject(i).getString("type")){
                                                                case "1":
                                                                    position = new LatLng(stats.getJSONObject(i).getDouble("lat"), stats.getJSONObject(i).getDouble("lon"));
                                                                    marker = googleMapOut.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                                                                    break;
                                                                case "2":
                                                                    position = new LatLng(stats.getJSONObject(i).getDouble("lat"), stats.getJSONObject(i).getDouble("lon"));
                                                                    marker = googleMapOut.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                                                    break;
                                                            }
                                                        }catch (Exception e){

                                                        }

                                                    }
                                                }
                                            });
                                        }
                                   });

                                    thread.start();

                                    if(stats.length()>5){
                                        JSONArray newArray = new JSONArray();
                                        for(int i = stats.length()-1; i>stats.length()-6; i--){
                                            try {
                                                newArray.put(stats.getJSONObject(i));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        HelpAdapter helpAdapter = new HelpAdapter(newArray);
                                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                myHelp.setAdapter(helpAdapter);
                                            }
                                        });
                                    }else{
                                        JSONArray newArray = new JSONArray();
                                        for(int i = stats.length()-1; i>-1; i--){
                                            try {
                                                newArray.put(stats.getJSONObject(i));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        HelpAdapter helpAdapter = new HelpAdapter(newArray);
                                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                myHelp.setAdapter(helpAdapter);
                                            }
                                        });
                                    }


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


        Thread getArticles = new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request(urls.URL(), urls.getArticles(), "POST", "POST");
                RequestData data = new RequestData();
                data.setData(new Data("token", preferences.getString("TOKEN", "")));
                data.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));

                Return response = fetch.fetch(request, data);
                if(getContext()!=null){
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (response.getType()){
                                case 0:
                                    JSONObject data = (JSONObject) response.getObject();
                                    try {
                                        JSONArray array = data.getJSONArray("articles");
                                        System.out.println(array);
                                        ArticlesAdapter articlesAdapter = new ArticlesAdapter(array);
                                        articles.setAdapter(articlesAdapter);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                            }
                        }
                    });
                }

            }
        });

        getArticles.start();

        return v;
    }


    public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.Holder>{
        JSONArray array = new JSONArray();

        public HelpAdapter(JSONArray array){
            this.array = array;
        }

        public class Holder extends RecyclerView.ViewHolder {
            TextView category;
            TextView quantity;
            ImageView catImg;
            public Holder(@NonNull View itemView) {
                super(itemView);
            }
        }
        @NonNull
        @Override
        public HelpAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_h_el, parent, false);
            Holder holder = new Holder(v);

            holder.category = v.findViewById(R.id.title);
            holder.quantity = v.findViewById(R.id.quantity);
            holder.catImg = v.findViewById(R.id.image);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull HelpAdapter.Holder holder, int position) {
            JSONObject stat = new JSONObject();
            try {
                stat = array.getJSONObject(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for(int i = 0; i < ids.size(); i++){
                try {
                    if(ids.get(i).equals(stat.getString("category"))){
                        String name = Arrays.asList(getResources().getStringArray(R.array.categoriesHumanIcons)).get(i).trim();
                        holder.catImg.setImageResource(getResources().getIdentifier(name, "drawable", getContext().getPackageName()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                holder.quantity.setText("Ilość pomocy w liczbach to: "+stat.getLong("quantity"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return array.length();
        }


    }


    public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.Article>{
        JSONArray array = new JSONArray();

        public ArticlesAdapter(JSONArray array){
            this.array = array;
        }

        public class Article extends RecyclerView.ViewHolder {
            TextView title;
            CardView go;
            public Article(@NonNull View itemView) {
                super(itemView);
            }
        }

        @NonNull
        @Override
        public ArticlesAdapter.Article onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_el, parent, false);
            Article article = new Article(v);
            article.title = v.findViewById(R.id.title);
            article.go = v.findViewById(R.id.go);
            return article;
        }

        @Override
        public void onBindViewHolder(@NonNull ArticlesAdapter.Article holder, int position) {
            try {
                JSONObject article = array.getJSONObject(position);
                holder.title.setText(article.getString("title"));
                holder.go.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArticleDialog articleDialog = new ArticleDialog();
                        articleDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
                        Bundle bundle = new Bundle();
                        try {
                            bundle.putString("title", article.getString("title"));
                            bundle.putString("id", article.getString("_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        articleDialog.setArguments(bundle);
                        articleDialog.show(getChildFragmentManager(), "d");
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return array.length();
        }


    }
}
