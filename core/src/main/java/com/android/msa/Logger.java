/*
 * Copyright (c) 2016-present. 贵州纳雍穿青人李裕江 and All Contributors.
 *
 * The software is licensed under the Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *     http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.android.msa;

import android.util.Log;

public final class Logger {
    private static final String TAG = "MSA";

    /** 是否启用日志输出，默认 true */
    private static boolean enabled = true;

    private Logger() {
        super();
    }

    /**
     * 设置是否启用日志输出
     *
     * @param enabled true 启用，false 禁用
     */
    public static void setEnabled(boolean enabled) {
        Logger.enabled = enabled;
    }

    /**
     * 是否启用日志输出
     */
    public static boolean isEnabled() {
        return enabled;
    }

    /**
     * 打印带 Throwable 的日志
     *
     * @param msg 日志信息
     * @param t   异常
     */
    public static void print(String msg, Throwable t) {
        if (enabled) {
            Log.i(TAG, msg + '\n' + Log.getStackTraceString(t));
        }
    }

    public static void v(String msg) {
        if (enabled) Log.v(TAG, msg);
    }

    public static void d(String msg) {
        if (enabled) Log.d(TAG, msg);
    }

    public static void i(String msg) {
        if (enabled) Log.i(TAG, msg);
    }

    public static void w(String msg) {
        if (enabled) Log.w(TAG, msg);
    }

    public static void w(Throwable t) {
        if (enabled) Log.w(TAG, Log.getStackTraceString(t));
    }

    public static void w(String msg, Throwable t) {
        if (enabled) Log.w(TAG, msg + '\n' + Log.getStackTraceString(t));
    }

    public static void e(String msg) {
        if (enabled) Log.e(TAG, msg);
    }

    public static void e(Throwable t) {
        if (enabled) Log.e(TAG, Log.getStackTraceString(t));
    }

    public static void e(String msg, Throwable t) {
        if (enabled) Log.e(TAG, msg + '\n' + Log.getStackTraceString(t));
    }
}
