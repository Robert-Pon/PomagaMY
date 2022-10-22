package com.name.social_helper_r_p.rooms;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Chat {

    @PrimaryKey
    public int id;

    @ColumnInfo(name="_id")
    public String _id;

    @ColumnInfo(name="blocked")
    public String blocked;

    @ColumnInfo(name="collaboratorID")
    public String collaboratorID;

    @ColumnInfo(name="collaboratorName")
    public String collaboratorName;

    @ColumnInfo(name="lastMessage")
    public String lastMessage;

    @ColumnInfo(name="time")
    public long time;

    @ColumnInfo(name="profile")
    public byte[] profile;


}
