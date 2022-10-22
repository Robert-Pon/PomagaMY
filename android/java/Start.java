package com.name.social_helper_r_p;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.name.social_helper_r_p.starter.LoginFragment;
import com.name.social_helper_r_p.starter.RegisterFragment;
import com.name.social_helper_r_p.starter.StartFragment;

public class Start extends AppCompatActivity implements LoginFragment.LoginFragmentInterface, RegisterFragment.RegisterFragmentInterface, StartFragment.StartFragmentInterface {

    View layout;

    RegisterFragment registerFragment = new RegisterFragment();
    LoginFragment loginFragment = new LoginFragment();
    StartFragment startFragment = new StartFragment();


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        System.out.println(":??");
        System.out.println(preferences.getString("TOKEN", ""));
 //getSupportFragmentManager().beginTransaction().replace(R.id.frame, startFragment).commit();

        if(preferences.contains("TOKEN") && !preferences.getString("TOKEN", "").equals("")){
            Intent intent = new Intent(this, Main.class);
            startActivity(intent);
            finish();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, startFragment).commit();
        }
        setContentView(R.layout.start_activity);
        layout = findViewById(R.id.frame);

    }

    @Override
    public void goToMainActivity() {
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goToLogin() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, loginFragment).addToBackStack(null).commit();

    }

    @Override
    public void goToRegister() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, registerFragment).addToBackStack(null).commit();
    }
}