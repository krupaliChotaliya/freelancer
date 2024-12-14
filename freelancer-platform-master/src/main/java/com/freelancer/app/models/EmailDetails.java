package com.freelancer.app.models;

import javax.persistence.*;

@Entity
public class EmailDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String[] recipients; // List of recipients

    private String msgBody;
    private String subject;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String[] getRecipients() {
        return recipients;
    }

    public void setRecipients(String[] recipients) {
        this.recipients = recipients;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
