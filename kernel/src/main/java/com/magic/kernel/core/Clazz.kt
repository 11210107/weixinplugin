package com.magic.kernel.core

import android.app.Application
import android.webkit.ValueCallback
import org.json.JSONObject

object Clazz {
    val Boolean = Boolean::class.java
    val File = java.io.File::class.java
    val FileInputStream = java.io.FileInputStream::class.java
    val FileOutputStream = java.io.FileOutputStream::class.java
    val Int = Int::class.java
    val Short = Short::class.java
    val Double = Double::class.java
    val Iterator = java.util.Iterator::class.java
    val Long = Long::class.java
    val Map = Map::class.java
    val Set = Set::class.java
    val Object = Object::class.java
    val String = String::class.java
    val CharSequence = CharSequence::class.java
    val Throwable = Throwable::class.java

    val Activity = android.app.Activity::class.java
    val Application = android.app.Application::class.java
    val Fragment = androidx.fragment.app.Fragment::class.java
    val AdapterView = android.widget.AdapterView::class.java
    val AdapterView_OnItemClickListener = android.widget.AdapterView.OnItemClickListener::class.java
    val AttributeSet = android.util.AttributeSet::class.java
    val BaseAdapter = android.widget.BaseAdapter::class.java
    val Bundle = android.os.Bundle::class.java
    val Button = android.widget.Button::class.java
    val Callback = android.os.Handler.Callback::class.java
    val Configuration = android.content.res.Configuration::class.java
    val ContentValues = android.content.ContentValues::class.java
    val Context = android.content.Context::class.java
    val ContextMenu = android.view.ContextMenu::class.java
    val ContextMenuInfo = android.view.ContextMenu.ContextMenuInfo::class.java
    val HeaderViewListAdapter = android.widget.HeaderViewListAdapter::class.java
    val Intent = android.content.Intent::class.java
    val KeyEvent = android.view.KeyEvent::class.java
    val ListAdapter = android.widget.ListAdapter::class.java
    val ListView = android.widget.ListView::class.java
    val Menu = android.view.Menu::class.java
    val Message = android.os.Message::class.java
    val MotionEvent = android.view.MotionEvent::class.java
    val Notification = android.app.Notification::class.java
    val NotificationManager = android.app.NotificationManager::class.java
    val View = android.view.View::class.java
    val ViewGroup = android.view.ViewGroup::class.java
    val LayoutInflater = android.view.LayoutInflater::class.java

    val Cursor = android.database.Cursor::class.java

    val ByteArray = ByteArray::class.java
    val IntArray = IntArray::class.java
    val ShortArray = ShortArray::class.java
    var LongArray = kotlin.LongArray::class.java
    val ObjectArray = Array<Any>::class.java
    val StringArray = Array<String>::class.java
    val JSONObject = JSONObject::class.java
    val ValueCallback = ValueCallback::class.java

    fun arrayOf(clazz: Class<*>): Class<*> = java.lang.reflect.Array.newInstance(clazz, 0).javaClass

}