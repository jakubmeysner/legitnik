package com.jakubmeysner.legitnik.ui.sdcatcard

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun TextFieldsList(
    fields: List<Pair<String, String>>,
    labelColor: Color = LocalContentColor.current,
    labelBeforeValue: Boolean = false,
) {
    FieldsList(
        fields = fields.map {
            it.first to {
                Text(text = it.second)
            }
        },
        labelColor = labelColor,
        labelBeforeValue = labelBeforeValue,
    )
}
