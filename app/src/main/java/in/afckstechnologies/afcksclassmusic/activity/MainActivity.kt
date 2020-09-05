package `in`.afckstechnologies.afcksclassmusic.activity

import `in`.afckstechnologies.afcksclassmusic.R
import `in`.afckstechnologies.afcksclassmusic.adapter.ClassMusicListAdpter
import `in`.afckstechnologies.afcksclassmusic.models.ClassMusicDAO
import `in`.afckstechnologies.afcksclassmusic.models.MusicModelClass
import `in`.afckstechnologies.afcksclassmusic.utils.DatabaseHandler
import `in`.afckstechnologies.afcksclassmusic.utils.PathUtil
import `in`.afckstechnologies.afcksclassmusic.utils.RealPathUtil
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*


class MainActivity : AppCompatActivity() {

    private var classMusicList: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        classMusicList = findViewById(R.id.classMusicList) as RecyclerView
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)

        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File("$root/ClassMusic")
        myDir.mkdirs()


        val fileList: ArrayList<String> = ArrayList()
        for (f in myDir.listFiles()) {
            fileList?.add(f.name)
        }
        val emp: List<MusicModelClass> = databaseHandler.viewMusic()
        if (emp!!.size > 0) {
            for (e in emp) {

                Log.d("Files Names ->", e.muccategory)
                // Do your stuff
                if (!fileList.contains(e.muccategory)) {
                    val status = databaseHandler.deleteMusic(MusicModelClass(e.mucid, "", "",""))

                }
            }

        }

        for (f in myDir.listFiles()) {
            if (f.isFile)
                Log.d("Files Names ->", f.name)
            // Do your stuff
            if (emp!!.size > 0) {
                if (!emp.any { it.muccategory == "" + f.name }) {

                    // if (emp.contains(f.name)) {
                    val status = databaseHandler.addMusic(
                        MusicModelClass(
                            Integer.parseInt("1"), f.name,
                            "8"
                       ,"0" )
                    )

                    if (status > -1) {
                        //  Toast.makeText(context, "record save", Toast.LENGTH_LONG).show()

                    }
                }
            } else {
                val status = databaseHandler.addMusic(
                    MusicModelClass(
                        Integer.parseInt("1"), f.name,
                        "8"
                    ,"0")
                )

                if (status > -1) {
                    //  Toast.makeText(context, "record save", Toast.LENGTH_LONG).show()

                }
            }

        }
        userAvailable(this, classMusicList!!).execute()
        refresh.setOnClickListener {

            for (f in myDir.listFiles()) {
                fileList?.add(f.name)
            }
            val emp: List<MusicModelClass> = databaseHandler.viewMusic()
            if (emp!!.size > 0) {
                for (e in emp) {

                    Log.d("Files Names ->", e.muccategory)
                    // Do your stuff
                    if (!fileList.contains(e.muccategory)) {
                        val status = databaseHandler.deleteMusic(MusicModelClass(e.mucid, "", "",""))

                    }
                }

            }

            for (f in myDir.listFiles()) {
                if (f.isFile)
                    Log.d("Files Names ->", f.name)
                // Do your stuff
                if (emp!!.size > 0) {
                    if (!emp.any { it.muccategory == "" + f.name }) {

                        // if (emp.contains(f.name)) {
                        val status = databaseHandler.addMusic(
                            MusicModelClass(
                                Integer.parseInt("1"), f.name,
                                "8"
                            ,"0")
                        )

                        if (status > -1) {
                            //  Toast.makeText(context, "record save", Toast.LENGTH_LONG).show()

                        }
                    }
                } else {
                    val status = databaseHandler.addMusic(
                        MusicModelClass(
                            Integer.parseInt("1"), f.name,
                            "8"
                        ,"0")
                    )

                    if (status > -1) {
                        //  Toast.makeText(context, "record save", Toast.LENGTH_LONG).show()

                    }
                }

            }
            userAvailable(this, classMusicList!!).execute()

        }
        addMusic.setOnClickListener {

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            val view: View = LayoutInflater.from(this).inflate(R.layout.alert_layout, null)
            builder.setView(view)
            val edt = view.findViewById<EditText>(R.id.passcode)
            builder.setPositiveButton("Submit",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    val passcode = edt.text.toString()
                    if (!passcode.equals("")) {
                        if (passcode.equals("1516")) {
                            val intent: Intent
                            intent = Intent()
                            intent.action = Intent.ACTION_GET_CONTENT
                            intent.type = "audio/*"
                            startActivityForResult(
                                Intent.createChooser(
                                    intent,
                                    getString(R.string.select_audio_file_title)
                                ), 1
                            )
                        } else {
                            Toast.makeText(this, "Please Enter Correct Passcode", Toast.LENGTH_LONG)
                                .show()
                        }
                    } else {
                        Toast.makeText(this, "Please Enter Passcode", Toast.LENGTH_LONG).show()
                    }
                })


            builder.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })


            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()


        }
        close.setOnClickListener {
            finishAffinity()
            System.exit(0)

        }

    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                val audioFileUri: Uri = data!!.data
                //showDialogMp(PathUtil.getUriRealPath(this, audioFileUri))

                showDialogMp(RealPathUtil.getRealPath(this, audioFileUri))

            }
        }
    }

    private fun showDialogMp(inputPath: String?) {
        //  Toast.makeText(this, ""+inputPath, Toast.LENGTH_LONG).show()

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val view: View = LayoutInflater.from(this).inflate(R.layout.filename_layout, null)
        builder.setView(view)
        val edt = view.findViewById<EditText>(R.id.passcode)
        builder.setPositiveButton("Add",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                val inputFile = edt.text.toString()
                if (!inputFile.equals("")) {


                    if (inputPath != null) {
                        copyFile(inputPath, inputFile)
                    }
                } else {
                    Toast.makeText(this, "Please Enter file Name", Toast.LENGTH_LONG).show()
                }
            })


        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })


        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()


    }

    private fun copyFile(inputPath: String?, inputFile: String) {

        try {

            val filein = File(inputPath)
            val to = File(Environment.getExternalStorageDirectory().absolutePath + "/ClassMusic/"+inputFile+".mp3")
            filein.copyTo(to, true, DEFAULT_BUFFER_SIZE)
            userAvailable(this, classMusicList!!).execute()
        } catch (fnfe1: FileNotFoundException) {
            Log.e("tag", fnfe1.message);
        } catch (e: Exception) {
            Log.e("tag", e.message);
        }

    }

    class userAvailable(
        val context: Context, val classMusicList: RecyclerView
    ) : AsyncTask<Void?, Void?, Void?>() {
        var mProgressDialog: ProgressDialog? = null
        var jsonArray: JSONArray? = null
        var trainerResponse: String? = ""
        var data: ArrayList<ClassMusicDAO?>? = null

        var emp: List<MusicModelClass>? = null
        override fun onPreExecute() {
            super.onPreExecute()
            // Create a progressdialog
            /*  mProgressDialog = ProgressDialog(context)
              // Set progressdialog title
              mProgressDialog!!.setTitle("Please Wait...")
              // Set progressdialog message
              mProgressDialog!!.setMessage("Loading...")
              //mProgressDialog.setIndeterminate(false);
  // Show progressdialog
              mProgressDialog!!.show()*/
        }

        override fun doInBackground(vararg params: Void?): Void? {

            //creating the instance of DatabaseHandler class
            data = ArrayList<ClassMusicDAO?>()
            data!!.clear()
            val databaseHandler: DatabaseHandler = DatabaseHandler(context)
            //   emp = databaseHandler.viewMusic()
            val emp: List<MusicModelClass> = databaseHandler.viewMusic()
            val root = Environment.getExternalStorageDirectory().toString()
            val myDir = File("$root/ClassMusic")
            if (emp!!.size > 0) {

                //Log.d("total count",""+cursor.getCount());

                var index = 0
                val empArrayName = Array<String>(emp!!.size) { "null" }
                var d = Log.d("emp", "" + empArrayName)
                for (e in emp) {


                    data!!.add(
                        ClassMusicDAO(
                            e.mucid.toString(),
                            e.muccategory.replace(".mp3", ""),
                            e.mucvolume,e.looping,
                            false

                        )
                    )
                    index++
                }
            }

            return null
        }


        override fun onPostExecute(args: Void?) {
            if (data!!.size > 0) {
                // Close the progressdialog
                //  mProgressDialog!!.dismiss()
                classMusicList.layoutManager = LinearLayoutManager(context)
                classMusicList.adapter = ClassMusicListAdpter(data!!, context)


            } else { // Close the progressdialog
                classMusicList.layoutManager = LinearLayoutManager(context)
                classMusicList.adapter = ClassMusicListAdpter(data!!, context)
                // mProgressDialog!!.dismiss()
            }
        }


    }
//


    //
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun getPathFromUri(context: Context?, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) { // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(
                    this,
                    contentUri,
                    null,
                    null
                )
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(
                    this,
                    contentUri,
                    selection,
                    selectionArgs
                )
            }
        } else if ("content".equals(
                uri.scheme,
                ignoreCase = true
            )
        ) { // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment
            else getDataColumn(
                this,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Audio.AudioColumns.DATA
        val projection = arrayOf(column)
        try {
            cursor =
                context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

}