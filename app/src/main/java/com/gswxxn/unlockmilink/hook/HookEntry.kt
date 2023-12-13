package com.gswxxn.unlockmilink.hook

import com.gswxxn.unlockmilink.data.DataConst
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {
    override fun onInit() = configs {
        debugLog { tag = "UnlockMiLink" }
        isDebug = false
    }

    override fun onHook() = encase {
        loadApp("com.xiaomi.mi_connect_service") {
            onAppLifecycle {
                onCreate {
                    when (prefs.get(DataConst.deviceType)) {
                        1 -> "miui.os.Build".toClass().field { name = "IS_TABLET" }.get().set(false)
                        2 -> "miui.os.Build".toClass().field { name = "IS_TABLET" }.get().set(true)
                    }
                }
            }
        }
        loadApp("com.milink.service", MiLinkHooker)
        loadApp("com.xiaomi.mirror", MirrorHooker)
    }

    override fun onXposedEvent() {
        YukiXposedEvent.events {
            onHandleLoadPackage {
                if ("com.xiaomi.mirror" == it.packageName) MirrorHooker.onXPEvent(it)
            }
        }
    }
}