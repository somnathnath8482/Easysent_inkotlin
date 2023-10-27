package com.easy.kotlintest.socket;

import com.easy.kotlintest.Room.Messages.Chats;

/**
 * Created by Somnath nath on 27,October,2023
 * Artix Development,
 * India.
 */
public class CustomMessage {
    String profile_image;
    Chats chats;
    String reciver;//socket message will be sent to this event

    public CustomMessage(String profile_image, Chats chats, String reciver) {
        this.profile_image = profile_image;
        this.chats = chats;
        this.reciver = reciver;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}
