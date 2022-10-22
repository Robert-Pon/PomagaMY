package com.name.social_helper_r_p.user;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketListener {
    SocketListenerInterface socketListenerInterface;

    boolean firstTime = true;
    Socket socket;
    public SocketListener(Socket socket, Context context){
        this.socket = socket;
        socketListenerInterface = (SocketListenerInterface) context;
    }

    public void listenChats(String token, String token_id) throws JSONException {
        JSONObject loginObject = new JSONObject();
        loginObject.put("token", token);
        loginObject.put("token_id", token_id);
        socket.connect();

        socket.on("connected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    String s = args[0].toString();
                }catch (Exception e){

                }
            }
        });

        socket.on("disconnect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socketListenerInterface.socketDisconnected();
            }
        });

        socket.on("connect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.emit("login", loginObject);
                if(!firstTime){
                    socketListenerInterface.connectedAgain();
                }else
                    firstTime = false;
            }
        });
        socket.on("newMessage", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println(args[0].toString());
                try {
                    JSONObject message = new JSONObject(args[0].toString());
                    System.out.println(args[0].toString());
                    socketListenerInterface.updateMessages(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        socket.on("newConversation", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject conversation = new JSONObject(args[0].toString());
                    socketListenerInterface.newConversation(conversation);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public Socket getSocket(){
        return socket;
    }

    public interface SocketListenerInterface{
        public void message();
        public void updateMessages(JSONObject message);
        public void newConversation(JSONObject newConversation);
        public void socketDisconnected();
        public void connectedAgain();

    }

}


