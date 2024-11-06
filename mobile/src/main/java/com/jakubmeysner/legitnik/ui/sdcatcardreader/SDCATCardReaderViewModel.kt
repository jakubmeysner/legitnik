package com.jakubmeysner.legitnik.ui.sdcatcardreader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SDCATCardReaderViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel()
