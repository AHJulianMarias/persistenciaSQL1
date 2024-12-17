package com.example.t8_ej01_persistenciadatossqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

// Clase DatabaseHelper que extiende SQLiteOpenHelper para manejar la base de datos de la aplicación.
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Bloque companion object para definir constantes que serán usadas en toda la clase.
    // Son como los valores estáticos en Java
    companion object {
        // Nombre de la base de datos.
        private const val DATABASE_NAME = "GatosDatabase"
        // Versión de la base de datos, útil para manejar actualizaciones esquemáticas.
        private const val DATABASE_VERSION = 1
        // Nombre de la tabla donde se almacenarán los Gatos.
        private const val TABLE_GATOS = "Gatos"
        // Nombres de las columnas de la tabla.
        private const val KEY_ID = "id"
        private const val KEY_NOMBRE = "nombre"
        private const val KEY_COLOR = "color"
    }

    // Método llamado cuando la base de datos se crea por primera vez.
    override fun onCreate(db: SQLiteDatabase) {
        // Define la sentencia SQL para crear la tabla de Gatos.
        val createGatosTable = ("CREATE TABLE " + TABLE_GATOS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NOMBRE + " TEXT,"
                + KEY_COLOR + " TEXT" + ")")

        // Ejecuta la sentencia SQL para crear la tabla.
        db.execSQL(createGatosTable)
    }

    // Método llamado cuando se necesita actualizar la base de datos, por ejemplo, cuando se incrementa DATABASE_VERSION.
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Elimina la tabla existente y crea una nueva.
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GATOS")
        onCreate(db)
    }

    // Método para obtener todos los Gatos de la base de datos.
    fun getAllGatos(): ArrayList<Gato> {
        // Lista para almacenar y retornar los Gatos.
        val GatosList = ArrayList<Gato>()
        // Consulta SQL para seleccionar todos los Gatos.
        val selectQuery = "SELECT  * FROM $TABLE_GATOS"

        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            // Ejecuta la consulta SQL.
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            // Maneja la excepción en caso de error al ejecutar la consulta.
            db.execSQL(selectQuery)
            return ArrayList()
        }

        // Variables para almacenar los valores de las columnas.
        var id: Int
        var nombre: String
        var color: String

        // Itera a través del cursor para leer los datos de la base de datos.
        if (cursor.moveToFirst()) {
            do {
                // Obtiene los índices de las columnas.
                val idIndex = cursor.getColumnIndex(KEY_ID)
                val nombreIndex = cursor.getColumnIndex(KEY_NOMBRE)
                val colorIndex = cursor.getColumnIndex(KEY_COLOR)

                // Verifica que los índices sean válidos.
                if (idIndex != -1 && nombreIndex != -1 && colorIndex != -1) {
                    // Lee los valores y los añade a la lista de Gatos.
                    id = cursor.getInt(idIndex)
                    nombre = cursor.getString(nombreIndex)
                    color = cursor.getString(colorIndex)


                    val Gato = Gato(id = id, nombre = nombre, color = color)
                    GatosList.add(Gato)
                }
            } while (cursor.moveToNext())
        }

        // Cierra el cursor para liberar recursos.
        cursor.close()
        return GatosList
    }

    // Método para actualizar un Gato en la base de datos.
    fun updateGato(Gato: Gato): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        // Prepara los valores a actualizar.
        contentValues.put(KEY_NOMBRE, Gato.nombre)
        contentValues.put(KEY_COLOR, Gato.color)

        // Actualiza la fila correspondiente y retorna el número de filas afectadas.
        return db.update(TABLE_GATOS, contentValues, "$KEY_ID = ?", arrayOf(Gato.id.toString()))
    }

    // Método para eliminar un Gato de la base de datos.
    fun deleteGato(Gato: Gato): Int {
        val db = this.writableDatabase
        // Elimina la fila correspondiente y retorna el número de filas afectadas.
        val success = db.delete(TABLE_GATOS, "$KEY_ID = ?", arrayOf(Gato.id.toString()))
        db.close()
        return success
    }

    // Método para añadir un nuevo Gato a la base de datos.
    fun addGato(Gato: Gato): Long {
        try {
            val db = this.writableDatabase
            val contentValues = ContentValues()
            // Prepara los valores a insertar.
            contentValues.put(KEY_NOMBRE, Gato.nombre)
            contentValues.put(KEY_COLOR, Gato.color)

            // Inserta el nuevo Gato y retorna el ID del nuevo Gato o -1 en caso de error.
            val success = db.insert(TABLE_GATOS, null, contentValues)
            db.close()
            return success
        } catch (e: Exception) {
            // Maneja la excepción en caso de error al insertar.
            Log.e("Error", "Error al agregar Gato", e)
            return -1
        }
    }
}


