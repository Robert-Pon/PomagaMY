package com.name.social_helper_r_p.user.add;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.connections.URLS;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.name.social_helper_r_p.user.InfoBottom;
import com.name.social_helper_r_p.user.LoaderBottom;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class AddAction extends DialogFragment {

    EditText title;
    EditText description;

    Spinner spinner;

    LinearLayout add_1;
    LinearLayout add_2;
    LinearLayout add_3;

    ImageView img1;
    ImageView img2;
    ImageView img3;

    Bitmap bit1 = null;
    Bitmap bit2 = null;
    Bitmap bit3 = null;
    EditText localization;

    Fetch fetch = new Fetch();
    URLS urls = new URLS();

    Button add;

    String addClicked;
    String categoryS ="evC1";

    SharedPreferences preferences;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.action_fragment, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        title = v.findViewById(R.id.title);
        description = v.findViewById(R.id.description);

        spinner = v.findViewById(R.id.category);

        add_1 = v.findViewById(R.id.add_1);
        add_2 = v.findViewById(R.id.add_2);
        add_3 = v.findViewById(R.id.add_3);

        img1 = v.findViewById(R.id.img1);
        img2 = v.findViewById(R.id.img2);
        img3 = v.findViewById(R.id.img3);

        localization = v.findViewById(R.id.localization);

        add = v.findViewById(R.id.add);

        add_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClicked = "1";
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, 200);
            }
        });
         add_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClicked = "2";
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, 200);
            }
        });
         add_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClicked = "3";
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, 200);
            }
        });

        ArrayAdapter <CharSequence> category = ArrayAdapter.createFromResource(getContext(), R.array.evCat, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(category);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<String> ids = Arrays.asList(getResources().getStringArray(R.array.evCatIDS));
                categoryS = ids.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
         add.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(!localization.getText().toString().trim().trim().equals("")) {
                     Thread send = new Thread(new Runnable() {
                         @Override
                         public void run() {
                             LoaderBottom loaderBottom = new LoaderBottom();
                             loaderBottom.show(getChildFragmentManager(), "d");
                             Request request = new Request(urls.URL(), urls.addType4(), "POST", "POST");
                             RequestData requestData = new RequestData();
                             requestData.setData(new Data("title", title.getText().toString()));
                             requestData.setData(new Data("description", description.getText().toString()));
                             requestData.setData(new Data("localization", localization.getText().toString()));
                             requestData.setData(new Data("category", categoryS));
                             requestData.setData(new Data("token", preferences.getString("TOKEN", "")));
                             requestData.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));
                             ByteArrayOutputStream stream = new ByteArrayOutputStream();
                             byte[] byteArray = new byte[1];
                             if (bit1 != null) {
                                 bit1.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                 byteArray = stream.toByteArray();
                                 requestData.setData(new Data("img1", byteArray, "file"));
                             }
                             if (bit2 != null) {
                                 stream = new ByteArrayOutputStream();
                                 bit2.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                 byteArray = stream.toByteArray();
                                 requestData.setData(new Data("img2", byteArray, "file"));
                             }
                             if (bit3 != null) {
                                 stream = new ByteArrayOutputStream();
                                 bit3.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                 byteArray = stream.toByteArray();
                                 requestData.setData(new Data("img3", byteArray, "file"));
                             }


                             Return response = fetch.fetch(request, requestData);

                             switch (response.getType()) {
                                 case 0:
                                     InfoBottom infoBottom = new InfoBottom();
                                     Bundle data = new Bundle();
                                     data.putString("description", "Pomyślnie dodano \nzgłoszenie");
                                     data.putInt("icon", R.drawable.happy);
                                     infoBottom.setArguments(data);
                                     infoBottom.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog);
                                     infoBottom.show(getParentFragmentManager(), "d");
                                     dismiss();
                                     break;
                                 case 1:
                                     InfoBottom infoBottom1 = new InfoBottom();
                                     Bundle data1 = new Bundle();
                                     data1.putString("description", "Wystąpił nieoczekiwany błąd,\n spróbuj ponownie później");
                                     data1.putInt("icon", R.drawable.error);
                                     infoBottom1.setArguments(data1);
                                     infoBottom1.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog);
                                     infoBottom1.show(getParentFragmentManager(), "d");
                                     dismiss();
                                     break;
                             }
                             loaderBottom.dismiss();
                         }
                     });
                     send.start();
                 }else {
                     Toast.makeText(getContext(), "Pole z lokalizacją jest wymagane", Toast.LENGTH_LONG).show();
                 }
             }
         });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200){
            final Uri imageUri = data.getData();
            InputStream imageStream = null;
            try {
                imageStream = ((Activity) getContext()).getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

            switch (addClicked){
                case "1":
                    bit1 = selectedImage;
                    img1.setImageBitmap(bit1);
                    break;
                case "2":
                    bit2 = selectedImage;
                    img2.setImageBitmap(bit2);
                    break;
                case "3":
                    bit3 = selectedImage;
                    img3.setImageBitmap(bit3);
                    break;
            }
        }
    }
}
