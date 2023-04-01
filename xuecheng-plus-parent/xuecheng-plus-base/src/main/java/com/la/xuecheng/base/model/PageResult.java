package com.la.xuecheng.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author LA
 * @createDate 2023-03-30-14:07
 * @description 分页查询结果模型类
 */

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {

    //数据列表
    private List<T> items;

    //总记录数
    private Long counts;

    //当前页码
    private Long pageNo;

    //每页记录条数
    private Long pageSize;

}
