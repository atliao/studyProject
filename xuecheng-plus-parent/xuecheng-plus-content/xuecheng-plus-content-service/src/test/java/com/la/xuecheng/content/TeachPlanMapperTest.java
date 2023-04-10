package com.la.xuecheng.content;

import com.la.xuecheng.content.mapper.TeachplanMapper;
import com.la.xuecheng.content.model.dto.TeachplanDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author LA
 * @createDate 2023-04-09-20:04
 * @description
 */
@SpringBootTest
public class TeachPlanMapperTest {

    @Resource
    TeachplanMapper teachplanMapper;

    @Test
    public void test(){
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(117L);
        System.out.println(teachplanDtos);
    }
}
