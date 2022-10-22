package com.name.social_helper_r_p;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.user.BillBottom;
import com.name.social_helper_r_p.user.InfoBottom;
import com.name.social_helper_r_p.user.LoaderBottom;
import com.name.social_helper_r_p.user.ProfileFragment;
import com.name.social_helper_r_p.user.ProfileViewFragment;
import com.name.social_helper_r_p.user.SearchBottom;
import com.name.social_helper_r_p.connections.URLS;
import com.name.social_helper_r_p.user.AnnouncementsBottom;
import com.name.social_helper_r_p.user.ChatFragment;
import com.name.social_helper_r_p.user.ChatsFragment;
import com.name.social_helper_r_p.user.FiltersBottom;
import com.name.social_helper_r_p.user.FragmentSwitcher;
import com.name.social_helper_r_p.user.MapFragment;
import com.name.social_helper_r_p.user.SettingsFragment;
import com.name.social_helper_r_p.user.SocketListener;
import com.name.social_helper_r_p.user.StartInfo;
import com.name.social_helper_r_p.user.StatsFragment;
import com.name.social_helper_r_p.user.ans.Type1BottomSheet;
import com.name.social_helper_r_p.user.ans.Type2BottomSheet;
import com.name.social_helper_r_p.user.ans.Type3BottomSheet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

public class Main extends AppCompatActivity implements
        AnnouncementsBottom.AnnouncementsFragmentInterface,
        SocketListener.SocketListenerInterface,
        FiltersBottom.FiltersBottomInterface,
        SearchBottom.SearchBottomInterface,
        SettingsFragment.SettingsFragmentInterface,
        ChatFragment.ChatInterface,
        Type1BottomSheet.Type1BottomSheetInterface,
        Type2BottomSheet.Type2BottomSheetInterface,
        Type3BottomSheet.Type3BottomSheetInterface,
        BillBottom.BillBottomInterface,
        ProfileViewFragment.ProfileViewFragmentInterface,
        ProfileFragment.ProfileFragmentInterface

{

    Tag tag;



    MapFragment mapFragment = new MapFragment();

    BottomNavigationView navigation;

    ChatsFragment chatsFragment = new ChatsFragment();

    FragmentSwitcher fragmentSwitcher = new FragmentSwitcher();

    StatsFragment statsFragment = new StatsFragment();

    String id = "";

    PendingIntent pendingIntent;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket((new URLS()).URL());
        } catch (URISyntaxException e) {}
    }


SharedPreferences manager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        navigation = findViewById(R.id.navigation);
        manager = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(manager.getBoolean("reg1",false )){
            SharedPreferences.Editor editor = manager.edit();
            editor.remove("reg1");
            editor.apply();

            StartInfo startInfo = new StartInfo();
            startInfo.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
            startInfo.show(getSupportFragmentManager(), "d");
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, mapFragment).commit();

        navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.map:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, mapFragment).commit();
                    break;
                    case R.id.chat:
                        Bundle data = new Bundle();
                        data.putString("action", "goToList");
                        chatsFragment.setArguments(data);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, chatsFragment).commit();
                    break;
                    case R.id.profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragmentSwitcher).commit();

                        break;
                    case R.id.stats:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, statsFragment).commit();

                        break;

                }

                return true;
            }
        });

        SocketListener listener = new SocketListener(mSocket, this);
        try {
            listener.listenChats(manager.getString("TOKEN", ""), manager.getString("TOKEN_ID", ""));
            chatsFragment.setSocket(listener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





    @Override
    public void goToChat(String id, String name, String surname) {
        navigation.setSelectedItemId(R.id.chat);
        Bundle data = new Bundle();
        data.putString("id", id);
        data.putString("action", "goToChat");
        data.putString("name", name);
        data.putString("surname", surname);
        System.out.println(data.getString("id"));
        chatsFragment.setArguments(data);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, chatsFragment).commit();
    }

    @Override
    public void message() {
        System.out.println("SMTH cccc");
    }

    @Override
    public void updateMessages(JSONObject message) {
        chatsFragment.updateMessages(message);
    }

    @Override
    public void newConversation(JSONObject newConversation) {
        chatsFragment.newConversation(newConversation);
    }

    @Override
    public void socketDisconnected() {
        chatsFragment.socketDisconnected();
    }

    @Override
    public void connectedAgain() {
        chatsFragment.connectedAgain();
    }

    @Override
    public void setFilters(List<Integer> types) {
        mapFragment.setFilters(types);
    }

    @Override
    public void sendSearch(JSONObject object) {
        mapFragment.sendSearch(object);
    }

    @Override
    public void logOut() {
        Intent intent = new Intent(this, Start.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void dismiss() {
        chatsFragment.disableOpen();
    }
    Bundle GetUpdateData = new Bundle();

    Fetch fetch = new Fetch();
    URLS urls = new URLS();

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try{


        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result!=null){
            if(result.getContents() == null) {
                Toast.makeText(this, "><\n_", Toast.LENGTH_LONG).show();
            } else {
                Thread send = new Thread(new Runnable() {
                    @Override
                    public void run() {


                        switch (GetUpdateData.getString("type")){
                            case "1":

                                Request request = new Request(urls.URL(), urls.getUpdate1(), "POST", "POST");
                                RequestData requestData = new RequestData();
                                requestData.setData(new Data("id", GetUpdateData.getString("id")));
                                requestData.setData(new Data("userID", result.getContents()));
                                requestData.setData(new Data("quantity", GetUpdateData.getIntegerArrayList("quantity").toString()));
                                requestData.setData(new Data("token", manager.getString("TOKEN", "")));
                                requestData.setData(new Data("token_ID",  manager.getString("TOKEN_ID", "")));
                                Return response = fetch.fetch(request, requestData);
                                switch (response.getType()){
                                    case 0:
                                        break;
                                }
                                break;
                            case "2":
                                Request request1 = new Request(urls.URL(), urls.getUpdate2(), "POST", "POST");
                                RequestData requestData1 = new RequestData();
                                requestData1.setData(new Data("id", GetUpdateData.getString("id")));
                                requestData1.setData(new Data("userID", result.getContents()));
                                requestData1.setData(new Data("quantity", String.valueOf(GetUpdateData.getInt("quantity"))));
                                requestData1.setData(new Data("token", manager.getString("TOKEN", "")));
                                requestData1.setData(new Data("token_ID",  manager.getString("TOKEN_ID", "")));
                                Return response1 = fetch.fetch(request1, requestData1);
                                switch (response1.getType()){
                                    case 0:

                                        break;
                                }
                                break;
                        }


                    }
                });
                send.start();

            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);

        }
        }catch (Exception e){
            InfoBottom infoBottom = new InfoBottom();
            Bundle bundle = new Bundle();
            bundle.putString("description", e.toString());
            bundle.putInt("icon", R.drawable.error);
            infoBottom.show(getSupportFragmentManager(), "d");
        }
    }

    @Override
    public void setGetUpdateData(Bundle data) {
        GetUpdateData = data;
    }

    @Override
    public void swipeLeft() {
        fragmentSwitcher.swipe(1);
    }
}
