package com.jakubmeysner.legitnik.ui.sdcatcardsaved.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SDCATCardSavedDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<SDCATCardSavedDetailsRoute>()
    private val id = UUID.fromString(route.idString)
}
