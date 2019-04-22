package com.declan.net.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * A demo frame of ChatBox's server
 * @author Declan
 */

public class Server {
    private List<MyChannel> all = new ArrayList<MyChannel>();

    public static void main(String[] args) throws IOException {
       new Server().start();
    }

    public void start() throws IOException {
        ServerSocket server = new ServerSocket(9999);
        while(true) {
            Socket client = server.accept();
            MyChannel channel = new MyChannel(client);
            all.add(channel);
            new Thread(channel).start();   //one thread
        }
    }

    /**
     * One Thread for each client
     */

    private class MyChannel implements Runnable {
        private DataInputStream dis;
        private DataOutputStream dos;
        private boolean isRunning = true;
        private String name;
        public MyChannel(Socket client) {
            try {
                dis = new DataInputStream(client.getInputStream());
                dos = new DataOutputStream(client.getOutputStream());
                this.name = dis.readUTF();
                send("Welcome to the ChatBox, " + this.name + "!");
                sendOthers(this.name+" just entered the ChatBox! ", true);
            } catch (IOException e) {
                //e.printStackTrace();
                CloseUtil.closeAll(dis,dos);
                isRunning = false;
            }
        }

        /**
         * Read the data
         * @return
         */
        private String receive() {
            String msg = "";
            try {
                msg = dis.readUTF();
            } catch (IOException e) {
                //e.printStackTrace();
                CloseUtil.closeAll(dis);
                isRunning = false;
                all.remove(this);   //remove the client self
            }
            return msg;
        }

        /**
         * Send the received data
         */
        private void send(String msg) {
            if(null == msg || msg.equals("")) {
                return ;
            }
            try {
                dos.writeUTF(msg);
                dos.flush();
            } catch (IOException e) {
                //e.printStackTrace();
                CloseUtil.closeAll(dos);
                isRunning = false;
                all.remove(this);   //remove the client self
            }

        }

        /**
         * Send to other clients
         */
        private void sendOthers(String msg, boolean sys) {
            //Check if it's a private conversation
            if(msg.startsWith("@") && msg.indexOf(":") > - 1) {   //if it's a private conversation
                //Find the target client's name
                String name = msg.substring(1,msg.indexOf(":"));
                String content = msg.substring(msg.indexOf(":") + 1);
                for(MyChannel other : all) {
                    if(other.name.equals(name)) {
                        other.send(this.name + " said something to you privately: " + content);
                    }
                }
            }
            else {
                for(MyChannel other : all) {
                    if(other == this) {
                        continue;
                    }
                    if(sys) {   //If it's a system notification
                        other.send("Notification: " + msg);
                    }
                    else {
                        //send to other clients with name tag
                        other.send(this.name + ": " + msg);
                    }
                }
            }
        }

        @Override
        public void run() {
            while(isRunning) {
                sendOthers(receive(),false);
            }
        }
    }
}
