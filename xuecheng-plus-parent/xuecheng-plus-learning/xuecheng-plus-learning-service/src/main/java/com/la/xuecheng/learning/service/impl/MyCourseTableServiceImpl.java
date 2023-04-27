package com.la.xuecheng.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.la.xuecheng.base.exception.XuechengPlusException;
import com.la.xuecheng.content.model.po.CoursePublish;
import com.la.xuecheng.learning.feignclient.ContentServiceClient;
import com.la.xuecheng.learning.mapper.XcChooseCourseMapper;
import com.la.xuecheng.learning.mapper.XcCourseTablesMapper;
import com.la.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.la.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.la.xuecheng.learning.model.po.XcChooseCourse;
import com.la.xuecheng.learning.model.po.XcCourseTables;
import com.la.xuecheng.learning.service.MyCourseTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author LA
 * @createDate 2023-04-27-14:27
 * @description
 */
@Slf4j
@Service
public class MyCourseTableServiceImpl implements MyCourseTableService {

    @Resource
    XcChooseCourseMapper xcChooseCourseMapper;

    @Resource
    XcCourseTablesMapper xcCourseTablesMapper;

    @Resource
    ContentServiceClient contentServiceClient;

    @Override
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId) {
        //调用内容管理查询课程收费规则
        CoursePublish coursePublish = contentServiceClient.getCoursepublish(courseId);
        if(coursePublish == null){
            XuechengPlusException.cast("课程不存在");
        }
        //收费规则
        String charge = coursePublish.getCharge();
        XcChooseCourse chooseCourse;
        XcCourseTables courseTables;
        if(charge.equals("201000")){ //课程免费，则直接添加,对选课记录表、我的课程表写数据
            chooseCourse = addFreeCoruse(userId, coursePublish);
            courseTables = addCourseTabls(chooseCourse);
        }else {//课程收费，则只对选课记录表写数据
            chooseCourse = addChargeCoruse(userId, coursePublish);
        }
        //判断学生的学习资格
        XcCourseTablesDto courseTablesDto = getLearningStatus(userId, courseId);
        //构造返回值
        XcChooseCourseDto xcChooseCourseDto = new XcChooseCourseDto();
        BeanUtils.copyProperties(chooseCourse,xcChooseCourseDto);
        //设置学习资格状态
        xcChooseCourseDto.setLearnStatus(courseTablesDto.getLearnStatus());

        return xcChooseCourseDto;
    }

    //返回//学习资格，[{"code":"702001","desc":"正常学习"},{"code":"702002","desc":"没有选课或选课后没有支付"},{"code":"702003","desc":"已过期需要申请续期或重新支付"}]
    @Override
    public XcCourseTablesDto getLearningStatus(String userId, Long courseId) {
        //返回的结果
        XcCourseTablesDto courseTablesDto = new XcCourseTablesDto();

        //查询我的课程表，如果查不到说明没有选课
        XcCourseTables xcCourseTables = getXcCourseTables(userId, courseId);
        if(xcCourseTables == null){
            //"code":"702002","desc":"没有选课或选课后没有支付"
            courseTablesDto.setLearnStatus("702002");
            return courseTablesDto;
        }
        //如果查到了，判断是否过期，如果过期不能继续学习，没有过期可以继续学习

        boolean before = xcCourseTables.getValidtimeEnd().isBefore(LocalDateTime.now());
        if(before){
            //"code":"702003","desc":"已过期需要申请续期或重新支付"
            BeanUtils.copyProperties(xcCourseTables,courseTablesDto);
            courseTablesDto.setLearnStatus("702003");
            return courseTablesDto;
        }else{
            //"code":"702001","desc":"正常学习"
            BeanUtils.copyProperties(xcCourseTables,courseTablesDto);
            courseTablesDto.setLearnStatus("702001");
            return courseTablesDto;
        }

    }


    //添加免费课程,免费课程加入选课记录表、我的课程表
    public XcChooseCourse addFreeCoruse(String userId, CoursePublish coursepublish) {
        //课程id
        Long courseId = coursepublish.getId();
        //判断，如果存在免费的选课记录且选课状态为成功，直接返回了
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<XcChooseCourse>()
                .eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getCourseId, courseId)
                .eq(XcChooseCourse::getOrderType, "700001")//免费课程
                .eq(XcChooseCourse::getStatus, "701001");//选课成功
        List<XcChooseCourse> xcChooseCourses = xcChooseCourseMapper.selectList(queryWrapper);
        if(xcChooseCourses.size()>0){
            return xcChooseCourses.get(0);
        }

        //向选课记录表写数据
        XcChooseCourse chooseCourse = new XcChooseCourse();

        chooseCourse.setCourseId(courseId);
        chooseCourse.setCourseName(coursepublish.getName());
        chooseCourse.setUserId(userId);
        chooseCourse.setCompanyId(coursepublish.getCompanyId());
        chooseCourse.setOrderType("700001");//免费课程
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setCoursePrice(coursepublish.getPrice());
        chooseCourse.setValidDays(365);
        chooseCourse.setStatus("701001");//选课成功
        chooseCourse.setValidtimeStart(LocalDateTime.now());//有效期的开始时间
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365));//有效期的结束时间

        int insert = xcChooseCourseMapper.insert(chooseCourse);
        if(insert<=0){
            XuechengPlusException.cast("添加选课记录失败");
        }

        return chooseCourse;
    }

    //添加到我的课程表
    public XcCourseTables addCourseTabls(XcChooseCourse xcChooseCourse){

        //选课成功了才可以向我的课程表添加
        String status = xcChooseCourse.getStatus();
        if(!"701001".equals(status)){
            XuechengPlusException.cast("选课没有成功无法添加到课程表");
        }
        XcCourseTables xcCourseTables = getXcCourseTables(xcChooseCourse.getUserId(), xcChooseCourse.getCourseId());
        if(xcCourseTables!=null){
            return xcCourseTables;
        }

        xcCourseTables = new XcCourseTables();
        BeanUtils.copyProperties(xcChooseCourse,xcCourseTables);
        xcCourseTables.setChooseCourseId(xcChooseCourse.getId());//记录选课表的主键
        xcCourseTables.setCourseType(xcChooseCourse.getOrderType());//选课类型
        xcCourseTables.setUpdateDate(LocalDateTime.now());
        int insert = xcCourseTablesMapper.insert(xcCourseTables);
        if(insert<=0){
            XuechengPlusException.cast("添加我的课程表失败");
        }

        return xcCourseTables;
    }

    /**
     * @description 根据课程和用户查询我的课程表中某一门课程
     * @param userId
     * @param courseId
     * @return com.xuecheng.learning.model.po.XcCourseTables
     * @author Mr.M
     * @date 2022/10/2 17:07
     */
    public XcCourseTables getXcCourseTables(String userId,Long courseId){
        XcCourseTables xcCourseTables = xcCourseTablesMapper.selectOne(new LambdaQueryWrapper<XcCourseTables>()
                .eq(XcCourseTables::getUserId, userId)
                .eq(XcCourseTables::getCourseId, courseId));
        return xcCourseTables;

    }


    //添加收费课程
    public XcChooseCourse addChargeCoruse(String userId,CoursePublish coursepublish){

        //课程id
        Long courseId = coursepublish.getId();
        //判断，如果存在收费的选课记录且选课状态为待支付，直接返回了
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<XcChooseCourse>().eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getCourseId, courseId)
                .eq(XcChooseCourse::getOrderType, "700002")//收费课程
                .eq(XcChooseCourse::getStatus, "701002");//待支付
        List<XcChooseCourse> xcChooseCourses = xcChooseCourseMapper.selectList(queryWrapper);
        if(xcChooseCourses.size()>0){
            return xcChooseCourses.get(0);
        }

        //向选课记录表写数据
        XcChooseCourse chooseCourse = new XcChooseCourse();

        chooseCourse.setCourseId(courseId);
        chooseCourse.setCourseName(coursepublish.getName());
        chooseCourse.setUserId(userId);
        chooseCourse.setCompanyId(coursepublish.getCompanyId());
        chooseCourse.setOrderType("700002");//收费课程
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setCoursePrice(coursepublish.getPrice());
        chooseCourse.setValidDays(365);
        chooseCourse.setStatus("701002");//待支付
        chooseCourse.setValidtimeStart(LocalDateTime.now());//有效期的开始时间
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365));//有效期的结束时间

        int insert = xcChooseCourseMapper.insert(chooseCourse);
        if(insert<=0){
            XuechengPlusException.cast("添加选课记录失败");
        }

        return chooseCourse;

    }
}
