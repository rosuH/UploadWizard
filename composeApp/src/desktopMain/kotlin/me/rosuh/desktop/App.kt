package me.rosuh.desktop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.awtTransferable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.rosuh.desktop.data.MainViewModel
import me.rosuh.desktop.data.UIAction
import me.rosuh.desktop.ui.theme.AppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import uploadwizard.composeapp.generated.resources.Res
import uploadwizard.composeapp.generated.resources.materialsymbolsdownload
import java.awt.Point
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
@Preview
fun App(viewModel: MainViewModel = koinInject()) {
    AppTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            Column(
                Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Upload the report", style = MaterialTheme.typography.displayMedium)
                        Text(
                            "Make sure the file format meets the requirements. it must be .doc or .pdf",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Image(
                            painter = rememberVectorPainter(Icons.Default.Close),
                            contentDescription = "close"
                        )
                    }
                }
                // upload drop area
                var showTargetBorder by remember { mutableStateOf(false) }
                var targetText by remember { mutableStateOf("Drop Here") }
                var dropPointX = remember { mutableStateOf(0f) }
                var dropPointY = remember { mutableStateOf(0f) }
                val scale by animateFloatAsState(if (showTargetBorder) 1.1f else 1.0f)
                var fileList by remember { mutableStateOf(emptyList<String>()) }
                val textAlpha by animateFloatAsState(if (viewModel.mainState.isUploading) 0f else 1f)
                val textScale by animateFloatAsState(if (viewModel.mainState.isUploading) 1.5f else 1.0f)
                val displayAlpha by remember { mutableStateOf(1f) }
                val dragAndDropTarget = remember {
                    object : DragAndDropTarget {

                        // Highlights the border of a potential drop target
                        override fun onStarted(event: DragAndDropEvent) {
                            // get cursor position
                            showTargetBorder = true
                        }

                        override fun onEnded(event: DragAndDropEvent) {
                            showTargetBorder = false
                            dropPointX.value =
                                (event.nativeEvent as DropTargetDropEvent).location.x.toFloat()
                            dropPointY.value =
                                (event.nativeEvent as DropTargetDropEvent).location.y.toFloat()
                        }

                        override fun onMoved(event: DragAndDropEvent) {
                            super.onMoved(event)
                            println("Moved: ${(event.nativeEvent as DropTargetDragEvent).location}")
                            dropPointX.value =
                                (event.nativeEvent as DropTargetDragEvent).location.x.toFloat()
                            dropPointY.value =
                                (event.nativeEvent as DropTargetDragEvent).location.y.toFloat()
                        }

                        override fun onDrop(event: DragAndDropEvent): Boolean {
                            val result = (targetText == "Drop here")
                            fileList = event.awtTransferable.let {
                                it.getTransferData(DataFlavor.javaFileListFlavor) as? List<String>
                                    ?: emptyList()
                            }
                            viewModel.processAction(UIAction.Upload(fileList))
                            return viewModel.mainState.isUploading.not()
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(400 / 234f)
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .then(
                            if (showTargetBorder)
                                Modifier.border(
                                    BorderStroke(
                                        3.dp,
                                        MaterialTheme.colorScheme.outline
                                    ), MaterialTheme.shapes.medium
                                )
                            else
                                Modifier.border(
                                    BorderStroke(
                                        3.dp,
                                        MaterialTheme.colorScheme.outlineVariant
                                    ), MaterialTheme.shapes.medium
                                )
                        )
                        .dragAndDropTarget(
                            // With "true" as the value of shouldStartDragAndDrop,
                            // drag-and-drop operations are enabled unconditionally.
                            shouldStartDragAndDrop = { true },
                            target = dragAndDropTarget
                        ).onPointerEvent(eventType = PointerEventType.Enter, onEvent = {
                            println("Enter")
                        }).onPointerEvent(eventType = PointerEventType.Exit, onEvent = {
                            println("Exit")
                        })
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = !viewModel.mainState.isUploading,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.materialsymbolsdownload),
                                contentDescription = "upload",
                                modifier = Modifier.size(100.dp).graphicsLayer(alpha = displayAlpha)
                            )
                            Text(
                                text = "Drag & Drop",
                                style = MaterialTheme.typography.displayMedium,
                                modifier = Modifier.graphicsLayer(
                                    alpha = textAlpha,
                                    scaleX = textScale,
                                    scaleY = textScale
                                )
                            )
                            Row {
                                Text(text = "or ", style = MaterialTheme.typography.labelMedium)
                                Text(
                                    text = "choose a file",
                                    style = MaterialTheme.typography.labelMedium,
                                    textDecoration = TextDecoration.Underline,
                                    modifier = Modifier.clickable(onClick = { /*TODO*/ })
                                        .graphicsLayer(alpha = displayAlpha)
                                )
                            }
                        }
                    }
                    androidx.compose.animation.AnimatedVisibility(
                        visible = viewModel.mainState.isUploading,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            NeonProgressIndicator(
                                modifier = Modifier.fillMaxWidth().padding(16.dp).padding(16.dp),
                                progress = viewModel.mainState.progress.toFloat() / 100
                            )
                            Text(
                                text = "${viewModel.mainState.progress}%", // Replace with actual progress value
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                text = "Uploading...",
                                style = MaterialTheme.typography.displaySmall,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NeonProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 100) // 动画持续时间
    )

    Box(modifier) {
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}