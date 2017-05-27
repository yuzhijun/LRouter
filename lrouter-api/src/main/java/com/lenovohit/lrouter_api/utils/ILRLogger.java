package com.lenovohit.lrouter_api.utils;

/**
 * 自定义日志接口
 * Created by yuzhijun on 2017/5/27.
 */
public interface ILRLogger {

    enum LogLevel {
        DBUG(1), INFO(2), WARN(3), ERROR(4);
        private int _level;

        private LogLevel(int level) {
            _level = level;
        }

        public int getLevel() {
            return this._level;
        }

        public static LogLevel getValue(int level) {
            for (LogLevel l : LogLevel.values()) {
                if (l.getLevel() == level) {
                    return l;
                }
            }
            return LogLevel.DBUG;
        }
    }

    void log(String msg, LogLevel level);

    void log(String msg, LogLevel level, Throwable th);
}
