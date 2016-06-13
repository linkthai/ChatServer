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
public class PackageStatus extends ChatPackage {
    
    private static final long serialVersionUID = 700L;
    
    int id;
    String status;
    int friend_id;

    public int getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(int friend_id) {
        this.friend_id = friend_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public PackageStatus(int id, String status) {
        this.type = "STATUS";
        this.id = id;
        this.friend_id = 0;
        this.status = status;
    }
}
