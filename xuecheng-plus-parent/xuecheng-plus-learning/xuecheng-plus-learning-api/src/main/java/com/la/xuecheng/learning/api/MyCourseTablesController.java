package com.la.xuecheng.learning.api;

import com.la.xuecheng.base.exception.XuechengPlusException;
import com.la.xuecheng.base.model.PageResult;
import com.la.xuecheng.learning.model.dto.MyCourseTableParams;
import com.la.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.la.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.la.xuecheng.learning.model.po.XcCourseTables;
import com.la.xuecheng.learning.service.MyCourseTableService;
import com.la.xuecheng.learning.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Mr.M
 * @version 1.0
 * @description 我的课程表接口
 * @date 2022/10/25 9:40
 */

@Api(value = "我的课程表接口", tags = "我的课程表接口")
@Slf4j
@RestController
public class MyCourseTablesController {

    @Resource
    MyCourseTableService myCourseTableService;

    @ApiOperation("添加选课")
    @PostMapping("/choosecourse/{courseId}")
    public XcChooseCourseDto addChooseCourse(@PathVariable("courseId") Long courseId) {
        //当前登陆的用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if(user == null){
            XuechengPlusException.cast("请登录");
        }
        //用户id
        String userId = user.getId();
        //添加选课
        XcChooseCourseDto xcChooseCourseDto = myCourseTableService.addChooseCourse(userId, courseId);
        return xcChooseCourseDto;
    }

    @ApiOperation("查询学习资格")
    @PostMapping("/choosecourse/learnstatus/{courseId}")
    public XcCourseTablesDto getLearnstatus(@PathVariable("courseId") Long courseId) {
        //当前登陆的用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if(user == null){
            XuechengPlusException.cast("请登录");
        }
        //用户id
        String userId = user.getId();
        //查询学习资格
        XcCourseTablesDto xcCourseTablesDto = myCourseTableService.getLearningStatus(userId, courseId);
        return xcCourseTablesDto;

    }

    @ApiOperation("我的课程表")
    @GetMapping("/mycoursetable")
    public PageResult<XcCourseTables> mycoursetable(MyCourseTableParams params) {
        return null;
    }

}
