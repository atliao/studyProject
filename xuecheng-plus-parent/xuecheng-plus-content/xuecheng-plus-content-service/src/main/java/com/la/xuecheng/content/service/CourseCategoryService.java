package com.la.xuecheng.content.service;

import com.la.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * @author LA
 * @createDate 2023-04-03-16:11
 * @description
 */
public interface CourseCategoryService {

    public List<CourseCategoryTreeDto> queryCourseCategoryTree(String id);
}
