package com.fwtai.tool;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Map;

public final class ToolClient{

  private static final String key_data = "data";
  private static final String key_msg = "msg";
  private static final String key_code = "code";

  public static String createJson(final int code,final String msg){
    final JsonObject json = new JsonObject();
    json.put(key_code,code);
    json.put(key_msg,msg);
    return json.encode();
  }

  public static String jsonFailure(){
    return createJson(199,"操作失败");
  }

  public static String jsonFailure(final String msg){
    return createJson(199,msg);
  }

  public static String jsonSucceed(){
    return createJson(200,"操作成功");
  }

  public static String jsonSucceed(final String msg){
    return createJson(200,msg);
  }

  public static String jsonEmpty(){
    return createJson(201,"暂无数据");
  }

  public static String jsonParams(){
    return createJson(202,"请求参数不完整");
  }

  public static String jsonError(){
    return createJson(204,"系统出现错误");
  }

  public static String jsonPermission(){
    return createJson(401,"没有操作权限");
  }

  public static String queryJson(final Object object){
    if(object == null || object.toString().trim().length() == 0)
      return jsonEmpty();
    if(object instanceof Map<?,?>){
      final Map<?,?> map = (Map<?,?>) object;
      if(map.size() <= 0){
        jsonEmpty();
      }
    }
    if(object instanceof List<?>){
      final List<?> list = (List<?>) object;
      if(list.size() <= 0){
        return jsonEmpty();
      }
    }
    final JsonObject json = new JsonObject();
    json.put(key_code,200);
    json.put(key_msg,"操作成功");
    json.put(key_data,object);
    return json.encode();
  }

  public static String executeRows(final int rows){
    final JsonObject json = new JsonObject();
    if(rows > 0){
      json.put(key_code,200);
      json.put(key_msg,"操作成功");
      json.put(key_data,rows);
      return json.encode();
    }else{
      return jsonFailure();
    }
  }

  public static void responseJson(final RoutingContext context,final String payload){
    context.response().putHeader("Cache-Control","no-cache").putHeader("content-type","application/json;charset=UTF-8").end(payload);
  }
}