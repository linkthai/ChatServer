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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.swing.JOptionPane;

/**
 *
 * @author Link
 */
public class ClientConnection extends Thread {

    private Socket socket;
    private String username;
    private int id;
    private BufferedReader in;
    private PrintWriter out;

    private ObjectOutputStream objectOutput;
    private ObjectInputStream objectInput;

    private boolean isLoggedIn = false;

    public ClientConnection(Socket newSocket) {
        socket = newSocket;

        //Declare in and out stream for the socket
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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

    public void sendOutput(String output) {
        out.println(output);
    }

    @Override
    public void run() {

        //listening through a while loop
        while (true) {

            String input = "";
            ChatPackage inputPackage = null;

            try {
                //input = in.readLine();
                inputPackage = (ChatPackage) objectInput.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
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
            sendObject(login);

            isLoggedIn = true;
            username = username_temp;
            id = id_temp;
            ChatDatabase.RegisterClient(this);
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
            
        }

    }
}
