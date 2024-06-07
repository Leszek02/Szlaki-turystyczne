package edu.put.szlaki.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import edu.put.szlaki.src.Trial


class TrialDatabaseHandler(context: Context, name: String?,
                           factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Trial.sb"
        const val TABLE_NAME = "trial"
        const val COLUMN_ID = "_id"
        const val COLUMN_TRIALNAME = "trialName"
        const val COLUMN_LENGTH = "length"
        const val COLUMN_IMAGE = "image"
        const val COLUMN_COMMENT = "comment"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " TEXT PRIMARY KEY," + COLUMN_TRIALNAME + " TEXT UNIQUE," + COLUMN_LENGTH + " TEXT DEFAULT '0', "
                + COLUMN_IMAGE + " TEXT, " + COLUMN_COMMENT + " TEXT DEFAULT ' '" + ")")
        db.execSQL(createTableQuery)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addTrial (context: Context, trial: Trial) { // add one trial to the database
        val name = trial.name
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_TRIALNAME LIKE \"$name\""
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst())
            Toast.makeText(context, "Trial name must be unique", Toast.LENGTH_SHORT).show()
        else {
            val values = ContentValues()
            values.put(COLUMN_ID, trial.id)
            values.put(COLUMN_TRIALNAME, name)
            values.put(COLUMN_IMAGE, trial.image)
            db.insert(TABLE_NAME, null, values)
        }
        cursor.close()
        db.close()
    }

    fun addComment(name: String?, comment: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_COMMENT, comment)
        db.update(TABLE_NAME, values, "$COLUMN_TRIALNAME = ?", arrayOf(name))
        db.close()
    }

    fun getTrial(context: Context, name: String?): Trial? {
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_TRIALNAME LIKE \"$name\""
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var trial: Trial? = null
        if (cursor.moveToFirst())
        {
            trial = Trial(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4))
        }
        else
        {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
        return trial
    }

    fun getTrialName(context: Context, id: String?): Trial? {
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID LIKE \"$id\""
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var trial: Trial? = null
        if (cursor.moveToFirst())
        {
            trial = Trial(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4))
        }
        else
        {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
        return trial
    }

    fun trialRowsNumber(): Int{ // how many trials in the database
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

    fun passTrials(): MutableList<Trial>? { // get all trials from the database
        val query = "SELECT * FROM $TABLE_NAME"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        val trials: MutableList<Trial> = mutableListOf()
        if (cursor.moveToFirst())
        {
            do {
                val name = cursor.getString(1)
                val image = cursor.getString(3)
                val trial = Trial("", name, "", image, "")
                trials.add(trial)
            } while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return trials
    }
}
