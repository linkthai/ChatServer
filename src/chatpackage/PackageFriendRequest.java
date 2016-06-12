/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatpackage;

/**
 *
 * @author Link
 */
public class PackageFriendRequest extends ChatPackage {
    
    private static final long serialVersionUID = 500L;
    
    private int userSender;
    private int userReceiver;
    private boolean request;
    private boolean accept;

    public boolean isRequest() {
        return request;
    }

    public void setRequest(boolean request) {
        this.request = request;
    }

    public boolean isAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }

    public int getUserSender() {
        return userSender;
    }

    public void setUserSender(int userSender) {
        this.userSender = userSender;
    }

    public int getUserReceiver() {
        return userReceiver;
    }

    public void setUserReceiver(int userReceiver) {
        this.userReceiver = userReceiver;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public PackageFriendRequest(int userA, int userB) {
        this.type = "FRIEND_REQUEST";
        this.userSender = userA;
        this.userReceiver = userB;
        this.accept = false;
    }
    
}
