package com.lenovohit.lrouter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lenovohit.basemodel.User;
import com.lenovohit.lrouter_api.base.LRouterAppcation;
import com.lenovohit.lrouter_api.core.LRouterRequest;
import com.lenovohit.lrouter_api.core.LocalRouter;
import com.lenovohit.lrouter_api.core.callback.IRequestCallBack;
import com.lenovohit.rxlrouter_api.core.RxLocalLRouter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private Handler handler;

    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        button6 = (Button) findViewById(R.id.button6);

        initEvent();
    }

    private void initEvent(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(MainActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
            }
        };
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LocalRouter.ListenerFutureTask response =  LocalRouter.getInstance(LRouterAppcation.getInstance())
                                    .navigation(MainActivity.this, LRouterRequest.getInstance(MainActivity.this).provider("main")
                                    .action("main")
                                    .param("1", "Hello")
                                    .param("2", "World"));
                    Toast.makeText(MainActivity.this, response.getLRouterResponse().get(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    LocalRouter.ListenerFutureTask response = LocalRouter.getInstance(LRouterAppcation.getInstance())
                            .navigation(MainActivity.this, LRouterRequest.getInstance(MainActivity.this)
                                    .processName("com.lenovohit.lrouter:moduleA")
                                    .provider("bussinessModuleA")
                                    .action("bussinessModuleA")
                                    .param("1", "Hello")
                                    .param("2", "Thread"))
                            .setCallBack(new IRequestCallBack() {
                                @Override
                                public void onSuccess(final String result) {
                                    Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Exception e) {

                                }
                            });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
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
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               SecondActivity.startSecondActivity(MainActivity.this,"第二个页面");
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
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
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalRouter.getInstance(LRouterAppcation.getInstance())
                        .socketNavigation("Hello-socket".getBytes(), 10000, new IRequestCallBack() {
                            @Override
                            public void onSuccess(final String result) {
                                Message message = new Message();
                                message.obj = result;
                                message.what =1;
                                handler.sendMessage(message);
                            }

                            @Override
                            public void onFailure(Exception e) {

                            }
                        });
            }
        });
    }
}
