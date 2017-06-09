package com.lenovohit.lrouter_api.core;


import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.util.concurrent.TimeUnit;

/**
 * 响应体
 * Created by yuzhijun on 2017/5/31.
 */
public class LRouterResponse {
    private static final int TIME_OUT = 50 * 1000;
    //从actionResult解析而来
    int mCode = -1;
    String mMessage = "";
    String mData;

    public String mActionResultStr;
    //采用软引用避免无法释放
    public SoftReference<LocalRouter.ListenerFutureTask> mAsyncResponse;

    //是否是非阻塞访问
    protected boolean mAsync = true;
    //异步获取数据是否有得到数据
    private boolean mHasGet = false;
    private long mTimeOut = 0;

    public boolean isAsync() {
        return mAsync;
    }

    public void setAsync(boolean async) {
        mAsync = async;
    }

    public LRouterResponse() {
        this(TIME_OUT);
    }

    public LRouterResponse(long timeout) {
        if (timeout > TIME_OUT * 2 || timeout < 0) {
            timeout = TIME_OUT;
        }
        mTimeOut = timeout;
    }

    private void parseResult(){
        if (!mHasGet) {
            try {
                JSONObject jsonObject = new JSONObject(mActionResultStr);
                this.mCode = jsonObject.getInt("code");
                this.mMessage = jsonObject.getString("msg");
                this.mData = jsonObject.getString("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mHasGet = true;
        }
    }

    public String get() throws Exception {
        if (mAsync) {
            mActionResultStr = mAsyncResponse.get().get(mTimeOut, TimeUnit.MILLISECONDS);
            parseResult();
        }else{
            parseResult();
        }
        return mActionResultStr;
    }

    public int getCode() throws Exception {
        if (!mHasGet) {
            get();
        }
        return mCode;
    }

    public String getMessage() throws Exception {
        if (!mHasGet) {
            get();
        }
        return mMessage;
    }

    public String getData() throws Exception {
        if (!mHasGet) {
            get();
        }
        return mData;
    }
}
