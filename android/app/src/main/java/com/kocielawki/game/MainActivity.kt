package com.kocielawki.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kocielawki.game.ui.KocieLawkiApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val store = PrefsStore(applicationContext)
        setContent {
            KocieLawkiApp(viewModel(factory = viewModelFactory { initializer { GameViewModel(store) } }))
        }
    }
}
