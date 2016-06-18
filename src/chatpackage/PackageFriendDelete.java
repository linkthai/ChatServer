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
public class PackageFriendDelete extends ChatPackage {    
    
    private static final long serialVersionUID = 1200L;
    
    private int sender;
    private int receiver;

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getReceiver() {
        return receiver;
    }

    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }
    
    public PackageFriendDelete(int sender, int receiver) {
        this.type = "FRIEND_DELETE";
        this.sender = sender;
        this.receiver = receiver;
    }
}
