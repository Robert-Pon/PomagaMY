package com.name.social_helper_r_p.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.rooms.AppDatabase;
import com.name.social_helper_r_p.rooms.ChatDao;
import com.name.social_helper_r_p.rooms.MessagesDao;

public class SettingsFragment extends Fragment {

    TextView data_1;
    TextView password;
    TextView image;
    TextView qr;
    TextView libs;
    TextView profession;
    TextView inst;

    Button logOut;

    SharedPreferences preferences;

    SettingsFragmentInterface fragmentInterface;
    ChatDao chatDao;
    MessagesDao messagesDao;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings, container, false);
        AppDatabase db = Room.databaseBuilder(getContext(),
                        AppDatabase.class, "socials").fallbackToDestructiveMigration()
                .build();
        chatDao = db.chatDao();
        messagesDao = db.messagesDao();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        data_1 = v.findViewById(R.id.data_1);
        password = v.findViewById(R.id.password);
        image = v.findViewById(R.id.image);
        logOut = v.findViewById(R.id.logOut);
        libs = v.findViewById(R.id.libs);
        inst = v.findViewById(R.id.inst);
        profession = v.findViewById(R.id.profession);
        qr = v.findViewById(R.id.qr);

        data_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDataBottom dataBottom = new MyDataBottom();
                dataBottom.show(getChildFragmentManager(), "d");
            }
        });

        inst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartInfo startInfo = new StartInfo();
                startInfo.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
                startInfo.show(getChildFragmentManager(), "d");
            }
        });
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordBottom passwordBottom = new PasswordBottom();
                passwordBottom.show(getChildFragmentManager(), "d");
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewProfileImageBottom newProfileImageBottom = new NewProfileImageBottom();
                newProfileImageBottom.show(getChildFragmentManager(), "d");
            }
        });

        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NFCBottom nfcBottom = new NFCBottom();
                nfcBottom.show(getChildFragmentManager(), "d");
            }
        });

        profession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfessionBottom professionBottom = new ProfessionBottom();
                professionBottom.show(getChildFragmentManager(), "d");
            }
        });

        libs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XBottm xBottm = new XBottm();
                xBottm.show(getChildFragmentManager(), "d");
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("TOKEN");
                editor.remove("TOKEN_ID");
                editor.apply();
                Thread clear = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        chatDao.clear();
                        messagesDao.clear();
                    }
                });
                clear.start();
                fragmentInterface.logOut();

            }
        });
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentInterface = (SettingsFragmentInterface) context;
    }

    public interface SettingsFragmentInterface{
        public void logOut();
    }
}
