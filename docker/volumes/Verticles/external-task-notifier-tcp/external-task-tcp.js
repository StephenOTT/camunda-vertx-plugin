exports.vertxStart = function() {
 console.log("Camunda External Task WebSocket Notifier has started")
}
exports.vertxStop = function() {
 console.log("Camunda External Task WebSocket Notifier has stopped")
}

var processEngine = Java.type("io.digitalstate.camunda.CamundaVertxPluginProcessEngine").getProcessEngine()

var Router = require("vertx-web-js/router");
var SockJSHandler = require("vertx-web-js/sock_js_handler");

var router = Router.router(vertx);

// Allow events for the designated addresses in/out of the event bus bridge
var opts = {
  "inboundPermitteds" : [
    {
      "address" : "send.to.server"
    }
  ],
  "outboundPermitteds" : [
    {
      "address" : "send.to.client"
    }
  ]
};

// Create the event bus bridge and add it to the router.
var ebHandler = SockJSHandler.create(vertx).bridge(opts);
router.route("/eventbus/*").handler(ebHandler.handle);
// if connecting without SockJS then use url: ws://localhost:8081/eventbus/websocket
vertx.createHttpServer().requestHandler(router.accept).listen(8081);

var eb = vertx.eventBus();

// Register to listen for messages coming IN to the server
eb.consumer("send.to.server").handler(function (message) {
  // Create a timestamp string
  var timestamp = Java.type("java.text.DateFormat").getDateTimeInstance(Java.type("java.text.DateFormat").SHORT, Java.type("java.text.DateFormat").MEDIUM).format(Java.type("java.util.Date").from(Java.type("java.time.Instant").now()));
  // Send the message back out to all clients with the timestamp prepended.
  eb.publish("chat.to.client", timestamp + ": " + message.body());
});

vertx.setPeriodic(5000, function (id) {
  console.log('sending message to eb')
  var tasksArray = [
    {
      "someKey": "someValue"
    },
    {
      "someKey": "someValue"
    }
  ]
  eb.publish("send.to.client", tasksArray);

});