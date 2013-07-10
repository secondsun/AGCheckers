import groovy.json.JsonSlurper
import net.saga.ag.checkers.handler.GameHandler
import net.saga.ag.checkers.handler.LoginHandler
import org.vertx.groovy.core.buffer.Buffer
import org.vertx.groovy.core.http.RouteMatcher

import java.util.logging.Level
import java.util.logging.Logger

Logger log = Logger.getLogger("server")
def eb = vertx.eventBus

def server = vertx.createHttpServer()

def routeMatcher = new RouteMatcher()
def LoginHandler loginHandler = new LoginHandler()
def GameHandler gameHandler = new GameHandler()

routeMatcher.post("/auth/login", {req ->

        req.bodyHandler {body ->

            try {

                log.log(Level.INFO, body.toString());

                def auth = new JsonSlurper().parseText(body.toString())



                assert auth.username != null
                assert auth.password != null

                def user = loginHandler.login(auth.username, auth.password)
                assert user != null;
                req.response.with {
                    if (user == null) {
                        statusCode = 401
                    }  else {
                        Date d = new Date()
                        d.time = d.time + 30 * 60 * 1000
                        headers['Set-Cookie'] = "session_id=$user.sessionId; Expires=${d.toGMTString()}; Path=/;"
                        statusCode = 200
                    }
                }
            } catch (Throwable t) {
                log.log(Level.SEVERE, t.getMessage(), t)
                req.response.statusCode = 401
            }
            req.response.end()
        }

    }
)

routeMatcher.post("/auth/logout", {req ->

    def cookieHeader = req.headers['cookie']
    List<HttpCookie> cookies = HttpCookie.parse(cookieHeader)
    cookies.each{ cookie ->
        if (cookie.name == 'session_id') {
            loginHandler.logout(cookie.value)
        }
    }
    req.response.statusCode = 200;
    req.response.end()
}
)

routeMatcher.post("/auth/enroll", {req ->
        req.bodyHandler {body ->

            try {

                log.log(Level.INFO, body.toString());

                def auth = new JsonSlurper().parseText(body.toString())

                assert auth.username != null
                assert auth.password != null

                def user = loginHandler.enroll(auth)
                assert user != null;
                req.response.with {
                    if (user == null) {
                        statusCode = 401
                    }  else {
                        Date d = new Date()
                        d.time = d.time + 30 * 60 * 1000
                        headers['Set-Cookie'] = "session_id=$user.sessionId; Expires=${d.toGMTString()}; Path=/;"
                        statusCode = 200
                    }
                }
            } catch (Throwable t) {
                log.log(Level.SEVERE, t.getMessage(), t)
                req.response.statusCode = 401
            }
            req.response.end()
        }
    }
)

server.requestHandler(routeMatcher.asClosure()).listen(8080, "localhost")