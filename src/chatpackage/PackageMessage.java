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
public class PackageMessage extends ChatPackage {
    
    private static final long serialVersionUID = 900L;
    
    ChatMessage message;
    String id_con;
    int sender;
    int receiver;

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

    public ChatMessage getMessage() {
        return message;
    }

    public void setMessage(ChatMessage message) {
        this.message = message;
    }

    public String getId_con() {
        return id_con;
    }

    public void setId_con(String id_con) {
        this.id_con = id_con;
    }
    
    public PackageMessage(int sender, int receiver, String text) {
        this.type = "MESSAGE";
        this.sender = sender;
        this.receiver = receiver;
        message = new ChatMessage(sender, text);
    }
}
