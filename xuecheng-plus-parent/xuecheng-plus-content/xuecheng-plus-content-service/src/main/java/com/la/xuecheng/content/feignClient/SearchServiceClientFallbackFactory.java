package com.la.xuecheng.content.feignClient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author LA
 * @createDate 2023-04-23-17:37
 * @description
 */
@Component
@Slf4j
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {

    @Override
    public SearchServiceClient create(Throwable throwable) {
        return new SearchServiceClient() {
            @Override
            public Boolean add(CourseIndex courseIndex) {
                log.error("添加课程索引发生熔断，索引信息:{},熔断异常:{}", courseIndex, throwable.toString(), throwable);
                //走降级，返回false
                return false;
            }
        };
    }
}
