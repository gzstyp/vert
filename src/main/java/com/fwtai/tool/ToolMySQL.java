package com.fwtai.tool;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Future;
import io.vertx.core.Promise;
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
public final class ToolMySQL{

  final InternalLogger logger = Log4JLoggerFactory.getInstance(getClass());

  private ConfigRetriever retriever;//配置文件

  // 创建数据库连接池
  private MySQLPool client;

  private MySQLConnectOptions connectOptions;

  public ToolMySQL(final Vertx vertx){
    retriever = ConfigRetriever.create(vertx);//实例化
    retriever.getConfig(ar -> {
      if(ar.succeeded()) {
        final JsonObject config = ar.result();
        connectOptions = new MySQLConnectOptions()
          .setPort(config.getInteger("port"))
          .setHost(config.getString("host"))
          .setDatabase(config.getString("database"))
          .setUser(config.getString("username"))
          .setPassword(config.getString("password"))
          .setCharset(config.getString("charset"))
          .setSsl(config.getBoolean("ssl"));
        //配置数据库连接池
        final PoolOptions pool = new PoolOptions().setMaxSize(config.getInteger("maxSize",16));
        client = MySQLPool.pool(vertx,connectOptions,pool);
      } else {
        logger.error("读取数据库配置文件失败");
      }
    });
  }

  //无参数 new ToolMySQL(vertx).queryList();
  public void queryList(final RoutingContext context,final String sql){
    client.getConnection((result) ->{
      if(result.succeeded()){
        final SqlConnection conn = result.result();
        conn.preparedQuery(sql).execute(rows ->{
          conn.close();//推荐写在第1行,防止忘记释放资源
          if(rows.succeeded()){
            final ArrayList<JsonObject> list = new ArrayList<>();
            final RowSet<Row> rowRowSet = rows.result();
            final List<String> columns = rowRowSet.columnsNames();
            rowRowSet.forEach((item) ->{
              final JsonObject jsonObject = new JsonObject();
              for(int i = 0; i < columns.size(); i++){
                final String column = columns.get(i);
                jsonObject.put(column,item.getValue(column));
              }
              list.add(jsonObject);
            });
            //操作数据库成功
            ToolClient.responseJson(context,ToolClient.queryJson(list));
          }else{
            //操作数据库失败
            final String json = ToolClient.createJson(199,"连接数据库失败");
            ToolClient.responseJson(context,json);
          }
        });
      }
    });
  }

  //有参数 new ToolMySQL(vertx).queryList();
  public void queryList(final RoutingContext context,final String sql,final List<Object> params){
    client.getConnection((result) ->{
      if(result.succeeded()){
        final SqlConnection conn = result.result();
        conn.preparedQuery(sql).execute(Tuple.wrap(params),rows ->{
          conn.close();//推荐写在第1行,防止忘记释放资源
          if(rows.succeeded()){
            final ArrayList<JsonObject> list = new ArrayList<>();
            final RowSet<Row> rowRowSet = rows.result();
            final List<String> columns = rowRowSet.columnsNames();
            rowRowSet.forEach((item) ->{
              final JsonObject jsonObject = new JsonObject();
              for(int i = 0; i < columns.size(); i++){
                final String column = columns.get(i);
                jsonObject.put(column,item.getValue(column));
              }
              list.add(jsonObject);
            });
            //操作数据库成功
            ToolClient.responseJson(context,ToolClient.queryJson(list));
          }else{
            //操作数据库失败
            final String json = ToolClient.createJson(199,"连接数据库失败");
            ToolClient.responseJson(context,json);
          }
        });
      }
    });
  }

  public void queryMap(final RoutingContext context,final String sql){
    client.getConnection((result) ->{
      if(result.succeeded()){
        final SqlConnection conn = result.result();
        conn.preparedQuery(sql).execute(rows ->{
          conn.close();//推荐写在第1行,防止忘记释放资源
          if(rows.succeeded()){
            final JsonObject jsonObject = new JsonObject();
            final RowSet<Row> rowSet = rows.result();
            final List<String> columns = rowSet.columnsNames();
            rowSet.forEach((item) ->{
              for(int i = 0; i < columns.size();i++){
                final String column = columns.get(i);
                jsonObject.put(column,item.getValue(column));
              }
            });
            ToolClient.responseJson(context,ToolClient.queryJson(jsonObject));
          }else{
            final String json = ToolClient.createJson(199,"连接数据库失败");
            ToolClient.responseJson(context,json);
          }
        });
      }
    });
  }

  public void queryMap(final RoutingContext context,final String sql,final List<Object> params){
    client.getConnection((result) ->{
      if(result.succeeded()){
        final SqlConnection conn = result.result();
        conn.preparedQuery(sql).execute(Tuple.wrap(params),rows ->{
          conn.close();//推荐写在第1行,防止忘记释放资源
          if(rows.succeeded()){
            final JsonObject jsonObject = new JsonObject();
            final RowSet<Row> rowSet = rows.result();
            final List<String> columns = rowSet.columnsNames();
            rowSet.forEach((item) ->{
              for(int i = 0; i < columns.size();i++){
                final String column = columns.get(i);
                jsonObject.put(column,item.getValue(column));
              }
            });
            ToolClient.responseJson(context,ToolClient.queryJson(jsonObject));
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
            ToolClient.responseJson(context,ToolClient.executeRows(count));
          }else{
            failure(context,rows.cause());
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
            ToolClient.responseJson(context,ToolClient.executeRows(count));
          }else{
            failure(context,rows.cause());
          }
        });
      }
    });
  }

  protected void failure(final RoutingContext context,final Throwable throwable){
    final String message = throwable.getMessage();
    if(message.contains("cannot be null")){
      ToolClient.responseJson(context,ToolClient.jsonParams());
    }else if(message.contains("Duplicate entry")){
      ToolClient.responseJson(context,ToolClient.createJson(199,"数据已存在"));
    }else{
      ToolClient.responseJson(context,ToolClient.jsonFailure());
    }
  }

  public void queryHashMap(final String sql,final List<Object> params){
    getCon().compose(connection -> getRows(connection,sql,params)).onSuccess(rows ->{
      final int total = rows.size();
      for(int i = 0; i < total; i++){
      }
      final List<String> columns = rows.columnsNames();
      final ArrayList<JsonObject> list = new ArrayList<>();
      rows.forEach(item ->{
        final JsonObject jsonObject = new JsonObject();
        for(int i = 0; i < columns.size(); i++){
          final String column = columns.get(i);
          jsonObject.put(column,item.getValue(column));
        }
        list.add(jsonObject);
      });
      System.out.println(list);
    });
  }

  // ①获取数据库连接,通过链式调用;异步+响应式的链式调用示例,有且只有包含 Handler + AsyncResult 才能封装成链式调用
  private Future<SqlConnection> getCon(){
    final Promise<SqlConnection> promise = Promise.promise();
    client.getConnection(asyncResult ->{
      if(asyncResult.succeeded()){
        //重点,固定写法
        promise.complete(asyncResult.result());
      }else{
        promise.fail(asyncResult.cause());
      }
    });
    return promise.future();
  }

  // ②用获取到的连接查询数据库
  private Future<RowSet<Row>> getRows(final SqlConnection connection,final String sql,final List<Object> params){
    final Promise<RowSet<Row>> promise = Promise.promise();
    connection.preparedQuery(sql).execute(Tuple.wrap(params),handler ->{
      if(handler.succeeded()){
        promise.complete(handler.result());
      }else{
        promise.fail(handler.cause());
      }
    });
    return promise.future();
  }
}