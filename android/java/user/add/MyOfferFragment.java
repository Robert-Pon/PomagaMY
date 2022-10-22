package com.name.social_helper_r_p.user.add;

import android.app.Activity;
import android.app.DatePickerDialog;
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

import org.json.JSONObject;

import java.text.SimpleDateFormat;
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

    LinearLayout under_el;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.i_can_help, container, false);



        choose_date = v.findViewById(R.id.choose_date);

        localizationChips = v.findViewById(R.id.localization);
        priorityChips = v.findViewById(R.id.priority);
        category = v.findViewById(R.id.category);
        secondCategory = v.findViewById(R.id.second_category);
        under_el = v.findViewById(R.id.under_el);

        title = v.findViewById(R.id.title);
        description = v.findViewById(R.id.description);
        quantity = v.findViewById(R.id.quantity);

        minus = v.findViewById(R.id.minus);
        plus = v.findViewById(R.id.image);

        send = v.findViewById(R.id.send);


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
                        break;
                    case "cH6":
                        secondAdapter = ArrayAdapter.createFromResource(getContext(), R.array.categoriesAnimals, android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case "cH9":
                        secondAdapter = ArrayAdapter.createFromResource(getContext(), R.array.categoriesLearning, android.R.layout.simple_spinner_dropdown_item);
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



        add = v.findViewById(R.id.image);

        send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                LoaderBottom loaderBottom = new LoaderBottom();
                loaderBottom.show(getChildFragmentManager(), "d");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                URLS urls = new URLS();
                Request request = new Request(urls.URL(), urls.addType2(), "", "POST");
                RequestData data = new RequestData();
                data.setData(new Data("title", title.getText().toString()));
                data.setData(new Data("description", description.getText().toString()));
                data.setData(new Data("localization", localization));
                data.setData(new Data("date", date));
                data.setData(new Data("quantity", quantity.getText().toString()));
                data.setData(new Data("category", main_category));
                data.setData(new Data("get", "0"));
                data.setData(new Data("secondCategory", second_category));
                data.setData(new Data("token", preferences.getString("TOKEN", "")));
                data.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));

                Thread send = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Fetch fetch = new Fetch();
                        Return response = fetch.fetch(request, data);
                        loaderBottom.dismiss();
                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (response.getType()){
                                    case 0:
                                        InfoBottom infoBottom = new InfoBottom();
                                        Bundle data = new Bundle();
                                        data.putString("description", "Pomyślnie dodano \nogłoszenie");
                                        data.putInt("icon", R.drawable.happy);
                                        infoBottom.setArguments(data);
                                        infoBottom.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog);
                                        infoBottom.show(getParentFragmentManager(), "d");
                                        dismiss();
                                        break;
                                    case 1:
                                        InfoBottom infoBottom1 = new InfoBottom();
                                        Bundle data1 = new Bundle();
                                        data1.putString("description", "Wystąpił nieoczekiwany błąd,\n spróbuj ponownie później");
                                        data1.putInt("icon", R.drawable.error);
                                        infoBottom1.setArguments(data1);
                                        infoBottom1.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog);
                                        infoBottom1.show(getParentFragmentManager(), "d");
                                        dismiss();
                                        break;
                                }

                            }
                        });
                    }
                });

                send.start();

            }
        });




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
                thing.put("secondCategory", 0);
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
