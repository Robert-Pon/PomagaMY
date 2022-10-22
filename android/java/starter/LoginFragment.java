package com.name.social_helper_r_p.starter;

import android.app.Activity;
import android.content.Context;
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
import androidx.fragment.app.Fragment;

import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.connections.URLS;
import com.name.social_helper_r_p.user.LoaderBottom;

import org.json.JSONObject;

public class LoginFragment extends Fragment {
    EditText email;
    EditText password;

    Button login;
    Button register;

    URLS urls = new URLS();

    LoginFragmentInterface fragmentInterface;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login, container, false);

        login = v.findViewById(R.id.login);
        register = v.findViewById(R.id.register);


        email = v.findViewById(R.id.email);
        password = v.findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread send = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LoaderBottom loaderBottom = new LoaderBottom();
                        loaderBottom.show(getChildFragmentManager(), "d");
                        Fetch fetch = new Fetch();
                        Request request = new Request(urls.URL(), urls.login(), "NORMAL", "POST");
                        RequestData data = new RequestData();
                        data.setData(new Data("email", email.getText().toString()));
                        data.setData(new Data("password", password.getText().toString()));

                        Return response = fetch.fetch(request, data);

                        if(response.getType()==0){

                        }else{
                            ((Activity) getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run(){
                                    try{
                                        System.out.println("????");
                                        JSONObject object = (JSONObject) response.getObject();
                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                        SharedPreferences.Editor editor = preferences.edit();

                                        String TOKEN = object.getString("token");
                                        String TOKEN_ID = object.getString("tokenID");
                                        String USER_ID = object.getString("userID");
                                        editor.putString("TOKEN", TOKEN);
                                        editor.putString("TOKEN_ID", TOKEN_ID);
                                        editor.putString("USER_ID", USER_ID);
                                        editor.putString("email", object.getString("email"));
                                        editor.putString("name", object.getString("name"));
                                        editor.putString("surname", object.getString("surname"));
                                        try {
                                            editor.putString("description", object.getString("description"));
                                        }catch (Exception e){

                                        }
                                        editor.apply();
                                        loaderBottom.dismiss();
                                        fragmentInterface.goToMainActivity();
                                    }catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                });

                send.start();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentInterface.goToRegister();
            }
        });


        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentInterface = (LoginFragmentInterface) context;
    }

    public interface LoginFragmentInterface{
        public void goToMainActivity();
        public void goToRegister();
    }
}
