package com.gswxxn.unlockmilink.dexkit.base

import android.content.Context
import com.gswxxn.unlockmilink.BuildConfig
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.java.JavaClass
import com.highcapable.yukihookapi.hook.type.java.JavaFieldClass
import com.highcapable.yukihookapi.hook.type.java.JavaMethodClass
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.matchers.ClassMatcher
import org.luckypray.dexkit.query.matchers.FieldMatcher
import org.luckypray.dexkit.query.matchers.MethodMatcher
import org.luckypray.dexkit.wrap.DexClass
import org.luckypray.dexkit.wrap.DexField
import org.luckypray.dexkit.wrap.DexMethod
import java.lang.reflect.Field
import java.lang.reflect.Method

/** DexKit 工具类 **/
object DexKitHelper {
    /**
     * 加载 Finder, 将 DexKitBridge 实例传递给 Finder
     *
     * @param finder 被操作的 Finder
     */
    fun DexKitBridge.loadFinder(finder: BaseFinder) {
        finder.bridge = this
        BaseFinder.finders += finder
    }

    /**
     * 查找方法并返回实例, 如果查找的结果为多个, 则抛出异常
     */
    fun BaseFinder.findUniqueMethod(init: MethodMatcher.() -> Unit): Method =
        bridge.findMethod { matcher(init) }.singleOrThrow {
            Throwable(
                "findUniqueMethod() Error: Result must contain exactly one item"
            )
        }.getMethodInstance(classLoader)

    /**
     * 查找字段并返回实例, 如果查找的结果为多个, 则抛出异常
     */
    fun BaseFinder.findUniqueField(init: FieldMatcher.() -> Unit): Field =
        bridge.findField { matcher(init) }.singleOrThrow {
            Throwable(
                "findUniqueField() Error: Result must contain exactly one item"
            )
        }.getFieldInstance(classLoader)

    /**
     * 查找类并返回实例, 如果查找的结果为多个, 则抛出异常
     */
    fun BaseFinder.findUniqueClass(init: ClassMatcher.() -> Unit): Class<*> =
        bridge.findClass { matcher(init) }.singleOrThrow {
            Throwable(
                "findUniqueClass() Error: Result must contain exactly one item"
            )
        }.getInstance(classLoader)

    /**
     * 将对象的成员信息存储到 SharedPreferences 中
     *
     * @param context 上下文对象，用于获取 SharedPreferences
     * @param obj 被存储的对象
     */
    fun BaseHookerWithDexKit.storeMembers(context: Context, obj: Any) {
        context.getSharedPreferences("${YLog.Configs.tag}_anti_obfuscation", Context.MODE_PRIVATE).edit().apply {
            clear()

            appVersionCode?.let { putLong("app_version_code", it) }
            appVersionName?.let { putString("app_version_name", it) }
            putString("module_version_name", BuildConfig.VERSION_NAME)
            putInt("module_version_code", BuildConfig.VERSION_CODE)

            obj.javaClass.declaredFields.forEach { field ->
                val key = field.name
                val value = when (field.type) {
                    JavaClass -> DexClass(field.get(null) as Class<*>).toString()
                    JavaMethodClass -> DexMethod(field.get(null) as Method).toString()
                    JavaFieldClass -> DexField(field.get(null) as Field).toString()
                    else -> null
                }
                value?.let { putString(key, it) }
            }
        }.apply()
    }

    /**
     * 从 SharedPreferences 中读取对象的成员信息
     *
     * @param context 上下文对象，用于获取 SharedPreferences
     * @param obj 被存储的对象
     * @return 是否成功读取
     */
    fun BaseHookerWithDexKit.loadMembers(context: Context, obj: Any): Boolean {
        val pref = context.getSharedPreferences("${YLog.Configs.tag}_anti_obfuscation", Context.MODE_PRIVATE)

        val isVersionSame = try {
            pref.getLong("app_version_code", 0) == appVersionCode &&
                    pref.getString("app_version_name", "") == appVersionName &&
                    pref.getString("module_version_name", "") == BuildConfig.VERSION_NAME &&
                    pref.getInt("module_version_code", 0) == BuildConfig.VERSION_CODE
        } catch (e: Exception) {
            YLog.error(msg = "failed to read app or module versions", e = e)
            return false
        }

        if (!isVersionSame) return false

        for (field in obj.javaClass.declaredFields) {
            val key = field.name
            val value = pref.getString(key, "")!!

            if (field.type !in arrayOf(JavaClass, JavaMethodClass, JavaFieldClass)) continue

            if (value.isEmpty()) {
                YLog.error(msg = "failed to load ${key}, pref empty")
                return false
            }

            val instance = try {
                when (field.type) {
                    JavaClass -> DexClass(value).getInstance(appClassLoader!!)
                    JavaMethodClass -> DexMethod(value).getMethodInstance(appClassLoader!!).apply { isAccessible = true }
                    JavaFieldClass -> DexField(value).getFieldInstance(appClassLoader!!).apply { isAccessible = true }
                    else -> null
                }
            } catch (e: ReflectiveOperationException) {
                YLog.error(msg = "failed to load ${key}, no such members", e = e)
                return false
            }

            field.set(null, instance)
        }
        return true
    }
}