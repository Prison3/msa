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
import android.content.pm.PackageInfo;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.msa.IMsaGetter;
import com.android.msa.IMsa;
import com.android.msa.MsaException;
import com.android.msa.Logger;

import repeackage.com.coolpad.deviceidsupport.IDeviceIdManager;

/**
 * @author 贵州山野羡民（1032694760@qq.com）
 * @since 2021/8/26 15:11
 */
public class CoolpadImpl implements IMsa {
    private final Context context;

    public CoolpadImpl(Context context) {
        if (context instanceof Application) {
            this.context = context;
        } else {
            this.context = context.getApplicationContext();
        }
    }

    @Override
    public boolean supported() {
        if (context == null) {
            return false;
        }
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo("com.coolpad.deviceidsupport", 0);
            return pi != null;
        } catch (Exception e) {
            Logger.print(e);
            return false;
        }
    }

    @Override
    public void doGet(final IMsaGetter getter) {
        if (context == null || getter == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.coolpad.deviceidsupport", "com.coolpad.deviceidsupport.DeviceIdService"));
        OAIDService.bind(context, intent, getter, new OAIDService.RemoteCaller() {
            @Override
            public String callRemoteInterface(IBinder service) throws MsaException, RemoteException {
                IDeviceIdManager anInterface = IDeviceIdManager.Stub.asInterface(service);
                if (anInterface == null) {
                    throw new MsaException("IDeviceIdManager is null");
                }
                return anInterface.getOAID(context.getPackageName());
            }
        });
    }

}
