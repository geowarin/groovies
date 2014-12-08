import groovy.json.JsonSlurper
import groovy.util.logging.Log
@Grapes(
  @Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.7.1')
)
import groovyx.net.http.RESTClient
import groovyx.net.http.ResponseParseException

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

def etcd = new Etcd()

def onSet = { node, previous ->
  println "set $node"
  sleep 2000
}
def onRm = { node, previous ->
  println "rm $node"
  sleep 2000
}
etcd.watchForever('/message', onSet, onRm)


@Log
class Etcd {
  final def ETCD_URL = 'http://192.168.59.103:4001/v2/keys'
  def client = new RESTClient(ETCD_URL)
  ExecutorService executorService = Executors.newFixedThreadPool(1)

  def watchForever(String key, Closure onSet, Closure onRm) {
    log.info "Watching $key"
    //noinspection GroovyInfiniteLoopStatement
    while (true) {
      def result = watch(key)
      def domain = result.node.key.replace(key + '/', '')
      try {
        switch (result.action) {
          case 'set':
            def parsedValue = new JsonSlurper().parseText(result.node.value)
            log.info("Recevied: ${result.action} on $domain with $parsedValue")
            executorService.submit({ ->
              onSet.call(domain, parsedValue)
            })
            break
          case 'delete':
            log.info("Recevied: ${result.action} on $domain")
            executorService.submit({ ->
              onRm.call(domain)
            })
            break
            log.warning("Unhandled action ${result.action}")
        }
      } catch (Exception e) {
        log.info("An error occured while processing $result.node : $e.message")
      }
    }
  }

  def watch(String key) {
    try {
      client.get(path: "/v2/keys$key", query: [wait: true, recursive: true]).data
    } catch (ResponseParseException e) {
      log.warning "Unexpected failure : ${e.message}"
      watch(key)
    }
  }

}
