package com.declan.net.chat;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Receive Thread
 * @author Declan
 */

public class Receive implements Runnable{
    //Input Stream
    private DataInputStream dis;
    //Flag of Thread
    private boolean isRunning = true;

    public Receive() {
    }

    public Receive(Socket client) {
        try {
            dis = new DataInputStream(client.getInputStream());
        } catch (IOException e) {
            //e.printStackTrace();
            isRunning = false;
            CloseUtil.closeAll(dis);
        }
    }

    //Receive the data
    public String receive() {
        String msg = "";
        try {
            msg = dis.readUTF();
        } catch (IOException e) {
            //e.printStackTrace();
            isRunning = false;
            CloseUtil.closeAll(dis);
        }
        return msg;
    }

    @Override
    public void run() {
        //Thread body
        while(isRunning) {
            System.out.println(receive());
        }
    }

}
