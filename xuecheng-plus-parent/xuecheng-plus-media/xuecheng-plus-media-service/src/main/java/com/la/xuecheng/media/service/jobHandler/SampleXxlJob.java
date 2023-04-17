package com.la.xuecheng.media.service.jobHandler;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author LA
 * @createDate 2023-04-17-11:46
 * @description
 */
@Component
public class SampleXxlJob {
    private static Logger logger = LoggerFactory.getLogger(SampleXxlJob.class);


    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("demoJobHandler")
    public void demoJobHandler() throws Exception {
        System.out.println("处理视频.......");


    }


    @XxlJob("demoJobHandler2")
    public void demoJobHandler2() throws Exception {
        System.out.println("处理文档.......");

        // default success
    }
}