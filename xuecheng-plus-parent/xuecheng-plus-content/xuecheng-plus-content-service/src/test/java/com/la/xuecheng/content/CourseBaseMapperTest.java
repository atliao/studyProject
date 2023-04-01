package com.la.xuecheng.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.la.xuecheng.base.model.PageParams;
import com.la.xuecheng.base.model.PageResult;
import com.la.xuecheng.content.mapper.CourseBaseMapper;
import com.la.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.la.xuecheng.content.model.po.CourseBase;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author LA
 * @createDate 2023-04-01-15:01
 * @description
 */

@SpringBootTest
public class CourseBaseMapperTest {

    @Resource
    CourseBaseMapper courseBaseMapper;

    @Test
    public void testCourseBaseMapper(){

        //查询条件
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");
        //queryCourseParamsDto.setPublishStatus("203002");
        queryCourseParamsDto.setAuditStatus("202004");
        //拼装查询条件
        QueryWrapper<CourseBase> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), "name", queryCourseParamsDto.getCourseName());
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), "audit_status", queryCourseParamsDto.getAuditStatus());
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()), "status", queryCourseParamsDto.getPublishStatus());

        //LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //模糊查询
        //queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());
        //queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
        //queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());

        //分页查询条件:当前页码，每页记录数
        PageParams pageParams = new PageParams();
        pageParams.setPageNo(1L);
        pageParams.setPageSize(3L);
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        //分页查询
        Page<CourseBase> result = courseBaseMapper.selectPage(page, queryWrapper);


        PageResult<CourseBase> pageResult = new PageResult<>();
        pageResult.setPageNo(result.getCurrent());
        pageResult.setPageSize(result.getSize());
        pageResult.setCounts(result.getTotal());
        pageResult.setItems(result.getRecords());
    }

}
