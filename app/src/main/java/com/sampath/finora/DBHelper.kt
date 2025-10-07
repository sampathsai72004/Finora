package com.sampath.finora

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "finora.db"
        private const val DATABASE_VERSION = 3 // Updated version to reflect all tables

        // User Table
        private const val TABLE_USERS = "users"
        private const val COL_USER_ID = "id" // Use consistent ID name
        private const val COL_EMAIL = "email"
        private const val COL_PASSWORD = "password"
        private const val COL_NAME = "name"
        private const val COL_CREATED = "created_at"

        // Reference Table
        private const val TABLE_SPECIES_REF = "species_reference"
        private const val REF_SPECIES_NAME = "species_name"
        private const val REF_LW_A = "lw_a"
        private const val REF_LW_B = "lw_b"

        // Catch Table (Header)
        private const val TABLE_CATCH = "catch_records"
        private const val CATCH_ID = "catch_id"
        private const val CATCH_USER_ID = "user_id"
        private const val CATCH_TIMESTAMP = "timestamp"
        private const val CATCH_GPS_LAT = "gps_latitude"
        private const val CATCH_GPS_LON = "gps_longitude"
        private const val CATCH_SYNCED = "is_synced"

        // Fish Details Table (Line Items)
        private const val TABLE_FISH_DETAILS = "fish_details"
        private const val FISH_DETAIL_ID = "detail_id"
        private const val FISH_CATCH_ID = "catch_id"
        private const val FISH_SPECIES = "species"
        private const val FISH_FRESHNESS_SCORE = "freshness_score"
        private const val FISH_EST_LENGTH = "est_length_cm"
        private const val FISH_EST_WEIGHT = "est_weight_kg"
        private const val FISH_CONFIDENCE = "detection_confidence"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 1. Create User Table
        val createUserTable = """
            CREATE TABLE $TABLE_USERS (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_EMAIL TEXT UNIQUE,
                $COL_PASSWORD TEXT,
                $COL_NAME TEXT,
                $COL_CREATED DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """.trimIndent()
        db.execSQL(createUserTable)

        // 2. Create Species Reference Table
        val createSpeciesRefTable = """
            CREATE TABLE $TABLE_SPECIES_REF (
                $REF_SPECIES_NAME TEXT PRIMARY KEY,
                $REF_LW_A REAL,
                $REF_LW_B REAL
            );
        """.trimIndent()
        db.execSQL(createSpeciesRefTable)

        // 3. Create Catch Table (Header)
        val createCatchTable = """
            CREATE TABLE $TABLE_CATCH (
                $CATCH_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $CATCH_USER_ID INTEGER,
                $CATCH_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP,
                $CATCH_GPS_LAT REAL,
                $CATCH_GPS_LON REAL,
                $CATCH_SYNCED INTEGER DEFAULT 0,
                FOREIGN KEY ($CATCH_USER_ID) REFERENCES $TABLE_USERS($COL_USER_ID)
            );
        """.trimIndent()
        db.execSQL(createCatchTable)

        // 4. Create Fish Details Table
        val createFishDetailsTable = """
            CREATE TABLE $TABLE_FISH_DETAILS (
                $FISH_DETAIL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $FISH_CATCH_ID INTEGER,
                $FISH_SPECIES TEXT,
                $FISH_FRESHNESS_SCORE REAL,
                $FISH_EST_LENGTH REAL,
                $FISH_EST_WEIGHT REAL,
                $FISH_CONFIDENCE REAL,
                FOREIGN KEY ($FISH_CATCH_ID) REFERENCES $TABLE_CATCH($CATCH_ID)
            );
        """.trimIndent()
        db.execSQL(createFishDetailsTable)

        insertInitialReferenceData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FISH_DETAILS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATCH")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SPECIES_REF")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    private fun insertInitialReferenceData(db: SQLiteDatabase) {
        val speciesList = listOf(
            Triple("Atlantic Cod", 0.0076, 3.0),
            Triple("Haddock", 0.005, 3.2),
            Triple("Mackerel", 0.008, 3.0),
            Triple("Tuna", 0.02, 2.9)
        )

        speciesList.forEach { (speciesName, a, b) ->
            val cv = ContentValues().apply {
                put(REF_SPECIES_NAME, speciesName)
                put(REF_LW_A, a)
                put(REF_LW_B, b)
            }
            db.insert(TABLE_SPECIES_REF, null, cv)
        }
    }

    // --- User Authentication/Management Methods (Required by Signup.kt) ---

    fun insertUser(email: String, password: String, name: String): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_EMAIL, email.trim())
            put(COL_PASSWORD, password)
            put(COL_NAME, name)
        }
        val id = db.insert(TABLE_USERS, null, cv)
        db.close()
        return id != -1L
    }

    fun isUserExists(email: String): Boolean {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_USER_ID),
            "$COL_EMAIL = ?",
            arrayOf(email.trim()),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun validateUser(email: String, password: String): Boolean {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_USER_ID),
            "$COL_EMAIL = ? AND $COL_PASSWORD = ?",
            arrayOf(email.trim(), password),
            null, null, null
        )
        val ok = cursor.count > 0
        cursor.close()
        db.close()
        return ok
    }

    fun getUserId(email: String): Long {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_USER_ID),
            "$COL_EMAIL = ?",
            arrayOf(email.trim()),
            null, null, null
        )
        var userId = -1L
        if (cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_ID))
        }
        cursor.close()
        return userId
    }

    fun getUserName(userId: Long): String {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_NAME),
            "$COL_USER_ID = ?",
            arrayOf(userId.toString()),
            null, null, null
        )
        var name = "Captain"
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME))
        }
        cursor.close()
        return name
    }

    // --- Other Catch Methods (Required by HomeDashboardActivity) ---
    // ... (Add startNewCatchRecord, insertFishDetail, getDashboardStats, etc. here)
    fun startNewCatchRecord(userId: Long, lat: Double, lon: Double): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(CATCH_USER_ID, userId)
            put(CATCH_GPS_LAT, lat)
            put(CATCH_GPS_LON, lon)
            put(CATCH_SYNCED, 0) // Starts as unsynced
        }
        val catchId = db.insert(TABLE_CATCH, null, cv)
        db.close()
        return catchId
    }

    fun insertFishDetail(
        catchId: Long,
        species: String,
        freshness: Double,
        length: Double,
        weight: Double,
        confidence: Double
    ): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(FISH_CATCH_ID, catchId)
            put(FISH_SPECIES, species)
            put(FISH_FRESHNESS_SCORE, freshness)
            put(FISH_EST_LENGTH, length)
            put(FISH_EST_WEIGHT, weight)
            put(FISH_CONFIDENCE, confidence)
        }
        val id = db.insert(TABLE_FISH_DETAILS, null, cv)
        db.close()
        return id != -1L
    }

    fun getDashboardStats(userId: Long): Triple<Int, Double, String> {
        val db = readableDatabase
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = formatter.format(Date())

        // 1. Total Scans (Individual fish counted today)
        val scansQuery = """
            SELECT COUNT(D.$FISH_DETAIL_ID)
            FROM $TABLE_FISH_DETAILS D
            INNER JOIN $TABLE_CATCH C ON D.$FISH_CATCH_ID = C.$CATCH_ID
            WHERE C.$CATCH_USER_ID = ? AND DATE(C.$CATCH_TIMESTAMP) = DATE(?)
        """.trimIndent()
        var scansCursor = db.rawQuery(scansQuery, arrayOf(userId.toString(), today))
        val totalScans = if (scansCursor.moveToFirst()) scansCursor.getInt(0) else 0
        scansCursor.close()

        // 2. Total Weight Today
        val weightQuery = """
            SELECT SUM(D.$FISH_EST_WEIGHT)
            FROM $TABLE_FISH_DETAILS D
            INNER JOIN $TABLE_CATCH C ON D.$FISH_CATCH_ID = C.$CATCH_ID
            WHERE C.$CATCH_USER_ID = ? AND DATE(C.$CATCH_TIMESTAMP) = DATE(?)
        """.trimIndent()
        var weightCursor = db.rawQuery(weightQuery, arrayOf(userId.toString(), today))
        val totalWeight = if (weightCursor.moveToFirst()) weightCursor.getDouble(0) else 0.0
        weightCursor.close()

        // 3. Top Species Today
        val speciesQuery = """
            SELECT D.$FISH_SPECIES, COUNT(D.$FISH_SPECIES) AS count
            FROM $TABLE_FISH_DETAILS D
            INNER JOIN $TABLE_CATCH C ON D.$FISH_CATCH_ID = C.$CATCH_ID
            WHERE C.$CATCH_USER_ID = ? AND DATE(C.$CATCH_TIMESTAMP) = DATE(?)
            GROUP BY D.$FISH_SPECIES
            ORDER BY count DESC
            LIMIT 1
        """.trimIndent()
        var speciesCursor = db.rawQuery(speciesQuery, arrayOf(userId.toString(), today))
        val topSpecies = if (speciesCursor.moveToFirst()) {
            speciesCursor.getString(speciesCursor.getColumnIndexOrThrow(FISH_SPECIES))
        } else {
            "N/A"
        }
        speciesCursor.close()

        db.close()
        return Triple(totalScans, totalWeight, topSpecies)
    }

    // --- Sync/Offline Methods ---
    fun getUnsyncedCatches(userId: Long): Cursor {
        val db = readableDatabase
        return db.rawQuery("""
            SELECT
                C.$CATCH_ID, C.$CATCH_TIMESTAMP, C.$CATCH_GPS_LAT, C.$CATCH_GPS_LON,
                D.$FISH_SPECIES, D.$FISH_EST_WEIGHT, D.$FISH_FRESHNESS_SCORE
            FROM $TABLE_CATCH C
            INNER JOIN $TABLE_FISH_DETAILS D ON C.$CATCH_ID = D.$FISH_CATCH_ID
            WHERE C.$CATCH_USER_ID = ? AND C.$CATCH_SYNCED = 0
            ORDER BY C.$CATCH_TIMESTAMP ASC
        """.trimIndent(), arrayOf(userId.toString()))
    }

    fun markCatchAsSynced(catchId: Long): Int {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(CATCH_SYNCED, 1)
        }
        val rowsAffected = db.update(
            TABLE_CATCH,
            cv,
            "$CATCH_ID = ?",
            arrayOf(catchId.toString())
        )
        db.close()
        return rowsAffected
    }
}