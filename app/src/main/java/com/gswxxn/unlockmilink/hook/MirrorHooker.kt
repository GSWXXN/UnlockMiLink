package com.gswxxn.unlockmilink.hook

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ScrollView
import com.gswxxn.unlockmilink.data.DataConst
import com.gswxxn.unlockmilink.dexkit.base.BaseHookerWithDexKit
import com.gswxxn.unlockmilink.dexkit.base.DexKitHelper.loadFinder
import com.gswxxn.unlockmilink.dexkit.finder.MirrorFinder
import com.gswxxn.unlockmilink.dexkit.member.MirrorMember
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.luckypray.dexkit.DexKitBridge

object MirrorHooker: BaseHookerWithDexKit() {
    override var storeMemberClass: Any? = MirrorMember
    override fun onFindMembers(bridge: DexKitBridge) {
        bridge.loadFinder(MirrorFinder)
    }

    @SuppressLint("DiscouragedApi")
    override fun startHook() {

        MirrorMember.DisplayManagerImpl_openDisplay.hook {
            before {
                MirrorMember.MAX_SCREEN_COUNT.set(instance, 999)
            }
        }

        MirrorMember.DeviceUtils_isPadDevice.hook {
            before {
                when (prefs.get(DataConst.deviceType)) {
                    1 -> result = false
                    2 -> result = true
                }
            }
        }

        MirrorMember.SystemUtils_isModelSupport.hook {
            replaceToTrue()
        }

        MirrorMember.PcAppendView_init.hook {
            before {
                val scrollView = ScrollView(appContext)
                instance<FrameLayout>().addView(scrollView)
                LayoutInflater.from(args(0).cast<Context>()).inflate(appResources!!.getIdentifier("pc_append_view", "layout", packageName), scrollView)
            }
            after {
                instance<FrameLayout>().removeViewAt(1)
            }
        }
    }

    fun onXPEvent(lpparam : XC_LoadPackage.LoadPackageParam) {
        if ((appVersionCode ?: Long.MAX_VALUE) >= 30726) return
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