package com.name.social_helper_r_p.user;

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
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.connections.URLS;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class NewProfileImageBottom extends BottomSheetDialogFragment {
    CardView image;

    Button send;

    ImageView photo;

    Bitmap PHOTO;

    Fetch fetch = new Fetch();
    URLS urls = new URLS();

    SharedPreferences preferences;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_image_bottom, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        image = v.findViewById(R.id.image);
        photo = v.findViewById(R.id.photo);
        send = v.findViewById(R.id.send);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, 200);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread send = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LoaderBottom loaderBottom = new LoaderBottom();
                        loaderBottom.show(getChildFragmentManager(), "d");
                        Request request = new Request(urls.URL(), urls.updateProfileImage(), "POST", "POST");
                        RequestData requestData = new RequestData();
                        requestData.setData(new Data("token", preferences.getString("TOKEN", "")));
                        requestData.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        PHOTO.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        requestData.setData(new Data("file", byteArray, "file"));

                        Return response = fetch.fetch(request, requestData);
                        switch (response.getType()){
                            case 0:
                                loaderBottom.dismiss();
                                dismiss();
                                break;
                        }
                    }
                });
                send.start();
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
            photo.setImageBitmap(selectedImage);
            PHOTO = selectedImage;

        }

    }
}
