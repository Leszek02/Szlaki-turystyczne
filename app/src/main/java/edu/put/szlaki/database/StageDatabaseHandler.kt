package edu.put.szlaki.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import edu.put.szlaki.src.Stage

class StageDatabaseHandler(context: Context, name: String?,
                           factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Stage.sb"
        const val TABLE_NAME = "stage"
        const val COLUMN_ID = "_id"
        const val COLUMN_STAGENAME = "stageName"
        const val COLUMN_LENGTH = "length"
        const val COLUMN_TIME = "time"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " TEXT, " + COLUMN_STAGENAME + " TEXT, " + COLUMN_LENGTH + " TEXT DEFAULT '0', " +
                COLUMN_TIME + " TEXT DEFAULT ' '," +
                "PRIMARY KEY(" + COLUMN_ID + ", " + COLUMN_STAGENAME + "), " +
                "FOREIGN KEY(" + COLUMN_ID + ") REFERENCES trial(" + "_id" + ")" +
                ")")
        db.execSQL(createTableQuery)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun stageRowsNumber(): Int{ // how many trials in the database
        var rowNumber = 0
        val query = "SELECT COUNT(*) FROM $TABLE_NAME"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst())
        {
            rowNumber = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return rowNumber
    }

    fun addStage(context: Context, stage: Stage) {
        val name = stage.name
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_STAGENAME LIKE \"$name\""
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst())
            Toast.makeText(context, "Stage name must be unique", Toast.LENGTH_SHORT).show()
        else {
            val values = ContentValues()
            values.put(COLUMN_ID, stage.id)
            values.put(COLUMN_STAGENAME, name)
            values.put(COLUMN_LENGTH, stage.length)
            values.put(COLUMN_TIME, stage.time)
            db.insert(TABLE_NAME, null, values)
        }
        cursor.close()
        db.close()
    }

    fun passStages(id: String?): MutableList<Stage>? { // get all trials from the database
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID LIKE \"$id\""
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        val stages: MutableList<Stage> = mutableListOf()
        if (cursor.moveToFirst())
        {
            do {
                val name = cursor.getString(1)
                val length = cursor.getString(2)
                val time = cursor.getString(3)
                val stage = Stage("", name, length, time)
                stages.add(stage)
            } while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return stages
    }

    fun deleteStage(id: String?, name: String?) {
        val query = "DELETE FROM $TABLE_NAME WHERE $COLUMN_ID = \"$id\" AND $COLUMN_STAGENAME = \"$name\""
        val db = this.writableDatabase
        db.execSQL(query)
    }
}