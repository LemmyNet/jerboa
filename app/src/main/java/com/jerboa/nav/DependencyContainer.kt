package com.jerboa.nav

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

class DependencyContainer<D : ViewModel> : ViewModel(), ViewModelProvider.Factory {
    internal var dependencies: D? = null

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        dependencies?.let {
            dependencies = null
            if (modelClass.isAssignableFrom(it.javaClass)) {
                return it as T
            }
        }
        return super.create(modelClass)
    }
}

@Composable
fun <D : ViewModel> dependencyContainer(): DependencyContainer<D> {
    return viewModel()
}
