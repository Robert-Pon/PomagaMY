package com.name.social_helper_r_p.user;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.name.social_helper_r_p.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class FiltersBottom extends BottomSheetDialogFragment {

    FiltersBottomInterface fragmentInterface;

    ChipGroup filters;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.filters_bottom, container, false);
        filters = v.findViewById(R.id.type);


        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        List<Integer> ids = filters.getCheckedChipIds();
        List<Integer> types = new ArrayList<>();
        ids.stream().forEach(e->{
            types.add(Integer.parseInt(getResources().getResourceName(e).split("/type_")[1]));
        });

        fragmentInterface.setFilters(types);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentInterface = (FiltersBottomInterface) context;
    }

    public interface FiltersBottomInterface{
        public void setFilters(List<Integer> types);
    }
}
