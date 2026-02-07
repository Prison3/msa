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
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import com.android.msa.IMsaGetter;
import com.android.msa.IMsa;
import com.android.msa.MsaException;
import com.android.msa.Logger;
import com.android.msa.RomDetector;

import java.util.Objects;

/**
 * 参阅 com.umeng.umsdk:oaid_vivo:1.0.0.1
 * 即 com.vivo.identifier.IdentifierManager
 *
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2020/5/30
 */
class VivoImpl implements IMsa {
    private final Context context;

    public VivoImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supported() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return false;
        }
        return RomDetector.sysProperty("persist.sys.identifierid.supported", "0").equals("1")
                || RomDetector.sysProperty("persist.sys.identifierid", "0").equals("1");
    }

    @Override
    public void doGet(final IMsaGetter getter) {
        if (context == null || getter == null) {
            return;
        }
        Uri uri = Uri.parse("content://com.vivo.vms.IdProvider/IdentifierId/OAID");
        try (Cursor cursor = context.getContentResolver().query(uri, null, null,
                null, null)) {
            Objects.requireNonNull(cursor).moveToFirst();
            int columnIndex = cursor.getColumnIndex("value");
            String oaid = cursor.getString(columnIndex);
            if (oaid == null || oaid.length() == 0) {
                throw new MsaException("OAID query failed");
            }
            Logger.print("OAID query success: " + oaid);
            getter.onOAIDGetComplete(oaid);
        } catch (Exception e) {
            Logger.print(e);
            getter.onOAIDGetError(e);
        }
    }

}
