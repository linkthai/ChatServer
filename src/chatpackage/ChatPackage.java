/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatpackage;

import java.io.Serializable;

/**
 *
 * @author Link
 */
public abstract class ChatPackage implements Serializable {
    
    private static final long serialVersionUID = 100L;
    
    String type = "";
    
    public String getType() {
        return type; 
    }
    
}