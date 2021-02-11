package com.atiencieemi.contactosapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PrincipalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        val username = intent.getStringExtra(LOGIN_KEY) ?: ""
        supportActionBar?.title = supportActionBar?.title.toString()+"-"+username!!.substringBefore("@")
    }
}