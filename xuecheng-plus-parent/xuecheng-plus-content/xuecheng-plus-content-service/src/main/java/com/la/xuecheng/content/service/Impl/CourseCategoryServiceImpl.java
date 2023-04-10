package com.la.xuecheng.content.service.Impl;

import com.la.xuecheng.content.mapper.CourseCategoryMapper;
import com.la.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.la.xuecheng.content.service.CourseBaseInfoService;
import com.la.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author LA
 * @createDate 2023-04-05-17:18
 * @description
 */
@Service
@Slf4j
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Resource
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryCourseCategoryTree(String id) {
        //调用mapper递归查询出需要的数据
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        //将其封装成tree,找到每个节点的子节点
        //先转换为map，方便获取
        HashMap<String, CourseCategoryTreeDto> hashMap = new HashMap<>();
        for(CourseCategoryTreeDto dto : courseCategoryTreeDtos){
            hashMap.put(dto.getId(), dto);
        }
        //转换
        for(CourseCategoryTreeDto dto : courseCategoryTreeDtos){
            if(dto.getParentid().equals("0")){
                continue;
            }
            String parentId  = dto.getParentid();
            if(hashMap.get(parentId).getChildrenTreeNodes() == null){
                hashMap.get(parentId).setChildrenTreeNodes(new ArrayList<>());
            }
            hashMap.get(parentId).getChildrenTreeNodes().add(dto);
        }

        return hashMap.get("1").getChildrenTreeNodes();
    }
}
