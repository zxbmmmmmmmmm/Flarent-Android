package com.bettafish.flarent.utils

import androidx.compose.ui.graphics.Color
import com.guru.fontawesomecomposelib.FaIconType
import com.guru.fontawesomecomposelib.FaIcons

fun String.toComposeColor(): Color? {
    try{
        if(this == ""){
            return null
        }
        return Color(this.removePrefix("#").toLong(16) or 0x00000000FF000000)
    }
    catch(ignored: Exception){
        return null
    }
}

fun String.toFaIcon(): FaIconType? {
    if (this.isBlank()) return null

    // 找到形如 "fa-xxx" 的片段（优先），否则用最后一个 token
    val parts = this.split(" ")
    val faPart = parts.find { it.startsWith("fa-") && it != "fa" } ?: parts.last()

    val base = faPart.removePrefix("fa-")
    val pascal = base.split('-').joinToString("") { it.replaceFirstChar { c -> c.uppercase() } }

    val clazz = FaIcons::class.java
    val candidates = listOf(pascal, pascal.replaceFirstChar { it.uppercase() }, base)
    val instance: Any? = try {
        clazz.getField("INSTANCE").get(null)
    } catch (ignored: Exception) {
        null
    }

    for (cand in candidates) {
        try {
            val getter = clazz.getMethod("get${cand}")
            return getter.invoke(instance) as FaIconType
        } catch (ignored: Exception) {}
    }
    return null
}