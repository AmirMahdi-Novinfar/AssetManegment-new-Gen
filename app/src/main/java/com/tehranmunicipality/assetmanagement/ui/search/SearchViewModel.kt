package com.tehranmunicipality.assetmanagement.ui.search

import android.content.Context
import com.tehranmunicipality.assetmanagement.repository.PrefRepository
import com.tehranmunicipality.assetmanagement.repository.Repository
import com.tehranmunicipality.assetmanagement.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    context: Context,
    repository: Repository,
    prefRepository: PrefRepository,
) : BaseViewModel(context,repository, prefRepository)