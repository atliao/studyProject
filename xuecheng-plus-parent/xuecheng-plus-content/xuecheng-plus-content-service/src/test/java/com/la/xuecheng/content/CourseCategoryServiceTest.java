package com.la.xuecheng.content;

import com.la.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.la.xuecheng.content.service.CourseCategoryService;
import com.la.xuecheng.content.service.Impl.CourseCategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author LA
 * @createDate 2023-04-05-17:32
 * @description
 */
@SpringBootTest
public class CourseCategoryServiceTest {

    @Resource
    CourseCategoryService courseCategoryService;

    @Test
    public void test(){
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryService.queryCourseCategoryTree("1");
        System.out.println(courseCategoryTreeDtos);
    }
}
