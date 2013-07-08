import org.vertx.groovy.core.http.RouteMatcher

def eb = vertx.eventBus

def modules = [
        'org.jboss.aerogear.basic_auth-v1',
        'org.jboss.aerogear.digest_auth-v1',
        'org.jboss.aerogear.multipart-v1'
]

def addresses = []

eb.registerHandler("registered", {message -> addresses.push message.body})

def done = { index ->
    if ((index + 1) == modules.size) {
        vertx.createHttpServer().requestHandler({ req ->
            def address = req.uri.replace('/', '.')
            def data = [:]
            def resp = req.response

            if (!addresses.containsAll([address])) {
                resp.with {
                    statusCode = 404
                    end 'Not Found'
                    close()
                }
                return;
            }
            data['uri'] = req.uri;
            data['data'] = ''
            data['headers'] = [:];
            req.headers.each { data['headers'][it.key] = it.value}
            req.dataHandler { buffer -> data['data'] << buffer?:'' }
            req.endHandler { eb.send(address, data){ message ->

                message.body['headers']?.each { resp.headers[it.key.toLowerCase()] = it.value }
                resp.with {
                    statusCode = message.body.statusCode?:404
                    end message.body.body?:''
                    close()
                }
            }
            }
        }).listen(8080)
    }
}

modules.eachWithIndex {mod, index -> container.deployModule(mod){done(index)}}