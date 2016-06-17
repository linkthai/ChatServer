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
public class PackageSeen extends ChatPackage {
    
    private static final long serialVersionUID = 1100L;
    
    String id_con;
    int id_user;
    boolean groupConversation;

    public boolean isGroupConversation() {
        return groupConversation;
    }

    public void setGroupConversation(boolean groupConversation) {
        this.groupConversation = groupConversation;
    }

    public String getId_con() {
        return id_con;
    }

    public void setId_con(String id_con) {
        this.id_con = id_con;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }
    
    public PackageSeen(int id_user, String id_con) {
        this.type = "SEEN";
        this.id_user = id_user;
        this.id_con = id_con;        
    }
    
}
