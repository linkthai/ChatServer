/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author Link
 */
public class ChatServer {

    private static final int PORT_NUMBER = 2833;

    private ServerSocket serverSocket;
    private ServerListener listener;
    private boolean isRunning = false;

    String log = "";
    JTextArea txt_log = null;

    public boolean startServer() {

        if (!ChatDatabase.StartConnection()) {
            return false;
        }

        try {
            serverSocket = new ServerSocket(PORT_NUMBER);
        } catch (IOException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {

            isRunning = true;

            listener = new ServerListener();
            Thread listenerThread = new Thread(listener);
            listenerThread.start();
        }

        writeLog("STARTED");
        return true;

    }

    public void stopServer() {

        try {
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            isRunning = false;
        }

    }

    public class ServerListener implements Runnable {

        Socket socket;
        ClientConnection client;

        Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread th, Throwable ex) {
                return;
            }
        };

        @Override
        public void run() {
            while (true) {

                if (serverSocket.isClosed()) {
                    break;
                }

                try {
                    socket = serverSocket.accept();
                    client = new ClientConnection(socket);
                    client.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                        public void uncaughtException(Thread th, Throwable ex) {
                            System.out.println("WriteLog");
                            return;
                        }
                    });
                    client.start();
                    writeLog("NEW CLIENT DETECTED!");
                } catch (IOException ex) {
                    Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                }

            }
        }

    }

    public boolean getRunning() {
        return isRunning;
    }

    public void setLog(JTextArea jtxt) {
        txt_log = jtxt;
    }

    public void writeLog(String input) {
        if (txt_log == null) {
            return;
        }

        log += input + "\n";
        txt_log.setText(log);
    }
}
