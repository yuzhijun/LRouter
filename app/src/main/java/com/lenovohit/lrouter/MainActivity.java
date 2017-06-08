package com.lenovohit.lrouter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lenovohit.lrouter_api.base.LRouterAppcation;
import com.lenovohit.lrouter_api.core.LRouterRequest;
import com.lenovohit.lrouter_api.core.LRouterResponse;
import com.lenovohit.lrouter_api.core.LocalRouter;

public class MainActivity extends AppCompatActivity {
    private Handler handler = new Handler();

    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);

        initEvent();
    }

    private void initEvent(){
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LRouterResponse response =  LocalRouter.getInstance(LRouterAppcation.getInstance())
                                    .navigation(MainActivity.this, LRouterRequest.getInstance(MainActivity.this).provider("main")
                                    .action("main")
                                    .param("1", "Hello")
                                    .param("2", "World"));
                    Toast.makeText(MainActivity.this, response.get(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    final long startTime = System.currentTimeMillis();
                    final LRouterResponse response = LocalRouter.getInstance(LRouterAppcation.getInstance())
                            .navigation(MainActivity.this, LRouterRequest.getInstance(MainActivity.this)
                                    .processName("com.lenovohit.lrouter:moduleA")
                                    .provider("bussinessModuleA")
                                    .action("bussinessModuleA")
                                    .param("1", "Hello")
                                    .param("2", "Thread"));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                final String temp = response.getData();
                                final long time = System.currentTimeMillis() - startTime;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Toast.makeText(MainActivity.this, "async:" + response.isAsync() + " cost:" + time + " response:" + response.get(), Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    final long startTime = System.currentTimeMillis();
                    final LRouterResponse response = LocalRouter.getInstance(LRouterAppcation.getInstance())
                            .navigation(MainActivity.this, LRouterRequest.getInstance(MainActivity.this)
                                    .processName("com.lenovohit.lrouter:moduleB")
                                    .provider("ModuleBProvider")
                                    .action("ModuleBAction")
                                    .param("1", "Hello")
                                    .param("2", "Annotation"));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                final String temp = response.getData();
                                final long time = System.currentTimeMillis() - startTime;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Toast.makeText(MainActivity.this, "async:" + response.isAsync() + " cost:" + time + " response:" + response.get(), Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                startActivity(intent);
            }
        });
    }
}
