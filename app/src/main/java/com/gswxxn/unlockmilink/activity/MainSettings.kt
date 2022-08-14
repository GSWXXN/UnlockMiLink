package com.gswxxn.unlockmilink.activity

import android.os.Bundle
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.view.SpinnerV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import com.gswxxn.unlockmilink.R
import com.gswxxn.unlockmilink.data.DataConst
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import com.topjohnwu.superuser.Shell
import kotlin.system.exitProcess

class MainSettings : MIUIActivity() {
    init {
        initView {
            registerMain(R.string.app_name.string(), false) {
                val deviceTypes = mapOf(
                    0 to R.string.default_type.string(),
                    1 to R.string.android_phone.string(),
                    2 to R.string.android_pad.string(),
                )
                TextWithSpinner(TextV(R.string.device_type.string()), SpinnerV(deviceTypes[modulePrefs.get(DataConst.deviceType)]!!) {
                    deviceTypes.forEach {
                        add(it.value) {
                            modulePrefs.put(DataConst.deviceType, it.key)
                            Shell.cmd(
                                "pkill -f com.milink.service",
                                "pkill -f com.xiaomi.mirror",
                                "pkill -f com.xiaomi.mi_connect_service"
                            ).exec()
                        }
                    }
                })
                Text(textId = R.string.before_using_tips, textSize = 15f, colorId = R.color.tips_color)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!YukiHookAPI.Status.isXposedModuleActive)
            MIUIDialog(this) {
                setCancelable(false)
                setTitle(titleId = R.string.dialog_title)
                setMessage(R.string.dialog_content)
                setRButton(R.string.dialog_confirm) { exitProcess(0) }
            }.show()
    }

    private fun Int.string() = getString(this)
}