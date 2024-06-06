package edu.put.szlaki.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import edu.put.szlaki.src.Stage

class TimeDatabaseHandler(context: Context, name: String?,
                           factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Time.sb"
        const val TABLE_NAME = "time"
        const val COLUMN_ID = "_id"
        const val COLUMN_TRIALNAME = "trialName"
        const val COLUMN_TIME = "time"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TRIALNAME + " TEXT, " +
                COLUMN_TIME + " TEXT DEFAULT ' '," +
                "FOREIGN KEY(" + COLUMN_ID + ") REFERENCES trial(" + "_id" + ")" +
                ")")
        db.execSQL(createTableQuery)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addTrialTime(context: Context, name: String, time: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TRIALNAME, name)
        values.put(COLUMN_TIME, time)
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun passTrialTime(name: String?): MutableList<String>? { // get all trials from the database
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_TRIALNAME LIKE \"$name\""
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        val times: MutableList<String> = mutableListOf()
        if (cursor.moveToFirst())
        {
            do {
                val time = cursor.getString(2)
                times.add(time)
            } while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return times
    }

    fun deleteTime(name: String?, time: String) {
        val query = "DELETE FROM $TABLE_NAME WHERE $COLUMN_TRIALNAME = \"$name\" AND $COLUMN_TIME = \"$time\""
        val db = this.writableDatabase
        db.execSQL(query)
    }
}