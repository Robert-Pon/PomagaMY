package com.name.social_helper_r_p.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.ChipGroup;
import com.name.social_helper_r_p.LoaderAdapter;
import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.connections.URLS;
import com.name.social_helper_r_p.user.ans.Type1BottomSheet;
import com.name.social_helper_r_p.user.ans.Type2BottomSheet;
import com.name.social_helper_r_p.user.ans.Type3BottomSheet;
import com.name.social_helper_r_p.user.ans.Type4BottomSheet;
import com.name.social_helper_r_p.user.ans.Type5BottomSheet;
import com.name.social_helper_r_p.user.edit.AddFragment;
import com.name.social_helper_r_p.user.edit.EventFragment;
import com.name.social_helper_r_p.user.edit.MyOfferFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileViewFragment extends BottomSheetDialogFragment {
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

    Bundle bundle;

    ProfileViewFragmentInterface fragmentInterface;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_v, container, false);
        title = v.findViewById(R.id.title);
        profile = v.findViewById(R.id.profile);
        items = v.findViewById(R.id.items);
        types = v.findViewById(R.id.types);
        type1 = v.findViewById(R.id.type1);
        description = v.findViewById(R.id.description);

        context = getContext();

        bundle = getArguments();

        System.out.println(bundle.getString("id"));

        ids = Arrays.asList(getResources().getStringArray(R.array.categoriesHumanIDS));

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        items.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        final Thread[] get = {req()};
        get[0].start();

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




        title.setText("---");
        description.setText("---");
        return v;
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
                    System.out.println(bundle.getString("id"));
                    requestData.setData(new Data("user", bundle.getString("id")));
                    requestData.setData(new Data("token", preferences.getString("TOKEN", "")));
                    requestData.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));

                    Return response = fetch.fetch(request, requestData);

                    switch (response.getType()){
                        case 0:
                            JSONObject data = (JSONObject) response.getObject();
                            JSONArray array = data.getJSONArray("selected");

                            ((Activity)getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CHOOSEN_TYPE = types_1;
                                    List<JSONObject> announcements = new ArrayList<>();
                                    try {

                                        for (int i = 0; i < array.length(); i++){
                                            announcements.add(array.getJSONObject(i));
                                        }


                                    }catch (Exception e){

                                    }

                                    ItemsAdapter itemsAdapter = new ItemsAdapter(announcements);
                                    items.setAdapter(itemsAdapter);


                                }
                            });
                            break;
                    }
                    request.setPath(urls.getUser());
                    System.out.println(request.getPath());
                    Return responseUser = fetch.fetch(request, requestData);
                    switch (responseUser.getType()){
                        case 0:
                            JSONObject data = (JSONObject) responseUser.getObject();

                            ((Activity)getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println(data);
                                    try {
                                        title.setText(data.getString("name")+" "+data.getString("surname"));
                                        description.setText(data.getString("description"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            break;
                    }
                    Bitmap photo = fetch.getImageFromURL(urls.URL()+urls.getProfileImage()+bundle.getString("id"), "POST");
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

    public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder>{

        List<JSONObject> array;

        public ItemsAdapter(List<JSONObject> array){
            this.array = array;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            CardView chat;
            CardView go;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

        @NonNull
        @Override
        public ItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.announcements_items, parent, false);
            ItemsAdapter.ViewHolder holder = new ItemsAdapter.ViewHolder(v);
            holder.title = v.findViewById(R.id.title);
            holder.chat = v.findViewById(R.id.chat);
            holder.go = v.findViewById(R.id.edit);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ItemsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            try {
                switch (CHOOSEN_TYPE){
                    case "1":
                        JSONArray things = array.get(position).getJSONObject("data").getJSONArray("things");
                        if(things.length()>1){
                            holder.title.setText(things.getJSONObject(0).getString("title")+"...");
                        }else
                            holder.title.setText(things.getJSONObject(0).getString("title")+"...");
                        break;
                    case "2":
                        holder.title.setText(array.get(position).getString("title"));
                        break;
                    case "3":
                        holder.title.setText(array.get(position).getString("name"));
                        break;
                    case "4":
                        holder.title.setText(array.get(position).getString("title"));
                        break;
                    case "5":
                        holder.title.setText(array.get(position).getString("title"));
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            holder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(CHOOSEN_TYPE.equals("1")){
                            String [] s = title.getText().toString().split(" ");
                            if(s.length>1){
                                fragmentInterface.goToChat(array.get(position).getJSONObject("data").getString("userID"), s[0],s[1]);

                            }else{
                                fragmentInterface.goToChat(array.get(position).getJSONObject("data").getString("userID"), s[0],"");

                            }

                        }else{
                            String [] s = title.getText().toString().split(" ");
                            if(s.length>1){
                                fragmentInterface.goToChat(array.get(position).getString("userID"), s[0],s[1]);
                            }else{
                                fragmentInterface.goToChat(array.get(position).getString("userID"), s[0],"");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (CHOOSEN_TYPE){
                        case "1":
                            Bundle bundle = new Bundle();
                            try {
                                bundle.putString("id", (array.get(position).getString("_id")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Type1BottomSheet bottomSheet = new Type1BottomSheet();
                            bottomSheet.setArguments(bundle);
                            bottomSheet.show(getChildFragmentManager(), "d");
                            break;
                        case "2":
                            Bundle bundle1 = new Bundle();
                            try {
                                bundle1.putString("id", (array.get(position).getString("_id")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Type2BottomSheet bottomSheet1 = new Type2BottomSheet();
                            bottomSheet1.setArguments(bundle1);
                            bottomSheet1.show(getChildFragmentManager(), "d");
                            break;
                        case "3":
                            Bundle bundle2 = new Bundle();
                            try {
                                bundle2.putString("id", (array.get(position).getString("_id")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Type3BottomSheet type3BottomSheet = new Type3BottomSheet();
                            type3BottomSheet.setArguments(bundle2);
                            type3BottomSheet.show(getChildFragmentManager(), "d");
                            break;
                        case "4":
                            Bundle bundle3 = new Bundle();
                            try {
                                bundle3.putString("id", (array.get(position).getString("_id")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Type4BottomSheet type4BottomSheet = new Type4BottomSheet();
                            type4BottomSheet.setArguments(bundle3);
                            type4BottomSheet.show(getChildFragmentManager(), "d");
                            break;
                        case "5":
                            Bundle bundle4 = new Bundle();
                            try {
                                bundle4.putString("id", (array.get(position).getString("_id")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Type5BottomSheet type5BottomSheet = new Type5BottomSheet();
                            type5BottomSheet.setArguments(bundle4);
                            type5BottomSheet.show(getChildFragmentManager(), "d");
                            break;

                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return array.size();
        }


    }

    public interface ProfileViewFragmentInterface{
        public void goToChat(String id, String name, String surname);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentInterface = (ProfileViewFragmentInterface) context;
    }


}
