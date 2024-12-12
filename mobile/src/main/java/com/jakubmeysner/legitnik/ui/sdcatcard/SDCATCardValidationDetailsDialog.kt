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
import eu.europa.esig.dss.enumerations.SubIndication
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

    val issuerMatchesCertificateSubjectLabel = stringResource(
        R.string.sdcat_card_validation_details_dialog_issuer_matches_certificate_subject_label
    )

    val certificateSubjectAuthorizedLabel = stringResource(
        R.string.sdcat_card_validation_details_dialog_certificate_subject_authorized_label
    )

    val notExpiredLabel = stringResource(
        R.string.sdcat_card_validation_details_dialog_not_expired_label
    )

    val yesIcon = @Composable {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = stringResource(
                R.string.sdcat_card_validation_details_dialog_yes
            ),
            tint = MaterialTheme.colorScheme.primary,
        )
    }

    val noIcon = @Composable {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(
                R.string.sdcat_card_validation_details_dialog_no
            ),
            tint = MaterialTheme.colorScheme.error,
        )
    }

    val fields = listOfNotNull<Pair<String, @Composable () -> Unit>>(
        signatureValidLabel to if (validationResult.signatureValid) yesIcon else noIcon,
        issuerMatchesCertificateSubjectLabel to if (
            validationResult.issuerMatchesCertificateSubject
        ) yesIcon else noIcon,
        certificateSubjectAuthorizedLabel to if (
            validationResult.certificateSubjectAuthorized
        ) yesIcon else noIcon,
        notExpiredLabel to if (validationResult.notExpired) {
            yesIcon
        } else noIcon,
    )

    val simpleReport = validationResult.signatureValidationReports.simpleReport
    val firstSignatureId = simpleReport.firstSignatureId
    val indication = simpleReport.getIndication(firstSignatureId)
    val subIndication: SubIndication? = simpleReport.getSubIndication(firstSignatureId)
    val detailedReport = validationResult.signatureValidationReports.detailedReport

    val signatureValidationDetailsFields: List<Pair<String, String>> = listOfNotNull(
        Pair(
            stringResource(R.string.sdcat_card_validation_details_indication),
            indication.name
        ),
        subIndication?.let {
            Pair(
                stringResource(R.string.sdcat_card_validation_details_sub_indication),
                it.name
            )
        },
    ).plus(
        listOf(
            Pair(
                R.string.sdcat_card_validation_details_ades_errors,
                detailedReport.getAdESValidationErrors(firstSignatureId)
            ),
            Pair(
                R.string.sdcat_card_validation_details_ades_warnings,
                detailedReport.getAdESValidationWarnings(firstSignatureId)
            ),
            Pair(
                R.string.sdcat_card_validation_details_ades_infos,
                detailedReport.getAdESValidationInfos(firstSignatureId)
            ),
            Pair(
                R.string.sdcat_card_validation_details_qualification_errors,
                detailedReport.getQualificationErrors(firstSignatureId)
            ),
            Pair(
                R.string.sdcat_card_validation_details_qualification_warnings,
                detailedReport.getQualificationWarnings(firstSignatureId)
            ),
            Pair(
                R.string.sdcat_card_validation_details_qualification_infos,
                detailedReport.getQualificationInfos(firstSignatureId)
            ),
        ).mapNotNull { pair ->
            if (pair.second.isNotEmpty()) {
                Pair(
                    stringResource(pair.first),
                    pair.second.joinToString(
                        prefix = "\u2022 ",
                        separator = "\n\u2022 ",
                    ) { it.value },
                )
            } else {
                null
            }
        }
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
            .joinToString("\n") { it.split("=").joinToString(" = ") },
        certificateIssuerLabel to certificate.issuerX500Principal.name.split(",")
            .joinToString("\n") { it.split("=").joinToString(" = ") },
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
                        R.string.sdcat_card_validation_details_signature_validation_title
                    ),
                    style = MaterialTheme.typography.titleMedium,
                )

                TextFieldsList(
                    fields = signatureValidationDetailsFields,
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
