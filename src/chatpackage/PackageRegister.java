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
public class PackageRegister extends ChatPackage {
    
    private static final long serialVersionUID = 300L;
    
    private String username;
    private String password;
    private String email;
    
    private boolean available;
    private boolean successful;
    
    public PackageRegister() {
        this.type = "REGISTER";
        available = false;
        successful = false;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
}
