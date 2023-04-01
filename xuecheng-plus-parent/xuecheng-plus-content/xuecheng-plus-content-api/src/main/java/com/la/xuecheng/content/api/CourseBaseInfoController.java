package com.la.xuecheng.content.api;

import com.la.xuecheng.base.model.PageParams;
import com.la.xuecheng.base.model.PageResult;
import com.la.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.la.xuecheng.content.model.po.CourseBase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LA
 * @createDate 2023-03-30-14:17
 * @description
 */
@Api(value = "课程信息管理接口", tags = "课程信息管理接口")
@RestController //@Controller + @ResponseBody
public class CourseBaseInfoController {

    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto){
        CourseBase courseBase = new CourseBase();
        courseBase.setName("测试名称");
        courseBase.setCreateDate(LocalDateTime.now());
        List<CourseBase> list = new ArrayList<>();
        list.add(courseBase);
        PageResult<CourseBase> pageResult = new PageResult<>(list,10L,1L,10L);
        return pageResult;
    }
}
