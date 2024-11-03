package me.rosuh.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import me.rosuh.desktop.data.MainViewModel
import org.koin.compose.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication
import org.koin.dsl.module

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "UploadWizard",
    ) {
        KoinApplication(koinConfiguration) {
            App()
        }
    }
}

val koinConfiguration: KoinAppDeclaration = {
    modules(module {
        single { MainViewModel() }
    })
}
