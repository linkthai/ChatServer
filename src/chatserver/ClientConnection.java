/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import chatpackage.*;

import chatpackage.PackageLogin;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
            case "STATUS":
                beginStatus(inputPackage);
                break;
            case "CONVERSATION":
                beginConversation(inputPackage);
                break;
            case "MESSAGE":
                beginMessage(inputPackage);
                break;
            case "SEEN":
                beginSeen(inputPackage);
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
            
            if (ChatDatabase.checkIsFriend(request.getUserSender(), request.getUserReceiver()))
                return;

            ChatDatabase.sendFriendRequest(request.getUserSender(), request.getUserReceiver());

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
        ChatDatabase.setNotSeen(message.getId_con(), id);
        ChatDatabase.SendObject(message, message.getReceiver());
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
        
        ChatDatabase.setSeen(seen.getId_con(), seen.getId_user());
    }

}