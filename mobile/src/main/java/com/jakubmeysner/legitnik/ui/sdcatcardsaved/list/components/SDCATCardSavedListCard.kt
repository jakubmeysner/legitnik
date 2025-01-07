package com.jakubmeysner.legitnik.ui.sdcatcardsaved.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardDataEntity
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardParsedContent
import java.util.UUID

@Composable
fun SDCATCardSavedListCard(
    data: SDCATCardDataEntity,
    navigateToSDCATCardSavedDetails: (id: UUID) -> Unit,
) {
    val cardColors = if (data.rawData.default == true) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    } else {
        CardDefaults.cardColors()
    }

    val fullName = "${data.parsedData.content.givenNames.joinToString(separator = " ")} ${
        data.parsedData.content.surname.joinToString(separator = " ")
    }"

    val type = stringResource(
        when (data.parsedData.content) {
            is SDCATCardParsedContent.StudentCardParsedContent -> R.string.sdcat_card_card_student
            is SDCATCardParsedContent.DoctoralCandidateCardParsedContent -> R.string.sdcat_card_card_doctoral_candidate
            is SDCATCardParsedContent.AcademicTeacherCardParsedContent -> R.string.sdcat_card_card_academic_teacher
        }
    )

    val subtitle = "$type (${data.parsedData.content.universityOrIssuerName})"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navigateToSDCATCardSavedDetails(data.rawData.id)
            },
        colors = cardColors,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(space = 4.dp),
            ) {
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.titleLarge,
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            if (data.rawData.default == true) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.contactless_payment_circle),
                    contentDescription = "Default",
                    modifier = Modifier.size(size = 32.dp),
                )
            }
        }
    }
}
