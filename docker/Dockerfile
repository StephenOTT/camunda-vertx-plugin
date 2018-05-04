FROM camunda/camunda-bpm-platform:tomcat-7.8.0

# add custom configurations
COPY docker/camunda/conf/ /camunda/conf

# add JS script for RSA encryption
COPY docker/camunda/lib/digitalstate.camunda.camunda-vertx-plugin-0.1.0-SNAPSHOT-jar-with-dependencies.jar /camunda/lib/digitalstate.camunda.camunda-vertx-plugin-0.1.0-SNAPSHOT-jar-with-dependencies.jar
COPY docker/camunda/lib/jedis-2.9.0.jar /camunda/lib/jedis-2.9.0.jar