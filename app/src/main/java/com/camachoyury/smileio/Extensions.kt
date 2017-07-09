package com.camachoyury.smileio

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

/**
 * Created by yury on 6/24/17.
 */


fun Activity.toast(message: CharSequence, duration: Int = Toast.LENGTH_LONG){
    Toast.makeText(this,message,duration)
}




public fun Any?.flags(flag: Int, vararg flags: Int): Int {
    var orFlags = flag;
    for (i in flags) {
        orFlags = orFlags or i
    }
    return orFlags
}

inline public fun <reified T : Activity> Activity.start() {
    startActivity(intent<T>())
}

inline public fun <reified T : Activity> Activity.start(flags: Int) {
    this.startActivity(intent<T>(flags))
}

inline public fun <reified T: Activity> Activity.start(extras: Bundle) {
    this.startActivity(intent<T>(extras))
}

inline public fun <reified T: Activity> Activity.start(extras: Bundle, flags: Int) {
    this.startActivity(intent<T>(extras, flags))
}



inline public fun <reified T: Context> Context.intent(): Intent {
    return Intent(this, T::class.java)
}

inline public fun <reified T: Context> Context.intent(flags: Int): Intent {
    val intent = intent<T>()
    intent.setFlags(flags)
    return intent
}

inline public fun <reified T: Context> Context.intent(extras: Bundle): Intent {
    return intent<T>(extras, 0)
}

inline public fun <reified T: Context> Context.intent(extras: Bundle, flags: Int): Intent {
    val intent = intent<T>(flags)
    intent.putExtras(extras)
    return intent
}