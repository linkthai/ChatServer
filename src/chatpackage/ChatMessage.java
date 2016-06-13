/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatpackage;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Link
 */
public class ChatMessage implements Serializable {
    
    private static final long serialVersionUID = 20L;
    
    Date date;
    private String type = "";
    private String message = "";

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }
    
    private int sender;
    
    public ChatMessage(int sender, String message) {
        this.type = "TEXT";
        this.message = message;
        this.date = new Date();
        this.sender = sender;
    }
    
}
