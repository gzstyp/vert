package com.fwtai.tool;

import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

/**
 * 操作数据库-获取连接
 * @作者 田应平
 * @版本 v1.0
 * @创建时间 2020-08-27 9:32
 * @QQ号码 444141300
 * @Email service@yinlz.com
 * @官网 <url>http://www.yinlz.com</url>
 */
public final class ToolData{

  // 创建数据库连接池
  private MySQLPool client;

  final MySQLConnectOptions connectOptions = new MySQLConnectOptions()
    .setPort(3306)
    .setHost("192.168.3.66")
    .setDatabase("vertx")
    .setUser("root")
    .setPassword("rootFwtai")
    .setCharset("utf8mb4")
    .setSsl(false);

  //配置数据库连接池
  final PoolOptions pool = new PoolOptions().setMaxSize(32);

  public ToolData(final Vertx vertx){
    //实例化,可选
    client = MySQLPool.pool(vertx,connectOptions,pool);
  }

  public MySQLPool getClient(){
    return client;
  }
}