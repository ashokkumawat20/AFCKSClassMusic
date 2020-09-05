package `in`.afckstechnologies.afcksclassmusic.utils
import `in`.afckstechnologies.afcksclassmusic.models.MusicModelClass
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteException
//creating the database logic, extending the SQLiteOpenHelper base class
class DatabaseHandler(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "MusicDatabase"
        private val TABLE_CONTACTS = "tbl_music"
        private val KEY_ID = "id"
        private val KEY_CATEGORY = "category"
        private val KEY_VOLUME = "volume"
        private val KEY_LOOP = "looping"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //creating table with fields
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_CATEGORY + " TEXT,"
                + KEY_VOLUME + " TEXT," + KEY_LOOP + " TEXT" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        onCreate(db)
    }


    //method to insert data
    fun addMusic(emp: MusicModelClass):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
       // contentValues.put(KEY_ID, emp.mucid)
        contentValues.put(KEY_CATEGORY, emp.muccategory) // EmpModelClass Name
        contentValues.put(KEY_VOLUME,emp.mucvolume ) // EmpModelClass Phone
        contentValues.put(KEY_LOOP,emp.looping ) // EmpModelClass Phone
        // Inserting Row
        val success = db.insert(TABLE_CONTACTS, null, contentValues)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    //method to read data
    fun viewMusic():List<MusicModelClass>{
        val empList:ArrayList<MusicModelClass> = ArrayList<MusicModelClass>()
        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var mucid: Int
        var muccategory: String
        var mucvolume: String
        var looping: String

        if (cursor.moveToFirst()) {
            do {
                mucid = cursor.getInt(cursor.getColumnIndex("id"))
                muccategory = cursor.getString(cursor.getColumnIndex("category"))
                mucvolume = cursor.getString(cursor.getColumnIndex("volume"))
                looping = cursor.getString(cursor.getColumnIndex("looping"))
                val emp= MusicModelClass(mucid = mucid, muccategory = muccategory, mucvolume = mucvolume, looping = looping)
                empList.add(emp)
            } while (cursor.moveToNext())
        }
        return empList
    }
    //method to update data
    fun updateMusic(emp: MusicModelClass):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_VOLUME,emp.mucvolume ) // EmpModelClass Email

        // Updating Row
        val success = db.update(TABLE_CONTACTS, contentValues,"id="+emp.mucid,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }

    fun updateLooping(emp: MusicModelClass):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_LOOP,emp.looping ) // EmpModelClass Email

        // Updating Row
        val success = db.update(TABLE_CONTACTS, contentValues,"id="+emp.mucid,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    //method to delete data
    fun deleteMusic(emp: MusicModelClass):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, emp.mucid) // EmpModelClass UserId
        // Deleting Row
        val success = db.delete(TABLE_CONTACTS,"id="+emp.mucid,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
}