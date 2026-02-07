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

import com.android.msa.IMsaGetter;
import com.android.msa.IMsa;
import com.android.msa.MsaException;

/**
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2020/5/30
 */
class DefaultImpl implements IMsa {

    @Override
    public boolean supported() {
        return false;
    }

    @Override
    public void doGet(final IMsaGetter getter) {
        if (getter == null) {
            return;
        }
        getter.onError(new MsaException("Unsupported"));
    }

}
