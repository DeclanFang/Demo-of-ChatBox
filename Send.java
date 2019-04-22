package com.declan.net.chat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Sending data in thread
 * @author Declan
 */

public class Send implements Runnable{
    //Console input
    private BufferedReader console;
    //Data out stream
    private DataOutputStream dos;
    //A flag for exception
    private boolean isRunning = true;
    //Name part
    private String name;

    public Send() {
        console = new BufferedReader(new InputStreamReader(System.in));
    }

    public Send(Socket client, String name) {
        this();
        try {
            dos = new DataOutputStream(client.getOutputStream());
            this.name = name;
            send(this.name);
        } catch (IOException e) {
            //e.printStackTrace();
            isRunning = false;
            CloseUtil.closeAll(dos,console);
        }
    }

    //Get msg from console
    private String getMsgFromConsole() {
        try {
            return console.readLine();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return "";
    }

    //Receive msg from console
    //Send the msg
    public void send(String msg) {
        if(null != msg && !msg.equals("")) {
            try {
                dos.writeUTF(msg);
                dos.flush();   //force to flush
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }
    @Override
    public void run() {
        //Thread body
        while(isRunning) {
            send(getMsgFromConsole());
        }
    }
}
