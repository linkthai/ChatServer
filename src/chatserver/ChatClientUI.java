package chatserver;

import chatpackage.*;

import chatpackage.PackageLogin;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Link
 */
public class ChatClientUI extends javax.swing.JFrame {

    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    private ObjectOutputStream objectOutput;
    private ObjectInputStream objectInput;

    ArrayList<ChatUser> friends;
    ArrayList<ChatUser> pendingFriends;

    String text = "";
    int client_id;
    String client_username;
    boolean isLoggedIn = false;
    
    String id_con;

    /**
     * Creates new form ChatClientUI
     */
    public ChatClientUI(boolean pos) {
        initComponents();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        if (pos) {
            this.setLocation(0, 500);
        }

        setTitle("Client");

        initClient();

    }

    public void initClient() {

        try {
            clientSocket = new Socket("localhost", 2833);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            objectInput = new ObjectInputStream(clientSocket.getInputStream());
            objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ChatClientUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        SocketListener listener = new SocketListener();
        Thread thread = new Thread(listener);
        thread.start();
    }

    public class SocketListener implements Runnable {

        @Override
        public void run() {
            while (true) {
                ChatPackage inputPackage = null;

                try {
                    inputPackage = (ChatPackage) objectInput.readObject();
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
                }

                analyseInput(inputPackage);
            }
        }
    }

    public void analyseInput(ChatPackage inputPackage) {
        switch (inputPackage.getType()) {
            case "LOGIN":
                PackageLogin login = (PackageLogin) inputPackage;
                if (login.isConfirm()) {
                    text += "YAY" + "\n";
                    isLoggedIn = true;
                    client_id = login.getId();
                    client_username = login.getUsername();
                    
                    cmbx_status.setSelectedItem(login.getUser().getStatus());

                    PackageFriendList friendList = new PackageFriendList();
                    sendObject(friendList);
                } else {
                    text += "NOPE" + "\n";
                }
                log.setText(text);
                break;
            case "REGISTER":
                PackageRegister register = (PackageRegister) inputPackage;
                if (register.isAvailable()) {
                    text += "AVAILABLE" + "\n";
                    if (register.isSuccessful()) {
                        text += "REGISTER SUCCESSFUL" + "\n";
                    } else {

                        text += "REGISTER FAILED" + "\n";
                    }

                } else {
                    text += "NOT_AVAILABLE" + "\n";
                }
                log.setText(text);
                break;
            case "SEARCH_USER":
                PackageSearchUser search_user = (PackageSearchUser) inputPackage;
                ArrayList<ChatUser> search_user_list = search_user.getList();
                for (int i = 0; i < search_user_list.size(); i++) {
                    text += search_user_list.get(i).getUsername() + "\n";
                }
                log.setText(text);
                break;
            case "FRIEND_REQUEST":
                PackageFriendRequest request = (PackageFriendRequest) inputPackage;

                if (request.isRequest()) {
                    Object[] options = {"Yes",
                        "No"};
                    int n = JOptionPane.showOptionDialog(this,
                            "Friend request from " + request.getUserSender(),
                            "Friend request",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1]);

                    if (n == 1) {
                        request.setAccept(false);
                    } else {
                        request.setAccept(true);
                    }
                    
                    request.setRequest(false);
                    sendObject(request);
                } else if (request.isAccept()) {
                    JOptionPane.showMessageDialog(null, "FRIEND REQUEST ACCEPTED");
                } //do something like add the friend to the list

                break;
            case "FRIEND_LIST":
                PackageFriendList friendList = (PackageFriendList) inputPackage;
                friends = friendList.getFriends();
                pendingFriends = friendList.getPendingFriends();
                for (ChatUser friend : friends) {
                    text += friend.getUsername() + " ";
                    if (friend.isOnline()) {
                        text += friend.getStatus();
                    } else {
                        text += "OFFLINE";
                    }
                    text += "\n";
                }

                for (ChatUser friend : pendingFriends) {
                    text += friend.getUsername() + " ";
                    text += "FRIEND REQUEST";
                    text += "\n";
                }

                log.setText(text);
                break;
            case "STATUS":
                PackageStatus status = (PackageStatus) inputPackage;
                
                if (status.getFriend_id() == client_id)
                {
                    JOptionPane.showMessageDialog(null, "FRIEND IS " + status.getStatus());
                }
                break;
            case "CONVERSATION":
                PackageConversation con = (PackageConversation) inputPackage;
                
                id_con = con.getId_con();
                ArrayList<ChatMessage> conversation = con.getConversation();
                for (int i = 0; i < conversation.size(); i++) {
                    text += conversation.get(i).getSender() + ":" + conversation.get(i).getContent() + "\n";
                }
                
                log.setText(text);
                break;
            case "MESSAGE":
                PackageMessage message = (PackageMessage) inputPackage;
                
                if (message.getReceiver() == client_id) {
                    text += message.getSender() + ":" + message.getMessage().getContent() + "\n";
                }
                
                log.setText(text);
                break;
            default:
                break;

        }
    }

    private void sendObject(Object obj) {
        try {
            objectOutput.writeObject(obj);
        } catch (IOException ex) {
            Logger.getLogger(ChatClientUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        txt_username = new javax.swing.JTextField();
        txt_password = new javax.swing.JTextField();
        txt_username1 = new javax.swing.JTextField();
        txt_password1 = new javax.swing.JTextField();
        txt_email = new javax.swing.JTextField();
        btn_register = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        log = new javax.swing.JTextArea();
        txt_search = new javax.swing.JTextField();
        btn_search = new javax.swing.JButton();
        btn_close = new javax.swing.JButton();
        txt_friend = new javax.swing.JTextField();
        btn_addfriend = new javax.swing.JButton();
        btn_getfriendlist = new javax.swing.JButton();
        cmbx_status = new javax.swing.JComboBox<>();
        text_message = new javax.swing.JTextField();
        btn_send = new javax.swing.JButton();
        text_friend_id = new javax.swing.JTextField();
        btn_get_con = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("login");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        txt_username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_usernameActionPerformed(evt);
            }
        });

        txt_password.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_passwordActionPerformed(evt);
            }
        });

        txt_username1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_username1ActionPerformed(evt);
            }
        });

        txt_password1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_password1ActionPerformed(evt);
            }
        });

        txt_email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_emailActionPerformed(evt);
            }
        });

        btn_register.setText("Register");
        btn_register.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_registerActionPerformed(evt);
            }
        });

        log.setColumns(20);
        log.setRows(5);
        jScrollPane1.setViewportView(log);

        txt_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_searchActionPerformed(evt);
            }
        });

        btn_search.setText("Search");
        btn_search.setToolTipText("");
        btn_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_searchActionPerformed(evt);
            }
        });

        btn_close.setText("Close");
        btn_close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_closeActionPerformed(evt);
            }
        });

        txt_friend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_friendActionPerformed(evt);
            }
        });

        btn_addfriend.setText("Add Friend");
        btn_addfriend.setToolTipText("");
        btn_addfriend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_addfriendActionPerformed(evt);
            }
        });

        btn_getfriendlist.setText("GetFriendList");
        btn_getfriendlist.setToolTipText("");
        btn_getfriendlist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_getfriendlistActionPerformed(evt);
            }
        });

        cmbx_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ONLINE", "BUSY", "AWAY", "HIDDEN" }));
        cmbx_status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbx_statusActionPerformed(evt);
            }
        });

        text_message.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_messageActionPerformed(evt);
            }
        });

        btn_send.setText("Send");
        btn_send.setToolTipText("");
        btn_send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_sendActionPerformed(evt);
            }
        });

        text_friend_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_friend_idActionPerformed(evt);
            }
        });

        btn_get_con.setText("Get Conversation");
        btn_get_con.setToolTipText("");
        btn_get_con.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_get_conActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_username, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_password, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(65, 65, 65)
                                .addComponent(jButton1)))
                        .addGap(41, 41, 41)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txt_username1, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 202, Short.MAX_VALUE)
                                .addComponent(btn_close, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txt_email, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(btn_getfriendlist))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txt_password1, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(txt_friend, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btn_addfriend)))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txt_search, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btn_search)
                                .addGap(47, 47, 47)
                                .addComponent(btn_register))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmbx_status, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(text_friend_id, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(btn_get_con))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addComponent(text_message, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btn_send, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                        .addGap(0, 46, Short.MAX_VALUE)))
                .addGap(22, 22, 22))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_username1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(btn_close)))
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_password1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_friend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_addfriend))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(txt_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_getfriendlist))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_register)
                    .addComponent(txt_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_search))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(cmbx_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(text_friend_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_get_con))
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_message, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_send, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(95, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        PackageLogin login = new PackageLogin();
        login.setUsername(txt_username.getText());
        login.setPassword(txt_password.getText());
        sendObject(login);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txt_usernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_usernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_usernameActionPerformed

    private void txt_passwordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_passwordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_passwordActionPerformed

    private void txt_username1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_username1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_username1ActionPerformed

    private void txt_password1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_password1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_password1ActionPerformed

    private void txt_emailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_emailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_emailActionPerformed

    private void btn_registerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_registerActionPerformed
        // TODO add your handling code here:
//        sendOutput("REGISTER");
//        sendOutput(txt_username1.getText());
//        sendOutput(txt_password1.getText());
//        sendOutput(txt_email.getText());

        PackageRegister register = new PackageRegister();
        register.setUsername(txt_username1.getText());
        register.setPassword(txt_password1.getText());
        register.setEmail(txt_email.getText());
        sendObject(register);
    }//GEN-LAST:event_btn_registerActionPerformed

    private void txt_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_searchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_searchActionPerformed

    private void btn_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_searchActionPerformed
        // TODO add your handling code here:
        PackageSearchUser search = new PackageSearchUser(txt_search.getText());
        sendObject(search);
    }//GEN-LAST:event_btn_searchActionPerformed

    private void btn_closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_closeActionPerformed
        try {
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ChatClientUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btn_closeActionPerformed

    private void txt_friendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_friendActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_friendActionPerformed

    private void btn_addfriendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_addfriendActionPerformed
        // TODO add your handling code here:
        PackageFriendRequest request = new PackageFriendRequest(client_id, Integer.parseInt(txt_friend.getText()));
        request.setRequest(true);
        sendObject(request);
    }//GEN-LAST:event_btn_addfriendActionPerformed

    private void btn_getfriendlistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_getfriendlistActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_getfriendlistActionPerformed

    private void cmbx_statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbx_statusActionPerformed
        // TODO add your handling code here:
        
        if (friends == null) 
            return;
        
        PackageStatus status = new PackageStatus(client_id, cmbx_status.getSelectedItem().toString());
        sendObject(status);
        
        //if the friend_id != 0 you sent it to friends
        for (ChatUser friend : friends) {
            status = new PackageStatus(client_id, cmbx_status.getSelectedItem().toString());
            status.setFriend_id(friend.getId());
            sendObject(status);
        }
    }//GEN-LAST:event_cmbx_statusActionPerformed

    private void text_messageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_messageActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_messageActionPerformed

    private void btn_sendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_sendActionPerformed
        // TODO add your handling code here:
        PackageMessage message = new PackageMessage(client_id, Integer.parseInt(text_friend_id.getText()), text_message.getText());
        message.setId_con(id_con);
        sendObject(message);
    }//GEN-LAST:event_btn_sendActionPerformed

    private void text_friend_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_friend_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_friend_idActionPerformed

    private void btn_get_conActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_get_conActionPerformed
        // TODO add your handling code here:
        PackageConversation conversation = new PackageConversation();
        conversation.setId_userA(client_id);
        conversation.setId_userB(Integer.parseInt(text_friend_id.getText()));
        sendObject(conversation);
    }//GEN-LAST:event_btn_get_conActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_addfriend;
    private javax.swing.JButton btn_close;
    private javax.swing.JButton btn_get_con;
    private javax.swing.JButton btn_getfriendlist;
    private javax.swing.JButton btn_register;
    private javax.swing.JButton btn_search;
    private javax.swing.JButton btn_send;
    private javax.swing.JComboBox<String> cmbx_status;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea log;
    private javax.swing.JTextField text_friend_id;
    private javax.swing.JTextField text_message;
    private javax.swing.JTextField txt_email;
    private javax.swing.JTextField txt_friend;
    private javax.swing.JTextField txt_password;
    private javax.swing.JTextField txt_password1;
    private javax.swing.JTextField txt_search;
    private javax.swing.JTextField txt_username;
    private javax.swing.JTextField txt_username1;
    // End of variables declaration//GEN-END:variables
}
