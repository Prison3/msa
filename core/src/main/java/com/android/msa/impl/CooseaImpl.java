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

import android.app.KeyguardManager;
import android.content.Context;

import com.android.msa.IMsaGetter;
import com.android.msa.IMsa;
import com.android.msa.MsaException;
import com.android.msa.Logger;

import java.util.Objects;

/**
 * @author 贵州山野羡民（1032694760@qq.com）
 * @since 2021/8/26 16:22
 */
public class CooseaImpl implements IMsa {
    private final Context context;
    private final KeyguardManager keyguardManager;

    public CooseaImpl(Context context) {
        this.context = context;
        this.keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
    }

    @Override
    public boolean supported() {
        if (context == null) {
            return false;
        }
        if (keyguardManager == null) {
            return false;
        }
        try {
            Object obj = keyguardManager.getClass().getDeclaredMethod("isSupported").invoke(keyguardManager);
            return (Boolean) Objects.requireNonNull(obj);
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
        if (keyguardManager == null) {
            getter.onError(new MsaException("KeyguardManager not found"));
            return;
        }
        try {
            Object obj = keyguardManager.getClass().getDeclaredMethod("obtainOaid").invoke(keyguardManager);
            if (obj == null) {
                throw new MsaException("OAID obtain failed");
            }
            String oaid = obj.toString();
            Logger.print("OAID obtain success: " + oaid);
            getter.onCompleted(oaid);
        } catch (Exception e) {
            Logger.print(e);
        }
    }

}
