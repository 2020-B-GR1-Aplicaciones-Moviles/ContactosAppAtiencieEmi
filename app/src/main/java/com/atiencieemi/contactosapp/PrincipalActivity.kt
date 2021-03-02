package com.atiencieemi.contactosapp

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.atiencieemi.contactosapp.database.ContactDbHelper

class PrincipalActivity : AppCompatActivity() {
    var contactos = arrayListOf<ContactoModelClass>()
    var selectedContactPosition = 0
    lateinit var listViewContacts: ListView
    lateinit var editTextUserId: EditText
    lateinit var editTextFirstName: EditText
    lateinit var editTextLastName: EditText
    lateinit var editTextPhoneNumber: EditText
    lateinit var editTextEmailAddress: EditText
    lateinit var buttonSave: Button
    lateinit var buttonView: Button
    lateinit var buttonUpdate: Button
    lateinit var buttonDelete: Button
    lateinit var buttonCall: Button


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
        buttonCall = findViewById(R.id.buttonCall)

        val username = intent.getStringExtra(LOGIN_KEY) ?: ""
        // Get the support action bar
        val actionBar = supportActionBar
        // Set the action bar title, subtitle and elevation
        actionBar!!.title = supportActionBar?.title.toString()
        actionBar.subtitle = username.substringBefore("@")
        actionBar.elevation = 4.0F

        //Poblar de forma temporal el listview de contactos
        //contactos.add(ContactoModelClass(1,"Emi","Atiencie","0995070468", "emi.atiencie@epn.edu.ec"))
        //contactos.add(ContactoModelClass(2,"Juan","Perez","0991234567", "juan.perez@epn.edu.ec"))
        //contactos.add(ContactoModelClass(3,"Michelle","Salinas","0995225599", "michelle.salinas@epn.edu.ec"))
        //contactos.add(ContactoModelClass(4,"Rosa","Vallejo","+593995225100", "rosa.vallejo@epn.edu.ec"))
        consultarContactos()

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
            //contactos.add(ContactoModelClass(id,nombre,apellido,telefono, email))
            val respuesta = ContactDbHelper(this).createContact(
                ContactoModelClass(
                    id,
                    nombre,
                    apellido,
                    telefono,
                    email
                )
            )

            if (respuesta == -1) {
                Toast.makeText(this, "Error al añadir contacto", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Contacto añadido", Toast.LENGTH_LONG).show()
            }
            consultarContactos()
        }

        buttonView.setOnClickListener {
            //listViewContacts.adapter = ContactoAdapter(this, contactos)
            //limpiarCamposEditables()
            consultarContactos()
        }

        buttonUpdate.setOnClickListener {
            /*contactos[selectedContactPosition].userId = editTextUserId.text.toString().toInt()
            contactos[selectedContactPosition].firstName = editTextFirstName.text.toString()
            contactos[selectedContactPosition].lastName = editTextLastName.text.toString()
            contactos[selectedContactPosition].phoneNumber = editTextPhoneNumber.text.toString()
            contactos[selectedContactPosition].emailAddress = editTextEmailAddress.text.toString()*/
            val id = editTextUserId.text.toString().toInt()
            val nombre = editTextFirstName.text.toString()
            val apellido = editTextLastName.text.toString()
            val telefono = editTextPhoneNumber.text.toString()
            val email = editTextEmailAddress.text.toString()
            val respuesta = ContactDbHelper(this).updateContact(
                ContactoModelClass(
                    id,
                    nombre,
                    apellido,
                    telefono,
                    email
                )
            )
            if (respuesta == -1) {
                Toast.makeText(this, "Error al actualizar el contacto", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Contacto actualizado exitosamente", Toast.LENGTH_LONG).show()
            }
            consultarContactos()
        }

        buttonDelete.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Confirmación de Eliminación")
            dialogBuilder.setIcon(R.drawable.ic_baseline_warning_24)
            dialogBuilder.setMessage("¿Esta seguro que desea eliminar el contacto ${contactos[selectedContactPosition].firstName} ${contactos[selectedContactPosition].lastName}?")
            dialogBuilder.setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->
                //contactos.removeAt(selectedContactPosition)
                val userId = editTextUserId.text.toString().toInt()
                val filasBorradas = ContactDbHelper(this).deleteContact(userId)
                if (filasBorradas == 0) {
                    Toast.makeText(this, "Error al eliminar el contacto", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Contacto eliminado exitosamente", Toast.LENGTH_LONG)
                        .show()
                }
                consultarContactos()
            })
            dialogBuilder.setNegativeButton(
                "Cancel",
                DialogInterface.OnClickListener { dialog, which ->
                    //pass
                })
            dialogBuilder.create().show()
        }

        buttonCall.setOnClickListener {
            /*val number = "0992342567"
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$number")
            startActivity(intent)*/
            makeCall()
        }
    }

    private fun limpiarCamposEditables() {
        editTextUserId.setText("")
        editTextFirstName.setText("")
        editTextLastName.setText("")
        editTextPhoneNumber.setText("")
        editTextEmailAddress.setText("")
    }

    fun consultarContactos() {
        contactos = ContactDbHelper(this).readAllContacts()
        val contactoAdapter = ContactoAdapter(this, contactos)
        listViewContacts.adapter = contactoAdapter
        limpiarCamposEditables()
    }

    fun makeCall() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:" + "0992342567")
            startActivity(intent)
        } else {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:" + "0992342567")
            val result = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            if (result == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent)
            } else {
                requestForCallPermission()
            }
        }
    }

    private fun requestForCallPermission() =
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CALL_PHONE
            )
        ) {
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                PERMISSION_REQUEST_CODE
            )
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Permission for call was granted!", Toast.LENGTH_SHORT)
                        .show();
                    makeCall()
                } else {
                    Toast.makeText(this, "Permission for call was denied!", Toast.LENGTH_SHORT)
                        .show();
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
}