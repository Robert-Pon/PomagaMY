package com.name.social_helper_r_p.user;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.name.social_helper_r_p.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchBottom extends BottomSheetDialogFragment {
    EditText search;

    CardView send;

    Spinner category;

    ChipGroup type;

    String main_category;

    SearchBottomInterface fragmentInterface;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_bottom, container, false);

        send = v.findViewById(R.id.send);
        search = v.findViewById(R.id.search);
        type = v.findViewById(R.id.type);
        category = v.findViewById(R.id.category);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.search, android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                main_category = Arrays.asList(getResources().getStringArray(R.array.searchIDS)).get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                List<Integer> ids = type.getCheckedChipIds();
                List<Integer> types = new ArrayList<>();
                ids.stream().forEach(e->{
                    types.add(Integer.parseInt(getResources().getResourceName(e).split("/type_")[1]));
                });
                JSONArray array = new JSONArray();
                types.stream().forEach(e->{
                    array.put(e);
                });
                JSONObject searchObject = new JSONObject();
                try {
                    searchObject.put("search", search.getText().toString());
                    searchObject.put("types", array);
                    searchObject.put("category", main_category);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                fragmentInterface.sendSearch(searchObject);
            }
        });
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentInterface = (SearchBottomInterface) context;
    }

    public interface SearchBottomInterface{
        public void sendSearch(JSONObject object);
    }
}
