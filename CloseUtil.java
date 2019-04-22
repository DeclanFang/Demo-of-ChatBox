package com.declan.net.chat;

import java.io.Closeable;
import java.io.IOException;

/**
 * Approach to shut down the stream
 * @author Declan
 */

public class CloseUtil {
    public static void closeAll(Closeable... io) {
        for(Closeable temp:io) {
            if(null != temp) {
                try {
                    temp.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }
    }
}
