package com.la.xuecheng.ucenter.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author LA
 * @createDate 2023-04-25-14:14
 * @description
 */
@FeignClient(value = "checkcode",fallbackFactory = CheckCodeClientFallbackFactory.class)
@RequestMapping("/checkcode")
public interface CheckCodeClient {

    @PostMapping(value = "/verify")
    public Boolean verify(@RequestParam("key") String key, @RequestParam("code") String code);
}
