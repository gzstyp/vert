package com.fwtai;

import com.fwtai.service.IndexHandle;
import com.fwtai.service.SqlServerHandle;
import com.fwtai.service.UrlHandle;
import com.fwtai.service.UserService;
import com.fwtai.tool.ToolClient;
import com.fwtai.tool.ToolDao;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Launcher extends AbstractVerticle {

  //第一步,声明router,如果有重复的 path 路由的话,它匹配顺序是从上往下的,仅会执行第一个.那如何更改顺序呢？可以通过 order(x)来更改顺序,值越小越先执行!
  private Router router;

  private ToolDao toolDao;

  @Override
  public void start(final Promise<Void> startPromise) throws Exception {

    toolDao = new ToolDao(vertx);

    //创建HttpServer
    final HttpServer httpServer = vertx.createHttpServer();

    //第二步,初始化|实例化 Router
    router = Router.router(vertx);

    //若想要或body的参数[含表单的form-data和json格式]需要添加,可选
    router.route().handler(BodyHandler.create());//支持文件上传的目录,ctrl + p 查看

    final Set<HttpMethod> methods = new HashSet<>();
    methods.add(HttpMethod.OPTIONS);
    methods.add(HttpMethod.GET);
    methods.add(HttpMethod.POST);

    //router.route().handler(CorsHandler.create("vertx\\.io").allowedMethods(methods));//支持正则表达式
    router.route().handler(CorsHandler.create("http://192.168.3.108:8080").allowedMethods(methods));//支持正则表达式

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
      ToolClient.responseJson(context,ToolClient.jsonSucceed());
    });

    router.route("/login").order(1).handler(context -> {
      ToolClient.responseJson(context,ToolClient.jsonSucceed("登录成功!"));
    });

    // http://192.168.3.108/register?username=txh&password=000000
    router.get("/register").handler((context) -> {
      final String username = context.request().getParam("username");
      final String password = context.request().getParam("password");
      final String sql = "INSERT INTO sys_user(username,`password`) VALUES (?,?)";
      final ArrayList<Object> params = new ArrayList<>();
      params.add(username);
      params.add(password);
      toolDao.exeSql(context,sql,params);
    });

    //获取url参数,经典模式,即url的参数 http://192.168.3.108/url?page=1&size=10
    router.route("/url").handler(context -> {
      final String page = context.request().getParam("page");
      final String size = context.request().getParam("size");
      final Integer pageSize = Integer.parseInt(size);
      final Integer section = (Integer.parseInt(page) - 1) * pageSize;
      final ArrayList<Object> params = new ArrayList<>();
      params.add(section);
      params.add(pageSize);

      final String kid = "kid";
      final String username = "username";
      final String password = "password";

      final ArrayList<String> columns = new ArrayList<>();
      columns.add(kid);
      columns.add(username);
      columns.add(password);

      final String field = " "+kid+","+username + "," + password +" ";

      final String sql = "SELECT "+field+" FROM sys_user limit ?,?";
      toolDao.queryList(context,sql,columns,params);

    });

    // http://192.168.3.108/rest/1
    router.route("/rest/:kid").handler(new UserService(toolDao));

    //获取url参数,restful模式,用:和url上的/对应的绑定,它和vue的:Xxx="Yy"同样的意思,注意顺序! http://192.168.3.108/restful/10/30
    router.route("/restful/:page/:size").handler(context -> {
      final String page = context.request().getParam("page");
      final String size = context.request().getParam("size");
      ToolClient.responseJson(context,ToolClient.jsonSucceed(page+",获取url参数,restful模式,"+size));
    });

    //获取body参数-->表单 multipart/form-data 格式,即请求头的 "Content-Type","application/x-www-form-urlencoded"
    router.route("/form").handler(context -> {
      final String page = context.request().getFormAttribute("page");
      final String param = context.request().getParam("page");
      ToolClient.responseJson(context,ToolClient.jsonSucceed(param + ",获取body参数-->表单form-data格式," + page));
    });

    //获取body参数-->json格式,即请求头的 "Content-Type","application/json"
    router.route("/json").handler(context -> {
      final JsonObject page = context.getBodyAsJson();
      final String json = ToolClient.createJson(200,page.toString() + "获取body参数-->json格式,"+page.encode()+",解析:"+page.getValue("page"));
      ToolClient.responseJson(context,json);
    });

    // http://127.0.0.1/controller
    router.route("/controller").handler(new IndexHandle());

    // http://127.0.0.1/client
    router.route("/client").handler(new UrlHandle(vertx));

    // http://127.0.0.1/api/sqlServer?route=map|list
    router.route("/api/sqlServer").handler(new SqlServerHandle(vertx));
  }
}