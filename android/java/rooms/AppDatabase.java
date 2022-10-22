package com.name.social_helper_r_p.rooms;

import androidx.room.Database;
import androidx.room.RoomDatabase;
@Database(entities = {Chat.class, Messages.class}, version = 6)
public abstract class AppDatabase  extends RoomDatabase {
    public abstract ChatDao chatDao();
    public abstract  MessagesDao messagesDao();
}
