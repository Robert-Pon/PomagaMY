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
import android.widget.Toast;

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

public class RegisterFragment extends Fragment {
    EditText email;
    EditText name;
    EditText surname;
    EditText password;
    EditText secondPassword;
    EditText village;
    EditText street;
    EditText region;

    Button login;
    Button register;

    RegisterFragmentInterface fragmentInterface;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.register, container, false);

        email = v.findViewById(R.id.email);
        name = v.findViewById(R.id.name);
        surname = v.findViewById(R.id.surname);
        password = v.findViewById(R.id.password);
        secondPassword = v.findViewById(R.id.secondPassword);
        village = v.findViewById(R.id.village);
        street = v.findViewById(R.id.street);
        region = v.findViewById(R.id.region);

        login = v.findViewById(R.id.login);
        register = v.findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if(password.getText().toString().length()>5) {
                    if (password.getText().toString().equals(secondPassword.getText().toString())) {
                        Thread send = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                LoaderBottom loaderBottom = new LoaderBottom();
                                loaderBottom.show(getChildFragmentManager(), "d");
                                URLS urls = new URLS();
                                Fetch fetch = new Fetch();
                                Request request = new Request(urls.URL(), urls.register(), "NORMAL", "POST");
                                RequestData data = new RequestData();
                                data.setData(new Data("email", email.getText().toString()));
                                data.setData(new Data("name", name.getText().toString()));
                                data.setData(new Data("surname", surname.getText().toString()));
                                data.setData(new Data("password", password.getText().toString()));
                                data.setData(new Data("region", region.getText().toString()));
                                data.setData(new Data("village", village.getText().toString()));
                                data.setData(new Data("street", street.getText().toString()));
                                data.getData().forEach(e -> {
                                    System.out.println("?");
                                });

                                Return response = fetch.fetch(request, data);
                                ((Activity) getContext()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (response.getType() == 1) {

                                            try {
                                                JSONObject object = (JSONObject) response.getObject();
                                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                                SharedPreferences.Editor editor = preferences.edit();
                                                System.out.println(object.getString("token"));
                                                String TOKEN = object.getString("token");
                                                String TOKEN_ID = object.getString("tokenID");
                                                String USER_ID = object.getString("userID");
                                                editor.putString("TOKEN", TOKEN);
                                                editor.putBoolean("reg1", true);
                                                editor.putString("TOKEN_ID", TOKEN_ID);
                                                editor.putString("USER_ID", USER_ID);
                                                editor.apply();
                                                loaderBottom.dismiss();
                                                fragmentInterface.goToMainActivity();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                        } else {
                                            Toast.makeText(getContext(), "Coś poszło nie tak, spróbuj później", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });
                        send.start();
                    }
                }else{
                    Toast.makeText(getContext(), "Hasło musi mieć przynajmniej 6 znaków", Toast.LENGTH_LONG).show();
                }

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentInterface.goToLogin();
            }
        });
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentInterface = (RegisterFragmentInterface) context;
    }

    public interface RegisterFragmentInterface{
        public void goToMainActivity();
        public void goToLogin();
    }
}
