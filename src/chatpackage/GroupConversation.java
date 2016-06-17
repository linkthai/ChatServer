/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatpackage;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Link
 */
public class GroupConversation implements Serializable {
    
    private static final long serialVersionUID = 30L;

    public ArrayList<ChatUser> getList_user() {
        return list_user;
    }

    public void setList_user(ArrayList<ChatUser> list_user) {
        this.list_user = list_user;
    }

    public ArrayList<ChatMessage> getConversation() {
        return conversation;
    }

    public void setConversation(ArrayList<ChatMessage> conversation) {
        this.conversation = conversation;
    }

    public String getId_con() {
        return id_con;
    }

    public void setId_con(String id_con) {
        this.id_con = id_con;
    }

    public int getMessage_not_seen() {
        return message_not_seen;
    }

    public void setMessage_not_seen(int message_not_seen) {
        this.message_not_seen = message_not_seen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    private ArrayList<ChatMessage> conversation;    
    private ArrayList<ChatUser> list_user;
    private String id_con;
    private int message_not_seen;
    private String name;
    
    public GroupConversation() {
        
    }
    
}
