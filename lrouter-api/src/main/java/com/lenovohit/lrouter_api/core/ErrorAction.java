package com.lenovohit.lrouter_api.core;

import android.content.Context;
public class ErrorAction extends LRAction {

    private static final String DEFAULT_MESSAGE = "出现错误啦,请紧急抢修!";
    private int mCode;
    private String mMessage;
    private boolean mAsync;
    public ErrorAction() {
        mCode = LRActionResult.RESULT_ERROR;
        mMessage = DEFAULT_MESSAGE;
        mAsync = false;
    }

    @Override
    public boolean needAsync(Context context, LRouterRequest requestData) {
        return mAsync;
    }

    @Override
    public LRActionResult invoke(Context context, LRouterRequest requestData) {
        LRActionResult result = new LRActionResult.Builder()
                .code(mCode)
                .msg(mMessage)
                .data(null)
                .build();
        return result;
    }

    public ErrorAction(boolean isAsync, int code, String message) {
        this.mCode = code;
        this.mMessage = message;
        this.mAsync = isAsync;
    }
}
