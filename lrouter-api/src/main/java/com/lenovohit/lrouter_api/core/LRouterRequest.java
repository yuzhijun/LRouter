package com.lenovohit.lrouter_api.core;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.lenovohit.lrouter_api.utils.ProcessUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 请求体
 * Created by yuzhijun on 2017/5/31.
 */
public class LRouterRequest<T> implements Parcelable {
    private static volatile String DEFAULT_PROCESS = "";
    private String processName;
    private String provider;
    private String action;
    private HashMap<String,String> params;
    T requestObject;

    //利用CAS算法实现非阻塞并发获取对象
    private static final int length = 64;
    AtomicBoolean isIdle = new AtomicBoolean(true);
    private static AtomicInteger sIndex = new AtomicInteger(0);
    private static final int RESET_NUM = 1000;
    private static volatile LRouterRequest[] table = new LRouterRequest[length];

    static {
        for (int i = 0; i < length; i++) {
            table[i] = new LRouterRequest();
        }
    }

    private LRouterRequest(){
        this.processName = DEFAULT_PROCESS;
        this.provider = "";
        this.action = "";
        this.params = new HashMap<>();
    }

    private LRouterRequest(Context context){
        this.processName = getProcess(context);
        this.provider = "";
        this.action = "";
        this.params = new HashMap<>();
    }

    private LRouterRequest(Builder builder) {
        this.processName = builder.mProcessName;
        this.provider = builder.mProvider;
        this.action = builder.mAction;
        this.params = builder.mParams;
    }

    public static LRouterRequest getInstance(Context context){
        return obtain(context,0);
    }

    private static LRouterRequest obtain(Context context,int retryTime){
        int index = sIndex.getAndIncrement();
        if (index > RESET_NUM) {
            sIndex.compareAndSet(index, 0);
            if (index > RESET_NUM * 2) {
                sIndex.set(0);
            }
        }

        int num = index & (length - 1);

        LRouterRequest target = table[num];

        if (target.isIdle.compareAndSet(true, false)) {
            target.processName = getProcess(context);
            target.provider = "";
            target.action = "";
            target.params.clear();
            return target;
        } else {
            if (retryTime < 5) {
                return obtain(context, ++retryTime);
            } else {
                return new LRouterRequest(context);
            }
        }
    }

    @Override
    public String toString() {
        //此处用java原生的json解析工具
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("processName", processName);
            jsonObject.put("provider", provider);
            jsonObject.put("action", action);

            try {
                JSONObject jsonData = new JSONObject();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    jsonData.put(entry.getKey(), entry.getValue());
                }
                jsonObject.put("data", jsonData);
            } catch (Exception e) {
                e.printStackTrace();
                jsonObject.put("data", "{}");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    private static String getProcess(Context context) {
        if (TextUtils.isEmpty(DEFAULT_PROCESS) || ProcessUtil.UNKNOWN_PROCESS_NAME.equals(DEFAULT_PROCESS)) {
            DEFAULT_PROCESS = ProcessUtil.getProcessName(context, ProcessUtil.getMyProcessId());
        }
        return DEFAULT_PROCESS;
    }

    public String getProcessName() {
        return processName;
    }

    public String getProvider() {
        return provider;
    }

    public String getAction() {
        return action;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public T getRequestObject(){
        return requestObject;
    }

    public LRouterRequest processName(String processName){
        this.processName = processName;
        return this;
    }

    public LRouterRequest provider(String provider) {
        this.provider = provider;
        return this;
    }


    public LRouterRequest action(String action) {
        this.action = action;
        return this;
    }


    public LRouterRequest param(String key, String data) {
        this.params.put(key, data);
        return this;
    }

    public LRouterRequest reqeustObject(T t) {
        this.requestObject = t;
        return this;
    }

    public static class Builder{
        private String mProcessName;
        private String mAction;
        private String mProvider;
        private HashMap<String, String> mParams;


        public Builder(Context context) {
            mProcessName = getProcess(context);
            mProvider = "";
            mAction = "";
            mParams = new HashMap<>();
        }

        public Builder json(String requestJsonString) {
            try {
                JSONObject jsonObject = new JSONObject(requestJsonString);
                this.mProcessName = jsonObject.getString("processName");
                this.mProvider = jsonObject.getString("provider");
                this.mAction = jsonObject.getString("action");
                try {
                    JSONObject jsonData = new JSONObject(jsonObject.getString("data"));
                    Iterator it = jsonData.keys();
                    while (it.hasNext()) {
                        String key = String.valueOf(it.next());
                        String value = (String) jsonData.get(key);
                        this.mParams.put(key, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    this.mParams = new HashMap<>();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }

        public Builder processName(String processName) {
            this.mProcessName = processName;
            return this;
        }


        public Builder provider(String provider) {
            this.mProvider = provider;
            return this;
        }


        public Builder action(String action) {
            this.mAction = action;
            return this;
        }


        public Builder param(String key, String data) {
            this.mParams.put(key, data);
            return this;
        }

        public LRouterRequest build() {
            return new LRouterRequest(this);
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.processName);
        dest.writeString(this.provider);
        dest.writeString(this.action);
        if(this.params !=null){
            dest.writeInt(this.params.size());
            for (Map.Entry<String, String> entry : this.params.entrySet()) {
                dest.writeString(entry.getKey());
                dest.writeString(entry.getValue());
            }
        }else{
            dest.writeInt(0);
        }
        dest.writeParcelable((Parcelable) this.requestObject, flags);
    }

    protected LRouterRequest(Parcel in) {
        this.processName = in.readString();
        this.provider = in.readString();
        this.action = in.readString();
        int mapSize = in.readInt();
        if (mapSize > 0) {
            this.params = new HashMap<>();
        }
        for (int i = 0; i < mapSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.params.put(key, value);
        }
        this.requestObject = (T) in.readParcelable(this.getClass().getClassLoader());
    }

    public static final Creator<LRouterRequest> CREATOR = new Creator<LRouterRequest>() {
        @Override
        public LRouterRequest createFromParcel(Parcel source) {
            return new LRouterRequest(source);
        }

        @Override
        public LRouterRequest[] newArray(int size) {
            return new LRouterRequest[size];
        }
    };
}
