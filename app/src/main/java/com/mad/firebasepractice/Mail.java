package com.mad.firebasepractice;

public class Mail {

    public String senderId;
    public String recipientId;
    public String mailTitle;
    public String mailMessage;

    public Mail() {
        // needed for writing to Firebase DB
    }

    public Mail(String senderId, String recipientId,
                String mailTitle, String mailMessage) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.mailTitle = mailTitle;
        this.mailMessage = mailMessage;
    }
}
