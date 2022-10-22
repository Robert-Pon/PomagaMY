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

public class SocialIdea extends DialogFragment {

    EditText title;
    EditText description;

    Spinner spinner;

    LinearLayout add_1;
    LinearLayout add_2;

    ImageView img1;
    ImageView img2;

    Bitmap bit1 = null;
    Bitmap bit2 = null;

    EditText localization;

    Fetch fetch = new Fetch();
    URLS urls = new URLS();

    Button add;

    String addClicked;
    String categoryS ="idC1";

    SharedPreferences preferences;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.social_idea, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        title = v.findViewById(R.id.title);
        description = v.findViewById(R.id.description);

        spinner = v.findViewById(R.id.category);

        add_1 = v.findViewById(R.id.add_1);
        add_2 = v.findViewById(R.id.add_2);

        img1 = v.findViewById(R.id.img1);
        img2 = v.findViewById(R.id.img2);

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

        ArrayAdapter <CharSequence> category = ArrayAdapter.createFromResource(getContext(), R.array.idCat, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(category);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<String> ids = Arrays.asList(getResources().getStringArray(R.array.idCatIDS));
                categoryS = ids.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
         add.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(! localization.getText().toString().trim().equals("")){
                 Thread send = new Thread(new Runnable() {
                     @Override
                     public void run() {
                         LoaderBottom loaderBottom = new LoaderBottom();
                         loaderBottom.show(getChildFragmentManager(), "d");
                         Request request = new Request(urls.URL(), urls.addType5(), "POST", "POST");
                         RequestData requestData = new RequestData();
                         requestData.setData(new Data("name", title.getText().toString()));
                         requestData.setData(new Data("description", description.getText().toString()));
                         requestData.setData(new Data("localization", localization.getText().toString()));
                         requestData.setData(new Data("category", categoryS));
                         requestData.setData(new Data("token", preferences.getString("TOKEN", "")));
                         requestData.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));
                         ByteArrayOutputStream stream = new ByteArrayOutputStream();
                         byte[] byteArray = new byte[1];
                         if (bit1!=null){
                             bit1.compress(Bitmap.CompressFormat.PNG, 100, stream);
                             byteArray = stream.toByteArray();
                             requestData.setData(new Data("img1",byteArray, "file"));
                         }
                         if(bit2!=null){
                             stream = new ByteArrayOutputStream();
                             bit2.compress(Bitmap.CompressFormat.PNG, 100, stream);
                             byteArray = stream.toByteArray();
                             requestData.setData(new Data("img2",byteArray, "file"));
                         }



                         Return response = fetch.fetch(request, requestData);
                         loaderBottom.dismiss();

                         switch (response.getType()){
                             case 0:
                                 ((Activity) getContext()).runOnUiThread(new Runnable() {
                                     @Override
                                     public void run() {
                                         InfoBottom infoBottom = new InfoBottom();
                                         Bundle data = new Bundle();
                                         data.putString("description", "Pomyślnie dodano \ninicjatywę");
                                         data.putInt("icon", R.drawable.happy);
                                         infoBottom.setArguments(data);
                                         infoBottom.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog);
                                         infoBottom.show(getParentFragmentManager(), "d");
                                         dismiss();
                                     }
                                 });
                                 break;
                             case 1:
                                 InfoBottom infoBottom = new InfoBottom();
                                 Bundle data = new Bundle();
                                 data.putString("description", "Wystąpił nieoczekiwany błąd,\n spróbuj ponownie później");
                                 data.putInt("icon", R.drawable.error);
                                 infoBottom.setArguments(data);
                                 infoBottom.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog);
                                 infoBottom.show(getParentFragmentManager(), "d");
                                 dismiss();
                                 break;
                         }
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
        if(resultCode==Activity.RESULT_OK){
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

            }
        }
    }
}
