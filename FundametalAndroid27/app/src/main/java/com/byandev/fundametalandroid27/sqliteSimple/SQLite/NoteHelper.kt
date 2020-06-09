package com.byandev.fundametalandroid27.sqliteSimple.SQLite

//class NoteHelper(context: Context) {
//
//    companion object {
//        private const val DATABASE_TABLE = TABLE_NAME
//        private lateinit var dataBaseHelper: DatabaseHelper
//        private var INSTANCE: NoteHelper? = null
//        private lateinit var database: SQLiteDatabase
//
//        fun getInstance(context: Context): NoteHelper =
//            INSTANCE ?: synchronized(this) {
//                INSTANCE ?: NoteHelper(context)
//            }
//    }
//
//    init {
//        dataBaseHelper = DatabaseHelper(context)
//    }
//
//    @Throws(SQLException::class)
//    fun open() {
//        database = dataBaseHelper.writableDatabase
//    }
//    fun close() {
//        dataBaseHelper.close()
//        if (database.isOpen)
//            database.close()
//    }
//
//    fun queryAll(): Cursor {
//        return database.query(
//            DATABASE_TABLE,
//            null,
//            null,
//            null,
//            null,
//            null,
//            "$_ID ASC")
//    }
//
//    fun queryById(id: String) : Cursor {
//        return database.query(
//            DATABASE_TABLE,
//            null,
//            "$_ID = ?",
//            arrayOf(id),
//            null,
//            null,
//            null,
//            null
//        )
//
//    }
//
//    fun insert(values: ContentValues?):Long {
//        return database.insert(DATABASE_TABLE,null, values)
//    }
//
//    fun update(id: String, values: ContentValues?): Int {
//        return database.update(DATABASE_TABLE, values, "$_ID = ?", arrayOf(id))
//    }
//
//    fun deleteById(id: String): Int {
//        return database.delete(DATABASE_TABLE, "$_ID = '$id'", null)
//    }
//}