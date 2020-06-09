package com.byandev.fundametalandroid27.optimizationSqliteQuery.db

import android.provider.BaseColumns

internal object DBContract {
    var TABLE_NAME = "table_mahasiswa"

    internal class MahasiswaColumns : BaseColumns {
        companion object {
            const val NAMA = "nama"
            const val NIM = "nim"
        }
    }
}