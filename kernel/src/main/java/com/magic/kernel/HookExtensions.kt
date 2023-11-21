package com.magic.kernel

import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

fun Class<*>.callStaticMethod(method: String, vararg args: Any?): Any? {
    return XposedHelpers.callStaticMethod(this, method, *args)
}

fun Any.callMethod(method: String, vararg args: Any?): Any? {
    return XposedHelpers.callMethod(this, method, *args)
}

fun String.newProxyInstance(handler: InvocationHandler): Any {
    return Proxy.newProxyInstance(MagicGlobal.classLoader,
        arrayOf(XposedHelpers.findClass(this, MagicGlobal.classLoader)), handler
    )
}

fun String.findClass(): Class<*> {
    return XposedHelpers.findClass(this, MagicGlobal.classLoader)
}

fun Any.getObjectField(fieldName: String): Any?{
    return XposedHelpers.getObjectField(this, fieldName)
}

fun Any.setObjectField(fieldName: String, value: Any){
    XposedHelpers.setObjectField(this, fieldName, value)
}

fun Any.getBooleanField(fieldName: String): Boolean{
    return XposedHelpers.getBooleanField(this, fieldName)
}

fun Any.setBooleanField(fieldName: String, value: Boolean){
    XposedHelpers.setBooleanField(this, fieldName, value)
}

fun Any.getByteField(fieldName: String): Byte{
    return XposedHelpers.getByteField(this, fieldName)
}

fun Any.setByteField(fieldName: String, value: Byte){
    XposedHelpers.setByteField(this, fieldName, value)
}

fun Any.getCharField(fieldName: String): Char{
    return XposedHelpers.getCharField(this, fieldName)
}

fun Any.setCharField(fieldName: String, value: Char){
    XposedHelpers.setCharField(this, fieldName, value)
}

fun Any.getDoubleField(fieldName: String): Double{
    return XposedHelpers.getDoubleField(this, fieldName)
}

fun Any.setDoubleField(fieldName: String, value: Double){
    XposedHelpers.setDoubleField(this, fieldName, value)
}

fun Any.getFloatField(fieldName: String): Float{
    return XposedHelpers.getFloatField(this, fieldName)
}

fun Any.setFloatField(fieldName: String, value: Float){
    XposedHelpers.setFloatField(this, fieldName, value)
}

fun Any.getIntField(fieldName: String): Int{
    return XposedHelpers.getIntField(this, fieldName)
}

fun Any.setIntField(fieldName: String, value: Int){
    XposedHelpers.setIntField(this, fieldName, value)
}

fun Any.getLongField(fieldName: String): Long{
    return XposedHelpers.getLongField(this, fieldName)
}

fun Any.setLongField(fieldName: String, value: Long){
    XposedHelpers.setLongField(this, fieldName, value)
}

fun Any.getShortField(fieldName: String): Short{
    return XposedHelpers.getShortField(this, fieldName)
}

fun Any.setShortField(fieldName: String, value: Short){
    XposedHelpers.setShortField(this, fieldName, value)
}

fun Class<*>.getStaticObjectField(fieldName: String): Any?{
    return XposedHelpers.getStaticObjectField(this, fieldName)
}

fun Class<*>.setStaticObjectField(fieldName: String, value: Any){
    XposedHelpers.setStaticObjectField(this, fieldName, value)
}

fun Class<*>.getStaticBooleanField(fieldName: String): Boolean{
    return XposedHelpers.getStaticBooleanField(this, fieldName)
}

fun Class<*>.setStaticBooleanField(fieldName: String, value: Boolean){
    XposedHelpers.setStaticBooleanField(this, fieldName, value)
}

fun Class<*>.getStaticByteField(fieldName: String): Byte{
    return XposedHelpers.getStaticByteField(this, fieldName)
}

fun Class<*>.setStaticByteField(fieldName: String, value: Byte){
    XposedHelpers.setStaticByteField(this, fieldName, value)
}

fun Class<*>.getStaticCharField(fieldName: String): Char{
    return XposedHelpers.getStaticCharField(this, fieldName)
}

fun Class<*>.setStaticCharField(fieldName: String, value: Char){
    XposedHelpers.setStaticCharField(this, fieldName, value)
}

fun Class<*>.getStaticDoubleField(fieldName: String): Double{
    return XposedHelpers.getStaticDoubleField(this, fieldName)
}

fun Class<*>.setStaticDoubleField(fieldName: String, value: Double){
    XposedHelpers.setStaticDoubleField(this, fieldName, value)
}

fun Class<*>.getStaticFloatField(fieldName: String): Float{
    return XposedHelpers.getStaticFloatField(this, fieldName)
}

fun Class<*>.setStaticFloatField(fieldName: String, value: Float){
    XposedHelpers.setStaticFloatField(this, fieldName, value)
}

fun Class<*>.getStaticIntField(fieldName: String): Int{
    return XposedHelpers.getStaticIntField(this, fieldName)
}

fun Class<*>.setStaticIntField(fieldName: String, value: Int){
    XposedHelpers.setStaticIntField(this, fieldName, value)
}

fun Class<*>.getStaticLongField(fieldName: String): Long{
    return XposedHelpers.getStaticLongField(this, fieldName)
}

fun Class<*>.setStaticLongField(fieldName: String, value: Long){
    XposedHelpers.setStaticLongField(this, fieldName, value)
}

fun Class<*>.getStaticShortField(fieldName: String): Short{
    return XposedHelpers.getStaticShortField(this, fieldName)
}

fun Class<*>.setStaticShortField(fieldName: String, value: Short){
    XposedHelpers.setStaticShortField(this, fieldName, value)
}