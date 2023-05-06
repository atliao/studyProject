package com.la.xuecheng.content.service.Impl;

import com.alibaba.fastjson.JSON;
import com.la.xuecheng.base.exception.CommonError;
import com.la.xuecheng.base.exception.XuechengPlusException;
import com.la.xuecheng.base.utils.JsonUtil;
import com.la.xuecheng.content.config.MultipartSupportConfig;
import com.la.xuecheng.content.feignClient.MediaServiceClient;
import com.la.xuecheng.content.mapper.CourseBaseMapper;
import com.la.xuecheng.content.mapper.CourseMarketMapper;
import com.la.xuecheng.content.mapper.CoursePublishMapper;
import com.la.xuecheng.content.mapper.CoursePublishPreMapper;
import com.la.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.la.xuecheng.content.model.dto.CoursePreviewDto;
import com.la.xuecheng.content.model.dto.TeachplanDto;
import com.la.xuecheng.content.model.po.CourseBase;
import com.la.xuecheng.content.model.po.CourseMarket;
import com.la.xuecheng.content.model.po.CoursePublish;
import com.la.xuecheng.content.model.po.CoursePublishPre;
import com.la.xuecheng.content.service.CourseBaseInfoService;
import com.la.xuecheng.content.service.CoursePublishService;
import com.la.xuecheng.content.service.TeachPlanService;
import com.la.xuecheng.messagesdk.model.po.MqMessage;
import com.la.xuecheng.messagesdk.service.MqMessageService;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.M
 * @version 1.0
 * @description 课程发布相关接口实现
 * @date 2023/2/21 10:04
 */
@Slf4j
@Service
public class CoursePublishServiceImpl implements CoursePublishService {

    @Resource
    CourseBaseInfoService courseBaseInfoService;

    @Resource
    TeachPlanService teachplanService;

    @Resource
    CourseBaseMapper courseBaseMapper;

    @Resource
    CourseMarketMapper courseMarketMapper;

    @Resource
    CoursePublishPreMapper coursePublishPreMapper;

    @Resource
    CoursePublishMapper coursePublishMapper;

    @Resource
    MqMessageService mqMessageService;

    @Resource
    MediaServiceClient mediaServiceClient;

    @Resource
    RedisTemplate redisTemplate;



    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        //课程基本信息,营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        coursePreviewDto.setCourseBase(courseBaseInfo);
        //课程计划信息
        List<TeachplanDto> teachplanTree = teachplanService.getTeachPlanTree(courseId);
        coursePreviewDto.setTeachplans(teachplanTree);

        return coursePreviewDto;
    }

    @Transactional
    @Override
    public void commitAudit(Long companyId, Long courseId) {

        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        if (courseBaseInfo == null) {
            XuechengPlusException.cast("课程找不到");
        }
        //审核状态
        String auditStatus = courseBaseInfo.getAuditStatus();

        //如果课程的审核状态为已提交则不允许提交
        if(auditStatus.equals("202003")){
            XuechengPlusException.cast("课程已提交请等待审核");
        }
        //本机构只能提交本机构的课程
        //todo:本机构只能提交本机构的课程

        //课程的图片、计划信息没有填写也不允许提交
        String pic = courseBaseInfo.getPic();
        if(StringUtils.isEmpty(pic)){
            XuechengPlusException.cast("请求上传课程图片");
        }
        //查询课程计划
        //课程计划信息
        List<TeachplanDto> teachplanTree = teachplanService.getTeachPlanTree(courseId);
        if(teachplanTree == null || teachplanTree.size()==0){
            XuechengPlusException.cast("请编写课程计划");
        }

        //查询到课程基本信息、营销信息、计划等信息插入到课程预发布表
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        BeanUtils.copyProperties(courseBaseInfo,coursePublishPre);
        //设置机构id
        coursePublishPre.setCompanyId(companyId);
        //营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //转json
        String courseMarketJson = JSON.toJSONString(courseMarket);
        coursePublishPre.setMarket(courseMarketJson);
        //计划信息
        //转json
        String teachplanTreeJson = JSON.toJSONString(teachplanTree);
        coursePublishPre.setTeachplan(teachplanTreeJson);
        //状态为已提交
        coursePublishPre.setStatus("202003");
        //提交时间
        coursePublishPre.setCreateDate(LocalDateTime.now());
        //查询预发布表，如果有记录则更新，没有则插入
        CoursePublishPre coursePublishPreObj = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPreObj==null){
            //插入
            coursePublishPreMapper.insert(coursePublishPre);
        }else {
            //更新
            coursePublishPreMapper.updateById(coursePublishPre);
        }

        //更新课程基本信息表的审核状态为已提交
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setAuditStatus("202003");//审核状态为已提交

        courseBaseMapper.updateById(courseBase);
    }

    @Transactional
    @Override
    public void publish(Long companyId, Long courseId) {

        //查询预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPre == null){
            XuechengPlusException.cast("课程没有审核记录，无法发布");
        }
        //状态
        String status = coursePublishPre.getStatus();
        //课程如果没有审核通过不允许发布
        if(!status.equals("202004")){
            XuechengPlusException.cast("课程没有审核通过不允许发布");
        }

        //向课程发布表写入数据
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre,coursePublish);
        //先查询课程发布，如果有则更新，没有再添加
        CoursePublish coursePublishObj = coursePublishMapper.selectById(courseId);
        if(coursePublishObj == null){
            coursePublishMapper.insert(coursePublish);
        }else{
            coursePublishMapper.updateById(coursePublish);
        }

        //向消息表写入数据
//        mqMessageService.addMessage("course_publish",String.valueOf(courseId),null,null);
        saveCoursePublishMessage(courseId);

        //将预发布表数据删除
        coursePublishPreMapper.deleteById(courseId);

        /*//将courseBase表发布状态更新 在同步完成后再更改
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setStatus("203002");
        courseBaseMapper.updateById(courseBase);*/

    }

    /**
     * @description 保存消息表记录
     * @param courseId  课程id
     * @return void
     * @author Mr.M
     * @date 2022/9/20 16:32
     */
    private void saveCoursePublishMessage(Long courseId) {
        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if (mqMessage == null) {
            XuechengPlusException.cast(CommonError.UNKOWN_ERROR);
        }

    }

    @Override
    public File generateCourseHtml(Long courseId) {

        Configuration configuration = new Configuration(Configuration.getVersion());
        //最终的静态文件
        File htmlFile = null;
        try {
            //拿到classpath路径
            String classpath = this.getClass().getResource("/").getPath();
            //指定模板的目录
            //configuration.setDirectoryForTemplateLoading(new File(classpath+"/templates/"));
            //更改为如下方式，否则打包部署到服务器上之后，无法找到templates目录
            configuration.setTemplateLoader(new ClassTemplateLoader(this.getClass().getClassLoader(),"/templates"));
            //指定编码
            configuration.setDefaultEncoding("utf-8");

            //得到模板
            Template template = configuration.getTemplate("course_template.ftl");
            //准备数据
            CoursePreviewDto coursePreviewInfo = this.getCoursePreviewInfo(courseId);
            HashMap<String, Object> map = new HashMap<>();
            map.put("model",coursePreviewInfo);

            //Template template 模板, Object model 数据
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            //输入流
            InputStream inputStream = IOUtils.toInputStream(html, "utf-8");
            htmlFile = File.createTempFile("coursepublish",".html");
            //输出文件
            FileOutputStream outputStream = new FileOutputStream(htmlFile);
            //使用流将html写入文件
            IOUtils.copy(inputStream,outputStream);
        }catch (Exception ex){
            log.error("页面静态化出现问题,课程id:{}",courseId,ex);
            ex.printStackTrace();
        }

        return htmlFile;
    }

    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        try {
            //将file转成MultipartFile
            MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
            //远程调用得到返回值
            String upload = mediaServiceClient.upload(multipartFile, "course/"+courseId+".html");
            if(upload==null){
                log.debug("远程调用走降级逻辑得到上传的结果为null,课程id:{}",courseId);
                XuechengPlusException.cast("上传静态文件过程中存在异常");
            }
        }catch (Exception ex){
            ex.printStackTrace();
            XuechengPlusException.cast("上传静态文件过程中存在异常");
        }

    }


    /**
     * 根据课程 id查询课程发布信息
     * @param courseId
     * @return
     */
    @Override
    public CoursePublish getCoursePublish(Long courseId){
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        return coursePublish ;
    }

    @Override
    public CoursePublish getCoursePublishCache(Long courseId) {
        //synchronized (this){
            Object jsonObj = redisTemplate.opsForValue().get("courseId:"+courseId);
            if(jsonObj != null){
                //System.out.println("**************redis****************");
                //直接返回数据
                String str = jsonObj.toString();
                //防止缓存穿透
                if("null".equals(str)){
                    return null;
                }
                CoursePublish coursePublish = JSON.parseObject(str, CoursePublish.class);
                return coursePublish;
            }
        //分布式锁1：调用redis set nx方法，拿到锁
        //Boolean lock = redisTemplate.opsForValue().setIfAbsent("courselock:" + courseId, "lock");
        //if(lock){
        synchronized (this) {

                //使用同步锁锁住该部分的话，需要在内部再次查询一下redis缓存
                // 防止第一个查询数据库还没有把数据放入缓存，后续的请求已经在排队等待锁了

                //再次查询缓存
                jsonObj = redisTemplate.opsForValue().get("courseId:"+courseId);
                if(jsonObj != null){
                    //System.out.println("**************redis****************");
                    //返回数据
                    String str = jsonObj.toString();
                    //防止缓存穿透
                    if("null".equals(str)){
                        return null;
                    }
                    CoursePublish coursePublish = JSON.parseObject(str, CoursePublish.class);
                    return coursePublish;
                }

                //从数据库查询
                CoursePublish coursePublish = getCoursePublish(courseId);
                //if(coursePublish != null){
                System.out.println("**************mysql****************");
                //存入redis
                String jsonString = JSON.toJSONString(coursePublish);
                redisTemplate.opsForValue().set("courseId:" + courseId, jsonString, 300, TimeUnit.SECONDS);
                //}
                return coursePublish;
            }
        //return null;
        //}
    }
}
