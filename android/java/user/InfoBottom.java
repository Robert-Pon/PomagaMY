package com.name.social_helper_r_p.user;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.name.social_helper_r_p.R;

public class InfoBottom extends DialogFragment {
    TextView description;
    ImageView icon;

    Bundle data;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.info_bottom, container, false);

        description = v.findViewById(R.id.description);
        icon = v.findViewById(R.id.icon);

        data = getArguments();

        icon.setImageResource(data.getInt("icon"));

        description.setText(data.getString("description"));

        return v;
    }


}
