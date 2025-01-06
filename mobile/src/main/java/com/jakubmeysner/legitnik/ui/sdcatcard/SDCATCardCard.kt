package com.jakubmeysner.legitnik.ui.sdcatcard

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardParsedContent
import java.util.TimeZone

@Composable
fun SDCATCardCard(
    content: SDCATCardParsedContent,
    valid: Boolean?,
    isSaved: Boolean,
    default: Boolean? = null,
    saveCard: () -> Unit,
    removeCard: () -> Unit,
    toggleDefault: (() -> Unit)? = null,
    onShowValidationDetails: () -> Unit,
) {
    val context = LocalContext.current

    val gmtDateFormat = remember(context) {
        DateFormat.getDateFormat(context).apply {
            timeZone = TimeZone.getTimeZone("GMT")
        }
    }

    val givenNames = content.givenNames.joinToString(separator = " ")
    val surname = content.surname.joinToString(separator = " ")
    val initials = "${givenNames.first()}${surname.first()}"
    val fullName = "$givenNames $surname"

    val type = stringResource(
        when (content) {
            is SDCATCardParsedContent.StudentCardParsedContent -> R.string.sdcat_card_card_student
            is SDCATCardParsedContent.DoctoralCandidateCardParsedContent -> R.string.sdcat_card_card_doctoral_candidate
            is SDCATCardParsedContent.AcademicTeacherCardParsedContent -> R.string.sdcat_card_card_academic_teacher
        }
    )

    val universityOrIssuerNameLabel = stringResource(
        when (content) {
            is SDCATCardParsedContent.DoctoralCandidateCardParsedContent -> R.string.sdcat_card_card_university_or_issuer
            else -> R.string.sdcat_card_card_university
        }
    )

    val albumOrCardNumberLabel = stringResource(
        when (content) {
            is SDCATCardParsedContent.StudentCardParsedContent -> R.string.sdcat_card_card_album_number
            else -> R.string.sdcat_card_card_card_number
        }
    )

    val editionNumberLabel = stringResource(R.string.sdcat_card_card_edition_number)
    val peselNumberLabel = stringResource(R.string.sdcat_card_card_pesel_number)
    val expiryDateLabel = stringResource(R.string.sdcat_card_card_expiry_date)
    val issueDateLabel = stringResource(R.string.sdcat_card_card_issue_date)

    val fields = listOfNotNull(
        universityOrIssuerNameLabel to content.universityOrIssuerName,
        albumOrCardNumberLabel to content.albumOrCardNumber,
        editionNumberLabel to content.editionNumber,
        if (content is SDCATCardParsedContent.SDCATCardParsedContentWithPeselNumber) {
            peselNumberLabel to content.peselNumber
        } else null,
        expiryDateLabel to gmtDateFormat.format(content.expiryDate),
        if (content is SDCATCardParsedContent.SDCATCardParsedContentWithIssueDate) {
            issueDateLabel to gmtDateFormat.format(content.issueDate)
        } else null,
    )

    Card(
        colors = if (valid != false) CardDefaults.cardColors() else CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .height(height = 48.dp)
                        .aspectRatio(1f)
                        .clip(shape = RoundedCornerShape(percent = 100))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = initials,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }

                Column(
                    modifier = Modifier.weight(weight = 1f),
                    verticalArrangement = Arrangement.spacedBy(space = 4.dp),
                ) {
                    Text(
                        text = fullName,
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Text(
                        text = type,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }

                if (valid != null) {
                    FilledTonalIconButton(
                        onClick = {
                            onShowValidationDetails()
                        },
                        colors = if (valid) IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ) else IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                        )
                    ) {
                        Icon(
                            imageVector = if (valid) Icons.Default.Check else ImageVector
                                .vectorResource(R.drawable.mdi_alert_octagram_outline),
                            contentDescription = if (valid) stringResource(
                                R.string.sdcat_card_card_valid
                            ) else stringResource(
                                R.string.sdcat_card_card_invalid
                            ),
                        )
                    }
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(size = 32.dp),
                    )
                }
            }

            TextFieldsList(
                fields = fields,
                labelColor = if (valid != false) MaterialTheme.colorScheme.onSurfaceVariant
                else LocalContentColor.current,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (default != null) {
                    if (default) {
                        Button(
                            onClick = toggleDefault ?: {},
                            enabled = toggleDefault != null,
                        ) {
                            Text(text = stringResource(R.string.sdcat_card_card_default))
                        }
                    } else {
                        OutlinedButton(
                            onClick = toggleDefault ?: {},
                            enabled = toggleDefault != null,
                        ) {
                            Text(text = stringResource(R.string.sdcat_card_card_mark_as_default))
                        }
                    }
                } else {
                    TextButton(
                        onClick = onShowValidationDetails,
                        enabled = valid != null,
                    ) {
                        Text(stringResource(R.string.sdcat_card_card_validation_details_button))
                    }
                }

                if (isSaved) {
                    OutlinedButton(
                        onClick = removeCard,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                    ) {
                        Text(
                            stringResource(
                                R.string.sdcat_card_card_remove_button_text
                            )
                        )
                    }
                } else {
                    Button(
                        onClick = saveCard,
                        enabled = valid ?: false
                    ) {
                        Text(
                            stringResource(
                                R.string.sdcat_card_card_save_button_text
                            )
                        )
                    }
                }
            }
        }
    }
}
