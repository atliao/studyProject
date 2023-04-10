package com.la.xuecheng.content.api;

import com.la.xuecheng.content.model.dto.SaveTeachPlanDto;
import com.la.xuecheng.content.model.dto.TeachplanDto;
import com.la.xuecheng.content.service.TeachPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author LA
 * @createDate 2023-04-08-21:49
 * @description
 */
@RestController
@Slf4j
@Api(value = "课程计划编辑接口", tags = "课程计划编辑接口")
public class TeachPlanController {

    @Resource
    TeachPlanService teachPlanService;

    //查询课程计划
    @ApiOperation("查询课程计划树形结构")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable("courseId") long courseId){

        return teachPlanService.getTeachPlanTree(courseId);

    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan( @RequestBody SaveTeachPlanDto teachplanDto){

        teachPlanService.saveTeachPlan(teachplanDto);
    }
}
