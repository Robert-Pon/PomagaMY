package com.name.social_helper_r_p.user;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.connections.URLS;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PasswordBottom extends BottomSheetDialogFragment {
    EditText password;
    EditText password_1;

    Button send;

    Fetch fetch = new Fetch();
    URLS urls = new URLS();

    SharedPreferences preferences;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.password_bottom, container, false);

        password = v.findViewById(R.id.pass_1);
        password_1 = v.findViewById(R.id.pass_2);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        send = v.findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread send = new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        LoaderBottom loaderBottom = new LoaderBottom();
                        loaderBottom.show(getChildFragmentManager(), "d");
                        Request request = new Request(urls.URL(), urls.setPassword(), "POST", "POST");
                        RequestData requestData = new RequestData();
                        requestData.setData(new Data("p1", password.getText().toString()));
                        requestData.setData(new Data("p2", password_1.getText().toString()));
                        requestData.setData(new Data("token", preferences.getString("TOKEN", "")));
                        requestData.setData(new Data("token_ID",  preferences.getString("TOKEN_ID", "")));
                        Return response = fetch.fetch(request, requestData);

                        switch (response.getType()){
                            case 0:
                                loaderBottom.dismiss();
                                dismiss();
                                break;

                        }
                    }
                });
                send.start();

            }
        });
        return v;
    }
}
