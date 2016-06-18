/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import chatpackage.*;

import chatpackage.PackageLogin;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 *
 * @author Link
 */
public class ClientConnection extends Thread {

    private Socket socket;
    private String username;
    private int id;

    private ObjectOutputStream objectOutput;
    private ObjectInputStream objectInput;

    private boolean isLoggedIn = false;
    private boolean isConnecting;

    public ClientConnection(Socket newSocket) {
        socket = newSocket;
        isConnecting = true;

        //Declare in and out stream for the socket
        try {
            objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectInput = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        }

    }

    public int getClientId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void run() {

        //listening through a while loop
        while (isConnecting) {

            ChatPackage inputPackage = null;

            try {
                inputPackage = (ChatPackage) objectInput.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                clientDisconnected();
                return;
            }

            analyseInput(inputPackage);

        }
    }

    public void analyseInput(ChatPackage inputPackage) {
        switch (inputPackage.getType()) {
            case "LOGIN":
                beginLogin(inputPackage);
                break;
            case "REGISTER":
                beginRegister(inputPackage);
                break;
            case "SEARCH_USER":
                beginSearchUser(inputPackage);
                break;
            case "FRIEND_REQUEST":
                beginFriendRequest(inputPackage);
                break;
            case "FRIEND_LIST":
                beginFriendList(inputPackage);
                break;
            case "FRIEND_DELETE":
                beginDeleteFriend(inputPackage);
                break;
            case "STATUS":
                beginStatus(inputPackage);
                break;
            case "CONVERSATION":
                beginConversation(inputPackage);
                break;
            case "MESSAGE":
                beginMessage(inputPackage);
                break;
            case "IMAGE":
                beginImage(inputPackage);
                break;
            case "SEEN":
                beginSeen(inputPackage);
                break;
            case "GROUP_CONVERSATION":
                beginGroupConversation(inputPackage);
                break;
            case "LOGOUT":
                beginLogout(inputPackage);
                break;
            default:
                break;

        }
    }

    public void sendObject(Object obj) {
        try {
            objectOutput.writeObject(obj);
        } catch (IOException ex) {
            Logger.getLogger(ChatClientUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void beginLogin(ChatPackage inputPackage) {

        if (this.isLoggedIn) {
            return;
        }

        String username_temp = "";
        String password = "";
        PackageLogin login = (PackageLogin) inputPackage;

        username_temp = login.getUsername();
        password = login.getPassword();

        int id_temp = ChatDatabase.CheckUser(username_temp, password);

        if (id_temp != 0) {
            login.setConfirm(true);
            login.setId(id_temp);
            login.setUser(ChatDatabase.getChatUser(id_temp));
            sendObject(login);

            isLoggedIn = true;
            username = username_temp;
            id = id_temp;
            ChatDatabase.RegisterClient(this);
            ChatDatabase.sendStatus(id, login.getUser().getStatus());
        } else {
            login.setConfirm(false);
            sendObject(login);
        }
    }

    public void updateLogin() {
        isLoggedIn = true;

    }

    public void beginSearchUser(ChatPackage inputPackage) {
        PackageSearchUser search = (PackageSearchUser) inputPackage;

        ArrayList<ChatUser> list = ChatDatabase.searchUsername(search.getSearch());
        search.setList(list);
        sendObject(search);

    }

    public void beginRegister(ChatPackage inputPackage) {

        if (this.isLoggedIn) {
            return;
        }

        String username_temp = "";
        String password = "";
        String email = "";
        PackageRegister register = (PackageRegister) inputPackage;

        username_temp = register.getUsername();
        password = register.getPassword();
        email = register.getEmail();

        if (ChatDatabase.checkUsernameAvailable(username_temp)) {
            register.setAvailable(true);
        } else {
            register.setAvailable(false);
            sendObject(register);
            return;
        }

        if (ChatDatabase.InsertUser(username_temp, password, email)) {
            register.setSuccessful(true);
        }

        sendObject(register);

    }

    public void beginFriendRequest(ChatPackage inputPackage) {
        PackageFriendRequest request = (PackageFriendRequest) inputPackage;

        //if request is true then it is a sent request
        if (request.isRequest() == true) {

            if (ChatDatabase.checkIsFriend(request.getUserSender(), request.getUserReceiver())) {
                return;
            }

            ChatDatabase.sendFriendRequest(request.getUserSender(), request.getUserReceiver());
            request.setUser(ChatDatabase.getChatUser(request.getUserSender()));

            //send request if user online
            if (ChatDatabase.CheckOnlineStatus(request.getUserReceiver())) {
                ChatDatabase.SendObject(request, request.getUserReceiver());
            }

        } else { //if request is false then it is an answer
            if (request.isAccept()) {
                ChatDatabase.acceptFriendRequest(id, request.getUserSender());
            } else {
                ChatDatabase.deleteFriend(id, request.getUserSender());
            }

            request.setUser(ChatDatabase.getChatUser(request.getUserReceiver()));
            ChatDatabase.SendObject(request, request.getUserSender());

        }

    }

    public void beginFriendList(ChatPackage inputPackage) {

        PackageFriendList friendList = (PackageFriendList) inputPackage;

        ChatDatabase.getFriendList(id, friendList.getFriends(), friendList.getPendingFriends());
        sendObject(friendList);
    }

    private void beginStatus(ChatPackage inputPackage) {

        PackageStatus status = (PackageStatus) inputPackage;

        //if (status.getFriend_id() == 0) {
        ChatDatabase.changeStatus(status.getId(), status.getStatus());
        //} else {
        //ChatDatabase.SendObject(status, status.getFriend_id());
        //}
    }

    private void beginConversation(ChatPackage inputPackage) {
        PackageConversation con = (PackageConversation) inputPackage;

        ChatDatabase.getConversation(con);
        sendObject(con);
    }

    private void beginMessage(ChatPackage inputPackage) {

        PackageMessage message = (PackageMessage) inputPackage;
        ChatDatabase.addMessage(message.getId_con(), message.getMessage());

        if (message.isGroupMessage()) {
            ChatDatabase.setNotSeenGroup(message.getId_con(), id);
            ArrayList<Integer> grp_list = ChatDatabase.getGroupMembers(message.getId_con());
            for (Integer i : grp_list) {
                ChatDatabase.SendObject(message, i);
            }
        } else {
            ChatDatabase.setNotSeen(message.getId_con(), id);
            ChatDatabase.SendObject(message, message.getReceiver());
        }
    }

    private void beginLogout(ChatPackage inputPackage) {
        PackageLogout logout = (PackageLogout) inputPackage;

        ChatDatabase.logoutUser(logout.getId());

        isLoggedIn = false;
        id = 0;
        username = "";
    }

    private void clientDisconnected() {

        if (isLoggedIn == true) {
            ChatDatabase.logoutUser(id);
        }

        try {
            objectInput.close();
            objectOutput.close();
            socket.close();
            isConnecting = false;
        } catch (IOException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void beginSeen(ChatPackage inputPackage) {
        PackageSeen seen = (PackageSeen) inputPackage;

        if (seen.isGroupConversation())
        {
            ChatDatabase.setSeenGroup(seen.getId_con(), seen.getId_user());
        }
        else
        {
            ChatDatabase.setSeen(seen.getId_con(), seen.getId_user());
        }
    }

    private void beginGroupConversation(ChatPackage inputPackage) {
        PackageGroupConversation group = (PackageGroupConversation) inputPackage;
        ArrayList<Integer> grp_list = ChatDatabase.getGroupMembers(group.getId_con());
        boolean flag = false;

        switch (group.getAction()) {
            case "CREATE":
                String newCon = ChatDatabase.createGroup(group.getId_sender(), group.getName());
                group.setId_con(newCon);
                sendObject(group);
                break;
            case "RENAME":
                flag = ChatDatabase.renameGroup(group.getId_sender(), group.getId_con(), group.getName());
                break;
            case "ADD":
                flag = ChatDatabase.addUser(group.getId_sender(), group.getId_con(), group.getId_receiver());
                group.setUser(ChatDatabase.getChatUser(group.getId_receiver()));
                ChatDatabase.SendObject(group, group.getId_receiver());
                break;
            case "KICK":
                flag = ChatDatabase.kickUser(group.getId_sender(), group.getId_con(), group.getId_receiver());
                break;
            case "LEAVE":
                flag = ChatDatabase.leaveConversation(group.getId_sender(), group.getId_con());
                break;
            case "PASS_MASTER":
                flag = ChatDatabase.passMaster(group.getId_sender(), group.getId_con(), group.getId_receiver());
                break;
            case "DELETE":
                flag = ChatDatabase.deleteGroup(group.getId_sender(), group.getId_con());
                break;
            case "CONVERSATION":
                ArrayList<GroupConversation> list_con;
                list_con = ChatDatabase.getGroupConversation(group.getId_sender());
                group.setList_con(list_con);
                sendObject(group);
                break;
        }

        if (flag) {
            for (Integer i : grp_list) {
                ChatDatabase.SendObject(group, i);
            }
        }

    }

    private void beginDeleteFriend(ChatPackage inputPackage) {
        PackageFriendDelete delete = (PackageFriendDelete) inputPackage;
        
        ChatDatabase.deleteFriend(delete.getSender(), delete.getReceiver());
        sendObject(delete);
        ChatDatabase.SendObject(delete, delete.getReceiver());
    }

    private void beginImage(ChatPackage inputPackage) {
        PackageImage image = (PackageImage) inputPackage;
        
        if (image.isUpload())
        {
            String id_image = ChatDatabase.generateNewFile("", image.getExtension());
            File f = new File("File//" + id_image + "." + image.getExtension());

            try {
                ImageIO.write(image.getImage(), image.getExtension(), f);
            } catch (IOException ex) {
                Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            }

            image.setId_image(id_image);
            sendObject(image);
        }
        else
        {
            File f = new File("File//" + image.getId_image() + "." + image.getExtension());
            try {
                BufferedImage newImage = ImageIO.read(f);
                image.setImage(newImage);
            } catch (IOException ex) {
                Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            sendObject(image);
        }
    }

}
