package com.name.social_helper_r_p.rooms;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChatDao {
    @Query("SELECT * FROM chat WHERE _id = :id")
    List<Chat> getChats(String id);

    @Query("SELECT * FROM chat")
    List<Chat> getAllChats();


    @Query("UPDATE chat SET blocked = :blocked WHERE _id = :id")
    void updateBlock(String blocked, String id);

    @Query("INSERT INTO chat (_id, collaboratorID, collaboratorName,lastMessage, time) VALUES (:id, :collaboratorID, :collaboratorName,:lastMessage, :time)")
    void createChat(String id, String collaboratorID,String collaboratorName, String lastMessage, long time );

    @Query("SELECT * FROM chat WHERE collaboratorID = :id")
    Chat getChat(String id);

    @Query("UPDATE chat SET lastMessage = :message, time = :time WHERE collaboratorID = :id")
    void updateChat(String message, long time, String id);

    @Query("UPDATE chat SET profile = :profile WHERE collaboratorID = :id")
    void updateProfile(byte[] profile, String id);
    @Query("DELETE FROM chat")
    void clear();

}
