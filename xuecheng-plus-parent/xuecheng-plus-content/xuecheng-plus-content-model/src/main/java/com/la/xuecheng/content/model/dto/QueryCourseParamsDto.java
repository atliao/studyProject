package com.la.xuecheng.content.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author LA
 * @createDate 2023-03-30-14:04
 * @description 课程查询条件模型类
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QueryCourseParamsDto {

    //审核状态
    private String auditStatus;

    //课程名称
    private String courseName;

    //发布状态
    private String publishStatus;
}
