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
public class PackageGroupConversation extends ChatPackage {    
    
    private static final long serialVersionUID = 1100L;
    
    private String action;
    private String id_con;
    private int id_sender;
    private int id_receiver;
    private String name;
    private ArrayList<GroupConversation> list_con;
    private ChatUser user;

    public ChatUser getUser() {
        return user;
    }

    public void setUser(ChatUser user) {
        this.user = user;
    }

    public ArrayList<GroupConversation> getList_con() {
        return list_con;
    }

    public void setList_con(ArrayList<GroupConversation> list_con) {
        this.list_con = list_con;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId_sender() {
        return id_sender;
    }

    public void setId_sender(int id_sender) {
        this.id_sender = id_sender;
    }

    public int getId_receiver() {
        return id_receiver;
    }

    public void setId_receiver(int id_receiver) {
        this.id_receiver = id_receiver;
    }

    public String getId_con() {
        return id_con;
    }

    public void setId_con(String id_con) {
        this.id_con = id_con;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
    
    public PackageGroupConversation() {
        this.type = "GROUP_CONVERSATION";
    }
    
}
