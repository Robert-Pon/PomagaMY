package com.name.social_helper_r_p.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.name.social_helper_r_p.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class LoaderBottom extends BottomSheetDialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_loader, container, false);
        return v;
    }
}
