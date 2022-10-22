package com.name.social_helper_r_p.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.connections.URLS;
import com.name.social_helper_r_p.user.ans.Type1BottomSheet;
import com.name.social_helper_r_p.user.ans.Type2BottomSheet;
import com.name.social_helper_r_p.user.ans.Type3BottomSheet;
import com.name.social_helper_r_p.user.ans.Type4BottomSheet;
import com.name.social_helper_r_p.user.ans.Type5BottomSheet;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementsBottom extends BottomSheetDialogFragment {
    RecyclerView items;

    SharedPreferences manager;

    List<JSONObject> announcements = new ArrayList<>();

    AnnouncementsFragmentInterface fragmentInterface;
    Thread send;
    Bundle bundle;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_scroll, container, false);
        items = v.findViewById(R.id.items);
        bundle = getArguments();

        manager = PreferenceManager.getDefaultSharedPreferences(getContext());
        send = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                URLS urls = new URLS();
                Fetch fetch = new Fetch();
                Request request = new Request(urls.URL(), urls.getFromLocalization(), "POST", "POST");
                RequestData data = new RequestData();
                data.setData(new Data("location", bundle.getString("id")));
                data.setData(new Data("type", bundle.getString("type")));
                data.setData(new Data("types", bundle.getString("types")));
                data.setData(new Data("token", manager.getString("TOKEN", "")));
                data.setData(new Data("token_ID", manager.getString("TOKEN_ID", "")));

                Return response = fetch.fetch(request, data);

                switch (response.getType()){
                    case 0:
                        if(((Activity)getContext())!=null){
                            ((Activity) getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject json = (JSONObject) response.getObject();
                                    try {
                                        JSONArray array = json.getJSONArray("announcements");

                                        for (int i = 0; i < array.length(); i++){
                                            announcements.add(array.getJSONObject(i));
                                        }


                                    }catch (Exception e){

                                    }

                                    ItemsAdapter adapter = new ItemsAdapter(announcements);
                                    items.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                    items.setAdapter(adapter);
                                }
                            });
                        }
                        break;

                }
            }
        });

        send.start();
        return v;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        send.interrupt();
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
            ImageView anType;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

        @NonNull
        @Override
        public ItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.announcements_items, parent, false);
            ViewHolder holder = new ViewHolder(v);
            holder.title = v.findViewById(R.id.title);
            holder.chat = v.findViewById(R.id.chat);
            holder.go = v.findViewById(R.id.edit);
            holder.anType = v.findViewById(R.id.anType);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ItemsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            JSONObject x = array.get(position);
            try {
                switch (x.getString("anType")){
                    case "1":
                        JSONArray things = array.get(position).getJSONObject("data").getJSONArray("things");
                        if(things.length()>1){
                            holder.title.setText(things.getJSONObject(0).getString("title")+"...");
                        }else
                            holder.title.setText(things.getJSONObject(0).getString("title")+"...");
                        break;
                    case "2":
                        holder.anType.setImageResource(R.drawable.handshake);
                        holder.title.setText(array.get(position).getString("title"));
                        break;
                    case "3":
                        holder.anType.setImageResource(R.drawable.event);
                        holder.title.setText(array.get(position).getString("name"));
                        break;
                    case "4":
                        holder.anType.setImageResource(R.drawable.alarm);

                        holder.title.setText(array.get(position).getString("title"));
                        break;
                    case "5":
                        holder.anType.setImageResource(R.drawable.social);

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
                        if(bundle.getString("type").equals("1")){
                            fragmentInterface.goToChat(array.get(position).getJSONObject("data").getString("userID"), "", "");

                        }else{
                            fragmentInterface.goToChat(array.get(position).getString("userID"), "", "");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        switch (x.getString("anType")){
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
                    }catch (Exception e){

                    }


                }
            });
        }

        @Override
        public int getItemCount() {
            return array.size();
        }


    }

    public interface AnnouncementsFragmentInterface{
        public void goToChat(String id, String name, String surname);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentInterface = (AnnouncementsFragmentInterface) context;
    }
}
