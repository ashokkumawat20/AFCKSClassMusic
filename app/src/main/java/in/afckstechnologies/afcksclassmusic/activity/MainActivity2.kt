package `in`.afckstechnologies.afcksclassmusic.activity

import `in`.afckstechnologies.afcksclassmusic.R
import `in`.afckstechnologies.afcksclassmusic.adapter.ClassMusicListAdpter
import `in`.afckstechnologies.afcksclassmusic.models.ClassMusicDAO
import `in`.afckstechnologies.afcksclassmusic.models.MusicModelClass
import `in`.afckstechnologies.afcksclassmusic.utils.Config
import `in`.afckstechnologies.afcksclassmusic.utils.DatabaseHandler
import `in`.afckstechnologies.afcksclassmusic.utils.JsonHelper
import `in`.afckstechnologies.afcksclassmusic.utils.WebClient
import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*


class MainActivity2 : AppCompatActivity() {

    private var classMusicList: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)
        classMusicList = findViewById(R.id.classMusicList) as RecyclerView
        // userAvailable(this, classMusicList!!).execute()
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File("$root/ClassMusic")
        myDir.mkdirs()

        for (f in myDir.listFiles()) {
            if (f.isFile)
                Log.d("Files Names ->", f.name)
            // Do your stuff
            val status =
                databaseHandler.addMusic(MusicModelClass(Integer.parseInt("1"), f.name, "8",""))
            if (status > -1) {
                Toast.makeText(applicationContext, "record save", Toast.LENGTH_LONG).show()

            }
        }

    }


    class userAvailable(
        val context: Context, val classMusicList: RecyclerView
    ) : AsyncTask<Void?, Void?, Void?>() {
        var mProgressDialog: ProgressDialog? = null
        var jsonArray: JSONArray? = null
        var trainerResponse: String? = ""
        var data: ArrayList<ClassMusicDAO?>? = null


        override fun onPreExecute() {
            super.onPreExecute()
            // Create a progressdialog
            mProgressDialog = ProgressDialog(context)
            // Set progressdialog title
            mProgressDialog!!.setTitle("Please Wait...")
            // Set progressdialog message
            mProgressDialog!!.setMessage("Loading...")
            //mProgressDialog.setIndeterminate(false);
// Show progressdialog
            mProgressDialog!!.show()
        }

        override fun doInBackground(vararg params: Void?): Void? {
            val json = JSONObject()
            try {
                json.put("trainer", "")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val serviceAccess = WebClient()
            trainerResponse = serviceAccess.SendHttpPost(Config.URL_getAllClassMusic, json)
            Log.i("resp", "advertisementListResponse$trainerResponse")
            data = ArrayList<ClassMusicDAO?>()
            if (trainerResponse!!.compareTo("") != 0) {
                if (isJSONValid(trainerResponse)) {

                    try {
                        val jsonHelper = JsonHelper()
                        data = jsonHelper.calssMusicList(trainerResponse)
                        jsonArray = JSONArray(trainerResponse)
                    } catch (e: JSONException) { // TODO Auto-generated catch block
                        e.printStackTrace()
                    }

                } else {

                    return null
                }
            } else {

                return null
            }

            return null
        }


        override fun onPostExecute(args: Void?) {
            if (data!!.size > 0) {
                // Close the progressdialog
                mProgressDialog!!.dismiss()
                classMusicList.layoutManager = LinearLayoutManager(context)
                classMusicList.adapter = ClassMusicListAdpter(data!!, context)


            } else { // Close the progressdialog

                mProgressDialog!!.dismiss()
            }
        }


    }

    //
    fun isJSONValid(callReoprtResponse2: String?): Boolean { // TODO Auto-generated method stub
        try {
            JSONObject(callReoprtResponse2)
        } catch (ex: JSONException) { // edited, to include @Arthur's comment
// e.g. in case JSONArray is valid as well...
            try {
                JSONArray(callReoprtResponse2)
            } catch (ex1: JSONException) {
                return false
            }
        }
        return true
    }
}