package com.la.xuecheng.content.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.la.xuecheng.base.exception.XuechengPlusException;
import com.la.xuecheng.base.model.PageParams;
import com.la.xuecheng.base.model.PageResult;
import com.la.xuecheng.content.mapper.CourseBaseMapper;
import com.la.xuecheng.content.mapper.CourseCategoryMapper;
import com.la.xuecheng.content.mapper.CourseMarketMapper;
import com.la.xuecheng.content.model.dto.AddCourseDto;
import com.la.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.la.xuecheng.content.model.dto.EditCourseDto;
import com.la.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.la.xuecheng.content.model.po.CourseBase;
import com.la.xuecheng.content.model.po.CourseCategory;
import com.la.xuecheng.content.model.po.CourseMarket;
import com.la.xuecheng.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author LA
 * @createDate 2023-04-02-11:43
 * @description
 */
@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Resource
    CourseBaseMapper courseBaseMapper;
    @Resource
    CourseMarketMapper courseMarketMapper;
    @Resource
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(Long companyId, PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {

        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        QueryWrapper<CourseBase> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), "name", queryCourseParamsDto.getCourseName());
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), "audit_status", queryCourseParamsDto.getAuditStatus());
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()), "status", queryCourseParamsDto.getPublishStatus());
        queryWrapper.eq("company_id", companyId);
        /*LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());
        */

        Page<CourseBase> result = courseBaseMapper.selectPage(page, queryWrapper);

        PageResult<CourseBase> pageResult = new PageResult<>(result.getRecords(), result.getTotal(), pageParams.getPageNo(), pageParams.getPageSize());

        return pageResult;
    }

    /**
     * 新增课程
     * @param companyId
     * @param addCourseDto
     * @return
     */
    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {
        //参数的合法性校验
        /*if (StringUtils.isBlank(addCourseDto.getName())) {
            //throw new RuntimeException("课程名称为空");
            XuechengPlusException.cast("课程名称为空");
        }

        if (StringUtils.isBlank(addCourseDto.getMt())) {
            //throw new RuntimeException("课程分类为空");
            XuechengPlusException.cast("课程分类为空");
        }

        if (StringUtils.isBlank(addCourseDto.getSt())) {
            //throw new RuntimeException("课程分类为空");
            XuechengPlusException.cast("课程分类为空");
        }

        if (StringUtils.isBlank(addCourseDto.getGrade())) {
            //throw new RuntimeException("课程等级为空");
            XuechengPlusException.cast("课程等级为空");
        }

        if (StringUtils.isBlank(addCourseDto.getTeachmode())) {
            //throw new RuntimeException("教育模式为空");
            XuechengPlusException.cast("教育模式为空");
        }

        if (StringUtils.isBlank(addCourseDto.getUsers())) {
            //throw new RuntimeException("适应人群为空");
            XuechengPlusException.cast("适应人群为空");
        }

        if (StringUtils.isBlank(addCourseDto.getCharge())) {
            //throw new RuntimeException("收费规则为空");
            XuechengPlusException.cast("收费规则为空");
        }*/

        //向CourseBase表写数据
        CourseBase courseBase = new CourseBase();
        BeanUtils.copyProperties(addCourseDto, courseBase);//拷贝名称一样的属性
        //机构ID
        courseBase.setCompanyId(companyId);
        //创建日期
        courseBase.setCreateDate(LocalDateTime.now());
        //审核状态：默认未提交
        courseBase.setAuditStatus("202002");
        //发布状态
        courseBase.setStatus("203001");
        int res = courseBaseMapper.insert(courseBase);
        if(res <= 0){
            //throw new RuntimeException("添加课程失败");
            XuechengPlusException.cast("添加课程失败");
        }

        //向CourseMarket表写数据
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(addCourseDto, courseMarket);
        //CourseBase插入成功后，就会有id
        Long courseId = courseBase.getId();
        courseMarket.setId(courseId);
        //保存营销信息
        saveCourseMarket(courseMarket);

        //查出详细信息，包含两部分，课程信息和营销信息
        CourseBaseInfoDto courseBaseInfoDto = getCourseBaseInfo(courseId);
        return courseBaseInfoDto;
    }

    @Override
    //查询课程详细信息
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId){

        //从课程基本信息表查询
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            return null;
        }

        //从课程营销表查询
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);

        //组装在一起
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if(courseMarket != null){
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }
        //根据分类表查分类名称
        String mtName = courseCategoryMapper.selectById(courseBase.getMt()).getName();
        String stName = courseCategoryMapper.selectById(courseBase.getSt()).getName();
        courseBaseInfoDto.setMtName(mtName);
        courseBaseInfoDto.setStName(stName);
        return courseBaseInfoDto;
    }

    @Transactional
    @Override
    public CourseBaseInfoDto updateCourseBaseInfo(Long companyId, EditCourseDto editCourseDto) {
        //拿到课程id
        Long id = editCourseDto.getId();
        //合法性校验
        //数据库存在改课程
        CourseBase courseBase = courseBaseMapper.selectById(id);
        if(courseBase == null){
            XuechengPlusException.cast("课程不存在");
        }
        //只能修改本机构的课程
        if(!companyId.equals(courseBase.getCompanyId())){
            XuechengPlusException.cast("非本机构课程");
        }
        //更新
        BeanUtils.copyProperties(editCourseDto, courseBase);
        //修改时间
        courseBase.setChangeDate(LocalDateTime.now());
        int res = courseBaseMapper.updateById(courseBase);
        if(res <= 0){
            XuechengPlusException.cast("课程更新失败");
        }
        CourseMarket newCourseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto, newCourseMarket);
        saveCourseMarket(newCourseMarket);

        //从课程营销表查询
        CourseMarket courseMarket = courseMarketMapper.selectById(id);

        //组装在一起
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if(courseMarket != null){
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }
        //根据分类表查分类名称
        String mtName = courseCategoryMapper.selectById(courseBase.getMt()).getName();
        String stName = courseCategoryMapper.selectById(courseBase.getSt()).getName();
        courseBaseInfoDto.setMtName(mtName);
        courseBaseInfoDto.setStName(stName);
        return courseBaseInfoDto;
    }

    //单独写一个方法保存营销信息，存在则更新，不存在则添加
    private void saveCourseMarket(CourseMarket newCourseMarket){

        //参数合法性校验
        String charge = newCourseMarket.getCharge();
        if(StringUtils.isEmpty(charge)){
            //throw new RuntimeException("收费规则为空");
            XuechengPlusException.cast("收费规则为空");
        }

        //如果课程收费
        if(charge.equals("201001")){
            if(newCourseMarket.getPrice() == null || newCourseMarket.getPrice() <= 0){
                //throw new RuntimeException("课程价格不能为空，且必须大于0");
                XuechengPlusException.cast("课程价格不能为空，且必须大于0");
            }
        }

        //从数据库查询营销信息，存在则更新，不存在则添加
        Long id = newCourseMarket.getId();
        CourseMarket courseMarket = courseMarketMapper.selectById(id);
        if(courseMarket == null){
            int res = courseMarketMapper.insert(newCourseMarket);
            if(res <= 0){
                //throw new RuntimeException("添加营销信息失败");
                XuechengPlusException.cast("添加营销信息失败");
            }
        }else{
            //将newCourseMarket拷贝到courseMarket
            BeanUtils.copyProperties(newCourseMarket, courseMarket);
            //更新
            int res = courseMarketMapper.updateById(courseMarket);
            if(res <= 0){
                //throw new RuntimeException("更新营销信息失败");
                XuechengPlusException.cast("更新营销信息失败");
            }
        }
    }
}
