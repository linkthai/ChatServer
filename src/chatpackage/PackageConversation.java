/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatpackage;

import java.util.ArrayList;

/**
 *
 * @author Link
 */
public class PackageConversation extends ChatPackage {
    
    private static final long serialVersionUID = 800L;

    public String getId_con() {
        return id_con;
    }

    public void setId_con(String id_con) {
        this.id_con = id_con;
    }

    public ArrayList<ChatMessage> getConversation() {
        return conversation;
    }

    public void setConversation(ArrayList<ChatMessage> conversation) {
        this.conversation = conversation;
    }

    public int getMessage_not_seen() {
        return message_not_seen;
    }

    public void setMessage_not_seen(int message_not_seen) {
        this.message_not_seen = message_not_seen;
    }
    
    private String id_con = "";
    private ArrayList<ChatMessage> conversation;
    private int id_userA;
    private int id_userB;
    private int message_not_seen = 0;

    public int getId_userA() {
        return id_userA;
    }

    public void setId_userA(int id_userA) {
        this.id_userA = id_userA;
    }

    public int getId_userB() {
        return id_userB;
    }

    public void setId_userB(int id_userB) {
        this.id_userB = id_userB;
    }
    
    public PackageConversation() {
        this.type = "CONVERSATION";
        this.conversation = new ArrayList();
    }
}
