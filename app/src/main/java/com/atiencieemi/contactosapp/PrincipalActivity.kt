package com.atiencieemi.contactosapp

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class PrincipalActivity : AppCompatActivity() {
    var contactos = arrayListOf<ContactoModelClass>()
    var selectedContactPosition = 0
    lateinit var listViewContacts : ListView
    lateinit var editTextUserId : EditText
    lateinit var editTextFirstName : EditText
    lateinit var editTextLastName : EditText
    lateinit var editTextPhoneNumber : EditText
    lateinit var editTextEmailAddress : EditText
    lateinit var buttonSave : Button
    lateinit var buttonView : Button
    lateinit var buttonUpdate : Button
    lateinit var buttonDelete : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        //inicizalizar variables
        listViewContacts = findViewById(R.id.listViewContacts)
        editTextUserId = findViewById(R.id.editTextUserId)
        editTextFirstName = findViewById(R.id.editTextFirstName)
        editTextLastName = findViewById(R.id.editTextLastName)
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber)
        editTextEmailAddress = findViewById(R.id.editTextEmailAddress)
        buttonSave = findViewById(R.id.buttonSave)
        buttonView = findViewById(R.id.buttonView)
        buttonUpdate = findViewById(R.id.buttonUpdate)
        buttonDelete = findViewById(R.id.buttonDelete)

        val username = intent.getStringExtra(LOGIN_KEY) ?: ""
        // Get the support action bar
        val actionBar = supportActionBar
        // Set the action bar title, subtitle and elevation
        actionBar!!.title = supportActionBar?.title.toString()
        actionBar.subtitle = username.substringBefore("@")
        actionBar.elevation = 4.0F

        //Poblar de forma temporal el listview de contactos
        contactos.add(ContactoModelClass(1,"Emi","Atiencie","0995070468", "emi.atiencie@epn.edu.ec"))
        contactos.add(ContactoModelClass(2,"Juan","Perez","0991234567", "juan.perez@epn.edu.ec"))
        contactos.add(ContactoModelClass(3,"Michelle","Salinas","0995225599", "michelle.salinas@epn.edu.ec"))
        contactos.add(ContactoModelClass(4,"Rosa","Vallejo","+593995225100", "rosa.vallejo@epn.edu.ec"))
        val contactoAdaptador = ContactoAdapter(this, contactos)
        listViewContacts.adapter = contactoAdaptador

        listViewContacts.setOnItemClickListener { parent, view, position, id ->
            selectedContactPosition = position
            editTextUserId.setText(contactos[selectedContactPosition].userId.toString())
            editTextFirstName.setText(contactos[selectedContactPosition].firstName.toString())
            editTextLastName.setText(contactos[selectedContactPosition].lastName.toString())
            editTextPhoneNumber.setText(contactos[selectedContactPosition].phoneNumber.toString())
            editTextEmailAddress.setText(contactos[selectedContactPosition].emailAddress.toString())
        }

        buttonSave.setOnClickListener {
            val id = editTextUserId.text.toString().toInt()
            val nombre = editTextFirstName.text.toString()
            val apellido = editTextLastName.text.toString()
            val telefono = editTextPhoneNumber.text.toString()
            val email = editTextEmailAddress.text.toString()
            contactos.add(ContactoModelClass(id,nombre,apellido,telefono, email))
            val contactoAdaptador = ContactoAdapter(this, contactos)
            listViewContacts.adapter = contactoAdaptador
            Toast.makeText(this,"Contacto añadido",Toast.LENGTH_LONG).show()
            limpiarCamposEditables()
        }

        buttonView.setOnClickListener {
            listViewContacts.adapter = ContactoAdapter(this, contactos)
            limpiarCamposEditables()
        }

        buttonUpdate.setOnClickListener {
            contactos[selectedContactPosition].userId = editTextUserId.text.toString().toInt()
            contactos[selectedContactPosition].firstName = editTextFirstName.text.toString()
            contactos[selectedContactPosition].lastName = editTextLastName.text.toString()
            contactos[selectedContactPosition].phoneNumber = editTextPhoneNumber.text.toString()
            contactos[selectedContactPosition].emailAddress = editTextEmailAddress.text.toString()
            listViewContacts.adapter = ContactoAdapter(this, contactos)
            Toast.makeText(this,"Contacto actualizado",Toast.LENGTH_LONG).show()
            limpiarCamposEditables()
        }

        buttonDelete.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            //agregadno icono al mensaje de delete
            dialogBuilder.setIcon(R.drawable.ic_baseline_warning_24)
            dialogBuilder.setTitle("Confirmación de Eliminación")
            dialogBuilder.setMessage("¿Esta seguro que desea eliminar el contacto ${contactos[selectedContactPosition].firstName} ${contactos[selectedContactPosition].lastName}?")
            dialogBuilder.setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->
                contactos.removeAt(selectedContactPosition)
                val contactoAdaptador = ContactoAdapter(this, contactos)
                listViewContacts.adapter = contactoAdaptador
                Toast.makeText(this,"Contacto eliminado",Toast.LENGTH_LONG).show()
                limpiarCamposEditables()
            })
            dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                //pass
            })
            dialogBuilder.create().show()
        }
    }

    private fun limpiarCamposEditables() {
        editTextUserId.setText("")
        editTextFirstName.setText("")
        editTextLastName.setText("")
        editTextPhoneNumber.setText("")
        editTextEmailAddress.setText("")
    }

}