package com.la.xuecheng.content;

import org.junit.Test;

import java.io.*;
import java.net.Socket;

/**
 * @author LA
 * @createDate 2023-04-20-16:19
 * @description
 */
public class FileTest {

    @Test
    public void test() throws IOException {
        File file = new File("//192.168.101.130/usr/local/nginx/html/index.html");
        FileInputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int len;
        while((len = inputStream.read(buffer)) != -1){
            String str = new String(buffer, 0, len);
            System.out.print(str);
        }
    }
}
