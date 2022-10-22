package com.name.social_helper_r_p.user.ans;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.connections.URLS;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class VoteBottom extends BottomSheetDialogFragment {
    Button accept;

    URLS urls = new URLS();
    Fetch fetch = new Fetch();

    Bundle bundle;

    SharedPreferences preferences;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.support_bottom, container, false);
        accept = v.findViewById(R.id.accept);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        bundle = getArguments();
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread send = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Request request = new Request(urls.URL(), urls.vote(), "POST","POST");
                        RequestData data = new RequestData();
                        data.setData(new Data("support", bundle.getString("support")));
                        data.setData(new Data("id", bundle.getString("id")));
                        data.setData(new Data("token", preferences.getString("TOKEN", "")));
                        data.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));

                        Return response = fetch.fetch(request, data);

                        switch (response.getType()){
                            case 0:
                                if(getContext()!=null){
                                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismiss();
                                        }
                                    });
                                }
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
