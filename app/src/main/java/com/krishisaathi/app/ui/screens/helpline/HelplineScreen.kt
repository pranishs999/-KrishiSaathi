package com.krishisaathi.app.ui.screens.helpline

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.krishisaathi.app.ui.components.UnderConstructionScreen

/**
 * Farmer Helpline ("किसान हेल्पलाइन") screen displaying the approved Under Construction state.
 */
@Composable
fun HelplineScreen(
    modifier: Modifier = Modifier
) {
    UnderConstructionScreen(
        title = "किसान हेल्पलाइन",
        icon = Icons.Outlined.SupportAgent,
        supportingText = "कृषि सम्बन्धी सम्पर्क तथा सहयोग विवरण चाँडै उपलब्ध हुनेछ।",
        modifier = modifier
    )
}
