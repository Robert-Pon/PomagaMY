package com.name.social_helper_r_p.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.X;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.connections.URLS;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class BillBottom extends BottomSheetDialogFragment {


    SharedPreferences preferences;

    URLS urls = new URLS();
    Fetch fetch = new Fetch();

    Bundle bundle;

    RecyclerView items;

    List<Integer> quantity = new ArrayList<>();

    Button next;

    BillBottomInterface fragmentInterface;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bill_bottom, container, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        bundle = getArguments();

        items = v.findViewById(R.id.items);

        next = v.findViewById(R.id.next);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putString("id", bundle.getString("id"));
                data.putString("type", bundle.getString("type"));

                switch (bundle.getString("type")){
                    case "1":

                        data.putIntegerArrayList("quantity", (ArrayList<Integer>) quantity);

                        fragmentInterface.setGetUpdateData(data);


                        break;
                    case "2":
                        data.putInt("quantity", quantity.get(0));

                        fragmentInterface.setGetUpdateData(data);

                        break;
                }

                IntentIntegrator integrator = new IntentIntegrator((Activity) getContext());
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Zeskanuj kod QR profilu");
                integrator.setOrientationLocked(true);
                integrator.setCaptureActivity(X.class);
                integrator.setBeepEnabled(false);
                integrator.initiateScan();

            }
        });


        Thread get = new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request(urls.URL(), urls.getAnn(), "POST", "POST");
                RequestData requestData = new RequestData();
                requestData.setData(new Data("id", bundle.getString("id")));
                requestData.setData(new Data("type", bundle.getString("type")));
                requestData.setData(new Data("token", preferences.getString("TOKEN", "")));
                requestData.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));
                Return response = fetch.fetch(request, requestData);
                System.out.println(response.getType());
                switch (response.getType()){
                    case 0:
                        JSONObject data = (JSONObject) response.getObject();
                        switch (bundle.getString("type")){
                            case "1":
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
                                            Type1Adapter adapter = new Type1Adapter(newThings);
                                            items.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                            items.setAdapter(adapter);

                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "2":
                                try {
                                    JSONObject announcement = data.getJSONObject("announcements");
                                    JSONObject user = data.getJSONObject("user");
                                    List<JSONObject> newThings = new ArrayList<>();
                                    newThings.add(announcement);
                                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Type1Adapter adapter = new Type1Adapter(newThings);
                                            items.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                            items.setAdapter(adapter);

                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;

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
            Button plus;
            Button minus;
            EditText quantity;
            public Thing(@NonNull View itemView) {
                super(itemView);
            }
        }

        @NonNull
        @Override
        public Type1Adapter.Thing onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.thing_counter, parent, false);
            Type1Adapter.Thing thing = new Type1Adapter.Thing(v);
            thing.plus = v.findViewById(R.id.plus);
            thing.minus = v.findViewById(R.id.minus);
            thing.quantity = v.findViewById(R.id.quantity);
            thing.title = v.findViewById(R.id.title);
            quantity.add(0);
            return thing;
        }

        @Override
        public void onBindViewHolder(@NonNull Type1Adapter.Thing holder, @SuppressLint("RecyclerView") int position) {
            try {
                holder.title.setText(things.get(position).getString("title"));
                long need = things.get(position).getLong("quantity")-things.get(position).getLong("get");
                holder.quantity.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(!holder.quantity.getText().toString().equals("")){
                            quantity.set(position, Integer.parseInt(holder.quantity.getText().toString()));
                        }else{
                            quantity.set(position,0);
                            holder.quantity.setText("0");
                        }
                    }
                });

                holder.plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if(Integer.parseInt(holder.quantity.getText().toString())<need){
                                holder.quantity.setText(String.valueOf(Integer.parseInt(holder.quantity.getText().toString())+1));
                            }
                        } catch (Exception e) {
                            try {
                                if(Integer.parseInt(holder.quantity.getText().toString())<need){
                                    holder.quantity.setText(String.valueOf(Integer.parseInt(holder.quantity.getText().toString())+1));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            e.printStackTrace();
                        }
                    }
                });

                holder.minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if(Integer.parseInt(holder.quantity.getText().toString())>0){
                                holder.quantity.setText(String.valueOf(Integer.parseInt(holder.quantity.getText().toString())-1));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
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
        fragmentInterface = (BillBottomInterface) context;
    }

    public interface BillBottomInterface{
        public void setGetUpdateData(Bundle data);
    }
}
