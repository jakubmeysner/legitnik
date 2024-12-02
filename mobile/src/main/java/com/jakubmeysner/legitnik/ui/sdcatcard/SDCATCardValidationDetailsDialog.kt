package com.jakubmeysner.legitnik.ui.sdcatcard

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardValidationResult
import java.security.cert.X509Certificate

@Composable
fun SDCATCardValidationDetailsDialog(
    certificate: X509Certificate,
    validationResult: SDCATCardValidationResult,
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val dateFormat = remember(context) { DateFormat.getDateFormat(context) }

    val signatureValidLabel = stringResource(
        R.string.sdcat_card_validation_details_dialog_valid_qualified_signature
    )

    val signatureStatusDetailsLabel = stringResource(
        R.string.sdcat_card_validation_details_dialog_qualified_signature_status_details
    )

    val issuerMatchesCertificateSubjectLabel = stringResource(
        R.string.sdcat_card_validation_details_dialog_issuer_matches_certificate_subject_label
    )

    val certificateSubjectAuthorizedLabel = stringResource(
        R.string.sdcat_card_validation_details_dialog_certificate_subject_authorized_label
    )

    val notExpiredLabel = stringResource(
        R.string.sdcat_card_validation_details_dialog_not_expired_label
    )

    val YesIcon = @Composable {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = stringResource(
                R.string.sdcat_card_validation_details_dialog_yes
            ),
            tint = MaterialTheme.colorScheme.primary,
        )
    }

    val NoIcon = @Composable {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(
                R.string.sdcat_card_validation_details_dialog_no
            ),
            tint = MaterialTheme.colorScheme.error,
        )
    }

    val fields = listOfNotNull<Pair<String, @Composable () -> Unit>>(
        signatureValidLabel to if (validationResult.signatureValid) YesIcon else NoIcon,
        validationResult.signatureSubIndication?.let {
            signatureStatusDetailsLabel to @Composable {
                Text(
                    text = it.toString(),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        },
        issuerMatchesCertificateSubjectLabel to if (
            validationResult.issuerMatchesCertificateSubject
        ) YesIcon else NoIcon,
        certificateSubjectAuthorizedLabel to if (
            validationResult.certificateSubjectAuthorized
        ) YesIcon else NoIcon,
        notExpiredLabel to if (validationResult.notExpired) YesIcon else NoIcon,
    )

    val certificateValidityLabel = stringResource(
        R.string.sdcat_card_validation_details_dialog_certificate_validity
    )

    val certificateSubjectLabel = stringResource(
        R.string.sdcat_card_validation_details_dialog_certificate_subject
    )

    val certificateIssuerLabel = stringResource(
        R.string.sdcat_card_validation_details_dialog_certificate_issuer
    )

    val certificateFields = listOfNotNull(
        certificateValidityLabel to "${dateFormat.format(certificate.notBefore)}-${
            dateFormat.format(certificate.notAfter)
        }",
        certificateSubjectLabel to certificate.subjectDN.name.split(",")
            .map { it.split("=").joinToString(" = ") }
            .joinToString("\n"),
        certificateIssuerLabel to certificate.issuerX500Principal.name.split(",")
            .map { it.split("=").joinToString(" = ") }
            .joinToString("\n"),
    )

    Dialog(onDismissRequest = onClose) {
        Card {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(all = 16.dp),
                verticalArrangement = Arrangement.spacedBy(space = 16.dp),
            ) {
                Text(
                    text = stringResource(R.string.sdcat_card_validation_details_dialog_title),
                    style = MaterialTheme.typography.titleLarge,
                )

                FieldsList(
                    fields = fields,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    labelBeforeValue = true,
                )

                Text(
                    text = stringResource(
                        R.string.sdcat_card_validation_details_dialog_certificate_title
                    ),
                    style = MaterialTheme.typography.titleMedium,
                )

                TextFieldsList(
                    fields = certificateFields,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    labelBeforeValue = true,
                )
            }
        }
    }
}
