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
public class PackageSearchUser extends ChatPackage {
    
    private static final long serialVersionUID = 400L;
    
    String search = "";

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public ArrayList<ChatUser> getList() {
        return list;
    }

    public void setList(ArrayList<ChatUser> list) {
        this.list = list;
    }
    
    ArrayList<ChatUser> list;
    
    public PackageSearchUser(String search) {        
        this.type = "SEARCH_USER";
        this.search = search;
    }
    
}
