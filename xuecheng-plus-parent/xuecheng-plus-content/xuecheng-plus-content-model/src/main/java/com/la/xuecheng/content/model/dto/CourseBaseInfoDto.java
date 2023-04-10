package com.la.xuecheng.content.model.dto;

import com.la.xuecheng.content.model.po.CourseBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author LA
 * @createDate 2023-04-06-10:54
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CourseBaseInfoDto extends CourseBase {


    /**
     * 收费规则，对应数据字典
     */
    private String charge;

    /**
     * 价格
     */
    private Float price;


    /**
     * 原价
     */
    private Float originalPrice;

    /**
     * 咨询qq
     */
    private String qq;

    /**
     * 微信
     */
    private String wechat;

    /**
     * 电话
     */
    private String phone;

    /**
     * 有效期天数
     */
    private Integer validDays;

    /**
     * 大分类名称
     */
    private String mtName;

    /**
     * 小分类名称
     */
    private String stName;

}
