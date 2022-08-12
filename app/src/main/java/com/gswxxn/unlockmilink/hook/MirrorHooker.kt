package com.gswxxn.unlockmilink.hook

import com.gswxxn.unlockmilink.data.DataConst
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

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

        "$packageName.utils.DeviceUtils".hook {
            injectMember {
                method {
                    name = "isPadDevice"
                    emptyParam()
                }
                beforeHook {
                    when (prefs.get(DataConst.deviceType)) {
                        1 -> result = false
                        2 -> result = true
                    }
                }
            }
        }

        "$packageName.utils.SystemUtils".hook {
            injectMember {
                method {
                    name = "isModelSupport"
                    param(ContextClass)
                }
                replaceToTrue()
            }
        }
    }

    fun onXPEvent(lpparam : XC_LoadPackage.LoadPackageParam) {
        val versionCode = XposedHelpers.getStaticIntField(XposedHelpers.findClass("${lpparam.packageName}.BuildConfig", lpparam.classLoader), "VERSION_CODE")
        if (versionCode >= 30726) return
        XposedHelpers.findAndHookMethod("${lpparam.packageName}.activity.ScanQRCodeActivity", lpparam.classLoader, "onCreate", BundleClass, ChangeDeviceTypeHook(lpparam))
        XposedHelpers.findAndHookMethod("${lpparam.packageName}.connection.idm.IDMManager", lpparam.classLoader, "initIDMServer", ChangeDeviceTypeHook(lpparam))
        XposedHelpers.findAndHookMethod("${lpparam.packageName}.connection.idm.IDMManager", lpparam.classLoader, "reRegisterIDMServer", ChangeDeviceTypeHook(lpparam))
    }

    class ChangeDeviceTypeHook(private val lpparam : XC_LoadPackage.LoadPackageParam) : XC_MethodHook() {
        private var isPadHook : XC_MethodHook.Unhook? = null
        override fun beforeHookedMethod(param: MethodHookParam?) {
            super.beforeHookedMethod(param)
            isPadHook = XposedHelpers.findAndHookMethod("${lpparam.packageName}.TerminalImpl", lpparam.classLoader, "isPad", object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    super.beforeHookedMethod(param)
                    param?.result = false
                }
            })
        }
        override fun afterHookedMethod(param: MethodHookParam?) {
            super.afterHookedMethod(param)
            isPadHook?.unhook()
        }
    }
}