package com.krishisaathi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.krishisaathi.app.ui.navigation.MainScaffold
import com.krishisaathi.app.ui.theme.KrishiSaathiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KrishiSaathiTheme {
                MainScaffold()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScaffoldPreview() {
    KrishiSaathiTheme {
        MainScaffold()
    }
}
