package com.hangu.controller;

import com.hangu.consumer.UserService;
import com.hangu.entity.UserInfo;
import com.hangu.provider.binder.WebDataBinder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wuzhenhong
 * @date 2023/8/10 9:28
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/a")
    public UserInfo a() {
        UserInfo userInfo = userService.getUserInfo((RpcResult) -> {
            System.out.println("8888888888888888888888888888888888888888888888888");
        });
        return userInfo;
    }

    @GetMapping("/b")
    public String b(@RequestParam("name") String name) {
        return userService.getUserInfo(name);
    }

    @GetMapping("/c")
    public com.hangu.entity.Address c() {
        return userService.getUserAddrss("赣州市", "于都县");
    }

    @GetMapping("/d")
    public UserInfo d() {
        return userService.getUserInfo("小风", 18);
    }

    @PostMapping("/e")
    public UserInfo e(@RequestBody UserInfo userInfo) {
        return userService.getUserInfo(userInfo);
    }

    @PostMapping("/f")
    public List<UserInfo> f(@RequestBody List<UserInfo> userInfos) {
        return userService.getUserInfos(userInfos);
    }

    @PostMapping("/g")
    public UserInfo g(HttpServletRequest request) {
        UserInfo userInfo = new UserInfo();
        Map<String, String[]> map = request.getParameterMap();
        MutablePropertyValues mpvs = new MutablePropertyValues();
        map.forEach((k, v) -> {
            Arrays.stream(v).forEach(value -> {
                mpvs.add(k, value);
            });
        });

        WebDataBinder webDataBinder = new WebDataBinder(userInfo, new DefaultConversionService());
        webDataBinder.bind(mpvs);
        return userInfo;
    }

}
