package com.gswxxn.unlockmilink.hook

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {
    override fun onInit() = configs {
        debugTag = "UnlockMiLink"
        isDebug = false
    }

    override fun onHook() = encase {
        loadApp("com.xiaomi.mi_connect_service") {
            onAppLifecycle {
                onCreate {
                    "miui.os.Build".clazz.field { name = "IS_TABLET" }.get().set(true)
                }
            }
        }
        loadApp("com.milink.service", MiLinkHooker())
        loadApp("com.xiaomi.mirror", MirrorHooker())
    }

    override fun onXposedEvent() {
        YukiXposedEvent.events {
            onHandleLoadPackage {
                if ("com.xiaomi.mirror" == it.packageName) MirrorHooker().onXPEvent(it)
            }
        }
    }
}