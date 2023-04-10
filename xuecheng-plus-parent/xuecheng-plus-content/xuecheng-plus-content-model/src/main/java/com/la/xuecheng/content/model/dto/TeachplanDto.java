package com.la.xuecheng.content.model.dto;

import com.la.xuecheng.content.model.po.Teachplan;
import com.la.xuecheng.content.model.po.TeachplanMedia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author LA
 * @createDate 2023-04-08-21:44
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TeachplanDto extends Teachplan {

    //与媒资管理的信息
    private TeachplanMedia teachplanMedia;

    //小章节list
    private List<TeachplanDto> teachPlanTreeNodes;
}
