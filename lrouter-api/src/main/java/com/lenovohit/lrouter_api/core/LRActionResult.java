package com.lenovohit.lrouter_api.core;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *  action返回结果
 * Created by yuzhijun on 2017/5/31.
 */
public class LRActionResult {
    public static final int RESULT_ERROR = 0x0000;//错误码
    public static final int RESULT_SUCESS = 0x0001;//成功码
    public static final int ACTION_NOT_FOUND = 0x0002;//action未找到
    public static final int RESULT_REMOTE_STOPPING = 0x0003;//远程路由已经停止
    public static final int RESULT_TARGET_IS_REMOTE = 0x0004;//访问的是远程路由的进程
    public static final int RESULT_ROUTER_NOT_REGISTER = 0x0005;//路由未注册
    public static final int RESULT_CANNOT_BIND_LOCAL = 0x0006;//本地路由未能绑定成功
    public static final int RESULT_CANNOT_BIND_REMOTE = 0x0007;//远程路由不能绑定成功
    public static final int RESULT_REMOTE_LOCAL_ERROR = 0x0008;//远程路由和本地路由通讯失败

    private int code;
    private String msg;
    private String data;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getData() {
        return data;
    }

    private LRActionResult(Builder builder){
        this.code = builder.mCode;
        this.msg = builder.mMsg;
        this.data = builder.mData;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", code);
            jsonObject.put("msg", msg);
            jsonObject.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static class Builder {
        private int mCode;
        private String mMsg;
        private String mData;

        public Builder(){
            mCode = RESULT_ERROR;
            mMsg = "";
            mData = null;
        }

        public Builder parseByJson(String resultString) {
            try {
                JSONObject jsonObject = new JSONObject(resultString);
                this.mCode = jsonObject.getInt("code");
                this.mMsg = jsonObject.getString("msg");
                this.mData = jsonObject.getString("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }

        public Builder code(int code) {
            this.mCode = code;
            return this;
        }

        public Builder msg(String msg) {
            this.mMsg = msg;
            return this;
        }

        public Builder data(String data) {
            this.mData = data;
            return this;
        }

        public LRActionResult build() {
            return new LRActionResult(this);
        }
    }
}
