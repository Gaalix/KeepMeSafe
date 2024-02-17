package com.example.keepmesafe

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.libsqlite.Report

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "KeepMeSafe.db"

        private const val TABLE_REPORTS = "reports"
        private const val COLUMN_ID = "id"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_LATITUDE = "latitude"
        private const val COLUMN_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_REPORTS_TABLE = ("CREATE TABLE " +
                TABLE_REPORTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_LATITUDE + " REAL,"
                + COLUMN_LONGITUDE + " REAL" + ")")
        db.execSQL(CREATE_REPORTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REPORTS")
        onCreate(db)
    }

    fun addReport(report: Report) {
        val values = ContentValues()
        values.put(COLUMN_DESCRIPTION, report.description)
        values.put(COLUMN_LATITUDE, report.latitude)
        values.put(COLUMN_LONGITUDE, report.longitude)

        val db = this.writableDatabase
        db.insert(TABLE_REPORTS, null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun getAllReports(): List<Report> {
        val reports = ArrayList<Report>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_REPORTS", null)

        if (cursor.moveToFirst()) {
            do {
                val report = Report(
                    id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                    latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE)),
                    longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE))
                )
                reports.add(report)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return reports
    }

    fun deleteReport(report: Report) {
        val db = this.writableDatabase
        db.delete(TABLE_REPORTS, "$COLUMN_ID=?", arrayOf(report.id.toString()))
        db.close()
    }

    fun updateReport(report: Report) {
        val values = ContentValues()
        values.put(COLUMN_DESCRIPTION, report.description)
        values.put(COLUMN_LATITUDE, report.latitude)
        values.put(COLUMN_LONGITUDE, report.longitude)

        val db = this.writableDatabase
        db.update(TABLE_REPORTS, values, "$COLUMN_ID=?", arrayOf(report.id.toString()))
        db.close()
    }
}