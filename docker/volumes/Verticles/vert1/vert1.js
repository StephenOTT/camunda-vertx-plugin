exports.vertxStart = function() {
 console.log("This is a Javascript Vertx Verticle Running")
}

var processEngine = Java.type("io.digitalstate.camunda.CamundaVertxPluginProcessEngine").getProcessEngine()
console.log("Process Engine: " + processEngine.getName())

var activeProcessInstanceCount = processEngine.getRuntimeService()
                                              .createProcessInstanceQuery()
                                              .active()
                                              .count()
console.log('Active Process instance Count: ' + activeProcessInstanceCount)

//-----------------------------------

// the 'vertx' variable is already provided by Vertx.

var Router = require("vertx-web-js/router");
var server = vertx.createHttpServer();
var router = Router.router(vertx);

router.route('POST', '/approvals/create').handler(function (routingContext) {
  
  var completionKey = routingContext.request().getParam("completion_key");
  var taskId = routingContext.request().getParam("task_id");

  approvalCreate(taskId, completionKey)
  
  var response = routingContext.response();
  response.putHeader("content-type", "text/plain");
  response.setStatusCode(201)
          .end("created");
});

router.route('POST', '/approvals/complete').handler(function (routingContext) {
  
  var completionKey = routingContext.request().getParam("completion_key");
  var taskId = routingContext.request().getParam("task_id");
  var taskResponse = routingContext.request().getParam("task_response");
  
  approvalComplete(taskId, completionKey, taskResponse)

  var response = routingContext.response();
  response.putHeader("content-type", "text/plain");
  response.setStatusCode(201)
          .end("completed");
});

// Startup the Server!!
server.requestHandler(router.accept).listen(8081);



//
// Helper Functions:
//

function approvalCreate(taskId, completionKey){
  var Jedis = Java.type('redis.clients.jedis.Jedis')
  var jedis = new Jedis("redisdb")

  vertx.executeBlocking(function (future) {
    var jedisRes = jedis.set(taskId, completionKey)

    future.complete(jedisRes);
  }, function (res, res_err) {
    console.log("Redis returned the following: " + res)
    console.log('Redis Data: ' + jedis.get(taskId))
  });
}

function approvalComplete(taskId, completionKey, taskResponse){
  var Jedis = Java.type('redis.clients.jedis.Jedis')
  var jedis = new Jedis("redisdb")
  
  vertx.executeBlocking(function (future) {
    var jedisRes = jedis.get(taskId)
    if (jedisRes != completionKey){
      future.fail("invalid completion key")
    } else {
      taskComplete(taskId, taskResponse)
      future.complete("task complete");
    }

  }, function (res, res_err) {
    if (res_err == null){
      console.log("Approval Complete returned the following: " + res);
    } else {
      console.log('Error Response: ' + res_err)
    }
  });
}

function taskComplete(taskId, taskResponse){
  // Uses Java.asJSONCompatiable to ensure that the JSON 
  // object is returned to Camunda as a Map<String, Object> 
  // as per required by the complete() method.
  processEngine.getTaskService()
               .complete(taskId, Java.asJSONCompatible(
                                              {
                                                'task_response': taskResponse,
                                                'reviewer': "john"
                                              }))
}