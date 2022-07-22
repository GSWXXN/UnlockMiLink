package com.gswxxn.unlockmilink.hook

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class MirrorHooker : YukiBaseHooker() {
    override fun onHook() {

        "$packageName.display.DisplayManagerImpl".hook {
            injectMember {
                method {
                    name = "openDisplay"
                    paramCount(3)
                }
                beforeHook {
                    field { name = "MAX_SCREEN_COUNT" }.get().set(999)
                }
            }
        }
    }
}