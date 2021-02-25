package com.atiencieemi.contactosapp.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.atiencieemi.contactosapp.ContactoModelClass
import java.util.ArrayList

class ContactDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "ContactBDD.db"
        const val TABLE_NAME = "TLB_CONTACTO"
        const val COLUMN_NAME_USERID = "UserID"
        const val COLUMN_NAME_FIRSTNAME = "FirstName"
        const val COLUMN_NAME_LASTNAME = "LastName"
        const val COLUMN_NAME_PHONENUMBER = "PhoneNumber"
        const val COLUMN_NAME_EMAIL = "Email"
        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${TABLE_NAME} (" +
                    "${COLUMN_NAME_USERID} INTEGER PRIMARY KEY," +
                    "${COLUMN_NAME_FIRSTNAME} TEXT," +
                    "${COLUMN_NAME_LASTNAME} TEXT," +
                    "${COLUMN_NAME_PHONENUMBER} TEXT," +
                    "${COLUMN_NAME_EMAIL} TEXT)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${TABLE_NAME}"
    }

    fun createContact(contact: ContactoModelClass): Int {
        // Gets the data repository in write mode
        val db = this.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(COLUMN_NAME_USERID, contact.userId)
            put(COLUMN_NAME_FIRSTNAME, contact.firstName)
            put(COLUMN_NAME_LASTNAME, contact.lastName)
            put(COLUMN_NAME_PHONENUMBER, contact.phoneNumber)
            put(COLUMN_NAME_EMAIL, contact.emailAddress)
        }

        // Insert the new row, returning the primary key value of the new row
        //val newRowId = db?.insert(TABLE_NAME, null, values)
        //db?.execSQL("Insert into ${TABLE_NAME} values(${contact.userId}, ${contact.firstName}, ${contact.lastName}, ${contact.phoneNumber}, ${contact.emailAddress})")
        // Insert the new row, returning the primary key value of the new row
        val newRowId = db?.insert(TABLE_NAME, null, values)
        return newRowId!!.toInt()
    }

    fun readAllContacts(): ArrayList<ContactoModelClass> {
        val db = this.readableDatabase
// Define a projection that specifies which columns from the database
// you will actually use after this query.
        val projection = arrayOf(
            COLUMN_NAME_USERID,
            COLUMN_NAME_FIRSTNAME,
            COLUMN_NAME_LASTNAME,
            COLUMN_NAME_PHONENUMBER,
            COLUMN_NAME_EMAIL
        )

// How you want the results sorted in the resulting Cursor
        val sortOrder = "${COLUMN_NAME_USERID} ASC"

        val cursor = db.query(
            TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            null,              // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            sortOrder               // The sort order
        )

        val contacts = arrayListOf<ContactoModelClass>()
        with(cursor) {
            while (moveToNext()) {
                val userId = getInt(getColumnIndexOrThrow(COLUMN_NAME_USERID))
                val firstName = getString(getColumnIndexOrThrow(COLUMN_NAME_FIRSTNAME))
                val lastName = getString(getColumnIndexOrThrow(COLUMN_NAME_LASTNAME))
                val phoneNumber = getString(getColumnIndexOrThrow(COLUMN_NAME_PHONENUMBER))
                val emailAddress = getString(getColumnIndexOrThrow(COLUMN_NAME_EMAIL))
                contacts.add(
                    ContactoModelClass(
                        userId,
                        firstName,
                        lastName,
                        phoneNumber,
                        emailAddress
                    )
                )
            }
        }

        return contacts
    }

    fun updateContact(contact: ContactoModelClass): Int {
        val db = this.writableDatabase

// New value for one column
        val title = "MyNewTitle"
        val values = ContentValues().apply {
            put(COLUMN_NAME_USERID, contact.userId)
            put(COLUMN_NAME_FIRSTNAME, contact.firstName)
            put(COLUMN_NAME_LASTNAME, contact.lastName)
            put(COLUMN_NAME_PHONENUMBER, contact.phoneNumber)
            put(COLUMN_NAME_EMAIL, contact.emailAddress)
        }

// Which row to update, based on the title
        val selection = "${COLUMN_NAME_USERID} = ?"
        val selectionArgs = arrayOf(contact.userId.toString())
        val count = db.update(
            TABLE_NAME,
            values,
            selection,
            selectionArgs
        )
        return count
    }

    fun deleteContact(userId: Int): Int {
        val db = this.writableDatabase
        // Define 'where' part of query.
        val selection = "${COLUMN_NAME_USERID} LIKE ?"
// Specify arguments in placeholder order.
        val selectionArgs = arrayOf(userId.toString())
// Issue SQL statement.
        val deletedRows = db.delete(TABLE_NAME, selection, selectionArgs)
        return deletedRows
    }
}