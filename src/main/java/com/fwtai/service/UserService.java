package com.fwtai.service;

import com.fwtai.tool.ToolMySQL;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;

/**
 * 分发处理
 * @作者 田应平
 * @版本 v1.0
 * @创建时间 2020-08-27 10:53
 * @QQ号码 444141300
 * @Email service@yinlz.com
 * @官网 <url>http://www.yinlz.com</url>
*/
public final class UserService implements Handler<RoutingContext>{

  private final ToolMySQL toolMySQL;

  public UserService(final ToolMySQL toolMySQL){
    this.toolMySQL = toolMySQL;
  }

  @Override
  public void handle(final RoutingContext context){
    final String id = context.request().getParam("kid");

    final ArrayList<Object> params = new ArrayList<>();
    params.add(id);

    final String kid = "kid";
    final String username = "username";
    final String password = "password";

    final ArrayList<String> columns = new ArrayList<>();
    columns.add(kid);
    columns.add(username);
    columns.add(password);

    final String field = " "+kid+","+username + "," + password +" ";
    final String sql = "SELECT "+field+" FROM sys_user where kid = ? limit 1";
    toolMySQL.queryMap(context,sql,columns,params);
  }
}