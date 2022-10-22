package com.name.social_helper_r_p.user.add;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.name.social_helper_r_p.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddBottomFragment extends BottomSheetDialogFragment {

    LinearLayout option_1;
    LinearLayout option_2;
    LinearLayout option_3;
    LinearLayout option_4;
    LinearLayout option_5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_view, container, false);

        option_1 = v.findViewById(R.id.option_1);
        option_2 = v.findViewById(R.id.option_2);
        option_3 = v.findViewById(R.id.option_3);
        option_4 = v.findViewById(R.id.option_4);
        option_5 = v.findViewById(R.id.option_5);



        option_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFragment addFragment = new AddFragment();
                addFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
                addFragment.show(getChildFragmentManager(), "d");
            }
        });

        option_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyOfferFragment myOfferFragment = new MyOfferFragment();
                myOfferFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
                myOfferFragment.show(getChildFragmentManager(), "d");
            }
        });

        option_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventFragment eventFragment = new EventFragment();
                eventFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
                eventFragment.show(getChildFragmentManager(), "d");
            }
        });

        option_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAction addAction = new AddAction();
                addAction.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
                addAction.show(getChildFragmentManager(), "d");
            }
        });

        option_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialIdea socialIdea = new SocialIdea();
                socialIdea.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
                socialIdea.show(getChildFragmentManager(), "d");
            }
        });

        return v;
    }
}
