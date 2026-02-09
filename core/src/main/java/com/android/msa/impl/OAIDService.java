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

package com.android.msa.impl;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.msa.IMsaGetter;
import com.android.msa.MsaException;
import com.android.msa.Logger;

/**
 * 绑定远程的 OAID 服务
 *
 * @author 贵州山野羡民（1032694760@qq.com）
 * @since 2021/5/20 11:59
 */
class OAIDService implements ServiceConnection {
    private final Context context;
    private final IMsaGetter getter;
    private final RemoteCaller caller;

    public static void bind(Context context, Intent intent, IMsaGetter getter, RemoteCaller caller) {
        new OAIDService(context, getter, caller).bind(intent);
    }

    private OAIDService(Context context, IMsaGetter getter, RemoteCaller caller) {
        if (context instanceof Application) {
            this.context = context;
        } else {
            this.context = context.getApplicationContext();
        }
        this.getter = getter;
        this.caller = caller;
    }

    private void bind(Intent intent) {
        try {
            boolean ret = context.bindService(intent, this, Context.BIND_AUTO_CREATE);
            if (!ret) {
                throw new MsaException("Service binding failed");
            }
            Logger.i("Service has been bound: " + intent);
        } catch (Exception e) {
            getter.onError(e);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Logger.i("Service has been connected: " + name.getClassName());
        try {
            String oaid = caller.callRemoteInterface(service);
            if (oaid == null || oaid.length() == 0) {
                throw new MsaException("OAID/AAID acquire failed");
            }
            Logger.i("OAID/AAID acquire success: " + oaid);
            getter.onCompleted(oaid);
        } catch (Exception e) {
            Logger.e("OAID/AAID acquire failed", e);
            getter.onError(e);
        } finally {
            try {
                context.unbindService(this);
                Logger.i("Service has been unbound: " + name.getClassName());
            } catch (Exception e) {
                Logger.e("Failed to unbind service", e);
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Logger.i("Service has been disconnected: " + name.getClassName());
    }

    @FunctionalInterface
    public interface RemoteCaller {

        String callRemoteInterface(IBinder binder) throws MsaException, RemoteException;

    }

}
