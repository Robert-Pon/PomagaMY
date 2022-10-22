package com.name.social_helper_r_p.rooms;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Messages {
    @PrimaryKey
    public int id;

    @ColumnInfo(name="room")
    public String room;

    @ColumnInfo(name="messages")
    public String messages;
}
