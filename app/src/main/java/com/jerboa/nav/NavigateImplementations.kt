package com.jerboa.nav

import androidx.lifecycle.ViewModel

class NavigateWithNoArgsAndDependencies(
    val navigate: () -> Unit,
)

class NavigateWithNoArgs<D : ViewModel>(
    private val container: DependencyContainer<D>,
    private val navigateToDestination: () -> Unit,
) {
    fun navigate(dependencies: D) {
        container.dependencies = dependencies
        navigateToDestination()
    }
}
