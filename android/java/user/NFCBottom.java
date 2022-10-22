package com.name.social_helper_r_p.user;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.name.social_helper_r_p.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class NFCBottom extends BottomSheetDialogFragment {

    ImageView code;

    SharedPreferences preferences;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.nfc, container, false);

        code = v.findViewById(R.id.code);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix x = qrCodeWriter.encode(preferences.getString("USER_ID",""), BarcodeFormat.QR_CODE,400, 400 );
            int width = x.getWidth();
            int height = x.getHeight();
            int[] pixels = new int[width*height];
            for (int i = 0; i < height; i++) {
                for (int i1 = 0; i1 < width; i1++) {
                    pixels[i*width+i1] = x.get(i1, i) ? Color.BLACK : Color.WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            code.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return v;
    }





}
