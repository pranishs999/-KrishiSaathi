package com.krishisaathi.app.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.NaturalIvory

/**
 * Shared pull-down-to-refresh container styled in Krishi Saathi palette.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KrishiPullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val pullState = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        state = pullState,
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = pullState,
                isRefreshing = isRefreshing,
                containerColor = NaturalIvory,
                color = ForestGreen,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        },
        content = content
    )
}
