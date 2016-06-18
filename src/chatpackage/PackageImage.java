/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatpackage;

import java.awt.image.BufferedImage;

/**
 *
 * @author Link
 */
public class PackageImage extends ChatPackage {
    
    private static final long serialVersionUID = 1300L;

    public int getId_sender() {
        return id_sender;
    }

    public void setId_sender(int id_sender) {
        this.id_sender = id_sender;
    }

    public String getId_con() {
        return id_con;
    }

    public void setId_con(String id_con) {
        this.id_con = id_con;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public String getId_image() {
        return id_image;
    }

    public void setId_image(String id_image) {
        this.id_image = id_image;
    }

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
    
    private int id_sender;
    private String id_con;
    private BufferedImage image;
    private String id_image;
    private String extension;
    private boolean upload;
    
    public PackageImage(int id_sender, String id_con, BufferedImage image) {
        this.type = "IMAGE";
        this.id_sender = id_sender;
        this.id_con = id_con;
        this.image = image;
        this.setUpload(true);
    }
    
    public PackageImage(int id_sender, String id_con, String id_image) {
        this.type = "IMAGE";
        this.id_sender = id_sender;
        this.id_con = id_con;
        this.id_image = id_image;
        this.setUpload(false);
    }
    
}
