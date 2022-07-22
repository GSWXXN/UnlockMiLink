package com.gswxxn.unlockmilink.hook

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class MiLinkHooker : YukiBaseHooker() {
    override fun onHook() {

        "$packageName.synergy.MiuiSynergySdk\$IRemoteDeviceListener".hook {
            injectMember {
                method {
                    name = "getListenManufacturer"
                    emptyParam()
                }
                intercept()
            }
        }

        "$packageName.synergy.MiuiSynergySdk".hook {
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
                    paramCount(1)
                }
                replaceToTrue()
            }
        }

        "$packageName.RemoteDeviceInfo".hook {
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