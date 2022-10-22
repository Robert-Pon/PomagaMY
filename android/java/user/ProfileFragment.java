package com.name.social_helper_r_p.user;

import static com.name.social_helper_r_p.R.drawable.bill;
import static com.name.social_helper_r_p.R.drawable.see;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.name.social_helper_r_p.LoaderAdapter;
import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.X;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.connections.URLS;
import com.name.social_helper_r_p.user.edit.AddFragment;
import com.name.social_helper_r_p.user.edit.EventFragment;
import com.name.social_helper_r_p.user.edit.MyOfferFragment;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileFragment extends Fragment {
    TextView title;
    TextView description;

    SharedPreferences preferences;

    ImageView profile;

    Fetch fetch = new Fetch();
    URLS urls = new URLS();

    RecyclerView items;

    ChipGroup types;

    String types_1="1";

    TextView type1;

    List<String> ids = new ArrayList<>();

    Context context;

    String CHOOSEN_TYPE="";

    CardView restart;
    CardView settings;

    ProfileFragmentInterface fragmentInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile, container, false);
        title = v.findViewById(R.id.title);
        profile = v.findViewById(R.id.profile);
        items = v.findViewById(R.id.items);
        types = v.findViewById(R.id.types);
        type1 = v.findViewById(R.id.type1);
        restart = v.findViewById(R.id.restart);
        settings = v.findViewById(R.id.settings);
        description = v.findViewById(R.id.description);

        context = getContext();

        ids = Arrays.asList(getResources().getStringArray(R.array.categoriesHumanIDS));

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        items.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        final Thread[] get = {req()};
        get[0].start();


        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(get[0].isAlive()){
                    get[0].interrupt();
                    get[0] = req();

                }else{
                    get[0] = req();
                }
                get[0].start();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Aby otworzyć ustawienia możesz przsunąć w lewo!", Toast.LENGTH_LONG).show();
                fragmentInterface.swipeLeft();
            }
        });

        types.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                try {
                    types_1 = getResources().getResourceName(types.getCheckedChipId()).split("/t")[1];
                    if(get[0].isAlive()){
                        get[0].interrupt();
                        get[0] = req();

                    }else{
                        get[0] = req();
                    }
                    get[0].start();
                }catch (Exception e){

                }



            }
        });




        title.setText(preferences.getString("name", "")+" "+preferences.getString("surname", ""));
        description.setText(preferences.getString("description", ""));
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentInterface = (ProfileFragmentInterface) context;
    }

    public Thread req(){
        return new Thread(new Runnable() {
            @Override
            public void run() {

                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoaderAdapter loaderAdapter = new LoaderAdapter();
                        items.setAdapter(loaderAdapter);
                        switch (types_1){
                            case "1":
                                type1.setText("Moja potrzeba pomocy");
                                break;
                            case "2":
                                type1.setText("Moja pomoc");
                                break;
                            case "3":
                                type1.setText("Moje wydarzenia charytatywne");
                                break;
                        }
                    }
                });
                try {
                    Request request = new Request(urls.URL(), urls.getProfileAn(), "POST", "POST");
                    RequestData requestData = new RequestData();
                    requestData.setData(new Data("type", types_1));
                    requestData.setData(new Data("user", preferences.getString("USER_ID", "")));

                    Return response = fetch.fetch(request, requestData);

                    switch (response.getType()){
                        case 0:
                            JSONObject data = (JSONObject) response.getObject();
                            JSONArray array = data.getJSONArray("selected");

                            ((Activity)getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CHOOSEN_TYPE = types_1;
                                    switch (types_1){
                                        case "1":
                                            MyItems myItems = new MyItems(array, "1");
                                            System.out.println(array);
                                            items.setAdapter(myItems);
                                            break;
                                        case "2":
                                            MyItems3 myItems1 = new MyItems3(array, "2");
                                            items.setAdapter(myItems1);
                                            break;
                                        case "3":
                                            MyItems2 myItems2 = new MyItems2(array);
                                            items.setAdapter(myItems2);
                                            break;
                                        case "4":
                                            MyItems4 myItems4 = new MyItems4(array);
                                            items.setAdapter(myItems4);
                                            break;
                                        case "5":
                                            MyItems4 myItems5 = new MyItems4(array);
                                            items.setAdapter(myItems5);
                                            break;

                                    }

                                }
                            });
                            break;
                    }

                    Bitmap photo = fetch.getImageFromURL(urls.URL()+urls.getProfileImage()+preferences.getString("USER_ID",""), "POST");
                    if(photo!=null){
                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                profile.setImageBitmap(photo);
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

    }

    public class MyItems extends RecyclerView.Adapter<MyItems.ViewHolder>{
        JSONArray items = new JSONArray();
        String type;
        public MyItems(JSONArray items, String type){
            this.items = items;
            this.type = type;
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            CardView bill;
            CardView edit;
            ImageView category;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.announcements_items_my, parent, false);
            ViewHolder holder = new ViewHolder(v);
            holder.bill = v.findViewById(R.id.bill);
            holder.title = v.findViewById(R.id.title);
            holder.edit = v.findViewById(R.id.edit);
            holder.category = v.findViewById(R.id.category);
            return holder;
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            try {
                JSONObject object = items.getJSONObject(position);
                JSONArray myItems = object.getJSONObject("data").getJSONArray("things");
                System.out.println("OBJ");
                System.out.println(object);
                holder.bill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        BillBottom bottom = new BillBottom();
                        Bundle data = new Bundle();
                        try {
                            data.putString("id", object.getString("_id"));
                            data.putString("type", "1");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        bottom.setArguments(data);
                        bottom.show(getChildFragmentManager(), "d");
                    }
                });

                for(int i = 0; i < ids.size(); i++){
                    try {
                        System.out.println(ids.get(i));
                        System.out.println(object.getJSONObject("data").getString("main_type"));
                        if(ids.get(i).equals(myItems.getJSONObject(0).getString("category"))){
                            String name = Arrays.asList(getResources().getStringArray(R.array.categoriesHumanIcons)).get(i).trim();
                            System.out.println("NAME");
                            System.out.println(name);
                            holder.category.setImageResource(getResources().getIdentifier(name, "drawable", getContext().getPackageName()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                holder.title.setText(myItems.getJSONObject(0).getString("title"));
                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (type){
                            case "1":
                                AddFragment addFragment = new AddFragment();
                                addFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
                                Bundle data = new Bundle();
                                try {
                                    data.putString("id", object.getString("_id"));
                                    data.putString("type", "1");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                addFragment.setArguments(data);
                                addFragment.show(getChildFragmentManager(), "d");
                                break;
                            case "2":
                                MyOfferFragment offerFragment = new MyOfferFragment();
                                offerFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
                                Bundle data1 = new Bundle();
                                try {
                                    data1.putString("id", object.getString("_id"));
                                    data1.putString("type", "2");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                offerFragment.setArguments(data1);
                                offerFragment.show(getChildFragmentManager(), "d");
                                break;

                        }

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return items.length();
        }

    }

    public class MyItems3 extends RecyclerView.Adapter<MyItems3.ViewHolder>{
        JSONArray items = new JSONArray();
        String type;
        public MyItems3(JSONArray items, String type){
            this.items = items;
            this.type = type;
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            CardView bill;
            CardView edit;
            ImageView category;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.announcements_items_my, parent, false);
            ViewHolder holder = new ViewHolder(v);
            holder.bill = v.findViewById(R.id.bill);
            holder.title = v.findViewById(R.id.title);
            holder.edit = v.findViewById(R.id.edit);
            holder.category = v.findViewById(R.id.category);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            try {
                JSONObject myItems = items.getJSONObject(position);
                holder.bill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                for(int i = 0; i < ids.size(); i++){
                    try {

                        if(ids.get(i).equals(myItems.getString("category"))){
                            String name = Arrays.asList(getResources().getStringArray(R.array.categoriesHumanIcons)).get(i).trim();
                            holder.category.setImageResource(getResources().getIdentifier(name, "drawable", getContext().getPackageName()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                holder.title.setText(myItems.getString("title"));
                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (type){
                            case "1":
                                AddFragment addFragment = new AddFragment();
                                addFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
                                Bundle data = new Bundle();
                                try {
                                    data.putString("id", myItems.getString("_id"));
                                    data.putString("type", "1");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                addFragment.setArguments(data);
                                addFragment.show(getChildFragmentManager(), "d");
                                break;
                            case "2":
                                MyOfferFragment offerFragment = new MyOfferFragment();
                                offerFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
                                Bundle data1 = new Bundle();
                                try {
                                    data1.putString("id", myItems.getString("_id"));
                                    data1.putString("type", "2");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                offerFragment.setArguments(data1);
                                offerFragment.show(getChildFragmentManager(), "d");
                                break;

                        }

                    }
                });

                holder.bill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BillBottom bottom = new BillBottom();
                        Bundle data = new Bundle();
                        try {
                            data.putString("id", myItems.getString("_id"));
                            data.putString("type", "2");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        bottom.setArguments(data);
                        bottom.show(getChildFragmentManager(), "d");
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return items.length();
        }

    }


    public class MyItems2 extends RecyclerView.Adapter<MyItems2.ViewHolder>{
        JSONArray items = new JSONArray();

        public MyItems2(JSONArray items){
            this.items = items;
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView quantity;
            CardView edit;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.announcements_items_my_2, parent, false);
            ViewHolder holder = new ViewHolder(v);
            holder.title = v.findViewById(R.id.title);
            holder.edit = v.findViewById(R.id.edit);
            holder.quantity = v.findViewById(R.id.quantity);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            try {
                JSONObject object = items.getJSONObject(position);
                holder.title.setText(object.getString("name"));
                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventFragment eventFragment = new EventFragment();
                        eventFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
                        Bundle data = new Bundle();
                        try {
                            data.putString("id", object.getString("_id"));
                            data.putString("type", "1");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        eventFragment.setArguments(data);
                        eventFragment.show(getChildFragmentManager(), "d");
                    }
                });
                holder.quantity.setText(object.getJSONArray("declared").getJSONObject(0).getLong("id")+" osób chce wziąść udział");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return items.length();
        }


    }

    public class MyItems4 extends RecyclerView.Adapter<MyItems4.ViewHolder>{
        JSONArray items = new JSONArray();

        public MyItems4(JSONArray items){
            this.items = items;
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            CardView delete;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.announcements_items_my_3, parent, false);
            ViewHolder holder = new ViewHolder(v);
            holder.title = v.findViewById(R.id.title);
            holder.delete = v.findViewById(R.id.delete);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            try {
                JSONObject object = items.getJSONObject(position);
                holder.title.setText(object.getString("title"));
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Chcesz usunąć te ogłoszenie?");
                        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Thread delete = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Request request = new Request(urls.URL(), urls.deleteAnn(), "POST", "POST");
                                        RequestData data = new RequestData();
                                        data.setData(new Data("type", CHOOSEN_TYPE));
                                        try {
                                            data.setData(new Data("id", object.getString("_id")));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        data.setData(new Data("token", preferences.getString("TOKEN", "")));
                                        data.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));
                                        fetch.fetch(request, data);
                                    }
                                });
                                delete.start();
                            }
                        });
                        builder.create().show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return items.length();
        }


    }

    public interface  ProfileFragmentInterface{
        public void swipeLeft();
    }

}
