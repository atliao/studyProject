package com.la.xuecheng.content;

import com.la.xuecheng.base.model.PageParams;
import com.la.xuecheng.base.model.PageResult;
import com.la.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.la.xuecheng.content.model.po.CourseBase;
import com.la.xuecheng.content.service.CourseBaseInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author LA
 * @createDate 2023-04-02-12:03
 * @description
 */
@SpringBootTest
public class CourseBaseInfoServiceTest {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Test
    public void testCourseBaseInfoService(){
        PageParams pageParams = new PageParams();
        pageParams.setPageNo(1L);
        pageParams.setPageSize(3L);

        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");

        PageResult<CourseBase> pageResult = courseBaseInfoService.queryCourseBaseList(1232141425L, pageParams, queryCourseParamsDto);

        System.out.println(pageResult);
    }
}
