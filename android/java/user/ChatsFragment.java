package com.name.social_helper_r_p.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.rooms.AppDatabase;
import com.name.social_helper_r_p.rooms.Chat;
import com.name.social_helper_r_p.rooms.ChatDao;
import com.name.social_helper_r_p.rooms.Messages;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.connections.URLS;
import com.name.social_helper_r_p.rooms.MessagesDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ChatsFragment extends Fragment  {
    List<ChatFragment> chatsFragments = new ArrayList<>();

    Map<String, Integer> exist = new ArrayMap<>();

    SocketListener listener;

    ChatDao chatDao;
    MessagesDao messagesDao;

    int openIndex = -1;

    RecyclerView chats;
    Bundle data;

    URLS urls = new URLS();
    Fetch fetch = new Fetch();
    SharedPreferences preferences;
    ChatsAdapter adapter = new ChatsAdapter(new ArrayList<>());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chats, container, false);

        data = getArguments();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        AppDatabase db = Room.databaseBuilder(getContext(),
                AppDatabase.class, "socials").fallbackToDestructiveMigration()
                .build();
        chatDao = db.chatDao();
        messagesDao = db.messagesDao();

        chats = v.findViewById(R.id.chats);
        chats.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        Thread get = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Chat> myChats = chatDao.getAllChats();
                Collections.sort(myChats, new Comparator<Chat>() {
                    @Override
                    public int compare(Chat o1, Chat o2) {
                        return Long.compare(o2.time, o1.time);
                    }
                });
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new ChatsAdapter(myChats);
                        chats.setAdapter(adapter);
                    }
                });

                try{
                    Request request = new Request(urls.URL(), urls.getLastChats(), "POST", "POST");
                    RequestData requestData = new RequestData();
                    requestData.setData(new Data("token", preferences.getString("TOKEN","")));
                    requestData.setData(new Data("token_ID", preferences.getString("TOKEN_ID","")));
                    Return response = fetch.fetch(request, requestData);
                    System.out.println(">>");
                    System.out.println(response.getType());
                    switch (response.getType()){
                        case 0:
                            JSONObject object = (JSONObject) response.getObject();
                            JSONArray chats = object.getJSONArray("chats");
                            for(int i = 0; i < chats.length(); i++){
                                JSONObject chat = chats.getJSONObject(i);
                                if(chatDao.getChat(chats.getJSONObject(i).getString("id"))==null){
                                   chatDao.createChat("", chat.getString("id"), chat.getString("name"), chat.getString("message"), chat.getLong("time"));
                                   Bitmap bit1 = fetch.getImageFromURL(urls.URL()+urls.getProfileImage()+chat.getString("id"), "POST");
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    byte[] byteArray = new byte[1];
                                    if (bit1!=null){
                                        bit1.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        byteArray = stream.toByteArray();
                                        chatDao.updateProfile(byteArray, chat.getString("id"));
                                    }
                                }else{
                                    chatDao.updateChat(chat.getString("message"), chat.getLong("time"),chat.getString("id"));
                                }
                            }
                            break;

                    }

                    List<Chat> newMyChats = chatDao.getAllChats();
                    Collections.sort(newMyChats, new Comparator<Chat>() {
                        @Override
                        public int compare(Chat o1, Chat o2) {
                            return Long.compare(o2.time, o1.time);
                        }
                    });

                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new ChatsAdapter(newMyChats);
                            chats.setAdapter(adapter);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        get.start();
        switch (data.getString("action")){
            case "goToChat":
                if(exist.get(data.getString("id")) == null){

                    Bundle chat_data = new Bundle();
                    chat_data.putString("time", "first");
                    chat_data.putString("id", data.getString("id"));
                    chat_data.putString("name", data.getString("name"));
                    chat_data.putString("surname", data.getString("surname"));
                    ChatFragment chatFragment = new ChatFragment(listener, chat_data);
                    chatFragment.setArguments(chat_data);
                    chatFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
                    chatsFragments.add(chatFragment);
                    exist.put(data.getString("id"), chatsFragments.size()-1);
                    chatFragment.show(getChildFragmentManager(), "d");

                }else{

                    chatsFragments.get((exist.get(data.getString("id")))).show(getChildFragmentManager(), "d");
                }
                openIndex = exist.get(data.getString("id"));
                break;
            case "goToList":

                openIndex = -1;
                break;
        }

        return v;
    }

    public void setSocket(SocketListener listener){
        this.listener = listener;
    }

  @RequiresApi(api = Build.VERSION_CODES.N)
  public void updateMessages(JSONObject message){
        Thread update = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("TUTAJ");
                    Messages list_messages = messagesDao.getMessages(message.getString("room"));
                    System.out.println("TUTAJ");
                    JSONArray messages = new JSONArray();
                    if(list_messages!=null){
                        messages = new JSONArray(list_messages.messages);
                        messages.put(message.getJSONObject("message"));
                        if(messages.length()>50){
                            messages.remove(0);
                        }
                        list_messages.messages = messages.toString();
                        messagesDao.updateMessages(messages.toString(), message.getString("room"));
                        JSONObject lastMessage = messages.getJSONObject(messages.length()-1);
                        chatDao.updateChat(lastMessage.getString("content"), lastMessage.getLong("time"),message.getString("room") );
                    }else{

                    }

                    if(getContext()!=null){
                        List<Chat> myChats = chatDao.getAllChats();
                        Collections.sort(myChats, new Comparator<Chat>() {
                            @Override
                            public int compare(Chat o1, Chat o2) {
                                return Long.compare(o2.time, o1.time);
                            }
                        });
                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.update(myChats);
                            }
                        });
                    }



                    System.out.println(message.getString("room"));
                    System.out.println(exist.get(message.getString("room")));
                    System.out.println(exist.get(message.getString("room"))==openIndex);
                    if(message.getString("room")!=null && exist.get(message.getString("room"))!=null && exist.get(message.getString("room"))==openIndex){
                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    chatsFragments.get(openIndex).update(message.getJSONObject("message"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        update.start();

  }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void newConversation(JSONObject message){

            Thread create = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(message.getJSONObject("newConversation").toString());
                        if(message.getBoolean("byMe")){
                            JSONObject newConversation = message.getJSONObject("newConversation");
                            JSONArray messages = message.getJSONArray("messages");
                            chatDao.createChat(newConversation.getString("_id"), newConversation.getString("id"), newConversation.getString("name"),newConversation.getString("message"), newConversation.getInt("time") );
                            messagesDao.createMessages(newConversation.getString("id"), messages.toString());
                            System.out.println("CREAYED");
                            if(openIndex!=-1){
                                if(message.getString("room")!=null && exist.get(message.getString("room"))!=null && exist.get(message.getString("room"))==openIndex){
                                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                chatsFragments.get(openIndex).update(newConversation.getJSONObject("message").getJSONObject("content"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
//                                System.out.println(openIndex);
//                                chatsFragments.get(openIndex).thisWasNewConversation();
//                                Bundle chat_data = new Bundle();
//                                chat_data.putString("time", "first");
//                                chat_data.putString("id", newConversation.getString("id"));
//                                ChatFragment chatFragment = new ChatFragment(listener, chat_data);
//                                chatFragment.setArguments(chat_data);
//                                chatFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
//                                chatsFragments.set(openIndex, chatFragment);
//                                chatsFragments.get(openIndex).show(getChildFragmentManager(), "d");
                            }
                        }else{
                            JSONObject newConversation = message.getJSONObject("newConversation");
                            chatDao.createChat(newConversation.getString("_id"), newConversation.getString("id"), newConversation.getString("name"),newConversation.getString("message"), newConversation.getInt("time") );
                            JSONArray messages = message.getJSONArray("messages");
                            messagesDao.createMessages(newConversation.getString("id"), messages.toString());

                        }



                        if(getContext()!=null){
                            List<Chat> myChats = chatDao.getAllChats();
                            Collections.sort(myChats, new Comparator<Chat>() {
                                @Override
                                public int compare(Chat o1, Chat o2) {
                                    return Long.compare(o2.time, o1.time);
                                }
                            });
                            ((Activity) getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.update(myChats);
                                }
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            create.start();


    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getStringifyJSONArray(JSONArray things){
        String string = "[";
        System.out.println(things.toString());
        for (int i = 0; i < things.length(); i++){
            try{
                System.out.println(things.getJSONObject(i).toString());

                if(i<things.length()-1){
                    string+=(things.getJSONObject(i).toString()+",");
                }else{
                    string+=(things.getJSONObject(i).toString());
                }
            }catch (Exception e){

            }

        }
        string+="]";
        System.out.println(string);
        return string;
    }

    public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.Chat>{

        List<com.name.social_helper_r_p.rooms.Chat> messages = new ArrayList<>();

        public ChatsAdapter(List<com.name.social_helper_r_p.rooms.Chat> messages){
            this.messages = messages;
        }

        public void update(List<com.name.social_helper_r_p.rooms.Chat> chats){
            messages.clear();
            messages.addAll(chats);
            notifyDataSetChanged();
        }
        public class Chat extends RecyclerView.ViewHolder{
            TextView name;
            TextView lastMessage;
            ImageView profile;
            ConstraintLayout chat;
            public Chat(@NonNull View itemView) {
                super(itemView);
            }
        }
        @NonNull
        @Override
        public ChatsAdapter.Chat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_element, parent, false);
            Chat chat = new Chat(v);
            chat.name = v.findViewById(R.id.nick);
            chat.lastMessage = v.findViewById(R.id.message);
            chat.chat = v.findViewById(R.id.chat);
            chat.profile = v.findViewById(R.id.profile);
            return chat;
        }

        @Override
        public void onBindViewHolder(@NonNull ChatsAdapter.Chat holder, @SuppressLint("RecyclerView") int position) {

            holder.name.setText(messages.get(position).collaboratorName);
            holder.lastMessage.setText(messages.get(position).lastMessage);
            if(messages.get(position).profile!=null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(messages.get(position).profile, 0, messages.get(position).profile.length);
                holder.profile.setImageBitmap(bitmap);
            }
            holder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(messages.get(position).collaboratorID);
                    System.out.println(exist.get(messages.get(position).collaboratorID));
                    if(exist.get(messages.get(position).collaboratorID) == null){

                        Bundle chat_data = new Bundle();
                        chat_data.putString("time", "first");
                        chat_data.putString("id", messages.get(position).collaboratorID);
                        ChatFragment chatFragment = new ChatFragment(listener, chat_data);
                        chatFragment.setArguments(chat_data);
                        chatFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
                        chatsFragments.add(chatFragment);
                        exist.put(messages.get(position).collaboratorID, chatsFragments.size()-1);
                        chatFragment.show(getChildFragmentManager(), "d");

                    }else{

                        chatsFragments.get(exist.get(messages.get(position).collaboratorID)).show(getChildFragmentManager(), "d");
                    }
                    openIndex = exist.get(messages.get(position).collaboratorID);
                }
            });
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void socketDisconnected(){
        chatsFragments.stream().forEach(chatFragment ->{
            chatFragment.messages = new ArrayList<>();
        });
    }

    public void connectedAgain(){
        if(openIndex!=-1){
            chatsFragments.get(openIndex).connectedAgain();
        }
    }



    public void disableOpen(){
        openIndex = -1;
    }
}
