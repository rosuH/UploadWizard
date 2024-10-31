package me.rosuh.desktop.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import uploadwizard.composeapp.generated.resources.Alatsi_Regular
import uploadwizard.composeapp.generated.resources.Res


@Composable
fun displayFontFamily() = FontFamily(
    Font(
        resource = Res.font.Alatsi_Regular,
        weight = FontWeight.W400,
        style = FontStyle.Normal,
    )
)

@Composable
fun bodyFontFamily() = FontFamily(
    Font(
        resource = Res.font.Alatsi_Regular,
        weight = FontWeight.W400,
        style = FontStyle.Normal,
    )
)