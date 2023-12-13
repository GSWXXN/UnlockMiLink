package com.gswxxn.unlockmilink.dexkit.member

import java.lang.reflect.Field
import java.lang.reflect.Method

object MirrorMember {
    lateinit var MAX_SCREEN_COUNT: Field

    lateinit var DisplayManagerImpl_openDisplay: Method

    lateinit var DeviceUtils_isPadDevice: Method

    lateinit var SystemUtils_isModelSupport: Method

    lateinit var PcAppendView_init: Method
}