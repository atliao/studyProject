package com.la.xuecheng.content.model.dto;

import com.la.xuecheng.base.exception.ValidationGroups;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

/**
 * @author LA
 * @createDate 2023-04-08-19:37
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EditCourseDto extends AddCourseDto{

    @ApiModelProperty(value = "课程id",required = true)
    private Long id;
}
