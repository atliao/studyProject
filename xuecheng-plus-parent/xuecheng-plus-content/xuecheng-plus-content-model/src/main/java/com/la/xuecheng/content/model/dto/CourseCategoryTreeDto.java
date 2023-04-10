package com.la.xuecheng.content.model.dto;

import com.la.xuecheng.content.model.po.CourseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LA
 * @createDate 2023-04-03-15:29
 * @description
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {

    private List<CourseCategoryTreeDto> childrenTreeNodes;


}
