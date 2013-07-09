package net.saga.ag.checkers;

import net.saga.ag.checkers.handler.LoginHandler;
import net.saga.ag.checkers.vo.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(JUnit4.class)
public class LoginTests {

    LoginHandler loginHandler = new LoginHandler();

    @Test
    public void testEnrollAndLogin() {
        String randomUsername = UUID.randomUUID().toString();
        String randomPassword = UUID.randomUUID().toString();

        Map<String, String> userData = new HashMap<>();
        userData.put("userName", randomUsername);
        userData.put("password", randomPassword);

        User user = loginHandler.enroll(userData);

        Assert.assertNotNull(user.getSessionId());
        loginHandler.logout(user.getSessionId());

        user = loginHandler.login(randomUsername, randomPassword);
        Assert.assertNotNull(user.getSessionId());
    }

    @Test
    public void testDupeEnrollFails() {
        String randomUsername = UUID.randomUUID().toString();
        String randomPassword = UUID.randomUUID().toString();

        Map<String, String> userData = new HashMap<>();
        userData.put("userName", randomUsername);
        userData.put("password", randomPassword);

        loginHandler.enroll(userData);
        try {
            loginHandler.enroll(userData);
        } catch (RuntimeException ex) {
            return;
        }

        Assert.fail();
    }

}