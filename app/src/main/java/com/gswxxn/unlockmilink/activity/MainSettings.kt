package com.gswxxn.unlockmilink.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.annotation.Keep
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMMainPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SpinnerV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import com.gswxxn.unlockmilink.R
import com.gswxxn.unlockmilink.data.DataConst
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.prefs
import com.topjohnwu.superuser.Shell
import kotlin.system.exitProcess

class MainSettings : MIUIActivity() {
    @Keep
    @SuppressLint("NonConstantResourceId")
    @BMMainPage(titleId = R.string.app_name, showBack = false)
    class MainPage: BasePage() {
        override fun onCreate() {
            val deviceTypes = mapOf(
                0 to R.string.default_type.string(),
                1 to R.string.android_phone.string(),
                2 to R.string.android_pad.string(),
            )
            TextWithSpinner(
                TextV(R.string.device_type.string()),
                SpinnerV(deviceTypes[activity.prefs().get(DataConst.deviceType)]!!) {
                    deviceTypes.forEach {
                        add(it.value) {
                            activity.prefs().edit { put(DataConst.deviceType, it.key) }
                            Shell.cmd(
                                "pkill -f com.milink.service",
                                "pkill -f com.xiaomi.mirror",
                                "pkill -f com.xiaomi.mi_connect_service",
                            ).exec()
                        }
                    }
                },
            )
            Text(
                textId = R.string.before_using_tips,
                textSize = 15f,
                colorId = R.color.tips_color,
            )
        }

        private fun Int.string() = getString(this)
    }
    init {
        registerPage(MainPage::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!YukiHookAPI.Status.isModuleActive)
            MIUIDialog(this) {
                setCancelable(false)
                setTitle(titleId = R.string.dialog_title)
                setMessage(R.string.dialog_content)
                setRButton(R.string.dialog_confirm) { exitProcess(0) }
            }.show()
    }
}