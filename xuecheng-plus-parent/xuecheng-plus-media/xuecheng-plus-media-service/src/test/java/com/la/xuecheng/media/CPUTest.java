package com.la.xuecheng.media;

import org.junit.Test;

/**
 * @author LA
 * @createDate 2023-04-18-12:16
 * @description
 */
public class CPUTest {

    @Test
    public void test(){
        int num = Runtime.getRuntime().availableProcessors();
        System.out.println(num);
    }
}
