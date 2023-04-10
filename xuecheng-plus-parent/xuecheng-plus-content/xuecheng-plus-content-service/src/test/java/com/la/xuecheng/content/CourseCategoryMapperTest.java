package com.la.xuecheng.content;

import com.la.xuecheng.content.mapper.CourseBaseMapper;
import com.la.xuecheng.content.mapper.CourseCategoryMapper;
import com.la.xuecheng.content.model.dto.CourseCategoryTreeDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author LA
 * @createDate 2023-04-05-16:21
 * @description
 */
@SpringBootTest
public class CourseCategoryMapperTest {

    @Resource
    CourseCategoryMapper courseCategoryMapper;

    @Test
    public void testCourseCategoryTreeNodes(){
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes("1");
        System.out.println(courseCategoryTreeDtos);
    }
}
