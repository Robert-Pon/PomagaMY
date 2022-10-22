package com.name.social_helper_r_p.user.add;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class EventFragment extends DialogFragment {
    EditText name;
    EditText description;
    EditText localization;

    ImageView poster;

    Spinner category;

    TextView startDate;
    TextView endingDate;

    Button add;

    Bitmap PHOTO = null;

    String categoryID;

    String startDateS;
    String endDateS;

    URLS urls = new URLS();
    Fetch fetch = new Fetch();

    SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.event_fragment, container, false);

        name = v.findViewById(R.id.name);
        description = v.findViewById(R.id.description);
        localization = v.findViewById(R.id.localization);
        poster = v.findViewById(R.id.poster);
        category = v.findViewById(R.id.category);
        startDate = v.findViewById(R.id.choose_start_date);
        endingDate = v.findViewById(R.id.choose_end_date);
        add = v.findViewById(R.id.image);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, 200);
            }
        });
        startDateS = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format((new Date((new Date()).getTime()+(1000*60*60*24))))+"T00:00:00";
        endDateS = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format((new Date((new Date()).getTime()+(1000*60*60*24*2))))+"T00:00:00";

        startDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format((new Date((new Date()).getTime()+(1000*60*60*24))))+" 00:00");
        endingDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format((new Date((new Date()).getTime()+(1000*60*60*24*2))))+" 00:00");
        ArrayAdapter<CharSequence> categories = ArrayAdapter.createFromResource(getContext(), R.array.eventCategories,  android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categories);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryID = Arrays.asList(getResources().getStringArray(R.array.categoriesIDS)).get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                DatePickerDialog pickerDialog = new DatePickerDialog(getContext());
                pickerDialog.show();

                pickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        startDate.setText(dayOfMonth+"-"+month+"-"+year);
                        if(month+1<10){
                            startDateS = year+"-0"+(month+1)+"-"+dayOfMonth;

                        }else{
                            startDateS = year+"-"+(month+1)+"-"+dayOfMonth;
                        }

                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener(){

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                startDate.setText(dayOfMonth+"-"+month+"-"+year+", "+hourOfDay+":"+minute);
                                String x = "";
                                String x1 = "";
                                if(hourOfDay<10 ){
                                    x="0";
                                }
                                if(minute<10 ){
                                    x1="0";
                                }

                                startDateS +=("T"+x+hourOfDay+":"+x1+minute+":00");
                            }
                        }, 0,0, true);
                        timePickerDialog.show();
                    }
                });


            }
        });

        endingDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                DatePickerDialog pickerDialog = new DatePickerDialog(getContext());
                pickerDialog.show();

                pickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        endingDate.setText(dayOfMonth+"-"+month+"-"+year);
                        endDateS = year+"-"+month+"-"+dayOfMonth;
                        if(month+1<10){
                            endDateS = year+"-0"+(month+1)+"-"+dayOfMonth;

                        }else{
                            endDateS = year+"-"+(month+1)+"-"+dayOfMonth;
                        }
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener(){

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                endingDate.setText(dayOfMonth+"-"+month+"-"+year+", "+hourOfDay+":"+minute);
                                String x = "";
                                String x1 = "";
                                if(hourOfDay<10 ){
                                    x="0";
                                }
                                if(minute<10 ){
                                    x1="0";
                                }
                                endDateS +=("T"+x+hourOfDay+":"+x1+minute+":00");
                            }
                        }, 0,0, true);
                        timePickerDialog.show();
                    }
                });


            }
        });



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!localization.getText().toString().trim().equals("")){
                    Thread send = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            LoaderBottom loaderBottom = new LoaderBottom();
                            loaderBottom.show(getChildFragmentManager(), "d");
                            Request request = new Request(urls.URL(), urls.addType3(), "POST", "POST");
                            RequestData requestData = new RequestData();
                            requestData.setData(new Data("name", name.getText().toString()));
                            requestData.setData(new Data("description", description.getText().toString()));
                            requestData.setData(new Data("startDate", startDateS));
                            requestData.setData(new Data("endingDate", endDateS));
                            requestData.setData(new Data("address", localization.getText().toString()));
                            requestData.setData(new Data("token", preferences.getString("TOKEN", "")));
                            requestData.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));
                            if(PHOTO!=null) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                PHOTO.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byteArray = stream.toByteArray();
                                requestData.setData(new Data("file", byteArray, "file"));
                            }
                            Return response = fetch.fetch(request, requestData);
                            switch (response.getType()){
                                case 0:
                                    loaderBottom.dismiss();
                                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            InfoBottom infoBottom = new InfoBottom();
                                            Bundle data = new Bundle();
                                            data.putString("description", "Pomyślnie dodano \nwydarzenie");
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
            poster.setImageBitmap(selectedImage);
            PHOTO = selectedImage;

        }

    }
}
