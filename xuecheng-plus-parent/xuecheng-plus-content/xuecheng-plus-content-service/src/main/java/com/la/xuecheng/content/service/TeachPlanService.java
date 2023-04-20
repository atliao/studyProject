package com.la.xuecheng.content.service;

import com.la.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.la.xuecheng.content.model.dto.SaveTeachPlanDto;
import com.la.xuecheng.content.model.dto.TeachplanDto;
import com.la.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/**
 * @author LA
 * @createDate 2023-04-09-20:24
 * @description
 */
public interface TeachPlanService {

    public List<TeachplanDto> getTeachPlanTree(Long courseId);

    public void saveTeachPlan(SaveTeachPlanDto saveTeachPlanDto);

    public void deleteTeachPlan(Long id);

    public List<CourseTeacher> getTeacher(Long courseId);

    public CourseTeacher saveTeacher(CourseTeacher courseTeacher);

    public CourseTeacher updateTeacher(CourseTeacher courseTeacher);

    public void deleteTeacher(Long courseId, Long id);

    public void associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);
}
