package com.mfahmialif.pulseoximeter

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

val DATABASE_NAME = "MyDB"
val TABLE_NAME = "data"
val COL_PETUGAS = "petugas"
val COL_NAMA = "nama"
val COL_PULSE = "pulse"
val COL_BPM = "bpm"
val COL_DATETIME = "datetime"

class DatabaseHandler(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME,null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE "+ TABLE_NAME+" (" +
                COL_PETUGAS + " VARCHAR(256)," +
                COL_NAMA + " VARCHAR(256)," +
                COL_PULSE + " DOUBLE," +
                COL_BPM + " DOUBLE," +
                COL_DATETIME + " DATETIME)"

        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun insertData(data: Data){
        val datBase = this.writableDatabase
        var cVal = ContentValues()
        cVal.put(COL_PETUGAS, data.petugas)
        cVal.put(COL_NAMA, data.nama)
        cVal.put(COL_PULSE, data.pulse)
        cVal.put(COL_BPM, data.bpm)
        cVal.put(COL_DATETIME, data.dtime)
        var result = datBase.insert(TABLE_NAME, null, cVal)
        if (result == -1.toLong())
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context, "Tidak ada internet, tunda pengiriman data", Toast.LENGTH_SHORT).show()
    }

    fun readData(): MutableList<Data> {
        val list: MutableList<Data> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM `data`"
        val result = db.rawQuery(query,null)
        if (result.moveToFirst()){
            do {
                val data = Data()
                data.petugas = result.getString(result.getColumnIndex(COL_PETUGAS))
                data.nama = result.getString(result.getColumnIndex(COL_NAMA))
                data.pulse = result.getString(result.getColumnIndex(COL_PULSE)).toDouble()
                data.bpm = result.getString(result.getColumnIndex(COL_BPM)).toDouble()
                data.dtime = result.getString(result.getColumnIndex(COL_DATETIME))
                list.add(data)
            } while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }

    fun deleteData(dtime:String): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, COL_DATETIME + "= '"+dtime+"'", null)
        return result
    }

    fun deleteAllData(){
        val db = this.writableDatabase
        val result = db.execSQL("delete from "+ TABLE_NAME)
        return result
    }

    fun getSize(): Long {
        val db = this.readableDatabase
        val count = DatabaseUtils.queryNumEntries(db, TABLE_NAME)
        db.close()
        return count
    }
}