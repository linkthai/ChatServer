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
public class PackageFriendList extends ChatPackage {
    
    private static final long serialVersionUID = 600L;
    
    ArrayList<ChatUser> friends;
    ArrayList<ChatUser> pendingFriends;

    public PackageFriendList() {
        this.type = "FRIEND_LIST";
        friends = new ArrayList<>();
        pendingFriends = new ArrayList<>();
    }

    public ArrayList<ChatUser> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<ChatUser> friends) {
        this.friends = friends;
    }

    public ArrayList<ChatUser> getPendingFriends() {
        return pendingFriends;
    }

    public void setPendingFriends(ArrayList<ChatUser> pendingFriends) {
        this.pendingFriends = pendingFriends;
    }
    
}
