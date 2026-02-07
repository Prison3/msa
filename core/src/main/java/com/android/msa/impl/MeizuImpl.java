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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;

import com.android.msa.IMsaGetter;
import com.android.msa.IMsa;
import com.android.msa.MsaException;
import com.android.msa.Logger;

import java.util.Objects;

/**
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2020/5/30
 */
class MeizuImpl implements IMsa {
    private final Context context;

    public MeizuImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supported() {
        if (context == null) {
            return false;
        }
        try {
            ProviderInfo pi = context.getPackageManager().resolveContentProvider("com.meizu.flyme.openidsdk", 0);
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
        Uri uri = Uri.parse("content://com.meizu.flyme.openidsdk/");
        try (Cursor cursor = context.getContentResolver().query(uri, null, null,
                new String[]{"oaid"}, null)) {
            Objects.requireNonNull(cursor).moveToFirst();
            @SuppressLint("Range")
            String oaid = cursor.getString(cursor.getColumnIndex("value"));
            if (oaid == null || oaid.length() == 0) {
                throw new MsaException("OAID query failed");
            }
            Logger.print("OAID query success: " + oaid);
            getter.onCompleted(oaid);
        } catch (Exception e) {
            Logger.print(e);
            getter.onError(e);
        }
    }

}
