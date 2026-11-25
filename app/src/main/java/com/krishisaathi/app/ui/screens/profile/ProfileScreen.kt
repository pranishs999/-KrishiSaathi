package com.krishisaathi.app.ui.screens.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.krishisaathi.app.ui.components.UnderConstructionScreen

/**
 * Farmer Profile ("मेरो विवरण") screen displaying the approved Under Construction state.
 * Registration details remain safely persisted locally without exposing edit/auth UI.
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier
) {
    UnderConstructionScreen(
        title = "मेरो विवरण",
        icon = Icons.Outlined.Construction,
        supportingText = "यो सुविधा हाल निर्माणको क्रममा छ। चाँडै उपलब्ध हुनेछ।",
        modifier = modifier
    )
}
