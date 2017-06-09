package com.lenovohit.lrouter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 *
 * Created by yuzhijun on 2017/6/7.
 */
public class SecondActivity extends AppCompatActivity {
    private static final String TITLE = "title";
    private String mTitle;

    private TextView tvSecond;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        mTitle = getIntent().getStringExtra(TITLE);

        tvSecond = (TextView) findViewById(R.id.tvSecond);
        tvSecond.setText(mTitle);
    }

    public static void startSecondActivity(Context context,String title){
        Intent intent = new Intent(context,SecondActivity.class);
        intent.putExtra(TITLE,title);
        context.startActivity(intent);
    }
}
