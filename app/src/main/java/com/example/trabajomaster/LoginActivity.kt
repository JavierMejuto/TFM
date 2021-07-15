package com.example.trabajomaster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_TrabajoMaster)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}