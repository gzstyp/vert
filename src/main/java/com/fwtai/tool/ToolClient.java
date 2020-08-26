package com.fwtai.tool;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public final class ToolClient{

  public static String createJson(final int code,final String msg){
    final JsonObject json = new JsonObject();
    json.put("code",code);
    json.put("msg",msg);
    return json.encode();
  }

  public static void responseJson(final RoutingContext request,final String json){
    request.response().putHeader("Cache-Control","no-cache").putHeader("content-type","application/json;charset=UTF-8").end(json);
  }
}
