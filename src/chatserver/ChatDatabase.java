/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import chatpackage.ChatMessage;
import chatpackage.ChatUser;
import chatpackage.PackageConversation;
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
        if (conn != null) {
            conn.sendObject(obj);
        }
    }

    public static boolean InsertUser(String username, String password, String email) {

        int id = generateNewUserId();
        String str_id = String.valueOf(id);
        Statement stmt = null;

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
        Statement stmt = null;
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
        Statement stmt = null;
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
        Statement stmt = null;
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
        Statement stmt = null;

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
        Statement stmt = null;

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

    public static boolean checkIsFriend(int userA, int userB) {
        ResultSet result = null;
        Statement stmt = null;
        boolean check = false;

        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery("select * from " + FRIEND + " where userA=" + userA + " and userB=" + userB);
            if (result.next()) {
                check = true;
            }
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        return check;

    }

    public static void sendFriendRequest(int userSender, int userReceiver) {

        String newConversation = generateNewConversation();
        Statement stmt = null;

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
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.execute("update " + FRIEND + " set CONFIRM=TRUE where userA=" + userReceiver + " and userB=" + userSender);
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void deleteFriend(int userA, int userB) {

        String conversation = "";
        ResultSet result = null;
        Statement stmt = null;

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

    public static ChatUser getChatUser(int id) {

        Statement stmt = null;
        ResultSet result = null;
        ChatUser user = null;

        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery("select * from " + CHAT_USER + " where id_user=" + id);

            if (result.next()) {
                id = result.getInt("ID_USER");
                String username = result.getString("USERNAME");
                String status = result.getString("STATUS");
                String email = result.getString("EMAIL");
                user = new ChatUser(id, username, email, status, CheckOnlineStatus(id));
                user.setOnline(CheckOnlineStatus(id));
            }
            stmt.close();

        } catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        }

        return user;
    }

    public static void getFriendList(int id, ArrayList<ChatUser> friends, ArrayList<ChatUser> pendingFriends) {

        Statement stmt = null;
        Statement stmt_sub = null;
        ResultSet result = null;
        ResultSet result_sub = null;

        try {
            stmt = conn.createStatement();
            stmt_sub = conn.createStatement();

            //find conversation
            result = stmt.executeQuery("select * from " + FRIEND + " where userA=" + id);

            while (result.next()) {

                int id_userB = result.getInt("USERB");

                //if confirm is false, the friend request is still pending
                if (result.getBoolean("CONFIRM") == false) {
                    pendingFriends.add(getChatUser(id_userB));
                } else { //check if the other friend relationship is confirmed yet, if it is, then add to friend list
                    result_sub = stmt_sub.executeQuery("select * from " + FRIEND + " where userA=" + id_userB + " and userB=" + id);
                    result_sub.next();
                    if (result_sub.getBoolean("CONFIRM") == true) {
                        friends.add(getChatUser(id_userB));
                    }
                }
            }

            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void changeStatus(int id, String status) {

        Statement stmt = null;

        try {
            stmt = conn.createStatement();
            stmt.execute("update " + CHAT_USER + " set status='" + status + "' where id_user=" + id);

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static public void getConversation(PackageConversation con) {

        Statement stmt = null;
        ResultSet result = null;

        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery("select * from " + FRIEND + " where userA=" + con.getId_userA() + " and userB=" + con.getId_userB());

            if (result.next()) {
                con.setId_con(result.getString("ID_CON"));
                con.setMessage_not_seen(result.getInt("MESSAGE_NOT_SEEN"));
            }

            result = stmt.executeQuery("select * from " + CON + " where id_con='" + con.getId_con() + "'");

            if (result.next()) {
                String str_con = result.getString("MESSAGE");
                con.setConversation(parseConversation(str_con));
            }

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static public ArrayList<ChatMessage> parseConversation(String str_con) {
        
        ArrayList<ChatMessage> conversation = new ArrayList();

        String[] parts = str_con.split("[|]");

        for (int i = 0; i < parts.length; i++) {

            if ("".equals(parts[i])) {
                continue;
            }

            ChatMessage message = null;

            int id = Integer.parseInt(parts[i]);
            i++;
            String type = parts[i];
            i++;
            if (type.equals("TEXT")) {
                message = new ChatMessage(id, parts[i]);
            }

            conversation.add(message);
        }
        
        return conversation;
    }

    static void addMessage(String id_con, ChatMessage message) {
        Statement stmt = null;
        ResultSet result = null;

        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery("select * from " + CON + " where id_con='" + id_con + "'");

            if (result.next()) {
                String str_con = result.getString("MESSAGE");
                str_con += message.getSender();
                str_con += "|";
                str_con += message.getType();
                str_con += "|";
                str_con += message.getMessage();
                str_con += "|";
                
                stmt.execute("update " + CON + " set message='" + str_con + "' where id_con='" + id_con + "'");
            }

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
