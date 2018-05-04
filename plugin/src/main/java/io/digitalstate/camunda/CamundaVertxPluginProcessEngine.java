package io.digitalstate.camunda;

import org.camunda.bpm.engine.ProcessEngine;

/**
 * Provides a generic static class for interacting 
 * with the Camunda BPM Process engine.
 * 
 * Provides the ability to set and get the process engine.
 * This class is primarily used by the Camunda Engine Plugin
 * and by any Vertx Verticles that will to access the Process Engine.
 * 
 * This class is provided as a helper / timesaver for use by Vertx Verticles.
 */
public class CamundaVertxPluginProcessEngine {

  // @TODO add generic Logger for better Camunda logging that can be accessed by Vertx Verticles.
  
  private static ProcessEngine camundaProcessEngine;

  public static void setProcessEngine(ProcessEngine processEngine){
    camundaProcessEngine = processEngine;
  }
  public static ProcessEngine getProcessEngine(){
    return camundaProcessEngine;
  }

}
