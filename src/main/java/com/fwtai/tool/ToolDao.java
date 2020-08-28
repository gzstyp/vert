package com.fwtai.tool;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作数据库-C、R、U、D
 * @作者 田应平
 * @版本 v1.0
 * @创建时间 2020-08-27 9:51
 * @QQ号码 444141300
 * @Email service@yinlz.com
 * @官网 <url>http://www.yinlz.com</url>
 */
public final class ToolDao{

  // 创建数据库连接池
  private final MySQLPool client;

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

  public ToolDao(final Vertx vertx){
    client = MySQLPool.pool(vertx,connectOptions,pool);
  }

  //无参数
  public void queryList(final RoutingContext context,final String sql,final ArrayList<String> columns){
    client.getConnection((result) ->{
      if(result.succeeded()){
        final SqlConnection conn = result.result();
        conn.preparedQuery(sql).execute(rows ->{
          conn.close();//推荐写在第1行,防止忘记释放资源
          if(rows.succeeded()){
            final ArrayList<JsonObject> list = new ArrayList<>();
            rows.result().forEach((item) ->{
              final JsonObject jsonObject = new JsonObject();
              for(int i = 0; i < columns.size(); i++){
                final String column = columns.get(i);
                jsonObject.put(column,item.getValue(column));
              }
              list.add(jsonObject);
            });
            //操作数据库成功
            final String json = list.toString();
            ToolClient.responseJson(context,json);
          }else{
            //操作数据库失败
            final String json = ToolClient.createJson(199,"连接数据库失败");
            ToolClient.responseJson(context,json);
          }
        });
      }
    });
  }

  //有参数
  public void queryList(final RoutingContext context,final String sql,final ArrayList<String> columns,final List<Object> params){
    client.getConnection((result) ->{
      if(result.succeeded()){
        final SqlConnection conn = result.result();
        conn.preparedQuery(sql).execute(Tuple.wrap(params),rows ->{
          conn.close();//推荐写在第1行,防止忘记释放资源
          if(rows.succeeded()){
            final ArrayList<JsonObject> list = new ArrayList<>();
            rows.result().forEach((item) ->{
              final JsonObject jsonObject = new JsonObject();
              for(int i = 0; i < columns.size(); i++){
                final String column = columns.get(i);
                jsonObject.put(column,item.getValue(column));
              }
              list.add(jsonObject);
            });
            //操作数据库成功
            final String json = list.toString();
            ToolClient.responseJson(context,json);
          }else{
            //操作数据库失败
            final String json = ToolClient.createJson(199,"连接数据库失败");
            ToolClient.responseJson(context,json);
          }
        });
      }
    });
  }

  public void queryMap(final RoutingContext context,final String sql,final ArrayList<String> columns){
    client.getConnection((result) ->{
      if(result.succeeded()){
        final SqlConnection conn = result.result();
        conn.preparedQuery(sql).execute(rows ->{
          conn.close();//推荐写在第1行,防止忘记释放资源
          if(rows.succeeded()){
            final JsonObject jsonObject = new JsonObject();
            rows.result().forEach((item) ->{
              for(int i = 0; i < columns.size();i++){
                final String column = columns.get(i);
                jsonObject.put(column,item.getValue(column));
              }
            });
            ToolClient.responseJson(context,jsonObject.encode());
          }else{
            final String json = ToolClient.createJson(199,"连接数据库失败");
            ToolClient.responseJson(context,json);
          }
        });
      }
    });
  }

  public void queryMap(final RoutingContext context,final String sql,final ArrayList<String> columns,final List<Object> params){
    client.getConnection((result) ->{
      if(result.succeeded()){
        final SqlConnection conn = result.result();
        conn.preparedQuery(sql).execute(Tuple.wrap(params),rows ->{
          conn.close();//推荐写在第1行,防止忘记释放资源
          if(rows.succeeded()){
            final JsonObject jsonObject = new JsonObject();
            rows.result().forEach((item) ->{
              for(int i = 0; i < columns.size();i++){
                final String column = columns.get(i);
                jsonObject.put(column,item.getValue(column));
              }
            });
            ToolClient.responseJson(context,jsonObject.encode());
          }else{
            final String json = ToolClient.createJson(199,"连接数据库失败");
            ToolClient.responseJson(context,json);
          }
        });
      }
    });
  }

  public void exeSql(final RoutingContext context,final String sql){
    client.getConnection((result) ->{
      if(result.succeeded()){
        final SqlConnection conn = result.result();
        conn.preparedQuery(sql).execute(rows ->{
          conn.close();//推荐写在第1行,防止忘记释放资源
          if(rows.succeeded()){
            final RowSet<Row> rowSet = rows.result();
            final int count = rowSet.rowCount();
            String json;
            if(count > 0){
              json = ToolClient.createJson(200,"操作成功");
            }else{
              json = ToolClient.createJson(199,"操作失败");
            }
            ToolClient.responseJson(context,json);
          }else{
            final String json = ToolClient.createJson(199,"操作失败,"+rows.cause());
            ToolClient.responseJson(context,json);
          }
        });
      }
    });
  }

  public void exeSql(final RoutingContext context,final String sql,final List<Object> params){
    client.getConnection((result) ->{
      if(result.succeeded()){
        final SqlConnection conn = result.result();
        conn.preparedQuery(sql).execute(Tuple.wrap(params),rows ->{
          conn.close();//推荐写在第1行,防止忘记释放资源
          if(rows.succeeded()){
            final RowSet<Row> rowSet = rows.result();
            final int count = rowSet.rowCount();
            String json;
            if(count > 0){
              json = ToolClient.createJson(200,"操作成功");
            }else{
              json = ToolClient.createJson(199,"操作失败");
            }
            ToolClient.responseJson(context,json);
          }else{
            final String json = ToolClient.createJson(199,"操作失败,"+rows.cause());
            ToolClient.responseJson(context,json);
          }
        });
      }
    });
  }
}