/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatpackage;

import java.awt.image.BufferedImage;
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
    private String content = "";
    private BufferedImage image;

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
        this.content = message;
        this.date = new Date();
        this.sender = sender;
    }
    
    public ChatMessage(int sender, String id_message, String type)
    {
        this.type = type;
        this.date = new Date();
        this.sender = sender;
        this.content = id_message;
    }
}
