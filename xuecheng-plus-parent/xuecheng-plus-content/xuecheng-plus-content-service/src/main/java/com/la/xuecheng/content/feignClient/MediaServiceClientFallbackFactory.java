package com.la.xuecheng.content.feignClient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author LA
 * @createDate 2023-04-21-14:45
 * @description 熔断降级
 */
@Component
@Slf4j
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {

    //可以拿到熔断异常信息
    @Override
    public MediaServiceClient create(Throwable throwable) {

        return new MediaServiceClient() {
            //上传服务熔断走该方法
            @Override
            public String upload(MultipartFile filedata, String objectName) throws IOException {
                log.debug("上传服务发生熔断, {}", throwable.toString(), throwable);
                return null;
            }
        };

    }
}
