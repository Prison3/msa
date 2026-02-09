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
import android.content.Context;

import com.android.msa.IMsa;
import com.android.msa.Logger;
import com.android.msa.RomDetector;

/**
 * @author 贵州山野羡民（1032694760@qq.com）
 * @since 2021/4/28 20:32
 */
public final class OAIDFactory {
    private static IMsa ioaid;

    private OAIDFactory() {
        super();
    }

    public static IMsa create(Context context) {
        if (context != null && !(context instanceof Application)) {
            // See https://github.com/gzu-liyujiang/Android_CN_OAID/pull/23
            context = context.getApplicationContext();
        }
        if (ioaid != null) {
            return ioaid;
        }
        // 优先尝试各厂商自家提供的接口
        ioaid = createManufacturerImpl(context);
        if (ioaid != null && ioaid.supported()) {
            Logger.i("Manufacturer interface has been found: " + ioaid.getClass().getName());
            return ioaid;
        }
        // 再尝试移动安全联盟及谷歌服务框架提供的接口
        ioaid = createUniversalImpl(context);
        return ioaid;
    }

    public static IMsa ofManufacturer(Context context) {
        IMsa impl = createManufacturerImpl(context);
        if (impl == null) {
            impl = new DefaultImpl();
        }
        return impl;
    }

    public static IMsa ofMsa(Context context) {
        return new MsaImpl(context);
    }

    public static IMsa ofGms(Context context) {
        return new GmsImpl(context);
    }

    private static IMsa createManufacturerImpl(Context context) {
        if (RomDetector.isLenovo() || RomDetector.isMotorola()) {
            return new LenovoImpl(context);
        }
        if (RomDetector.isMeizu()) {
            return new MeizuImpl(context);
        }
        if (RomDetector.isNubia()) {
            return new NubiaImpl(context);
        }
        if (RomDetector.isXiaomi() || RomDetector.isBlackShark() || RomDetector.isMiui()) {
            if (RomDetector.isMiuiGlobal()) {
                return new GmsImpl(context);
            }
            return new XiaomiImpl(context);
        }
        if (RomDetector.isSamsung()) {
            return new SamsungImpl(context);
        }
        if (RomDetector.isVivo()) {
            return new VivoImpl(context);
        }
        if (RomDetector.isASUS()) {
            return new AsusImpl(context);
        }
        if (RomDetector.isHonor() && !RomDetector.isEmui()) {
            HonorImpl honor = new HonorImpl(context);
            if (honor.supported()) {
                // 支持的话（Magic UI 4.0,5.0,6.0及MagicOS 7.0或以上）直接使用荣耀的实现，否则尝试华为的实现
                return honor;
            }
        }
        if (RomDetector.isHuawei() || RomDetector.isHonor() || RomDetector.isHarmonyOS() || RomDetector.isEmui() || RomDetector.isMagicUI()) {
            return new HuaweiImpl(context);
        }
        if (RomDetector.isOppo() || RomDetector.isOnePlus()) {
            OppoImpl oppo = new OppoImpl(context);
            if (oppo.supported()) {
                return oppo;
            }
            return new OppoExtImpl(context);
        }
        if (RomDetector.isCoolpad(context)) {
            return new CoolpadImpl(context);
        }
        if (RomDetector.isCoosea()) {
            return new CooseaImpl(context);
        }
        if (RomDetector.isFreeme()) {
            return new FreemeImpl(context);
        }
        if (RomDetector.is360OS()) {
            return new QikuImpl(context);
        }
        return null;
    }

    private static IMsa createUniversalImpl(Context context) {
        // 若各厂商自家没有提供接口，则优先尝试移动安全联盟的接口
        IMsa ioaid = new MsaImpl(context);
        if (ioaid.supported()) {
            Logger.i("Mobile Security Alliance has been found: " + ioaid.getClass().getName());
            return ioaid;
        }
        // 若不支持移动安全联盟的接口，则尝试谷歌服务框架的接口
        ioaid = new GmsImpl(context);
        if (ioaid.supported()) {
            Logger.i("Google Play Service has been found: " + ioaid.getClass().getName());
            return ioaid;
        }
        // 默认不支持
        ioaid = new DefaultImpl();
        Logger.w("OAID/AAID was not supported: " + ioaid.getClass().getName());
        return ioaid;
    }

}
