package com.fingoal.app
import com.fingoal.app.di.initKoin
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initKoin()

    return ComposeUIViewController {
        App()
    }
}