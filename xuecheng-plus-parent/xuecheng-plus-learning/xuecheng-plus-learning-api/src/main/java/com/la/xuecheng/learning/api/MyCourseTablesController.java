package com.la.xuecheng.learning.api;

import com.la.xuecheng.base.exception.XuechengPlusException;
import com.la.xuecheng.base.model.PageResult;
import com.la.xuecheng.learning.model.dto.MyCourseTableParams;
import com.la.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.la.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.la.xuecheng.learning.model.po.XcCourseTables;
import com.la.xuecheng.learning.service.MyCourseTablesService;
import com.la.xuecheng.learning.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    MyCourseTablesService myCourseTablesService;

    @ApiOperation("添加选课")
    @RequestMapping("/choosecourse/{courseId}")
    public XcChooseCourseDto addChooseCourse(@PathVariable("courseId") Long courseId) {
        //当前登陆的用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if(user == null){
            XuechengPlusException.cast("请登录");
        }
        //用户id
        String userId = user.getId();
        //添加选课
        XcChooseCourseDto xcChooseCourseDto = myCourseTablesService.addChooseCourse(userId, courseId);
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
        XcCourseTablesDto xcCourseTablesDto = myCourseTablesService.getLearningStatus(userId, courseId);
        return xcCourseTablesDto;

    }

    @ApiOperation("我的课程表")
    @GetMapping("/mycoursetable")
    public PageResult<XcCourseTables> mycoursetable(MyCourseTableParams params) {
        //登录用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if(user == null){
            XuechengPlusException.cast("请登录后继续选课");
        }
        String userId = user.getId();
        //设置当前的登录用户
        params.setUserId(userId);

        return myCourseTablesService.mycoursetables(params);
    }


}
