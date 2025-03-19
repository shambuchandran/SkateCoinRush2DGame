package org.example.skatecoinrush

import android.app.Application
import org.example.skatecoinrush.di.initializeKoin

class MyApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        initializeKoin()
    }

}