package com.la.xuecheng.content.api;

import com.la.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.la.xuecheng.content.model.dto.SaveTeachPlanDto;
import com.la.xuecheng.content.model.dto.TeachplanDto;
import com.la.xuecheng.content.model.po.CourseTeacher;
import com.la.xuecheng.content.service.TeachPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
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
        Long companyId = 1232141425L;
        teachPlanService.saveTeachPlan(teachplanDto);
    }

    @ApiOperation("课程计划删除")
    @DeleteMapping("/teachplan/{id}")
    public void deleteTeachplan(@PathVariable("id") Long id){

        teachPlanService.deleteTeachPlan(id);
    }

    @ApiOperation("获取教师信息")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> getTeacher(@PathVariable("courseId") Long courseId){

        return teachPlanService.getTeacher(courseId);
    }


    @ApiOperation("创建或修改教师信息")
    @PostMapping("/courseTeacher")
    public CourseTeacher saveTeacher(@RequestBody CourseTeacher courseTeacher){

        return teachPlanService.saveTeacher(courseTeacher);
    }

    @ApiOperation("修改教师信息")
    @PutMapping("/courseTeacher")
    public CourseTeacher updateTeacher(@RequestBody CourseTeacher courseTeacher){

        return teachPlanService.updateTeacher(courseTeacher);
    }

    @ApiOperation("删除教师信息")
    @DeleteMapping("/courseTeacher/course/{courseId}/{id}")
    public void deleteTeacher(@PathVariable("courseId") Long courseId, @PathVariable("id") Long id){

        teachPlanService.deleteTeacher(courseId, id);
    }

    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        teachPlanService.associationMedia(bindTeachplanMediaDto);
    }
}
