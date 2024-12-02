package com.jakubmeysner.legitnik.ui.sdcatcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FieldsList(
    fields: List<Pair<String, @Composable () -> Unit>>,
    labelColor: Color = LocalContentColor.current,
    labelBeforeValue: Boolean = false,
) {
    for ((label, Value) in fields) {
        key(label) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(space = 4.dp),
            ) {
                val Label = @Composable {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = labelColor,
                    )
                }

                if (labelBeforeValue) {
                    Label()
                }

                ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) {
                    Value()
                }

                if (!labelBeforeValue) {
                    Label()
                }
            }
        }
    }
}
