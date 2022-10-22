package com.name.social_helper_r_p.user.edit;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import androidx.fragment.app.DialogFragment;

import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.connections.URLS;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.google.android.material.chip.ChipGroup;
import com.name.social_helper_r_p.user.InfoBottom;
import com.name.social_helper_r_p.user.LoaderBottom;

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

public class MyOfferFragment extends DialogFragment {

    Thing things;

    String localization = "l1";
    String priority = "p1";
    String second_category = "";
    String date = "";

    Spinner category;
    Spinner secondCategory;


    Button add;


    ChipGroup localizationChips;
    ChipGroup priorityChips;

    TextView choose_date;

    LinearLayout element;

    EditText title;
    EditText description;
    EditText quantity;

    Button minus;
    Button plus;

    String main_category;

    Button send;

    URLS urls = new URLS();
    Fetch fetch = new Fetch();

    Bundle bundle;

    SharedPreferences preferences;

    LinearLayout under_el;

    List<String> secondIDS = new ArrayList<>();

    Button delete;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.i_can_help_edit, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        delete = v.findViewById(R.id.delete);
        choose_date = v.findViewById(R.id.choose_date);
        under_el = v.findViewById(R.id.under_el);

        localizationChips = v.findViewById(R.id.localization);
        priorityChips = v.findViewById(R.id.priority);
        category = v.findViewById(R.id.category);
        secondCategory = v.findViewById(R.id.second_category);

        title = v.findViewById(R.id.title);
        description = v.findViewById(R.id.description);
        quantity = v.findViewById(R.id.quantity);

        minus = v.findViewById(R.id.minus);
        plus = v.findViewById(R.id.image);

        send = v.findViewById(R.id.send);

        bundle = getArguments();

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(quantity.getText().toString())<1000){
                    quantity.setText(String.valueOf(Integer.parseInt(quantity.getText().toString())+1));
                }
            }
        });

       minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(quantity.getText().toString())>1){
                   quantity.setText(String.valueOf(Integer.parseInt(quantity.getText().toString())-1));
                }
            }
        });

        final String[] category_1 = {""};

        ViewGroup.LayoutParams params = under_el.getLayoutParams();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.categoriesHuman, android.R.layout.simple_spinner_dropdown_item);

        category.setAdapter(adapter);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int inner_position, long id) {
                category_1[0] = Arrays.asList(getResources().getStringArray(R.array.categoriesHumanIDS)).get(inner_position);
                main_category = category_1[0];
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

                ArrayAdapter<CharSequence> secondAdapter = null;
                switch (category_1[0]){
                    case "cH5":
                        secondAdapter = ArrayAdapter.createFromResource(getContext(), R.array.categoriesAssortment, android.R.layout.simple_spinner_dropdown_item);
                        secondIDS = Arrays.asList(getResources().getStringArray(R.array.categoriesAssortmentIDS));
                        break;
                    case "cH6":
                        secondAdapter = ArrayAdapter.createFromResource(getContext(), R.array.categoriesAnimals, android.R.layout.simple_spinner_dropdown_item);
                        secondIDS = Arrays.asList(getResources().getStringArray(R.array.categoriesAssortmentIDS));
                        break;
                    case "cH9":
                        secondAdapter = ArrayAdapter.createFromResource(getContext(), R.array.categoriesLearning, android.R.layout.simple_spinner_dropdown_item);
                        secondIDS = Arrays.asList(getResources().getStringArray(R.array.categoriesAssortmentIDS));
                        break;
                    default:
                        params.height = 0;
                        break;
                }
                under_el.setLayoutParams(params);
                secondCategory.setAdapter(secondAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        secondCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                second_category = secondIDS.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format((new Date((new Date()).getTime()+(1000*60*60*24))));

        choose_date.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format((new Date((new Date()).getTime()+(1000*60*60*24)))));
        localizationChips.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                localization = getResources().getResourceName(localizationChips.getCheckedChipId()).split("/")[1];
            }
        });
        priorityChips.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                priority = getResources().getResourceName(priorityChips.getCheckedChipId()).split("/")[1];
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

        add = v.findViewById(R.id.image);

        send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                LoaderBottom loaderBottom = new LoaderBottom();
                loaderBottom.show(getChildFragmentManager(), "d");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                URLS urls = new URLS();
                Request request = new Request(urls.URL(), urls.updateType2(), "", "POST");
                RequestData data = new RequestData();
                data.setData(new Data("title", title.getText().toString()));
                data.setData(new Data("id", bundle.getString("id")));
                data.setData(new Data("description", description.getText().toString()));
                data.setData(new Data("localization", localization));
                System.out.println(date);
                data.setData(new Data("date", date));
                data.setData(new Data("quantity", quantity.getText().toString()));
                data.setData(new Data("category", main_category));
                data.setData(new Data("secondCategory", second_category));
                data.setData(new Data("token", preferences.getString("TOKEN", "")));
                data.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));

                Thread send = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Fetch fetch = new Fetch();
                        Return response = fetch.fetch(request, data);
                        switch (response.getType()){
                            case 0:
                                InfoBottom infoBottom = new InfoBottom();
                                Bundle data = new Bundle();
                                data.putString("description", "Pomyślnie zaktualizowano \nogłoszenie");
                                data.putInt("icon", R.drawable.happy);
                                infoBottom.setArguments(data);
                                infoBottom.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog);
                                infoBottom.show(getParentFragmentManager(), "d");
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
                        loaderBottom.dismiss();
                    }
                });

                send.start();
            }
        });


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
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void run() {
                                try {

                                    title.setText(finalAnnouncement.getString("title"));
                                    description.setText(finalAnnouncement.getString("description"));
                                    quantity.setText(finalAnnouncement.getString("quantity"));
                                    List<String> ids = Arrays.asList(getResources().getStringArray(R.array.categoriesHumanIDS));

                                    for(int i = 0; i < ids.size(); i++){
                                        try {
                                            if(ids.get(i).equals(finalAnnouncement.getString("category"))){
                                                main_category = finalAnnouncement.getString("category");
                                                category.setSelection(i);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    try {
                                        localizationChips.check(getResources().getIdentifier(finalAnnouncement.getString("localization"), "id",getContext().getPackageName()));
                                        localization = finalAnnouncement.getString("localization");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                    try {
                                        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(finalAnnouncement.getLong("endingDate")/1000+3600*2, 0, ZoneOffset.UTC);
                                        date = formatter.format(dateTime).toString();
                                        choose_date.setText(date);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    try {
                                        priorityChips.check(getResources().getIdentifier(finalAnnouncement.getString("priority"), "id",getContext().getPackageName()));
                                        priority = finalAnnouncement.getString("priority");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    List<String> secondAdapter = null;
                                    switch (main_category){
                                        case "cH5":
                                            secondAdapter = Arrays.asList(getResources().getStringArray(R.array.categoriesAssortmentIDS));
                                            break;
                                        case "cH6":
                                            secondAdapter = Arrays.asList(getResources().getStringArray(R.array.categoriesAnimalsIDS));
                                            break;
                                        case "cH9":
                                            secondAdapter = Arrays.asList(getResources().getStringArray(R.array.categoriesLearningIDS));
                                            break;
                                    }

                                    for(int i = 0; i < ids.size(); i++){
                                        try {
                                            if(secondAdapter.get(i).equals(finalAnnouncement.getString("secondCategory"))){
                                                second_category = finalAnnouncement.getString("secondCategory");
                                                secondCategory.setSelection(i);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        break;
                }
            }
        });

        get.start();

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getStringifyJSONArray(List<Thing> things){
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


}
