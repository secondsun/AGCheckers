package net.saga.ag.checkers.handler

import com.gmongo.GMongo
import com.mongodb.DB
import net.saga.ag.checkers.vo.User

import java.security.MessageDigest

class LoginHandler {

    private final GMongo mongo = new GMongo()
    private final DB db = mongo.getDB("checkers")
    private final Map<String, User> sessions = [:];

    User login(String userName, String password) {
        MessageDigest digest = MessageDigest.getInstance("MD5")
        digest.update(password.bytes);
        String hashedPW = new BigInteger(1, digest.digest()).toString(16).padLeft(32, '0')

        User user = db.users.find([userName:userName, password:hashedPW])[0].findAll { it.key != '_id' }

        startSession(user)
    }

    User getUser(String sessionId) {
        sessions[sessionId];
    }

    User enroll(Map<String, String> userData) {

        String userName = userData['userName']
        String password = userData['password']
        MessageDigest digest = MessageDigest.getInstance("MD5")
        digest.update(password.bytes);
        String hashedPW = new BigInteger(1, digest.digest()).toString(16).padLeft(32, '0')

        User user = db.users.find(userName:userName)[0]?.findAll { it.key != '_id' }
        if (user != null) {
            throw new RuntimeException("Duplicate User");
        }

        db.users.insert([userName:userName, password: hashedPW])
        user = db.users.find([userName:userName, password:hashedPW])[0].findAll { it.key != '_id' }
        startSession(user)
    }

    def logout(String sessionId) {
       sessions.remove(sessionId)
    }

    def getSessionId() {UUID.randomUUID().toString()}

    private User startSession(User user) {
        user.sessionId = getSessionId()
        sessions[user.sessionId] = user
        return user;
    }

}
