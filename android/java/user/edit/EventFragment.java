package com.name.social_helper_r_p.user.edit;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

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

    Bundle bundle;

    Button delete;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.event_fragment_edit, container, false);

        name = v.findViewById(R.id.name);
        description = v.findViewById(R.id.description);
        localization = v.findViewById(R.id.localization);
        poster = v.findViewById(R.id.poster);
        category = v.findViewById(R.id.category);
        startDate = v.findViewById(R.id.choose_start_date);
        endingDate = v.findViewById(R.id.choose_end_date);
        add = v.findViewById(R.id.image);
        delete = v.findViewById(R.id.delete);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        bundle = getArguments();


        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, 200);
            }
        });

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


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Chcesz usunąć te ogłoszenie?");
                builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Thread delete = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Request request = new Request(urls.URL(), urls.deleteAnn(), "POST", "POST");
                                RequestData data = new RequestData();
                                data.setData(new Data("type", "2"));
                                try {
                                    data.setData(new Data("id", bundle.getString("id")));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                data.setData(new Data("token", preferences.getString("TOKEN", "")));
                                data.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));
                                Return response = fetch.fetch(request, data);

                                switch (response.getType()){
                                    case 0:
                                        InfoBottom infoBottom1 = new InfoBottom();
                                        Bundle data1 = new Bundle();
                                        data1.putString("description", "Pomyślnie usunięto \nwydarzenie");
                                        data1.putInt("icon", R.drawable.happy);
                                        infoBottom1.setArguments(data1);
                                        infoBottom1.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog);
                                        infoBottom1.show(getParentFragmentManager(), "d");
                                        dismiss();
                                        break;
                                    case 1:
                                        InfoBottom infoBottom2 = new InfoBottom();
                                        Bundle data2 = new Bundle();
                                        data2.putString("description", "Wystąpił nieoczekiwany błąd,\n spróbuj ponownie później");
                                        data2.putInt("icon", R.drawable.happy);
                                        infoBottom2.setArguments(data2);
                                        infoBottom2.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog);
                                        infoBottom2.show(getParentFragmentManager(), "d");
                                        break;
                                }
                            }
                        });
                        delete.start();
                    }
                });
                builder.create().show();
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
                Thread send = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LoaderBottom loaderBottom = new LoaderBottom();
                        loaderBottom.show(getChildFragmentManager(), "d");
                        Request request = new Request(urls.URL(), urls.updateType3(), "POST", "POST");
                        RequestData requestData = new RequestData();
                        requestData.setData(new Data("name", name.getText().toString()));
                        requestData.setData(new Data("id", bundle.getString("id")));
                        requestData.setData(new Data("description", description.getText().toString()));
                        requestData.setData(new Data("startDate", startDateS));
                        requestData.setData(new Data("endingDate", endDateS));
                        requestData.setData(new Data("token", preferences.getString("TOKEN", "")));
                        requestData.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));
                        if(PHOTO!=null){
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            PHOTO.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();
                            requestData.setData(new Data("file", byteArray, "file"));
                        }
                        Return response = fetch.fetch(request, requestData);
                        switch (response.getType()){
                            case 0:
                                InfoBottom infoBottom = new InfoBottom();
                                Bundle data = new Bundle();
                                data.putString("description", "Pomyślnie zaktualizowano \nwydarzenie");
                                data.putInt("icon", R.drawable.happy);
                                infoBottom.setArguments(data);
                                infoBottom.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog);
                                infoBottom.show(getParentFragmentManager(), "d");
                                loaderBottom.dismiss();
                                break;
                            case 1:
                                InfoBottom infoBottom1 = new InfoBottom();
                                Bundle data1 = new Bundle();
                                data1.putString("description", "Wystąpił nieoczekiwany błąd,\n spróbuj ponownie później");
                                data1.putInt("icon", R.drawable.happy);
                                infoBottom1.setArguments(data1);
                                infoBottom1.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog);
                                infoBottom1.show(getParentFragmentManager(), "d");
                                dismiss();
                                break;
                        }
                    }
                });
                send.start();
            }
        });


        Thread get = new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request(urls.URL(), urls.getAnn(), "POST", "POST");
                RequestData requestData = new RequestData();
                requestData.setData(new Data("id", bundle.getString("id")));
                requestData.setData(new Data("type", "3"));
                requestData.setData(new Data("token", preferences.getString("TOKEN", "")));
                requestData.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));
                Return response = fetch.fetch(request, requestData);
                System.out.println(response.getType());
                switch (response.getType()){
                    case 0:
                        JSONObject data = (JSONObject) response.getObject();

                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void run() {
                                try {
                                    JSONObject announcement = data.getJSONObject("announcements");
                                    JSONObject user = data.getJSONObject("user");
                                    name.setText(announcement.getString("name"));
                                    description.setText(announcement.getString("description"));
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:ss");
                                    try {
                                        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(announcement.getLong("startDate")/1000, 0, ZoneOffset.UTC);
                                        startDate.setText(formatter.format(dateTime));
                                        startDateS = dateTime.toString();
                                    }catch (Exception e){

                                    }
                                    try {
                                        LocalDateTime dateTime2 = LocalDateTime.ofEpochSecond(announcement.getLong("endingDate")/1000, 0, ZoneOffset.UTC);
                                        endingDate.setText(formatter.format(dateTime2));
                                        endDateS = dateTime2.toString();
                                    }catch (Exception e){

                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        Bitmap photo = fetch.getImageFromURL(urls.URL()+urls.getPoster()+"/"+bundle.getString("id"), "POST");
                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                poster.setImageBitmap(photo);
                            }
                        });
                        break;
                }
            }
        });

        get.start();
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
