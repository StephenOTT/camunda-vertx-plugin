package io.digitalstate.camunda;

import org.camunda.commons.logging.BaseLogger;

public class CamundaVertxPluginLogger extends BaseLogger {

  public static CamundaVertxPluginLogger LOG = createLogger(
    CamundaVertxPluginLogger.class, "CamundaVertxPluginLogger", "io.digitalstate.camunda", "CamundaVertxPluginLogger"
  );

  public void debug(String debugId, String message) {
    logDebug(debugId, message);
  }

  public void info(String infoId, String message) {
    logInfo(infoId, message);
  }

  public void error(String errorId, String message) {
    logError(errorId, message);
  }
}