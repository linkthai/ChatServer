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
public class PackageClearConversation extends ChatPackage {    
    
    private static final long serialVersionUID = 1300L;
    
    private int sender;
    private int receiver;
    private String id_con;

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

    public String getId_con() {
        return id_con;
    }

    public void setId_con(String id_con) {
        this.id_con = id_con;
    }
    
    public PackageClearConversation(int sender, int receiver, String id_con) {
        this.type = "CLEAR";
        this.sender = sender;
        this.receiver = receiver;
        this.id_con = id_con;
    }
}
