package com.fwtai;

import com.fwtai.tool.ToolClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnection;

public class Launcher extends AbstractVerticle {

  //第一步,声明router,如果有重复的 path 路由的话,它匹配顺序是从上往下的,仅会执行第一个.那如何更改顺序呢？可以通过 order(x)来更改顺序,值越小越先执行!
  Router router;

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

  // 创建连接池
  MySQLPool client;
  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    //实例化
    client = MySQLPool.pool(vertx,connectOptions,pool);

    //第二步,初始化|实例化 Router
    router = Router.router(vertx);

    //若想要或body的参数[含表单的form-data和json格式]需要添加
    router.route().handler(BodyHandler.create());//支持文件上传的目录,ctrl + p 查看

    final HttpServer httpServer = vertx.createHttpServer();

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
      context.response()
        .putHeader("content-type","application/json;charset=UTF-8")
        .end(ToolClient.json(200,"操作成功"));
    });

    router.route("/login").order(1).handler(context -> {
      context.response()
        .putHeader("content-type","application/json;charset=UTF-8")
        .end(ToolClient.json(200,"登录成功!"));
    });

    router.post("/register").handler((context) -> {
      final String username = context.request().getParam("username");
      final String password = context.request().getParam("password");
      client.getConnection((result ->{

        if (result.succeeded()) {
          System.out.println("连接数据库成功");
          // Obtain our connection
          final SqlConnection conn = result.result();
          // All operations execute on the same connection
          conn.query("SELECT * FROM sys_user WHERE kid='julien'").execute(row -> {
              if (row.succeeded()) {
                conn.query("SELECT * FROM sys_user WHERE kid='emad'").execute(rt -> {
                  //处理业务
                  conn.close();
                  });
              } else {
                conn.close();
              }
            });
        } else {
          System.out.println("连接数据库失败,"+ result.cause().getMessage());
        }
      }));
      context.response()
        .putHeader("content-type","application/json;charset=UTF-8")
        .end(ToolClient.json(200,"注册成功"));
    });

    //获取url参数,经典模式,即url的参数 http://192.168.3.108/url?page=1&size=20
    router.route("/url").handler(context -> {
      final String page = context.request().getParam("page");
      final String size = context.request().getParam("size");
      context.response()
        .putHeader("content-type","application/json;charset=UTF-8")
        .end(ToolClient.json(200,page +",获取url参数,经典模式,即url的参数 ,"+size));
    });

    //获取url参数,restful模式,用:和url上的/对应的绑定,它和vue的:Xxx="Yy"同样的意思,注意顺序! http://192.168.3.108/restful/10/30
    router.route("/restful/:page/:size").handler(context -> {
      final String page = context.request().getParam("page");
      final String size = context.request().getParam("size");
      context.response()
        .putHeader("content-type","application/json;charset=UTF-8")
        .end(ToolClient.json(200,page+",获取url参数,restful模式,"+size));
    });

    //获取body参数-->表单form-data格式,即请求头的 "Content-Type","application/x-www-form-urlencoded"
    router.route("/form").handler(context -> {
      final String page = context.request().getFormAttribute("page");
      final String param = context.request().getParam("page");
      System.out.println(param);
      context.response()
        .putHeader("content-type","application/json;charset=UTF-8")
        .end(ToolClient.json(200,param + ",获取body参数-->表单form-data格式," + page));
    });

    //获取body参数-->json格式,即请求头的 "Content-Type","application/json"
    router.route("/json").handler(context -> {
      final JsonObject page = context.getBodyAsJson();
      context.response()
        .putHeader("content-type","application/json;charset=UTF-8")
        .end(ToolClient.json(200,page.toString() + "获取body参数-->json格式,"+page.encode()+",解析:"+page.getValue("page")));
    });
  }
}