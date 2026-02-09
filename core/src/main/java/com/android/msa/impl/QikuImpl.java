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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.msa.IMsaGetter;
import com.android.msa.IMsa;
import com.android.msa.MsaException;
import com.android.msa.Logger;

import repeackage.com.qiku.id.IOAIDInterface;
import repeackage.com.qiku.id.QikuIdmanager;

/**
 * @author 10cl
 * @since 2024/03/06
 */
public class QikuImpl implements IMsa {
    private final Context context;
    private boolean mUseQikuId = true;

    public QikuImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supported() {
        if (context == null) {
            return false;
        }
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo("com.qiku.id", 0);
            if (pi != null) {
                return true;
            } else {
                mUseQikuId = false;
                return new QikuIdmanager().isSupported();
            }
        } catch (Exception e) {
            Logger.e("Qiku OAID supported check failed", e);
            return false;
        }
    }

    @Override
    public void doGet(IMsaGetter getter) {
        if (context == null || getter == null) {
            return;
        }
        if (mUseQikuId) {
            Intent intent = new Intent("qiku.service.action.id");
            intent.setPackage("com.qiku.id");
            OAIDService.bind(context, intent, getter, new OAIDService.RemoteCaller() {
                @Override
                public String callRemoteInterface(IBinder service) throws MsaException, RemoteException {
                    IOAIDInterface anInterface = IOAIDInterface.Stub.asInterface(service);
                    if (anInterface == null) {
                        throw new MsaException("IOAIDInterface is null");
                    }
                    return anInterface.getOAID();
                }
            });
        } else {
            try {
                String oaid = new QikuIdmanager().getOAID();
                if (oaid == null || oaid.length() == 0) {
                    throw new MsaException("OAID/AAID acquire failed");
                }
                getter.onCompleted(oaid);
            } catch (Exception e) {
                getter.onError(e);
            }
        }
    }

}
