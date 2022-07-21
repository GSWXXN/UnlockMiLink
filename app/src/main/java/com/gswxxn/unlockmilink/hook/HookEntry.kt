package com.gswxxn.unlockmilink.hook

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {
    override fun onInit() = configs {
        debugTag = "UnlockMiLink"
        isDebug = false
    }

    override fun onHook() = encase {
        loadApp("com.milink.service") {
            findClass("com.xiaomi.mirror.synergy.MiuiSynergySdk\$IRemoteDeviceListener").hook {
                injectMember {
                    method {
                        name = "getListenManufacturer"
                        emptyParam()
                    }
                    intercept()
                }
            }

            findClass("com.xiaomi.mirror.synergy.MiuiSynergySdk").hook {
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

            findClass("com.miui.circulate.api.protocol.miuiplus.MiuiPlusServiceController").hook {
                injectMember {
                    method {
                        name = "isSupportSendApp"
                        paramCount(1)
                    }
                    replaceToTrue()
                }
            }

            findClass("com.xiaomi.mirror.RemoteDeviceInfo").hook {
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
}