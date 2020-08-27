package com.fwtai.service;

import com.fwtai.tool.ToolClient;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**MVC-->C,Handler这就是Controller。*/
public class IndexHandle implements Handler<RoutingContext>{

  @Override
  public void handle(final RoutingContext context){
    final String json = ToolClient.createJson(200,"分发到具体的类的方法上");
    ToolClient.responseJson(context,json);
  }
}