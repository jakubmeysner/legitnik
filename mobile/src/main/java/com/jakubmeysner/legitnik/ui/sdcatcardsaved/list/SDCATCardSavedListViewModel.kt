package com.jakubmeysner.legitnik.ui.sdcatcardsaved.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardDataEntity
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardRepository
import com.jakubmeysner.legitnik.domain.sdcatcard.toParsed
import com.jakubmeysner.legitnik.util.ClassSimpleNameLoggingTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SDCATCardSavedListUiState(
    val cards: List<SDCATCardDataEntity>? = null,
)

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class SDCATCardSavedListViewModel @Inject constructor(
    sdcatCardRepository: SDCATCardRepository,
) : ViewModel(), ClassSimpleNameLoggingTag {
    private val cards = sdcatCardRepository.getAllCardsFlow().mapLatest { list ->
        list.map {
            SDCATCardDataEntity(
                rawData = it,
                parsedData = it.toParsed(),
            )
        }
    }

    val uiState = cards.mapLatest {
        SDCATCardSavedListUiState(
            cards = it,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SDCATCardSavedListUiState(),
    )
}
