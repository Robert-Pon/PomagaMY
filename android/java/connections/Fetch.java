package com.name.social_helper_r_p.connections;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Fetch {
    private String DEFAULT_URL = null;

    public Fetch(){

    }

    public Fetch(String DEFAULT_URL){
        this.DEFAULT_URL = DEFAULT_URL;
    }
    public Return fetch(Request request, RequestData requestData) {
        Return effect = new Return();
        try {
            List<Data> data = requestData.getData();
            System.out.println(request.getUrl()+request.getPath());
            URL url;
            if(request.getUrl()!=null){
                url = new URL(request.getUrl()+request.getPath());
            }else if(DEFAULT_URL!=null){
                url = new URL(DEFAULT_URL+request.getPath());
            }else{
                effect.setType(0);
                effect.setObject(new Error(0, "No URL"));
                return effect;
            }
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("ENCTYPE", "multipart/form-data");
            connection.setRequestProperty("charset","utf-16");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=*****");
            connection.setRequestProperty("image", "name");
            OutputStream outputStream = connection.getOutputStream();
            DataOutputStream writer = new DataOutputStream(outputStream);
            for(int i = 0; i < data.size(); i++){
                System.out.println(data.get(i).getName());
                System.out.println(data.get(i).getObject());
                switch (data.get(i).getType()){
                    case "file":
                        writer.writeBytes("--*****\r\n");
                        writer.writeBytes("Content-Disposition: form-data; name=file; filename=file\r\n");
                        writer.writeBytes("\r\n");
                        writer.write((byte[])data.get(i).getObject());
                        writer.writeBytes("\r\n");
                        break;
                    case "param":
                        System.out.println(data.get(i));
                        writer.writeBytes("--*****\r\n");
                        writer.writeBytes("Content-Disposition: form-data; name="+data.get(i).getName()+"\r\n");
                        writer.writeBytes("\r\n");
                        writer.write(String.valueOf(data.get(i).getObject()).getBytes(StandardCharsets.UTF_8));
                        writer.writeBytes("\r\n");
                        break;
                }
                if(i==data.size()-1){
                    writer.writeBytes("--*****--\r\n");
                }else{
                    writer.writeBytes("--*****\r\n");
                }

            }
            if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                System.out.println(connection.getResponseCode());

                writer.flush();
                writer.close();
                System.out.println(connection.getResponseCode());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String  i = "";

                while ((i = bufferedReader.readLine()) != null) {
                    JSONObject response = new JSONObject(String.valueOf(i));
                    effect.setObject(response.getJSONObject("data"));
                    effect.setType(response.getInt("type"));
                }
            }else{
                effect.setType(1);
                effect.setObject("{}");
            }

            return effect;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e);
            effect.setType(1);
            effect.setObject("{}");
        }
        return effect;
    }


    public Bitmap getImageFromURL(String url, String type){
        Bitmap photo = null;
        try {
            URL connectionURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) connectionURL.openConnection();
            connection.setDoInput(true);
            connection.connect();

            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                //download
                InputStream in = connection.getInputStream();
                photo = BitmapFactory.decodeStream(in);
                in.close();
            }

        }catch (Exception e){

        }
        return photo;
    }
}
