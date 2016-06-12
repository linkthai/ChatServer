/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import chatpackage.ChatUser;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.swing.JOptionPane;

/**
 *
 * @author Link
 */
public class ChatDatabase {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:derby://localhost:1527/ChatManager;create=true";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "admin";
    // jdbc Connection
    private static Connection conn = null;
    private static Statement stmt = null;

    //Database Tables
    static final String CHAT_USER = "CHAT_USER";
    static final String FRIEND = "FRIEND_RELATIONSHIP";
    static final String CON = "CONVERSATION";

    private static HashMap<Integer, ClientConnection> clientList;

    public static boolean StartConnection() {
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            //Get a connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            clientList = new HashMap<>();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException except) {

            JOptionPane.showMessageDialog(null, except.getClass(), null, JOptionPane.PLAIN_MESSAGE);
            return false;
        }

        return CheckValid();
    }

    public static void RegisterClient(ClientConnection client) {
        clientList.put(client.getClientId(), client);
    }

    public static void UnregisterClient(ClientConnection client) {
        clientList.remove(client.getClientId(), client);
        JOptionPane.showMessageDialog(null, client.getUsername());
    }

    public static boolean CheckOnlineStatus(int id) {
        ClientConnection conn = clientList.get(id);

        if (conn != null) {
            return true;
        }
        return false;
    }

    public static boolean CheckValid() {

        try {
            return conn != null && conn.isValid(1);
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public static void SendObject(Object obj, int receiver) {        
       ClientConnection conn = clientList.get(receiver);
       conn.sendObject(obj);
    }

    public static boolean InsertUser(String username, String password, String email) {

        int id = generateNewUserId();
        String str_id = String.valueOf(id);

        try {
            stmt = conn.createStatement();
            stmt.execute("insert into " + CHAT_USER + "(ID_USER, USERNAME, PASSWORD, EMAIL)" + " values (" + str_id + ",'"
                    + username + "','" + password + "','" + email + "')");
            stmt.close();
        } catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean checkUsernameAvailable(String username) {
        ResultSet result = null;
        boolean isAvailable = true;

        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery("select * from " + CHAT_USER + " where USERNAME='" + username + "'");

            //if username found, then it's not available.
            isAvailable = !result.next();
            stmt.close();

        } catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        }

        return isAvailable;
    }

    public static int CheckUser(String username, String password) {
        ResultSet result = null;
        int id = 0;

        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery("select * from " + CHAT_USER + " where USERNAME='" + username + "' and PASSWORD='" + password + "'");

            if (result.next()) {
                id = (int) result.getInt("ID_USER");
            }

            stmt.close();

        } catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        }

        return id;
    }

    public static int generateNewUserId() {
        ResultSet result = null;
        int id = 1;

        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery("select MAX(ID_USER) from " + CHAT_USER);

            if (result.next()) {
                id = (int) result.getInt(1);
                id++;
            }
            stmt.close();

        } catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        }

        return id;

    }

    public static ArrayList<ChatUser> searchUsername(String string) {
        ArrayList<ChatUser> list = new ArrayList<>();

        ResultSet result = null;

        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery("select * from " + CHAT_USER + " where username like '" + string + "%'");

            while (result.next()) {
                int id = (int) result.getInt("ID_USER");
                String username = result.getString("USERNAME");
                String status = result.getString("STATUS");
                String email = result.getString("EMAIL");
                ChatUser user = new ChatUser(id, username, email, status, CheckOnlineStatus(id));
                list.add(user);
            }
            stmt.close();

        } catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        }

        return list;
    }

    public static String generateNewConversation() {

        UUID uuid;
        boolean check = false;
        ResultSet result = null;

        //generate a unique id
        do {
            uuid = UUID.randomUUID();

            try {
                stmt = conn.createStatement();
                result = stmt.executeQuery("select * from " + CON + " where ID_CON='" + uuid.toString() + "'");

                if (result.next()) {
                    check = false;
                } else {
                    check = true;
                }
                stmt.close();

            } catch (SQLException ex) {
                Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
            }

        } while (check == false);

        //add conversation
        try {
            stmt = conn.createStatement();
            stmt.execute("insert into " + CON + " values ('" + uuid.toString() + "','')");
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        return uuid.toString();
    }

    public static void sendFriendRequest(int userSender, int userReceiver) {

        String newConversation = generateNewConversation();

        try {
            stmt = conn.createStatement();
            stmt.execute("insert into " + FRIEND + "(userA, userB, id_con, message_not_seen, confirm) "
                    + "values (" + userSender + "," + userReceiver + ",'" + newConversation + "',1, true)");
            stmt.execute("insert into " + FRIEND + "(userA, userB, id_con, message_not_seen, confirm) "
                    + "values (" + userReceiver + "," + userSender + ",'" + newConversation + "',1, false)");
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void acceptFriendRequest(int userReceiver, int userSender) {
        try {
            stmt = conn.createStatement();
            stmt.execute("update " + FRIEND + " set confirm=true where userA=" + userReceiver + " and userB=" + userSender);
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void deleteFriend(int userA, int userB) {

        String conversation = "";
        ResultSet result = null;

        try {
            stmt = conn.createStatement();

            //find conversation
            result = stmt.executeQuery("select * from " + FRIEND + " where userA=" + userA + " and userB=" + userB);
            if (result.next()) {
                conversation = result.getString("ID_CON");
            }

            //delete both friend relationship
            stmt.execute("delete from " + FRIEND + " where userA=" + userA + " and userB=" + userB);
            stmt.execute("delete from " + FRIEND + " where userA=" + userB + " and userB=" + userA);

            //delete conversation
            stmt.execute("delete from " + CON + " where id_con='" + conversation + "'");

            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
