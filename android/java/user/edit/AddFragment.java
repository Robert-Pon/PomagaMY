package com.name.social_helper_r_p.user.edit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.connections.URLS;
import com.name.social_helper_r_p.user.InfoBottom;
import com.name.social_helper_r_p.user.LoaderBottom;
import com.name.social_helper_r_p.user.add.Thing;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddFragment extends DialogFragment {

    List<com.name.social_helper_r_p.user.add.Thing> things = new ArrayList<>();

    String localization = "l1";
    String priority = "p1";
    String person = "pe1";
    String main_type = "c1";
    String date = "";
    RecyclerView scrollList;

    Spinner type;

    Button add;

    Button add_option;

    ChipGroup localizationChips;
    ChipGroup priorityChips;
    ChipGroup personChips;

    TextView choose_date;

    URLS urls = new URLS();

    Bundle bundle;

    SharedPreferences preferences;

    Fetch fetch = new Fetch();

    String id="";
    List<String> ids = new ArrayList<>();

    List<CharSequence> secondIDS = new ArrayList<>();

    Adapter adapter;

    Button delete;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.help_human_edit, container, false);

        ids = Arrays.asList(getResources().getStringArray(R.array.categoriesHumanIDS));

        scrollList = v.findViewById(R.id.list);

        adapter = new Adapter(things);
        delete = v.findViewById(R.id.delete);

        type = v.findViewById(R.id.type);

        choose_date = v.findViewById(R.id.choose_date);

        localizationChips = v.findViewById(R.id.localization);
        priorityChips = v.findViewById(R.id.priority);
        personChips = v.findViewById(R.id.person);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        bundle = getArguments();

        delete.setOnClickListener(new View.OnClickListener() {
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
                                data.setData(new Data("type", "1"));
                                try {
                                    data.setData(new Data("id", bundle.getString("id")));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                data.setData(new Data("token", preferences.getString("TOKEN", "")));
                                data.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));
                                Return response = fetch.fetch(request, data);

                                switch (response.getType()){
                                    case 0:
                                        InfoBottom infoBottom1 = new InfoBottom();
                                        Bundle data1 = new Bundle();
                                        data1.putString("description", "Pomyślnie usunięto \nogłoszenie");
                                        data1.putInt("icon", R.drawable.happy);
                                        infoBottom1.setArguments(data1);
                                        infoBottom1.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog);
                                        infoBottom1.show(getParentFragmentManager(), "d");
                                        dismiss();
                                        break;
                                    case 1:
                                        InfoBottom infoBottom2 = new InfoBottom();
                                        Bundle data2 = new Bundle();
                                        data2.putString("description", "Wystąpił nieoczekiwany błąd,\n spróbuj ponownie później");
                                        data2.putInt("icon", R.drawable.happy);
                                        infoBottom2.setArguments(data2);
                                        infoBottom2.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog);
                                        infoBottom2.show(getParentFragmentManager(), "d");
                                        break;
                                }
                            }
                        });
                        delete.start();
                    }
                });
                builder.create().show();
            }
        });

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.categories, android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(typeAdapter);

        scrollList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
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
                        try {
                            JSONObject announcement = data.getJSONObject("announcements");
                            JSONObject user = data.getJSONObject("user");
                            JSONObject innerData = announcement.getJSONObject("data");
                            JSONArray arrayThings = innerData.getJSONArray("things");
                            List<com.name.social_helper_r_p.user.add.Thing> newThings = new ArrayList<>();
                            for(int i =0; i < arrayThings.length(); i++){
                                newThings.add(new com.name.social_helper_r_p.user.add.Thing(arrayThings.getJSONObject(i).getString("title"),
                                        arrayThings.getJSONObject(i).getString("description"),
                                        arrayThings.getJSONObject(i).getInt("quantity"),
                                        arrayThings.getJSONObject(i).getString("category"),
                                        arrayThings.getJSONObject(i).getString("secondCategory"),
                                        0
                                        ));
                            }
                            things.addAll(newThings);
                            ((Activity) getContext()).runOnUiThread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @SuppressLint("ResourceType")
                                @Override
                                public void run() {

                                    adapter = new Adapter(things);
                                    scrollList.setAdapter(adapter);
                                    try {
                                        localizationChips.check(getResources().getIdentifier(innerData.getString("localization"), "id",getContext().getPackageName()));
                                        localization = innerData.getString("localization");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        priorityChips.check(getResources().getIdentifier(innerData.getString("priority"), "id",getContext().getPackageName()));
                                        priority = innerData.getString("priority");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        personChips.check(getResources().getIdentifier(innerData.getString("person"), "id",getContext().getPackageName()));
                                        person = innerData.getString("person");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                    List<String> ids = Arrays.asList(getResources().getStringArray(R.array.categoriesIDS));

                                    for(int i = 0; i < ids.size(); i++){
                                        try {
                                            if(ids.get(i).equals(innerData.getString("main_type"))){
                                                main_type = innerData.getString("main_type");
                                                type.setSelection(i);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    try {
                                        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(innerData.getLong("endingDate")/1000+3600*2, 0, ZoneOffset.UTC);
                                        date = formatter.format(dateTime).toString();
                                        choose_date.setText(date);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
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

        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format((new Date((new Date()).getTime()+(1000*60*60*24))));

        choose_date.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format((new Date((new Date()).getTime()+(1000*60*60*24)))));
        localizationChips.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                try {
                    System.out.println(localizationChips.getCheckedChipId());
                    localization = getResources().getResourceName(localizationChips.getCheckedChipId()).split("/")[1];
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        priorityChips.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                try {
                    priority = getResources().getResourceName(priorityChips.getCheckedChipId()).split("/")[1];
                }catch (Exception e){

                }
            }
        });

        personChips.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                try {
                    person = getResources().getResourceName(personChips.getCheckedChipId()).split("/")[1];
                }catch (Exception e){

                }
            }
        });



        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                main_type = Arrays.asList(getResources().getStringArray(R.array.categoriesIDS)).get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        choose_date.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                DatePickerDialog pickerDialog = new DatePickerDialog(getContext());
                pickerDialog.show();

                pickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        choose_date.setText(dayOfMonth+"-"+month+"-"+year);
                        if(month+1<10){
                            date = year+"-0"+(month+1)+"-"+dayOfMonth;

                        }else{
                            date = year+"-"+(month+1)+"-"+dayOfMonth;
                        }
                    }
                });
            }
        });

        scrollList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        scrollList.setAdapter(adapter);
        scrollList.setNestedScrollingEnabled(false);

        add = v.findViewById(R.id.image);
        add_option = v.findViewById(R.id.add_option);
        add.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                LoaderBottom bottom = new LoaderBottom();
                bottom.show(getChildFragmentManager(), "d");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                URLS urls = new URLS();
                Request request = new Request(urls.URL(), urls.updateType1(), "", "POST");
                RequestData data = new RequestData();
                data.setData(new Data("main_type", main_type));
                data.setData(new Data("id", bundle.getString("id")));
                data.setData(new Data("localization", localization));
                data.setData(new Data("priority", priority));
                data.setData(new Data("person", person));
                data.setData(new Data("endingDate", date));
                data.setData(new Data("token", preferences.getString("TOKEN", "")));
                data.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));
                data.setData(new Data("things", getStringifyJSONArray(things)));

                Thread send = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Fetch fetch = new Fetch();
                        Return response = fetch.fetch(request, data);
                        switch (response.getType()){
                            case 0:
                                InfoBottom infoBottom = new InfoBottom();
                                Bundle data = new Bundle();
                                data.putString("description", "Pomyślnie zaktualizowano \nzgłoszenie");
                                data.putInt("icon", R.drawable.happy);
                                infoBottom.setArguments(data);
                                infoBottom.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog);
                                infoBottom.show(getParentFragmentManager(), "d");
                                bottom.dismiss();
                                break;
                            case 1:
                                InfoBottom infoBottom1 = new InfoBottom();
                                Bundle data1 = new Bundle();
                                data1.putString("description", "Wystąpił nieoczekiwany błąd,\n spróbuj ponownie później");
                                data1.putInt("icon", R.drawable.happy);
                                infoBottom1.setArguments(data1);
                                infoBottom1.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog);
                                infoBottom1.show(getParentFragmentManager(), "d");
                                dismiss();
                                break;
                        }
                    }
                });

                send.start();
            }
        });

        add_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CK");
                things.add(new Thing());
                adapter.updateList(things);
//                adapter = new Adapter(things);
//                scrollList.setAdapter(adapter);
            }
        });



        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getStringifyJSONArray(List<com.name.social_helper_r_p.user.add.Thing> things){
        String string = "[";
        for (int i = 0; i < things.size(); i++){
            try{
                JSONObject thing = new JSONObject();
                thing.put("title", things.get(i).getTitle().toString().replace("\\", "\\\\").replace("\"", "\\\""));
                thing.put("description", things.get(i).getDescription().toString().replace("\\", "\\\\").replace("\"", "\\\""));
                thing.put("quantity", things.get(i).getQuantity());
                thing.put("category", things.get(i).getCategory());
                thing.put("secondCategory", things.get(i).getSecondCategory());
                thing.put("get", things.get(i).getGet());
                if(i<things.size()-1){
                    string+=(thing.toString()+",");
                }else{
                    string+=(thing.toString());
                }
            }catch (Exception e){

            }

        }
        string+="]";
        return string;
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

        int length = 0;

        List<com.name.social_helper_r_p.user.add.Thing> r1 = new ArrayList<>();
        public Adapter(List<com.name.social_helper_r_p.user.add.Thing> r){
            r1.addAll(r);
        }

        public void updateList(List<Thing> r){
           r1.clear();
           r1.addAll(r);
           notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout element;

            EditText title;
            EditText description;
            EditText quantity;

            Button minus;
            Button plus;

            Spinner category;
            Spinner secondCategory;

            CardView delete;
            CardView hide;

            LinearLayout under_el;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }


        @RequiresApi(api = Build.VERSION_CODES.N)
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.thing_resources, parent, false);
            ViewHolder holder = new ViewHolder(v);

            holder.category = v.findViewById(R.id.category);
            holder.secondCategory = v.findViewById(R.id.second_category);

            holder.delete = v.findViewById(R.id.delete);
            holder.hide = v.findViewById(R.id.hide);

            holder.title = v.findViewById(R.id.title);
            holder.description = v.findViewById(R.id.description);
            holder.quantity = v.findViewById(R.id.quantity);

            holder.minus = v.findViewById(R.id.minus);
            holder.plus = v.findViewById(R.id.image);

            holder.element = v.findViewById(R.id.element);
            holder.under_el = v.findViewById(R.id.under_el);
            System.out.println("Holder");
            return holder;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            final String[] category = {""};

            ViewGroup.LayoutParams params = holder.under_el.getLayoutParams();
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.categoriesHuman, android.R.layout.simple_spinner_dropdown_item);

            holder.category.setAdapter(adapter);

            for(int i = 0; i < ids.size(); i++){
                try {
                    if(ids.get(i).equals(things.get(position).getCategory())){
                        holder.category.setSelection(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            holder.category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int inner_position, long id) {
                    category[0] = Arrays.asList(getResources().getStringArray(R.array.categoriesHumanIDS)).get(inner_position);
                    things.get(position).setCategory(category[0]);
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

                    ArrayAdapter<CharSequence> secondAdapter = null;
                    switch (category[0]){
                        case "cH5":
                            secondAdapter = ArrayAdapter.createFromResource(getContext(), R.array.categoriesAssortment, android.R.layout.simple_spinner_dropdown_item);
                            secondIDS = Arrays.asList(getResources().getStringArray(R.array.categoriesAssortmentIDS));
                            break;
                        case "cH6":
                            secondAdapter = ArrayAdapter.createFromResource(getContext(), R.array.categoriesAnimals, android.R.layout.simple_spinner_dropdown_item);
                            secondIDS = Arrays.asList(getResources().getStringArray(R.array.categoriesAnimalsIDS));
                            break;
                        case "cH9":
                            secondAdapter = ArrayAdapter.createFromResource(getContext(), R.array.categoriesLearning, android.R.layout.simple_spinner_dropdown_item);
                            secondIDS = Arrays.asList(getResources().getStringArray(R.array.categoriesLearningIDS));

                            break;
                        default:
                            params.height = 0;
                            break;
                    }
                    holder.under_el.setLayoutParams(params);
                    holder.secondCategory.setAdapter(secondAdapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            holder.title.setText(things.get(position).getTitle());
            holder.description.setText(things.get(position).getDescription());
            holder.quantity.setText(String.valueOf(things.get(position).getQuantity())  );

            holder.title.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    things.get(position).setTitle(holder.title.getText().toString());
                }
            });

            holder.description.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    things.get(position).setDescription(holder.description.getText().toString());
                }
            });

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
                        things.get(position).setQuantity(Integer.parseInt(holder.quantity.getText().toString()));
                    }else{
                        holder.quantity.setText("1");
                    }
                }
            });

            holder.plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(things.get(position).getQuantity()<1000){
                        holder.quantity.setText(String.valueOf(things.get(position).getQuantity()+1));
                    }
                }
            });

            holder.minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(things.get(position).getQuantity()>1){
                        holder.quantity.setText(String.valueOf(things.get(position).getQuantity()-1));
                    }
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    things.remove(position);
                    notifyDataSetChanged();
                }
            });

            holder.secondCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    things.get(position).setSecondCategory((String) secondIDS.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            holder.hide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewGroup.LayoutParams params = holder.element.getLayoutParams();

                    if(((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 71, getResources().getDisplayMetrics()))!=params.height){
                        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 71, getResources().getDisplayMetrics());
                        holder.element.setLayoutParams(params);
                    }else{
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        holder.element.setLayoutParams(params);
                    }

                }
            });

        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }
        @Override
        public int getItemCount() {
            return things.size();
        }


    }
}
