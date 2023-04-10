package com.la.xuecheng.content.service;

import com.la.xuecheng.content.model.dto.SaveTeachPlanDto;
import com.la.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

/**
 * @author LA
 * @createDate 2023-04-09-20:24
 * @description
 */
public interface TeachPlanService {

    public List<TeachplanDto> getTeachPlanTree(Long courseId);

    public void saveTeachPlan(SaveTeachPlanDto saveTeachPlanDto);
}
