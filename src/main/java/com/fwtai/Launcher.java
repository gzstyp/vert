package com.fwtai;

import com.fwtai.service.IndexHandle;
import com.fwtai.tool.ToolClient;
import com.fwtai.tool.ToolData;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;

public class Launcher extends AbstractVerticle {

  //第一步,声明router,如果有重复的 path 路由的话,它匹配顺序是从上往下的,仅会执行第一个.那如何更改顺序呢？可以通过 order(x)来更改顺序,值越小越先执行!
  Router router;

  private MySQLPool client;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    client = new ToolData(vertx).getClient();

    //创建HttpServer
    final HttpServer httpServer = vertx.createHttpServer();

    //第二步,初始化|实例化 Router
    router = Router.router(vertx);

    //若想要或body的参数[含表单的form-data和json格式]需要添加,可选
    router.route().handler(BodyHandler.create());//支持文件上传的目录,ctrl + p 查看

    //第三步,将router和 HttpServer 绑定
    httpServer.requestHandler(router).listen(80, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("---应用启动成功---");
      } else {
        startPromise.fail(http.cause());
        System.out.println("---应用启动失败---");
      }
    });

    //第四步,配置Router解析url
    router.get("/").handler(context -> {
      final String json = ToolClient.createJson(200,"操作成功");
      ToolClient.responseJson(context,json);
    });

    router.route("/login").order(1).handler(context -> {
      final String json = ToolClient.createJson(200,"登录成功!");
      ToolClient.responseJson(context,json);
    });

    router.post("/register").handler((context) -> {
      final String username = context.request().getParam("username");
      final String password = context.request().getParam("password");

      client.getConnection((result) ->{
        if(result.succeeded()){
          final SqlConnection conn = result.result();
          conn.preparedQuery("INSERT INTO sys_user(username,`password`) VALUES (?,?)").execute(Tuple.of(username,password),rows ->{
            conn.close();//推荐写在第1行,防止忘记释放资源
            if(rows.succeeded()){
              final RowSet<Row> rowSet = rows.result();
              final int count = rowSet.rowCount();
              final String json = ToolClient.createJson(200,"注册成功,条数:"+count);
              ToolClient.responseJson(context,json);
            }else{
              final String json = ToolClient.createJson(199,"注册失败,原因:"+rows.cause());
              ToolClient.responseJson(context,json);
            }
          });
        }
      });
    });

    //获取url参数,经典模式,即url的参数 http://192.168.3.108/url?page=1&size=10
    router.route("/url").handler(context -> {
      final String page = context.request().getParam("page");
      final String size = context.request().getParam("size");
      /*不带参数的,client.getConnection((result) ->{
        if(result.succeeded()){
          final SqlConnection conn = result.result();
          conn.query("SELECT kid,username,password FROM sys_user").execute(rows ->{
            conn.close();//推荐写在第1行,防止忘记释放资源
            if(rows.succeeded()){
              final ArrayList<JsonObject> list = new ArrayList<>();
              rows.result().forEach((item) ->{
                final JsonObject jsonObject = new JsonObject();
                jsonObject.put("kid",item.getValue("kid"));
                jsonObject.put("username",item.getValue("username"));
                jsonObject.put("password",item.getValue("password"));
                list.add(jsonObject);
              });
              final String json = ToolClient.createJson(200,page +",操作数据库成功,"+list.toString()+",获取url参数,经典模式,即url的参数 ,"+size);
              ToolClient.responseJson(context,json);
            }else{
              final String json = ToolClient.createJson(199,page +",操作数据库失败,"+rows.cause()+",获取url参数,经典模式,即url的参数 ,"+size);
              ToolClient.responseJson(context,json);
            }
          });
        }
      });*/

      final Integer pageSize = Integer.parseInt(size);
      final Integer section = (Integer.parseInt(page) - 1) * pageSize;
      //带参数的
      client.getConnection((result) ->{
        if(result.succeeded()){
          final SqlConnection conn = result.result();
          conn.preparedQuery("SELECT kid,username,password FROM sys_user limit ?,?").execute(Tuple.of(section,pageSize),rows ->{
            conn.close();//推荐写在第1行,防止忘记释放资源
            if(rows.succeeded()){
              final ArrayList<JsonObject> list = new ArrayList<>();
              rows.result().forEach((item) ->{
                final JsonObject jsonObject = new JsonObject();
                jsonObject.put("kid",item.getValue("kid"));
                jsonObject.put("username",item.getValue("username"));
                jsonObject.put("password",item.getValue("password"));
                list.add(jsonObject);
              });
              final String json = ToolClient.createJson(200,page +",操作数据库成功,"+list.toString()+",获取url参数,经典模式,即url的参数 ,"+size);
              ToolClient.responseJson(context,json);
            }else{
              final String json = ToolClient.createJson(199,page +",操作数据库失败,"+rows.cause()+",获取url参数,经典模式,即url的参数 ,"+size);
              ToolClient.responseJson(context,json);
            }
          });
        }
      });
    });

    //获取url参数,restful模式,用:和url上的/对应的绑定,它和vue的:Xxx="Yy"同样的意思,注意顺序! http://192.168.3.108/restful/10/30
    router.route("/restful/:page/:size").handler(context -> {
      final String page = context.request().getParam("page");
      final String size = context.request().getParam("size");
      final String json = ToolClient.createJson(200,page+",获取url参数,restful模式,"+size);
      ToolClient.responseJson(context,json);
    });

    //获取body参数-->表单 multipart/form-data 格式,即请求头的 "Content-Type","application/x-www-form-urlencoded"
    router.route("/form").handler(context -> {
      final String page = context.request().getFormAttribute("page");
      final String param = context.request().getParam("page");
      final String json = ToolClient.createJson(200,param + ",获取body参数-->表单form-data格式," + page);
      ToolClient.responseJson(context,json);
    });

    //获取body参数-->json格式,即请求头的 "Content-Type","application/json"
    router.route("/json").handler(context -> {
      final JsonObject page = context.getBodyAsJson();
      final String json = ToolClient.createJson(200,page.toString() + "获取body参数-->json格式,"+page.encode()+",解析:"+page.getValue("page"));
      ToolClient.responseJson(context,json);
    });

    // http://127.0.0.1/controller
    router.route("/controller").handler(new IndexHandle());
  }
}