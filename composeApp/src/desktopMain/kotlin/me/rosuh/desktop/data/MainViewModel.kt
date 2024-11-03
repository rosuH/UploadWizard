package me.rosuh.desktop.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel {

    private val scope = CoroutineScope(Dispatchers.Default)

    val mainState = MainState()

    fun processAction(action: UIAction) {
        when (action) {
            is UIAction.Upload -> {
                scope.launch {
                    withContext(Dispatchers.Main) {
                        updateMainState {
                            isUploading = true
                        }
                    }
                    while (mainState.progress < 100) {
                        println("progress: ${mainState.progress}")
                        delay(300)
                        updateMainState {
                            progress += 10
                        }
                    }
                    withContext(Dispatchers.Main) {
                        updateMainState {
                            isUploading = false
                            isUploadSuccess = true
                            progress = 0
                        }
                    }
                }
            }
        }
    }

    private suspend fun updateMainState(reducer: MainState.() -> Unit) {
        coroutineScope {
            mainState.reducer()
        }
    }
}


@Stable
class MainState(
    isUploading: Boolean = false,
    isUploadSuccess: Boolean = false,
    isUploadFailed: Boolean = false,
    progress: Int = 0
) {
    var isUploading by mutableStateOf(isUploading)
    var isUploadSuccess by mutableStateOf(isUploadSuccess)
    var isUploadFailed by mutableStateOf(isUploadFailed)
    var progress by mutableStateOf(progress)
}

sealed class UIAction {
    data class Upload(val fileList: List<String>) : UIAction()
}