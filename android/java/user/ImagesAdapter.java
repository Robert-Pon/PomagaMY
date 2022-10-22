package com.name.social_helper_r_p.user;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.URLS;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.Image> {

    String type;
    JSONArray images;
    String ID;
    Context context;

    Fetch fetch = new Fetch();
    URLS urls = new URLS();

    List<Bitmap> imagesList = new ArrayList<>();
    public ImagesAdapter(JSONArray images, String ID, String type, Context context){
        this.ID = ID;
        this.images = images;
        this.type = type;
        this.context = context;
    }

    public class Image extends RecyclerView.ViewHolder{
        ImageView image;
        public Image(@NonNull View itemView) {
            super(itemView);
        }
    }


    @NonNull
    @Override
    public Image onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_element, parent, false);
        Image image = new Image(v);
        image.image = v.findViewById(R.id.image);
        return image;
    }

    @Override
    public void onBindViewHolder(@NonNull Image holder, int position) {
        Thread get = new Thread(new Runnable() {
            @Override
            public void run() {
                if(imagesList.size()<=position){
                    Bitmap photo = fetch.getImageFromURL(urls.URL()+urls.getImageFromAnn()+"/"+type+"/"+ID+"/"+String.valueOf(position), "POST");
                    imagesList.add(photo);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.image.setImageBitmap(photo);
                        }
                    });

                }else{
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.image.setImageBitmap(imagesList.get(position));
                        }
                    });
                }

            }
        });
        get.start();
//        switch (type){
//            case "4":
//                Thread get = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(imagesList.size()<=position){
//                            Bitmap photo = fetch.getImageFromURL(urls.URL()+urls.getImageFromAnn()+"/"+type+"/"+ID+"/"+String.valueOf(position), "POST");
//                            imagesList.add(photo);
//                            ((Activity) context).runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    holder.image.setImageBitmap(photo);
//                                }
//                            });
//
//                        }else{
//                            ((Activity) context).runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    holder.image.setImageBitmap(imagesList.get(position));
//                                }
//                            });
//                        }
//
//                    }
//                });
//                get.start();
//                break;
//            case "5":
//
//                break;
//        }
    }

    @Override
    public int getItemCount() {
        return images.length();
    }


}
