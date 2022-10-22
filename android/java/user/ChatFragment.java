package com.name.social_helper_r_p.user;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.connections.URLS;
import com.name.social_helper_r_p.rooms.AppDatabase;
import com.name.social_helper_r_p.rooms.Chat;
import com.name.social_helper_r_p.rooms.ChatDao;
import com.name.social_helper_r_p.rooms.Messages;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.name.social_helper_r_p.rooms.MessagesDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.socket.client.Socket;

public class ChatFragment extends DialogFragment {
    ChatDao chatDao;

    TextView title;

    RecyclerView chat;

    EditText message;

    CardView send;

    CardView photo;

    SocketListener listener;

    List<JSONObject> messages = new ArrayList<>();
    Bundle bundleData;

    MessagesAdapter adapter;

    MessagesDao messagesDao;
    Socket socket;

    URLS urls = new URLS();
    Fetch fetch = new Fetch();
    public ChatFragment(SocketListener listener, Bundle data){
        this.listener = listener;
        socket = this.listener.getSocket();
        this.bundleData = data;

    }

    ChatInterface chatInterface;
    List<JSONObject> newMessages = new ArrayList<>();
    SharedPreferences preferences;

    boolean created = false;
Context context;

    public void getMessages(){
        Thread getMessages = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Chat myChat1 = chatDao.getChat(bundleData.getString("id"));
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                title.setText(myChat1.collaboratorName);

                            }catch (Exception e){

                            }
                        }
                    });
                    Messages messagesObject = messagesDao.getMessages(bundleData.getString("id"));
                    if (messagesObject!=null) {

                        try {
                            JSONArray savedMessages = new JSONArray(messagesObject.messages);
                            List<JSONObject> newMessages = new ArrayList<>();
                            for(int i = 0;  i < savedMessages.length(); i++){
                                newMessages.add(savedMessages.getJSONObject(i));
                                System.out.println(savedMessages.getJSONObject(i));
                            }
                            Collections.reverse(newMessages);
                            messages = newMessages;
                            if (adapter != null) {
                                ((Activity) getContext()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter = new MessagesAdapter(messages);
                                        chat.setAdapter(adapter);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Request request = new Request(urls.URL(), urls.getLastMessages(), "POST", "POST");
                        RequestData requestData = new RequestData();
                        requestData.setData(new Data("userID", bundleData.getString("id")));
                        requestData.setData(new Data("token", preferences.getString("TOKEN", "")));
                        requestData.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));

                        Return response = fetch.fetch(request, requestData);

                        switch (response.getType()) {
                            case 0:
                                JSONObject data = (JSONObject) response.getObject();
                                System.out.println(data);
                                try {
                                    JSONArray oldMessages = data.getJSONArray("messages").getJSONObject(0).getJSONArray("messages");
                                    List<JSONObject> messagesCopy = new ArrayList<>();
                                    System.out.println(messages.get(messages.size() - 1));
                                    if(oldMessages.length()>0 && messages.size()>0) {
                                        for (int i = oldMessages.length() - 1; i > -1; i--) {
                                            JSONObject messageObject = oldMessages.getJSONObject(i);
                                            if (!messageObject.getString("_id").equals(messages.get(messages.size() - 1).getString("_id"))) {
                                                messagesCopy.add(oldMessages.getJSONObject(i));
                                            } else {
                                                messagesCopy.add(oldMessages.getJSONObject(i));
                                                i = 0;
                                            }
                                        }
                                    }else{
                                        for (int i = oldMessages.length() - 1; i > -1; i--) {
                                            messagesCopy.add(oldMessages.getJSONObject(i));

                                        }
                                    }

                                    messagesDao.updateMessages(oldMessages.toString(), bundleData.getString("id"));
                                    //Collections.reverse(messagesCopy);
                                    messages = messagesCopy;
                                    System.out.println(messagesCopy.size());
                                    if (adapter != null && getContext() != null) {
                                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapter = new MessagesAdapter(messages);
                                                chat.setAdapter(adapter);
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                break;
                        }

                    } else {

                        Request request = new Request(urls.URL(), urls.getLastMessages(), "POST", "POST");
                        RequestData requestData = new RequestData();
                        requestData.setData(new Data("userID", bundleData.getString("id")));
                        requestData.setData(new Data("token", preferences.getString("TOKEN", "")));
                        requestData.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));

                        Return response = fetch.fetch(request, requestData);

                        switch (response.getType()) {
                            case 0:
                                JSONObject data = (JSONObject) response.getObject();
                                System.out.println(data);
                                try {
                                    JSONArray oldMessages = data.getJSONArray("messages").getJSONObject(0).getJSONArray("messages");
                                    System.out.println(oldMessages.length());
                                    System.out.println(messages.size());
                                    List<JSONObject> messagesCopy = new ArrayList<>();
                                    if(oldMessages.length()>0 && messages.size()>0) {
                                        for (int i = oldMessages.length() - 1; i > -1; i--) {
                                            JSONObject messageObject = oldMessages.getJSONObject(i);
                                            if (!messageObject.getString("_id").equals(messages.get(messages.size() - 1).getString("_id"))) {
                                                messagesCopy.add(oldMessages.getJSONObject(i));
                                            } else {
                                                messagesCopy.add(oldMessages.getJSONObject(i));
                                                i = 0;
                                            }
                                        }
                                    }else{
                                        for (int i = oldMessages.length() - 1; i > -1; i--) {
                                            messagesCopy.add(oldMessages.getJSONObject(i));
                                        }
                                    }
                                    Messages messages1 = messagesDao.getMessages(bundleData.getString("id"));
                                    if(messages1==null){
                                        messagesDao.createMessages(bundleData.getString("id"), oldMessages.toString());
                                    }else{

                                    }
                                    messages.addAll(messagesCopy);
                                    System.out.println(messagesCopy.size());
                                    if (adapter != null && getContext()!=null) {
                                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapter = new MessagesAdapter(messages);
                                                chat.setAdapter(adapter);
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                break;
                        }

                    }
                }catch (Exception e){

                }


            }
        });
        getMessages.start();
    }




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chat, container, false);

        title = v.findViewById(R.id.title);
        try {
            String name = bundleData.getString("name", "");
            String surname = bundleData.getString("surname", "");
            title.setText(name+" "+surname);
        }catch (Exception e){
            e.printStackTrace();
        }
        chat = v.findViewById(R.id.chat);
        message = v.findViewById(R.id.message);
        send = v.findViewById(R.id.send);

        photo = v.findViewById(R.id.photo);
 context = getContext();
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, 200);
            }
        });


        chat.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
        created = true;
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        AppDatabase db = Room.databaseBuilder(getContext(),
                        AppDatabase.class, "socials").fallbackToDestructiveMigration()
                .build();
        messagesDao = db.messagesDao();
        chatDao = db.chatDao();
        getMessages();


        adapter = new MessagesAdapter(messages);
        chat.setAdapter(adapter);




        //System.out.println();



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject newMessage = new JSONObject();
                try {
                    newMessage.put("userID", bundleData.getString("id"));
                    newMessage.put("message", message.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("sendMessage", newMessage);
                message.setText("");
            }
        });



        return v;
    }

    public void update(JSONObject message){
        messages.add(0, message);
        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.updateMessages(messages);
            }
        });
    }

    public void thisWasNewConversation(){
        getMessages();
    }

    public void setSocket(SocketListener listener){
        this.listener = listener;
    }


    public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        List<JSONObject> messages = new ArrayList<>();

        public MessagesAdapter(List<JSONObject> messages){
            this.messages = messages;
        }

        public void updateMessages(List<JSONObject> messages){


            this.messages = messages;
            notifyDataSetChanged();
        }

        class MyMessage extends RecyclerView.ViewHolder{
            TextView message;

            public MyMessage(@NonNull View itemView) {
                super(itemView);
            }
        }
        class NotMyMessage extends RecyclerView.ViewHolder{
            TextView message;
            public NotMyMessage(@NonNull View itemView) {
                super(itemView);
            }
        }
        class NotMyMessageImage extends RecyclerView.ViewHolder{
            ImageView message;
            public NotMyMessageImage(@NonNull View itemView) {
                super(itemView);
            }
        }
        class MyMessageImage extends RecyclerView.ViewHolder{
            ImageView message;
            public MyMessageImage(@NonNull View itemView) {
                super(itemView);
            }
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType){
                case 0:
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.not_my_message, parent, false);
                    NotMyMessage notMyMessage = new NotMyMessage(v);
                    notMyMessage.message = v.findViewById(R.id.message);
                    return notMyMessage;
                case 1:
                    View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_message, parent, false);
                    MyMessage myMessage = new MyMessage(v1);
                    myMessage.message = v1.findViewById(R.id.message);
                    return myMessage;
                case 2:
                    View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_message_img, parent, false);
                    MyMessageImage myMessageImage = new MyMessageImage(v2);
                    myMessageImage.message = v2.findViewById(R.id.message);
                    return myMessageImage;
                case 3:
                    View v3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.not_my_message_img, parent, false);
                    NotMyMessageImage notMyMessageImage = new NotMyMessageImage(v3);
                    notMyMessageImage.message = v3.findViewById(R.id.message);
                    return notMyMessageImage;

            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()){
                case 0:
                    try {
                        NotMyMessage notMyMessage = (NotMyMessage) holder;
                        JSONObject message = messages.get(position);
                        notMyMessage.message.setText(message.getString("content"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    try {
                        MyMessage myMessage = (MyMessage) holder;
                        JSONObject message = messages.get(position);
                        myMessage.message.setText(message.getString("content"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        MyMessageImage myMessageImage = (MyMessageImage) holder;
                        JSONObject message = messages.get(position);
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Bitmap b = fetch.getImageFromURL(urls.URL()+urls.getImageFromCon()+message.getString("content")+"/"+preferences.getString("TOKEN","")+"/"+preferences.getString("TOKEN_ID",""), "POST");
                                    if(getContext()!=null){
                                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                myMessageImage.message.setImageBitmap(b);
                                            }
                                        });
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                        //myMessageImage.message.s(message.getString("content"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        NotMyMessageImage notMyMessageImage = (NotMyMessageImage) holder;
                        JSONObject message = messages.get(position);
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Bitmap b = fetch.getImageFromURL(urls.URL()+urls.getImageFromCon()+message.getString("content")+"/"+preferences.getString("TOKEN","")+"/"+preferences.getString("TOKEN_ID",""), "POST");
                                    if(getContext()!=null){
                                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                notMyMessageImage.message.setImageBitmap(b);
                                            }
                                        });
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                        //myMessage.message.setText(message.getString("content"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

            }

        }

        @Override
        public int getItemViewType(int position) {
            int num = 0;
            try {
                if(messages.get(position).getString("user").equals(bundleData.getString("id"))){
                    if(messages.get(position).getString("type").equals("text")){
                        num = 0;
                    }else{
                        num = 3;
                    }

                }else{
                    if(messages.get(position).getString("type").equals("text")){
                        num = 1;
                    }else{
                        num = 2;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("NUM "+num);
            return num;
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        chatInterface.dismiss();
    }

    public void connectedAgain(){
getMessages();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        chatInterface = (ChatInterface) context;
     }

    public interface  ChatInterface{
        public void dismiss();
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
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper((Activity)getContext(),R.style.AlertDialogCustom));
            alertDialog.setTitle("Czy chcesz wysłać to zdjęcie?");
            alertDialog.setNegativeButton("Tak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Thread send = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Request request = new Request(urls.URL(), urls.uploadImageToChat(), "POST", "POST");
                            RequestData requestData = new RequestData();
                            requestData.setData(new Data("userID", bundleData.getString("id")));
                            requestData.setData(new Data("token", preferences.getString("TOKEN", "")));
                            requestData.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));
                            requestData.setData(new Data("file", byteArray, "file"));
                            Return response = fetch.fetch(request, requestData);
                        }
                    });
                    send.start();
                }
            });

            alertDialog.create().show();
            //requestData.setData(new Data("file", byteArray, "file"));

        }
    }
}
