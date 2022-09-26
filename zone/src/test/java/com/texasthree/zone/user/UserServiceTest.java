package com.texasthree.zone.user;

import com.texasthree.utility.utlis.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testSave() throws Exception {
        var user = this.userService.user(StringUtils.get10UUID(), StringUtils.get10UUID(), true, StringUtils.get10UUID());
        assertEquals(BigDecimal.ZERO, user.getBalance());

        var amount = BigDecimal.valueOf(100);
        user = this.userService.balance(user.getId(), amount);
        assertEquals(0, user.getBalance().compareTo(amount));


        var amount1 = BigDecimal.valueOf(-50);
        user = this.userService.balance(user.getId(), amount1);
        assertEquals(0, user.getBalance().compareTo(amount.add(amount1)));
    }
}