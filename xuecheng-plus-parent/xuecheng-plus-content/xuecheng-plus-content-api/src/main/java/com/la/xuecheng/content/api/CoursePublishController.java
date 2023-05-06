package com.la.xuecheng.content.api;

import com.alibaba.fastjson.JSON;
import com.la.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.la.xuecheng.content.model.dto.CoursePreviewDto;
import com.la.xuecheng.content.model.dto.TeachplanDto;
import com.la.xuecheng.content.model.po.CoursePublish;
import com.la.xuecheng.content.service.CoursePublishService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author LA
 * @createDate 2023-04-19-14:36
 * @description
 */
@Controller
@Slf4j
public class CoursePublishController {

    @Resource
    CoursePublishService coursePublishService;

    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") Long courseId){
        ModelAndView modelAndView = new ModelAndView();

        //从数据库查询信息
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);
        //指定模型
        modelAndView.addObject("model", coursePreviewInfo);
        //指定模板
        modelAndView.setViewName("course_template"); //根据视图名称拼接，配置文件中的后缀 "ftl"

        return modelAndView;
    }

    @ApiOperation("课程提交")
    @ResponseBody
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") Long courseId) {
        Long companyId = 1232141425L;
        coursePublishService.commitAudit(companyId, courseId);
    }

    @ApiOperation("课程发布")
    @ResponseBody
    @PostMapping ("/coursepublish/{courseId}")
    public void coursepublish(@PathVariable("courseId") Long courseId){
        Long companyId = 1232141425L;
        coursePublishService.publish(companyId,courseId);
    }

    @ApiOperation("查询课程发布信息")
    @ResponseBody
    @GetMapping("/r/coursepublish/{courseId}")
    public CoursePublish getCoursepublish(@PathVariable("courseId") Long courseId) {
        //查询课程发布信息
        CoursePublish coursePublish = coursePublishService.getCoursePublish(courseId);
        return coursePublish;
    }

    @ApiOperation("获取课程发布信息")
    @ResponseBody
    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getCoursePublish(@PathVariable("courseId") Long courseId) {
        //封装数据
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();

        //查询课程发布表
        //CoursePublish coursePublish = coursePublishService.getCoursePublish(courseId);
        CoursePublish coursePublish = coursePublishService.getCoursePublishCache(courseId);
        if(coursePublish == null){
            return coursePreviewDto;
        }
        //开始向coursePreviewDto填充数据
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(coursePublish,courseBaseInfoDto);
        //课程计划信息
        String teachplanJson = coursePublish.getTeachplan();
        //转成List<TeachplanDto>
        List<TeachplanDto> teachplanDtos = JSON.parseArray(teachplanJson, TeachplanDto.class);
        coursePreviewDto.setCourseBase(courseBaseInfoDto);
        coursePreviewDto.setTeachplans(teachplanDtos);
        return coursePreviewDto;

    }



}
