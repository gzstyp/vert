package com.fwtai.tool;

import io.vertx.core.json.JsonObject;

public final class ToolClient{

  public static String json(final int code,final String msg){
    final JsonObject json = new JsonObject();
    json.put("code",code);
    json.put("msg",msg);
    return json.encode();
  }
}
