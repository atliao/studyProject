package com.la.xuecheng.content.service;

import com.la.xuecheng.base.model.PageParams;
import com.la.xuecheng.base.model.PageResult;
import com.la.xuecheng.content.model.dto.AddCourseDto;
import com.la.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.la.xuecheng.content.model.dto.EditCourseDto;
import com.la.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.la.xuecheng.content.model.po.CourseBase;

/**
 * @author LA
 * @createDate 2023-04-02-11:37
 * @description 课程信息管理接口
 */

public interface CourseBaseInfoService {

    //课程分页查询
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    //新增课程
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    public CourseBaseInfoDto getCourseBaseInfo(Long id);

    public CourseBaseInfoDto updateCourseBaseInfo(Long companyId, EditCourseDto editCourseDto);
}
