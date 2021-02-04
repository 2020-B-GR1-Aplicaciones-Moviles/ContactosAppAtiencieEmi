package com.atiencieemi.contactosapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class LoginActivity : AppCompatActivity() {
    lateinit var buttonLogin : Button
    lateinit var editTextTextEmailAddress : EditText
    lateinit var editTextTextPassword : EditText
    lateinit var checkBoxRecordarme : CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //temporal escritura de datos memoria externa
        EscribirDatosEnArchivoExterno()
        LeerDatosEnArchivoExterno()

        //temporal escritura de datos memoria interna
        EscribirDatosEnArchivoInterno4()
        LeerDatosEnArchivoInterno4()

        val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            "secret_shared_prefs",//filename
            masterKeyAlias,
            this,//context
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        //val sharedPref = getPreferences(Context.MODE_PRIVATE)

        //Inicialización de variables
        buttonLogin = findViewById<Button>(R.id.buttonLogin)
        editTextTextEmailAddress = findViewById<EditText>(R.id.editTextTextEmailAddress)
        editTextTextPassword = findViewById<EditText>(R.id.editTextTextPassword)
        checkBoxRecordarme = findViewById<CheckBox>(R.id.checkBoxRecordarme)

        //Lectura de valores de archivo de preferencias en caso que exitan
        editTextTextEmailAddress.setText ( sharedPreferences.getString(LOGIN_KEY,"") )
        editTextTextPassword.setText ( sharedPreferences.getString(PASSWORD_KEY,"") )

        buttonLogin.setOnClickListener {
            if (!ValidarDatos())
                return@setOnClickListener
            if(checkBoxRecordarme.isChecked){
                /*sharedPref
                    .edit()
                    .putString(LOGIN_KEY,editTextTextEmailAddress.text.toString())
                    .putString(PASSWORD_KEY,editTextTextPassword.text.toString())
                    .apply()*/

                sharedPreferences
                    .edit()
                    .putString(LOGIN_KEY,editTextTextEmailAddress.text.toString())
                    .putString(PASSWORD_KEY,editTextTextPassword.text.toString())
                    .apply()
            }
            else{
                val editor = sharedPreferences.edit()
                editor.putString(LOGIN_KEY,"")
                editor.putString(PASSWORD_KEY,"")
                editor.commit()
            }
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

        // Activar el Checkbox
        if (editTextTextEmailAddress.text.length != 0 && editTextTextPassword.text.length != 0){
            checkBoxRecordarme.isChecked = true
        }
        return true
    }

    fun EscribirDatosEnArchivoInterno4(){
        val texto = "texto" + System.lineSeparator() + "almacenado"
        openFileOutput("fichero.txt", Context.MODE_PRIVATE).bufferedWriter().use {fos ->
            fos.write(texto)
        }
    }

    fun LeerDatosEnArchivoInterno4() {
        openFileInput("fichero.txt").bufferedReader().use {
            val datoLeido = it.readText()
            val textArray = datoLeido.split("\n")
            val texto1 = textArray[0]
            val texto2 = textArray[1]
        }
    }

    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }
    fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }

    fun EscribirDatosEnArchivoExterno(){
        if (isExternalStorageWritable()) {
            FileOutputStream(File(getExternalFilesDir(null),CONTACTS_FILENAME)).bufferedWriter().use { outputStream ->
                outputStream.write("dato1")
                outputStream.write(System.lineSeparator())
                outputStream.write("dato2")
            }
        }
    }

    fun LeerDatosEnArchivoExterno(){
        if (isExternalStorageReadable()) {
            FileInputStream(File(getExternalFilesDir(null),CONTACTS_FILENAME)).bufferedReader().use {
                val datoLeido = it.readText()
                val textArray = datoLeido.split(System.lineSeparator())
                val texto1 = textArray[0]
                val texto2 = textArray[1]
            }
        }
    }

}