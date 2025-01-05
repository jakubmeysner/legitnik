package com.jakubmeysner.legitnik.ui.sdcatcardsaved.details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardDataEntity
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardRepository
import com.jakubmeysner.legitnik.domain.sdcatcard.toParsed
import com.jakubmeysner.legitnik.util.ClassSimpleNameLoggingTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import javax.inject.Inject

data class SDCATCardSavedDetailsUiState(
    val card: SDCATCardDataEntity?,
    val error: Boolean,
    val loading: Boolean,
)

@HiltViewModel
class SDCATCardSavedDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sdcatCardRepository: SDCATCardRepository,
) : ViewModel(), ClassSimpleNameLoggingTag {
    private val route = savedStateHandle.toRoute<SDCATCardSavedDetailsRoute>()
    private val id = UUID.fromString(route.idString)

    private val error = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val card = sdcatCardRepository.getCardFlow(id).mapLatest {
        it?.let {
            SDCATCardDataEntity(
                rawData = it,
                parsedData = it.toParsed(),
            )
        }
    }.catch {
        Log.e(tag, "An exception occurred while loading card", it)
    }

    val uiState = combine(card, error) { data, error ->
        SDCATCardSavedDetailsUiState(
            card = data,
            error = error,
            loading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SDCATCardSavedDetailsUiState(
            card = null,
            error = false,
            loading = true,
        )
    )

    suspend fun removeCard() {
        sdcatCardRepository.removeCard(id)
    }
}
