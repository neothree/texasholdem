package com.texasthree.admin;

import com.texasthree.admin.operator.Operator;
import com.texasthree.admin.operator.OperatorService;
import com.texasthree.security.shiro.AbstractMeController;
import com.texasthree.security.shiro.LoginerRealm;
import com.texasthree.utility.restful.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class LoginController extends AbstractMeController<Operator> {

    @Autowired
    private LoginerRealm<Operator> loginerRealm;

    @Autowired
    private OperatorService userService;

    @PostMapping(value = "/login")
    public RestResponse login(
            @RequestParam("username") String username,
            @RequestParam("password") String password) throws Exception {

        // 没有的话创建一个
        var user = this.userService.getDataByUsername(username);


        var res = loginerRealm.login(username, password);
        if (!res.isSuccess()) {
            return res;
        }
        return RestResponse.SUCCESS;
    }

}
