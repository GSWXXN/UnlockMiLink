package com.gswxxn.unlockmilink.hook

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

class MiLinkHooker : YukiBaseHooker() {
    override fun onHook() {
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

        "com.miui.circulate.api.protocol.miuiplus.MiuiPlusServiceController".toClass().method {
            name = "isSupportSendApp"
        }.hook {
            replaceToTrue()
        }

        "com.miui.circulate.world.permission.method.PermissionCheck\$BaseCheck".toClass().method {
            name = "check"
            emptyParam()
        }.hook {
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