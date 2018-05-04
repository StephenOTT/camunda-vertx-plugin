package io.digitalstate.camunda;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.config.ConfigRetrieverOptions;

/**
 * @TODO
 * Crate a event bus endpoint for getting a copy of the process engine 
 * configuration. The process engine configuration is comes from the
 * Plugin class.
 */

public class CamundaVertxPluginMainVerticle extends AbstractVerticle {

  private final static CamundaVertxPluginLogger LOG = CamundaVertxPluginLogger.LOG;

  @Override
  public void start(Future<Void> startFuture) {
    // @TODO create a longer duration between checks.
    ConfigStoreOptions store = new ConfigStoreOptions()
      .setType("file")
      .setFormat("yaml")
      .setOptional(false)
      .setConfig(new JsonObject()
        .put("path", config().getString("verticle_deploy_yml"))
      );

    ConfigRetriever retriever = ConfigRetriever.create(vertx,
        new ConfigRetrieverOptions().addStore(store)
                                    .setScanPeriod(3600000));

    Future<JsonObject> configFuture = ConfigRetriever.getConfigAsFuture(retriever);

    configFuture.setHandler(ar -> {
      if (ar.failed()) {
        LOG.error("VERTX-YAML-RETRIEVER", "Unable to get YAML: " + ar.result().toString());
      
      } else {
          ar.result().getJsonObject("verticles").forEach( v -> {
            // Deploy the verticle that is configured in the YAML
            this.deployVertxVerticle((JsonObject) v.getValue());
          });
      } // end of else
    });

    LOG.info("vertx-verticle-start", "Camunda Vertx Starting Verticle has started");
    startFuture.complete();
  }

  @Override
  public void stop(Future<Void> stopFuture) {
    LOG.error("vertx-verticle-stop", "Camunda Main Vertx Verticle has stopped.");
    stopFuture.complete();
  }

  private void deployVertxVerticle(JsonObject verticle){
    DeploymentOptions deploymentOptions = new DeploymentOptions(verticle.getJsonObject("deployment_options"));
    
    LOG.info("Vertx-Verticle-Deployment", "Deploying Vertx Vertical: " + verticle.getString("name"));
    
    vertx.deployVerticle(verticle.getString("path"), deploymentOptions);
  }

}