package com.gswxxn.unlockmilink.dexkit.finder

import com.gswxxn.unlockmilink.dexkit.base.BaseFinder
import com.gswxxn.unlockmilink.dexkit.base.DexKitHelper.findUniqueMethod
import com.gswxxn.unlockmilink.dexkit.member.MiLinkMember
import com.gswxxn.unlockmilink.hook.MiLinkHooker

object MiLinkFinder: BaseFinder() {
    override val classLoader: ClassLoader
        get() = MiLinkHooker.appClassLoader!!

    override fun onFindMembers() {
        MiLinkMember.PermissionCheck_Check = findUniqueMethod {
            usingStrings("miui: ", ", device: ")
            usingNumbers(13, 40, 30600)
        }

        MiLinkMember.MiuiPlusServiceController_isSupportSendApp = findUniqueMethod {
            usingStrings("isSupportSendApp pkg= ", ", result=", ", deviceType=")
        }
    }
}

