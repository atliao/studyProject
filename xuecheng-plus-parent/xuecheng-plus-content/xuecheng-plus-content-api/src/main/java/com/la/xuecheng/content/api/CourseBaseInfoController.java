package com.la.xuecheng.content.api;

import com.la.xuecheng.base.exception.ValidationGroups;
import com.la.xuecheng.base.model.PageParams;
import com.la.xuecheng.base.model.PageResult;
import com.la.xuecheng.content.model.dto.AddCourseDto;
import com.la.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.la.xuecheng.content.model.dto.EditCourseDto;
import com.la.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.la.xuecheng.content.model.po.CourseBase;
import com.la.xuecheng.content.service.CourseBaseInfoService;
import com.la.xuecheng.content.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author LA
 * @createDate 2023-03-30-14:17
 * @description
 */
@Api(value = "课程信息管理接口", tags = "课程信息管理接口")
@RestController //@Controller + @ResponseBody
public class CourseBaseInfoController {

    @Resource
    CourseBaseInfoService courseBaseInfoService;

    @ApiOperation("课程查询接口")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")//指定权限表示符
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto){
        //根据令牌拿到当前用户信息
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        //拿到companyId
        String companyIdStr = user.getCompanyId();
        Long companyId = null;
        if(!companyIdStr.isEmpty()){
            companyId = Long.parseLong(companyIdStr);
        }
        PageResult<CourseBase> pageResult = courseBaseInfoService.queryCourseBaseList(companyId, pageParams, queryCourseParamsDto);
        return pageResult;
    }

    @ApiOperation("新增课程")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated(value = ValidationGroups.Insert.class) AddCourseDto addCourseDto){
        Long companyId = 1232141425L;
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.createCourseBase(companyId, addCourseDto);
        return courseBaseInfoDto;
    }

    @ApiOperation("查询课程")
    @GetMapping("/course/{id}")
    public CourseBaseInfoDto queryCourseBase(@PathVariable Long id){
        //获取用户信息，从上下文拿到认证信息
        /*Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(principal.toString());*/
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.getCourseBaseInfo(id);
        return courseBaseInfoDto;
    }

    @ApiOperation("修改课程")
    @PutMapping("/course")
    public CourseBaseInfoDto updateCourseBase(@RequestBody @Validated(ValidationGroups.Update.class) EditCourseDto editCourseDto){
        Long companyId = 1232141425L;
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.updateCourseBaseInfo(companyId, editCourseDto);
        return courseBaseInfoDto;
    }
}