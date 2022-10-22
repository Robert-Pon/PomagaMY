package com.name.social_helper_r_p.rooms;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface MessagesDao {
   @Query("SELECT * FROM messages WHERE room =:id")
   Messages getMessages(String id);

   @Query("INSERT INTO messages (room, messages) VALUES (:room, :messages)")
    void createMessages(String room, String messages);

   @Query("UPDATE messages SET messages = :messages WHERE room = :room")
    void updateMessages(String messages, String room);
    @Query("DELETE FROM messages")
    void clear();
}
