package com.example.holidayplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.holidayplanner.data.HolidayViewModel
import com.example.holidayplanner.main.HolidayPlannerApp
import com.google.firebase.FirebaseApp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            val vm: HolidayViewModel = viewModel()
            HolidayPlannerApp(viewModel = vm)
        }
    }
}
