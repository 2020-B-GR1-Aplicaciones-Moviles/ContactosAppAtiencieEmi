package com.atiencieemi.contactosapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText

class LoginActivity : AppCompatActivity() {
    lateinit var buttonLogin : Button
    lateinit var editTextTextEmailAddress : EditText
    lateinit var editTextTextPassword : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        buttonLogin = findViewById<Button>(R.id.buttonLogin)
        editTextTextEmailAddress = findViewById<EditText>(R.id.editTextTextEmailAddress)
        editTextTextPassword = findViewById<EditText>(R.id.editTextTextPassword)

        buttonLogin.setOnClickListener {
            if (!ValidarDatos())
                return@setOnClickListener
        }
    }
    fun ValidarDatos(): Boolean {
        fun CharSequence?.isValidEmail() =
            !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
        if (editTextTextEmailAddress.text.isNullOrEmpty()) {
            editTextTextEmailAddress.setError(getString(R.string.editTextTextEmailAddress_hint))
            editTextTextEmailAddress.requestFocus()
            return false
        }
        if (!editTextTextEmailAddress.text.isValidEmail()) {
            editTextTextEmailAddress.setError(getString(R.string.email_NoValido))
            editTextTextEmailAddress.requestFocus()
            return false
        }
        if (editTextTextPassword.text.isNullOrEmpty()) {
            editTextTextPassword.setError(getString(R.string.editTextPassword_hint))
            editTextTextPassword.requestFocus()
            return false
        }
        if (editTextTextPassword.text.length < MIN_PASSWORD_LENGTH) {
            editTextTextPassword.setError(getString(R.string.password_longitudNoValida))
            editTextTextPassword.requestFocus()
            return false
        }
        return true
    }
}