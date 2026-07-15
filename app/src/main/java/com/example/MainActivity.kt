package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.data.database.AppDatabase
import com.example.data.repository.ShopRepository
import com.example.ui.ShopApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ShopViewModel
import com.example.ui.viewmodel.ShopViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize AppDatabase using the activity lifecycleScope for the background data seeder
        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        
        // 2. Initialize ShopRepository
        val repository = ShopRepository(database.shopDao())
        
        // 3. Initialize ShopViewModel using custom Factory
        val viewModelFactory = ShopViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[ShopViewModel::class.java]

        setContent {
            MyApplicationTheme {
                ShopApp(viewModel = viewModel)
            }
        }
    }
}
