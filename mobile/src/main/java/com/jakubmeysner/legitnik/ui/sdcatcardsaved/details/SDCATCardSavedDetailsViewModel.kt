package com.jakubmeysner.legitnik.ui.sdcatcardsaved.details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardDataEntity
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardRepository
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardValidationResult
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardValidator
import com.jakubmeysner.legitnik.domain.sdcatcard.toParsed
import com.jakubmeysner.legitnik.util.ClassSimpleNameLoggingTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

data class SDCATCardSavedDetailsUiState(
    val card: SDCATCardDataEntity? = null,
    val validationResult: SDCATCardValidationResult? = null,
    val error: Boolean = false,
    val loading: Boolean = true,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SDCATCardSavedDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val cardRepository: SDCATCardRepository,
    private val cardValidator: SDCATCardValidator,
) : ViewModel(), ClassSimpleNameLoggingTag {
    private val route = savedStateHandle.toRoute<SDCATCardSavedDetailsRoute>()
    private val id = UUID.fromString(route.idString)

    private val error = MutableStateFlow(false)

    private val card = cardRepository.getCardFlow(id).mapLatest {
        it?.let {
            SDCATCardDataEntity(
                rawData = it,
                parsedData = it.toParsed(),
            )
        }
    }.catch {
        Log.e(tag, "An exception occurred while loading card", it)
        error.value = true
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null,
    )

    private val validationResult = card.mapLatest {
        it?.let {
            withContext(Dispatchers.Default) {
                cardValidator.getValidationResult(it)
            }
        }
    }.catch {
        Log.e(tag, "An exception occurred while loading validation result", it)
        error.value = true
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null,
    )

    val uiState = combine(card, validationResult, error) { card, validationResult, error ->
        SDCATCardSavedDetailsUiState(
            card = card,
            validationResult = validationResult,
            error = error,
            loading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SDCATCardSavedDetailsUiState(),
    )

    fun toggleDefault() {
        viewModelScope.launch {
            val card = card.value ?: return@launch

            if (card.rawData.default == true) {
                cardRepository.unsetDefaultCard()
            } else {
                cardRepository.replaceDefaultCard(card.rawData.id)
            }
        }
    }

    suspend fun removeCard() {
        cardRepository.removeCard(id)
    }
}
