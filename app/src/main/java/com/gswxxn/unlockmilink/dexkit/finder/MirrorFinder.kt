package com.gswxxn.unlockmilink.dexkit.finder

import com.gswxxn.unlockmilink.dexkit.base.BaseFinder
import com.gswxxn.unlockmilink.dexkit.base.DexKitHelper.findUniqueClass
import com.gswxxn.unlockmilink.dexkit.base.DexKitHelper.findUniqueField
import com.gswxxn.unlockmilink.dexkit.base.DexKitHelper.findUniqueMethod
import com.gswxxn.unlockmilink.dexkit.member.MirrorMember
import com.gswxxn.unlockmilink.hook.MirrorHooker
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import org.luckypray.dexkit.wrap.DexMethod

object MirrorFinder: BaseFinder() {
    override val classLoader: ClassLoader = MirrorHooker.appClassLoader!!

    override fun onFindMembers() {
        MirrorMember.DisplayManagerImpl_openDisplay = findUniqueMethod {
            usingStrings("screen count is up to MAX_SCREEN_COUNT", "release referece", "openDisplay-> mDisplayMap:")
        }

        MirrorMember.MAX_SCREEN_COUNT = findUniqueField {
            type(IntType)
            declaredClass(MirrorMember.DisplayManagerImpl_openDisplay.declaringClass)

            addReadMethod(DexMethod(MirrorMember.DisplayManagerImpl_openDisplay).toString())
            addWriteMethod {
                declaredClass(MirrorMember.DisplayManagerImpl_openDisplay.declaringClass)
                name("<clinit>")
            }
        }

        val deviceUtilsClass = findUniqueClass {
            usingStrings("701478a1e3b4b7e3978ea6946941f13")
        }
        MirrorMember.DeviceUtils_isPadDevice = findUniqueMethod {
            declaredClass(deviceUtilsClass)
            paramCount(0)
            returnType(BooleanType)
            addUsingField {
                declaredClass("miui.os.Build")
                name("IS_TABLET")
            }
        }

        MirrorMember.SystemUtils_isModelSupport = findUniqueMethod {
            usingStrings("isModelSupport, mirrorManager is null ")
        }

        MirrorMember.PcAppendView_init = findUniqueMethod {
            declaredClass {
                usingStrings("显示Windows桌面")
            }
            annotations {
                add {
                    usingStrings("ClickableViewAccessibility")
                }
            }
            paramTypes(ContextClass)
            returnType(Void.TYPE)
        }
    }
}