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
        loadApp("com.milink.service", MiLinkHooker())
        loadApp("com.xiaomi.mirror", MirrorHooker())
    }
}