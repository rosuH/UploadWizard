package me.rosuh.desktop

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.awtTransferable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.rosuh.desktop.ui.theme.AppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import uploadwizard.composeapp.generated.resources.Res
import uploadwizard.composeapp.generated.resources.materialsymbolsdownload
import java.awt.datatransfer.DataFlavor

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
@Preview
fun App() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Upload the report", style = MaterialTheme.typography.displayMedium)
                        Text(
                            "Make sure the file format meets the requirements. it must be .doc or .pdf",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Image(painter = rememberVectorPainter(Icons.Default.Close), contentDescription = "close")
                    }
                }
                // upload drop area
                var showTargetBorder by remember { mutableStateOf(false) }
                var targetText by remember { mutableStateOf("Drop Here") }
                val coroutineScope = rememberCoroutineScope()
                val dragAndDropTarget = remember {
                    object : DragAndDropTarget {

                        // Highlights the border of a potential drop target
                        override fun onStarted(event: DragAndDropEvent) {
                            showTargetBorder = true
                        }

                        override fun onEnded(event: DragAndDropEvent) {
                            showTargetBorder = false
                        }

                        override fun onDrop(event: DragAndDropEvent): Boolean {
                            // Prints the type of action into system output every time
                            // a drag-and-drop operation is concluded.
                            println("Action at the target: ${event.action}")

                            val result = (targetText == "Drop here")

                            // Changes the text to the value dropped into the composable.
                            targetText = event.awtTransferable.let {
                                if (it.isDataFlavorSupported(DataFlavor.stringFlavor))
                                    it.getTransferData(DataFlavor.stringFlavor) as String
                                else
                                    it.transferDataFlavors.first().humanPresentableName
                            }

                            // Reverts the text of the drop target to the initial
                            // value after 2 seconds.
                            coroutineScope.launch {
                                delay(2000)
                                targetText = "Drop here"
                            }
                            return result
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(400 / 234f)
                        .then(
                            if (showTargetBorder)
                                Modifier.border(BorderStroke(3.dp, MaterialTheme.colorScheme.outline), MaterialTheme.shapes.medium)
                            else
                                Modifier.border(BorderStroke(3.dp, MaterialTheme.colorScheme.outlineVariant), MaterialTheme.shapes.medium)
                        )
                        .dragAndDropTarget(
                            // With "true" as the value of shouldStartDragAndDrop,
                            // drag-and-drop operations are enabled unconditionally.
                            shouldStartDragAndDrop = { true },
                            target = dragAndDropTarget
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.materialsymbolsdownload),
                        contentDescription = "upload",
                        modifier = Modifier.size(100.dp)
                    )
                    Text(text = "Drag & Drop", style = MaterialTheme.typography.displayMedium)
                    Row {
                        Text(text = "or ", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "choose a file",
                            style = MaterialTheme.typography.labelMedium,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable(onClick = { /*TODO*/ })
                        )
                    }
                }
            }
        }
    }
}