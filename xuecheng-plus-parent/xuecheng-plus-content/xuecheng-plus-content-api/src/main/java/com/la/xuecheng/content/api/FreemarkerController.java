package com.la.xuecheng.content.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author LA
 * @createDate 2023-04-18-20:47
 * @description
 */
@Slf4j
@Controller //返回页面，用 @controller
public class FreemarkerController {

    @GetMapping("/testFreemarker")
    public ModelAndView test(){

        ModelAndView modelAndView = new ModelAndView();

        //指定模型
        modelAndView.addObject("name", "freemarker");
        //指定模板
        modelAndView.setViewName("test"); //根据视图名称拼接，配置文件中的后缀 "ftl"

        return modelAndView;

    }


}
