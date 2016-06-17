/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import chatpackage.ChatMessage;
import chatpackage.ChatUser;
import chatpackage.GroupConversation;
import chatpackage.PackageConversation;
import chatpackage.PackageStatus;
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
    static final String GROUP = "GROUP_RELATIONSHIP";

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
    }

    public static void UnregisterClient(int id) {
        clientList.remove(id);
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

    public static String generateNewConversation(String name) {

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
            stmt.execute("insert into " + CON + " values ('" + uuid.toString() + "','','" + name + "')");
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

        String newConversation = generateNewConversation("");
        Statement stmt = null;

        try {
            stmt = conn.createStatement();
            stmt.execute("insert into " + FRIEND + "(userA, userB, id_con, message_not_seen, confirm) "
                    + "values (" + userSender + "," + userReceiver + ",'" + newConversation + "',0, true)");
            stmt.execute("insert into " + FRIEND + "(userA, userB, id_con, message_not_seen, confirm) "
                    + "values (" + userReceiver + "," + userSender + ",'" + newConversation + "',0, false)");
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

        sendStatus(id, status);
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
                str_con += message.getContent();
                str_con += "|";

                stmt.execute("update " + CON + " set message='" + str_con + "' where id_con='" + id_con + "'");
            }

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static public ArrayList<Integer> getFriendIds(int id) {
        Statement stmt = null;
        ResultSet result = null;
        ArrayList<Integer> friendIds = new ArrayList();

        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery("select * from " + FRIEND + " where userA=" + id);

            while (result.next()) {
                friendIds.add(result.getInt("USERB"));
            }

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        return friendIds;
    }

    public static void sendStatus(int id, String str_status) {
        ArrayList<Integer> friendIds = getFriendIds(id);
        PackageStatus status = new PackageStatus(id, str_status);
        friendIds.stream().forEach((i) -> {
            SendObject(status, i);
        });
    }

    static public void logoutUser(int id) {
        UnregisterClient(id);

        sendStatus(id, "OFFLINE");
    }
    
    static void setNotSeenGroup(String id_con, int id_user) {
        Statement stmt = null;
        ResultSet result = null;

        try {
            stmt = conn.createStatement();

            result = stmt.executeQuery("select * from " + GROUP + " where id_con='" + id_con + "' and id_user=" + id_user);

            if (result.next()) {
                int message_not_seen = result.getInt("MESSAGE_NOT_SEEN");
                message_not_seen++;

                stmt.execute("update " + GROUP + " set message_not_seen=" + message_not_seen + " where id_con='" + id_con + "' and id_user=" + id_user);
            }

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    static void setSeenGroup(String id_con, int id_user) {
        Statement stmt = null;

        try {
            stmt = conn.createStatement();

            stmt.execute("update " + GROUP + " set message_not_seen=" + 0 + " where id_con='" + id_con + "' and id_user=" + id_user);

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void setNotSeen(String id_con, int id_user) {
        Statement stmt = null;
        ResultSet result = null;

        try {
            stmt = conn.createStatement();

            result = stmt.executeQuery("select * from " + FRIEND + " where id_con='" + id_con + "' and userB=" + id_user);

            if (result.next()) {
                int message_not_seen = result.getInt("MESSAGE_NOT_SEEN");
                message_not_seen++;

                stmt.execute("update " + FRIEND + " set message_not_seen=" + message_not_seen + " where id_con='" + id_con + "' and userB=" + id_user);
            }

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void setSeen(String id_con, int id_user) {
        Statement stmt = null;

        try {
            stmt = conn.createStatement();

            stmt.execute("update " + FRIEND + " set message_not_seen=" + 0 + " where id_con='" + id_con + "' and userA=" + id_user);

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String createGroup(int id_user, String name) {
        Statement stmt = null;

        String newConversation = generateNewConversation(name);

        try {
            stmt = conn.createStatement();

            stmt.execute("insert into " + GROUP + " (id_user, id_con, master) values (" + id_user + ", '" + newConversation + "', true)");

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        return newConversation;
    }

    public static boolean checkMaster(int id_sender, String id_con) {
        Statement stmt = null;
        ResultSet result = null;
        boolean isMaster = false;

        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery("select * from " + GROUP + " where id_user=" + id_sender + " and id_con='" + id_con + "'");

            if (result.next()) {
                isMaster = result.getBoolean("MASTER");
            }

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        return isMaster;
    }

    public static boolean renameGroup(int id_sender, String id_con, String name) {
        Statement stmt = null;
        boolean flag = false;

        try {
            stmt = conn.createStatement();

            if (checkMaster(id_sender, id_con)) {
                stmt.execute("update " + CON + " set name='" + name + "' where id_con='" + id_con + "'");
                flag = true;
            }

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        return flag;
    }
    
    public static boolean addUser(int id_sender, String id_con, int id_receiver) {
        Statement stmt = null;
        boolean flag = false;

        try {
            stmt = conn.createStatement();

            if (checkMaster(id_sender, id_con)) {
                stmt.execute("insert into " + GROUP + " (id_user, id_con, master) values (" + id_receiver + ", '" + id_con + "', false)");
                flag = true;
            }

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        return flag;
    }

    static boolean kickUser(int id_sender, String id_con, int id_receiver) {
        Statement stmt = null;
        boolean flag = false;

        try {
            stmt = conn.createStatement();

            if (checkMaster(id_sender, id_con)) {
                stmt.execute("delete from " + GROUP + " where id_user=" + id_receiver + " and id_con='" + id_con + "'");
                flag = true;
            }

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        return flag;
    }

    static boolean leaveConversation(int id_sender, String id_con) {
        Statement stmt = null;
        boolean flag = false;

        try {
            stmt = conn.createStatement();

            if (!checkMaster(id_sender, id_con)) {
                stmt.execute("delete from " + GROUP + " where id_user=" + id_sender + " and id_con='" + id_con + "'");
                flag = true;
            }

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        return flag;
    }

    static boolean passMaster(int id_sender, String id_con, int id_receiver) {
        Statement stmt = null;
        boolean flag = false;

        try {
            stmt = conn.createStatement();

            if (checkMaster(id_sender, id_con)) {
                stmt.execute("update " + GROUP + " set master=false where id_user=" + id_sender + " and id_con='" + id_con + "'");
                stmt.execute("update " + GROUP + " set master=true where id_user=" + id_receiver + " and id_con='" + id_con + "'");
                flag = true;
            }

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        return flag;
    }

    public static boolean deleteGroup(int id_sender, String id_con) {
        Statement stmt = null;
        boolean flag = false;

        try {
            stmt = conn.createStatement();

            if (checkMaster(id_sender, id_con)) {
                
                stmt.execute("delete from " + GROUP + " where id_con='" + id_con + "'");
                stmt.execute("delete from " + CON + " where id_con='" + id_con + "'");
                flag = true;
            }

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        return flag;
    }
    
    public static ArrayList<Integer> getGroupMembers(String id_con) {
        Statement stmt = null;
        ResultSet result = null;
        ArrayList<Integer> list = new ArrayList();

        try {
            stmt = conn.createStatement();

            result = stmt.executeQuery("select * from " + GROUP + " where id_con='" + id_con + "'");
            
            while (result.next()) {
                int id_user = result.getInt("ID_USER");
                list.add(id_user);
            }

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;

    }

    static ArrayList<GroupConversation> getGroupConversation(int id_user) {
        Statement stmt = null;
        ResultSet result = null;
        
        Statement stmt_sub = null;
        ResultSet result_sub = null;

        ArrayList<GroupConversation> list_grpCon = new ArrayList();
        
        try {
            stmt = conn.createStatement();
            stmt_sub = conn.createStatement();
            
            result = stmt.executeQuery("select * from " + GROUP + " where id_user=" + id_user);
            
            while (result.next()) {
                GroupConversation con = new GroupConversation();
                
                int message_not_seen = result.getInt("MESSAGE_NOT_SEEN");
                con.setMessage_not_seen(message_not_seen);
                
                String id_con = result.getString("ID_CON");
                con.setId_con(id_con);
                
                ArrayList<ChatUser> list_user = getUserFromConversation(id_con);
                con.setList_user(list_user);
                
                result_sub = stmt_sub.executeQuery("select * from " + CON + " where id_con='" + id_con + "'");
                
                result_sub.next();
                ArrayList<ChatMessage> conversation = parseConversation(result_sub.getString("MESSAGE"));
                con.setConversation(conversation);
                
                list_grpCon.add(con);
                
            }

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list_grpCon;
    }
    
    static ArrayList<ChatUser> getUserFromConversation(String id_con) {
        Statement stmt = null;
        ResultSet result = null;
        ArrayList<ChatUser> list_user = new ArrayList();

        try {
            stmt = conn.createStatement();

            result = stmt.executeQuery("select * from " + GROUP + " where id_con='" + id_con + "'");
            
            while (result.next()) {
                int id_user = result.getInt("ID_USER");
                ChatUser user = getChatUser(id_user);
                list_user.add(user);
            }

            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChatDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list_user;
    }

}
