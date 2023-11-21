package com.magic.xmagichooker

import com.magic.kernel.MagicGlobal
import com.magic.kernel.newProxyInstance
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.InvocationHandler

interface IBaseClass {
    var name: String

    fun getClazz(): Class<*> = XposedHelpers.findClass(name, MagicGlobal.classLoader)

    fun newInstance(vararg args: Any?): Any = XposedHelpers.newInstance(getClazz(), *args)

    fun newProxyInstance(handler: InvocationHandler): Any = name.newProxyInstance(handler)
}