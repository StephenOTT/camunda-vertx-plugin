package io.digitalstate.camunda;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.camunda.commons.utils.IoUtil;

/**
 * Provides a Camunda BPMN plugin that creates a Vertx Instance and deploys a Main Verticle using Java
 * The Plugin as two parameters: 
 * 1. vertxOptionsPath (Absolute Path)(String)
 * 2. vertxVerticlesYmlPath (Absolute Path)(String)
 */
public class CamundaVertxPluginProcessEnginePlugin implements ProcessEnginePlugin {
  
  private String vertxOptionsPath;
  private String vertxVerticlesYmlPath;

  private final static CamundaVertxPluginLogger LOG = CamundaVertxPluginLogger.LOG;

  // @TODO Only setup for handling a single engine at the moment
  private static ProcessEngine camundaProcessEngine;

  @Override
  public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
   }

  @Override
  public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
 
  }

  @Override
  public void postProcessEngineBuild(ProcessEngine processEngine) {
    CamundaVertxPluginProcessEngine.setProcessEngine(processEngine);

    JsonObject vertxOptions = new JsonObject();

    // If there are Vertx Instance configs defined, then use them:
    if (vertxOptions != null && !vertxOptions.isEmpty()){
      try {
        InputStream fileStream = Files.newInputStream(Paths.get(this.vertxOptionsPath));
        JsonObject jsonFile =  new JsonObject(IoUtil.inputStreamAsString(fileStream));
        vertxOptions.mergeIn(jsonFile, true);
      
      } catch (Exception e) {
        LOG.error("vertx-options-file-reader", "Unable to read given file from VertxOptionsPath.  ERROR: " + e);
      }
    }
    // Create Vertx instance:
    Vertx vertx = Vertx.vertx(new VertxOptions(vertxOptions));

    // Setup Main Verticle
    DeploymentOptions mainVerticleOptions= new DeploymentOptions();
    JsonObject mainVerticleConfig = new JsonObject();
    // Set the path of the verticles yml file as a value inside of the Config of the DeploymentOptions
    mainVerticleConfig.put("verticle_deploy_yml", this.vertxVerticlesYmlPath);
    mainVerticleOptions.setConfig(mainVerticleConfig);

    // Deploy Main Verticle
    vertx.deployVerticle("io.digitalstate.camunda.CamundaVertxPluginMainVerticle", mainVerticleOptions);
  }

  /**
   * @param vertxOptionsPath path to a json file defining vertx instance options
  */
  public void setVertxOptionsPath(String fileAbsolutePath) {
      this.vertxOptionsPath = fileAbsolutePath;
  }

  /**
   * @param vertxVerticlesYmlPath Absolute path to vertx verticles yml file.
  */
  public void setVertxVerticlesYmlPath(String ymlAbsolutePath) {
      this.vertxVerticlesYmlPath = ymlAbsolutePath;
  }

  /**
   * @param processEngine the process engine created by the Camunda BPM 
   */
  public void setProcessEngine(ProcessEngine processEngine) {
      camundaProcessEngine = processEngine;
  }

  /**
   * Provides the process engine for Camunda BPM
   */
  public ProcessEngine getProcessEngine() {
      return camundaProcessEngine;
  }

}