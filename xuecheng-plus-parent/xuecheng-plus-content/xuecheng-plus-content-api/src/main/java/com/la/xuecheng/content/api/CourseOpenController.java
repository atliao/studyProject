package com.la.xuecheng.content.api;

import com.la.xuecheng.content.model.dto.CoursePreviewDto;
import com.la.xuecheng.content.service.CourseBaseInfoService;
import com.la.xuecheng.content.service.CoursePublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2023/2/21 10:33
 */
@RestController
@RequestMapping("/open")
public class CourseOpenController {

    @Resource
    private CoursePublishService coursePublishService;

    //根据课程id查询课程信息
    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getPreviewInfo(@PathVariable("courseId") Long courseId) {
        //获取课程预览信息
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);
        return coursePreviewInfo;
    }



}
