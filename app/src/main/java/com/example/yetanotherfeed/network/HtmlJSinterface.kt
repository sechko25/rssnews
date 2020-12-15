package com.example.yetanotherfeed.network

import java.util.*

class HtmlJSInterface : Observable() {

    var html: String? = null
        set(html) {
            field = html
            setChanged()
            notifyObservers(html)
        }
}