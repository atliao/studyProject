package com.la.xuecheng.content.service.jobHandler;

import com.la.xuecheng.base.exception.XuechengPlusException;
import com.la.xuecheng.content.feignClient.CourseIndex;
import com.la.xuecheng.content.feignClient.SearchServiceClient;
import com.la.xuecheng.content.mapper.CoursePublishMapper;
import com.la.xuecheng.content.model.dto.CoursePreviewDto;
import com.la.xuecheng.content.model.po.CoursePublish;
import com.la.xuecheng.content.service.CoursePublishService;
import com.la.xuecheng.messagesdk.model.po.MqMessage;
import com.la.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.la.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author LA
 * @createDate 2023-04-20-14:58
 * @description 课程发布任务类
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    @Resource
    CoursePublishService coursePublishService;

    @Resource
    SearchServiceClient searchServiceClient;

    @Resource
    CoursePublishMapper coursePublishMapper;

    //任务调度入口
    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();//执行器的序号，从0开始
        int shardTotal = XxlJobHelper.getShardTotal();//执行器总数
        //调用抽象类的方法执行任务
        process(shardIndex,shardTotal, "course_publish",2,60);
    }


    //执行课程发布任务
    @Override
    public boolean execute(MqMessage mqMessage) {

        //从mqMessage表中拿到courseId
        Long courseId = Long.parseLong(mqMessage.getBusinessKey1());


        //将静态页面上传到minio
        generateCourseHtml(mqMessage, courseId);

        //向elasticsearch写索引数据
        saveCourseIndex(mqMessage,courseId);

        //向redis写缓存

        //返回true表示任务完成
        return true;
    }

    //生成课程静态化页面并上传至文件系统
    private void generateCourseHtml(MqMessage mqMessage,long courseId){
        //消息id
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();

        //做任务幂等性处理
        //查询数据库取出该阶段执行状态
        int stageOne = mqMessageService.getStageOne(taskId);
        if(stageOne>0){
            log.debug("课程静态化任务完成，无需处理...");
            return ;
        }

        //开始进行课程静态化 生成html页面
        File file = coursePublishService.generateCourseHtml(courseId);
        if(file == null){
            XuechengPlusException.cast("生成的静态页面为空");
        }
        // 将html上传到minio
        coursePublishService.uploadCourseHtml(courseId,file);


        //..任务处理完成写任务状态为完成
        mqMessageService.completedStageOne(taskId);


    }

    //保存课程索引信息 第二个阶段任务
    private void saveCourseIndex(MqMessage mqMessage,long courseId){
        //任务id
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        //取出第二个阶段状态
        int stageTwo = mqMessageService.getStageTwo(taskId);

        //任务幂等性处理
        if(stageTwo>0){
            log.debug("课程索引信息已写入，无需执行...");
            return;
        }
        //从课程发布表查询信息
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish, courseIndex);
        //调用搜索服务添加索引
        Boolean res = searchServiceClient.add(courseIndex);
        if(!res){
            XuechengPlusException.cast("远程调用课程索引添加失败");
        }

        //完成本阶段的任务
        mqMessageService.completedStageTwo(taskId);


    }
}
