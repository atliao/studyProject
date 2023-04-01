package com.la.xuecheng.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author LA
 * @createDate 2023-03-30-13:59
 * @description 分页查询分页参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PageParams {

    //当前页码
    @ApiModelProperty("页码")
    private Long pageNo;

    //每页记录数
    @ApiModelProperty("每页记录数")
    private Long pageSize;


}
