package com.gswxxn.unlockmilink.hook

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class MiLinkHooker : YukiBaseHooker() {
    override fun onHook() {
        val mirrorClass = "com.xiaomi.mirror"

        "$mirrorClass.synergy.MiuiSynergySdk\$IRemoteDeviceListener".hook {
            injectMember {
                method {
                    name = "getListenManufacturer"
                    emptyParam()
                }
                intercept()
            }
        }

        "$mirrorClass.synergy.MiuiSynergySdk".hook {
            injectMember {
                method {
                    name = "queryRemoteDevices"
                    paramCount(3)
                }
                beforeHook {
                    args(1).set(null)
                }
            }
        }

        "com.miui.circulate.api.protocol.miuiplus.MiuiPlusServiceController".hook {
            injectMember {
                method {
                    name = "isSupportSendApp"
                }
                replaceToTrue()
            }
        }

        "com.miui.circulate.world.permission.method.PermissionCheck\$BaseCheck".hook {
            injectMember {
                method {
                    name = "check"
                    emptyParam()
                }
                replaceToTrue()
            }
        }

        "$mirrorClass.RemoteDeviceInfo".hook {
            injectMember {
                method {
                    name = "isSupportSendApp"
                    emptyParam()
                }
                replaceToTrue()
            }
        }
    }
}