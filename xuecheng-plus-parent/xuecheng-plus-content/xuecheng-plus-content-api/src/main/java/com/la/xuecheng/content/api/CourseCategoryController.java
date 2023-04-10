package com.la.xuecheng.content.api;

import com.la.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.la.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author LA
 * @createDate 2023-04-03-16:10
 * @description
 */
@RestController
@Slf4j
public class CourseCategoryController {

    @Resource
    CourseCategoryService courseCategoryService;

    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes(){
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryService.queryCourseCategoryTree("1");
        return courseCategoryTreeDtos;
    }
}
