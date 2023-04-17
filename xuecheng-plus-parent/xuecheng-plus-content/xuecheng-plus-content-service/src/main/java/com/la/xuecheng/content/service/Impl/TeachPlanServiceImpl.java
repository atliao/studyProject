package com.la.xuecheng.content.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.la.xuecheng.content.mapper.CourseTeacherMapper;
import com.la.xuecheng.content.mapper.TeachplanMapper;
import com.la.xuecheng.content.model.dto.SaveTeachPlanDto;
import com.la.xuecheng.content.model.dto.TeachplanDto;
import com.la.xuecheng.content.model.po.CourseTeacher;
import com.la.xuecheng.content.model.po.Teachplan;
import com.la.xuecheng.content.service.TeachPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author LA
 * @createDate 2023-04-09-20:25
 * @description
 */
@Service
public class TeachPlanServiceImpl implements TeachPlanService {

    @Resource
    TeachplanMapper teachplanMapper;

    @Resource
    CourseTeacherMapper courseTeacherMapper;

    @Override
    public List<TeachplanDto> getTeachPlanTree(Long courseId) {

        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Transactional
    @Override
    public void saveTeachPlan(SaveTeachPlanDto saveTeachPlanDto) {
        //通过课程计划id判断是新增还是修改
        Long teachPlanId = saveTeachPlanDto.getId();
        Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
        if(teachplan == null){
            //新增
            Teachplan teachPlanNew = new Teachplan();
            BeanUtils.copyProperties(saveTeachPlanDto, teachPlanNew);
            //确定排序字段,找到同级节点个数，再+1
            Long parentId = saveTeachPlanDto.getParentid();
            Long courseId = saveTeachPlanDto.getCourseId();
            Integer count = getTeachplanCount(parentId, courseId);
            teachPlanNew.setOrderby(count+1);
            teachplanMapper.insert(teachPlanNew);
        }else {
            //修改
            BeanUtils.copyProperties(saveTeachPlanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        }

    }

    @Transactional
    @Override
    public void deleteTeachPlan(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        if(teachplan != null){
            teachplanMapper.deleteById(id);
        }
    }

    @Override
    public List<CourseTeacher> getTeacher(Long courseId) {

        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        return courseTeacherMapper.selectList(queryWrapper);
    }

    @Transactional
    @Override
    public CourseTeacher saveTeacher(CourseTeacher courseTeacher) {

        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        Long courseId = courseTeacher.getCourseId();
        Long id = courseTeacher.getId();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId).eq(CourseTeacher::getId, id);
        CourseTeacher teacher = courseTeacherMapper.selectOne(queryWrapper);

        if(teacher != null){
            BeanUtils.copyProperties(courseTeacher, teacher);
            courseTeacherMapper.updateById(teacher);
            return teacher;
        }else{
            CourseTeacher teacherNew = new CourseTeacher();
            BeanUtils.copyProperties(courseTeacher, teacherNew);
            courseTeacherMapper.insert(teacherNew);
            return teacherNew;
        }
    }

    @Transactional
    @Override
    public CourseTeacher updateTeacher(CourseTeacher courseTeacher) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        Long courseId = courseTeacher.getCourseId();
        Long id = courseTeacher.getId();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId).eq(CourseTeacher::getId, id);
        CourseTeacher teacher = courseTeacherMapper.selectOne(queryWrapper);
        BeanUtils.copyProperties(courseTeacher, teacher);
        courseTeacherMapper.updateById(teacher);
        return teacher;
    }

    @Transactional
    @Override
    public void deleteTeacher(Long courseId, Long id) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId).eq(CourseTeacher::getId, id);
        courseTeacherMapper.delete(queryWrapper);
    }

    private Integer getTeachplanCount(Long parentId, Long courseId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }
}
