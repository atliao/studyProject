package com.la.xuecheng.content;

import com.la.xuecheng.content.config.MultipartSupportConfig;
import com.la.xuecheng.content.feignClient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * @author LA
 * @createDate 2023-04-21-13:56
 * @description 测试远程调用
 */
@SpringBootTest
public class FeignTest {

    @Resource
    MediaServiceClient mediaServiceClient;

    @Test
    public void test() throws IOException {

        //将file类型转为imultipartFile
        File file = new File("D:\\minio\\test\\html\\7.html");
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        String upload = mediaServiceClient.upload(multipartFile, "course/7.html");
        if(upload == null){
            System.out.println("走了降级...");
        }

    }
}
