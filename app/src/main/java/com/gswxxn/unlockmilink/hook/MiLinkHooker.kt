package com.gswxxn.unlockmilink.hook

import com.gswxxn.unlockmilink.dexkit.finder.MiLinkFinder
import com.gswxxn.unlockmilink.dexkit.base.BaseHookerWithDexKit
import com.gswxxn.unlockmilink.dexkit.base.DexKitHelper.loadFinder
import com.gswxxn.unlockmilink.dexkit.member.MiLinkMember
import com.highcapable.yukihookapi.hook.factory.method
import org.luckypray.dexkit.DexKitBridge

object MiLinkHooker : BaseHookerWithDexKit() {
    override var storeMemberClass: Any? = MiLinkMember

    override fun onFindMembers(bridge: DexKitBridge) {
        bridge.loadFinder(MiLinkFinder)
    }

    override fun startHook() {
        val mirrorClass = "com.xiaomi.mirror"

        "$mirrorClass.synergy.MiuiSynergySdk\$IRemoteDeviceListener".toClass().method {
            name = "getListenManufacturer"
            emptyParam()
        }.hook {
            intercept()
        }

        "$mirrorClass.synergy.MiuiSynergySdk".toClass().method {
            name = "queryRemoteDevices"
            paramCount(3)
        }.hook {
            before {
                args(1).set(null)
            }
        }

        // 允许流转所有应用
        MiLinkMember.MiuiPlusServiceController_isSupportSendApp.hook {
            replaceToTrue()
        }

        // 忽略检查miui版本, 跨屏协同服务版本, 小米互联通信服务版本
        MiLinkMember.PermissionCheck_Check.hook {
            replaceToTrue()
        }

        "$mirrorClass.RemoteDeviceInfo".toClass().method {
            name = "isSupportSendApp"
            emptyParam()
        }.hook {
            replaceToTrue()
        }
    }
}