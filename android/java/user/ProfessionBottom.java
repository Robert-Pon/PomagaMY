package com.name.social_helper_r_p.user;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.connections.URLS;

import java.util.Arrays;
import java.util.List;

public class ProfessionBottom extends BottomSheetDialogFragment {
    Spinner profession;

    Button update;

    Fetch fetch = new Fetch();
    URLS urls = new URLS();

    String professionS = "p1";

    List<String> ids;

    Bundle bundle;

    SharedPreferences preferences;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profession_bottom, container, false);

        bundle = getArguments();

        profession = v.findViewById(R.id.profession);

        update = v.findViewById(R.id.update);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        ArrayAdapter<CharSequence> professions = ArrayAdapter.createFromResource(getContext(), R.array.profession, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

        ids = Arrays.asList(getResources().getStringArray(R.array.professionIDS));
        profession.setAdapter(professions);

        profession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                professionS = ids.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                LoaderBottom loaderBottom = new LoaderBottom();
                                loaderBottom.show(getChildFragmentManager(), "d");
                                Request request = new Request(urls.URL(), urls.setProfession(), "POST", "POST");
                                RequestData requestData = new RequestData();
                                requestData.setData(new Data("profession", professionS));
                                requestData.setData(new Data("token", preferences.getString("TOKEN", "")));
                                requestData.setData(new Data("token_ID",  preferences.getString("TOKEN_ID", "")));

                                Return response = fetch.fetch(request, requestData);

                                switch (response.getType()){
                                    case 0:
//                                        SharedPreferences.Editor editor = preferences.edit();
//                                        editor.putString("profession", profession);
//                                        editor.apply();
                                        loaderBottom.dismiss();
                                        dismiss();
                                        break;
                                }
                            }
                        });
                        thread.start();
            }
        });


        return v;
    }
}
