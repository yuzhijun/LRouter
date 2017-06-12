## 项目路由框架(详细指导文档敬请期待...)

```
此框架是多进程多模块支持拦截,注解用法的框架
```
---
#### 功能：
1.**支持跨进程访问其他模块**<br>							
2.**支持跨进程请求拦截**<br>								
3.**支持功能类注解注入**<br>							

#### TODO：
1.同模块间请求拦截<br>
2.多模块间频繁数据请求<br>
3.注解注入修改成编译期反射<br>
4.请求路由的管理<br>


## 1.开始使用

### 1.1.在项目中集成

```
首先我们需要在主APP工程里面和module工程里面都添加进依赖库工程（完善版本之后会上传至jcenter）
lrouter-api（多进程多模块框架库）和rxlrouter-api（扩展框架支持rxjava）
```

### 1.2.创建自定义applicaiton

创建自定义applicaiton继承自LRouterAppcation
```
public class MainApplication extends LRouterAppcation {

}
```

然后在AndroidManifest.xml中加入

```
y<application
      ...
      android:name=".CustomApplication"
      ...
      >
      ...
</application>
```
### 1.3.创建自定义类applicaiton

因为多进程application会执行多次，为了让每个进程初始化都能初始化自己的模块的逻辑所以定义了类applicaiton
```
@Application(name = "com.lenovohit.lrouter",priority = 999)
public class MainAnologyApplication extends AnologyApplication {
}
```
其中@Application注解name为进程名字，priority为优先级

### 1.4.实现Provider

一个工程或者模块只有一个provider，创建方法如下：
```
@Provider(name = "main")
public class MainProvider extends LRProvider {
}
```
其中@Provider注解中name字段是根据自己喜好可以随意取得，但是尽量取一个有意义的名字

### 1.5.实现Action

一个action代表一个要执行的动作，用于跨模块的调用（跨进程跨模块也是action）
```
@Action(name = "main",provider = "main")
public class MainAction extends LRAction {//动作的执行
}
```
其中@Action注解name也是根据自己喜好取得，provider名字必须跟之前创建的provider名字一致
```
 @Override
    public boolean needAsync(Context context, LRouterRequest requestData) {
        return false;
    }
```
这个标识标志了是异步访问还是同步访问

### 1.6.多进程支持

如果需要开启多进程则需要在applicaiton的方法设置为true
```
  @Override
    public boolean needMultipleProcess() {
        return true;
    }
```

### 1.7.创建本地跨进程通讯的service

因为跨进程用AIDL通讯，所以每个工程在多进程情况下都需要创建这个service用于本地进程与远程进程的通讯
```
@Service(name = "com.lenovohit.lrouter")
public class MainRouterConnectService extends LocalRouterService {
}
```
其中@Service注解的name应为进程名

### 1.8.跨进程请求拦截器

跨进程请求拦截器是为了做埋点检测等功能实现的
```
@Interceptor
public class AppInterceptor extends AopInterceptor {
    @Override
    public void enterRequestIntercept(String methodName, String[] paramNames, Object[] paramValues) {
      
    }

    @Override
    public void exitRequestIntercept(String methodName, String[] paramNames, Object[] paramValues, long lengthMillis) {

    }
}
```
其中enterRequestIntercept即为进入请求方法前执行，exitRequestIntercept即为请求方法后执行，拦截器可以创建多个

## 2.调用方法

### 2.1.模块内无跨进程同步调用方法
```
    LocalRouter.ListenerFutureTask response =  LocalRouter.getInstance(LRouterAppcation.getInstance())
                                    .navigation(MainActivity.this, LRouterRequest.getInstance(MainActivity.this).provider("main")
                                    .action("main")
                                    .param("1", "Hello")
                                    .param("2", "World"));                              
 ```     
  response.get();直接获取请求结果
  
 ### 2.2.跨模块跨进程异步调用方法
 
 此处建议跨模块跨进程最好使用异步调用
  ```     
     LocalRouter.ListenerFutureTask response = LocalRouter.getInstance(LRouterAppcation.getInstance())
                            .navigation(MainActivity.this, LRouterRequest.getInstance(MainActivity.this)
                                    .processName("com.lenovohit.lrouter:moduleB")
                                    .provider("ModuleBProvider")
                                    .action("ModuleBAction")
                                    .param("1", "Hello")
                                    .param("2", "Annotation")
                                    .reqeustObject(new User("yuzhijun","123456")))
                            .setCallBack(new IRequestCallBack() {
                                @Override
                                public void onSuccess(final String result) {
                                    Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Exception e) {

                                }
                            });
  ```     
  processName表示要访问的进程名字，provider表示要访问的provider，action表示要执行的动作，param表示要传的参数，reqeustObject表示传的对象参数
  
  ### 2.3.使用rxjava方式访问
  如果引入了rxlrouter-api则可以使用rxjava的方式进行访问
    ```  
     RxLocalLRouter.getInstance(LRouterAppcation.getInstance())
                            .rxProxyNavigation(MainActivity.this,LRouterRequest.getInstance(MainActivity.this)
                                    .processName("com.lenovohit.lrouter:moduleA")
                                    .provider("bussinessModuleA")
                                    .action("bussinessModuleA")
                                    .param("1", "Hello")
                                    .param("2", "Thread"))
                            .subscribeOn(Schedulers.from(LocalRouter.getThreadPool()))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {

                                }
                            });
  ```  
  ## 3.后续工作
  
  文档补充与功能实现均还在路上，期待进一步...
