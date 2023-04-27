package com.la.xuecheng.ucenter.feignClient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author LA
 * @createDate 2023-04-25-14:17
 * @description
 */
@Slf4j
@Component
public class CheckCodeClientFallbackFactory implements FallbackFactory<CheckCodeClient> {
    @Override
    public CheckCodeClient create(Throwable throwable) {
        return new CheckCodeClient() {
            @Override
            public Boolean verify(String key, String code) {
                log.error("checkcodeVerify走了降级方法...");
                return null;
            }
        };
    }
}
