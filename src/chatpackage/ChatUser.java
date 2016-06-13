/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatpackage;

import java.io.Serializable;

/**
 *
 * @author Link
 */
public class ChatUser implements Serializable {
    
    private static final long serialVersionUID = 10L;
    
    int id;
    String username;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
    String status;
    String email;
    boolean online;
    
    public ChatUser() {
        
    }
    
    public ChatUser(int id, String username, String email, String status, boolean online) {
        this.id = id;
        this.username = username;
        this.status = status;
        this.online = online;
    }
    
}
