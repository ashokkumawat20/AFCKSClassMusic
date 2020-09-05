package `in`.afckstechnologies.afcksclassmusic.activity

import `in`.afckstechnologies.afcksclassmusic.R
import `in`.afckstechnologies.afcksclassmusic.adapter.ClassMusicListAdpter
import `in`.afckstechnologies.afcksclassmusic.models.ClassMusicDAO
import `in`.afckstechnologies.afcksclassmusic.utils.Config
import `in`.afckstechnologies.afcksclassmusic.utils.JsonHelper
import `in`.afckstechnologies.afcksclassmusic.utils.WebClient
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main1.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.URL
import java.net.URLConnection
import java.util.*


class MainActivity1 : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var runnable: Runnable
    private var handler: Handler = Handler()
    private var pause: Boolean = false
    val androidName = listOf<String>("Cupcake", "Donut", "Eclair", "Froyo",
        "Gingerbread", "Honeycomb", "Ice Cream Sandwich", "JellyBean", "Kitkat",
        "Lollipop", "Marshmallow", "Nougat", "Oreo")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)
        // Start the media player
       // classMusicList.layoutManager=LinearLayoutManager(this, LinearLayout.VERTICAL,false)
       // classMusicList.adapter=ClassMusicListAdpter(androidName,this)

    mediaPlayer.start();
        userAvailable(this).execute()
        playBtn.setOnClickListener {
            if (pause) {
                mediaPlayer.seekTo(mediaPlayer.currentPosition)
                mediaPlayer.start()
                pause = false
                Toast.makeText(this, "media playing", Toast.LENGTH_SHORT).show()
            } else {
                // String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFCKS" + File.separator + "ashok.jpg";
                val filePath =
                    Environment.getExternalStorageDirectory().absolutePath + "/ClassMusic" + File.separator + "audio.mp3"
                val uri1 =
                    Uri.parse(filePath)
                val uri: Uri =
                    Uri.parse("android.resource://$packageName/raw/corporate")
                mediaPlayer = MediaPlayer.create(applicationContext, uri1)
                mediaPlayer.start()
                Toast.makeText(this, "media playing", Toast.LENGTH_SHORT).show()

            }
            initializeSeekBar()
            playBtn.isEnabled = false
            pauseBtn.isEnabled = true
            stopBtn.isEnabled = true
            loopingBtn.isEnabled = true
            loopingStopBtn.isEnabled = true
            loopingNotClickBtn.isEnabled = true
            playBtn.visibility = View.GONE
            pauseBtn.visibility = View.VISIBLE
            loopingNotClickBtn.visibility = View.GONE
            loopingBtn.visibility = View.VISIBLE
            stopNotBtn.visibility = View.GONE
            stopBtn.visibility = View.VISIBLE
            mediaPlayer.setOnCompletionListener {
                pauseBtn.visibility = View.GONE
                playBtn.visibility = View.VISIBLE
                playBtn.isEnabled = true
                pauseBtn.isEnabled = false
                stopBtn.isEnabled = false
                Toast.makeText(this, "end", Toast.LENGTH_SHORT).show()
            }
        }
        // Pause the media player
        pauseBtn.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                pause = true
                playBtn.isEnabled = true
                pauseBtn.isEnabled = false
                stopBtn.isEnabled = true
                playBtn.visibility = View.VISIBLE
                pauseBtn.visibility = View.GONE
                Toast.makeText(this, "media pause", Toast.LENGTH_SHORT).show()
            }
        }
        // Stop the media player
        stopBtn.setOnClickListener {
            if (mediaPlayer.isPlaying || pause.equals(true)) {
                pause = false
                seek_bar.setProgress(0)
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
                handler.removeCallbacks(runnable)

                pauseBtn.visibility = View.GONE
                loopingBtn.visibility = View.GONE
                loopingStopBtn.visibility = View.GONE
                stopBtn.visibility = View.GONE
                playBtn.visibility = View.VISIBLE
                stopNotBtn.visibility = View.VISIBLE
                loopingNotClickBtn.visibility = View.VISIBLE
                playBtn.isEnabled = true
                pauseBtn.isEnabled = false
                stopBtn.isEnabled = false

                tv_pass.text = ""
                tv_due.text = ""
                Toast.makeText(this, "media stop", Toast.LENGTH_SHORT).show()
            }
        }
        loopingBtn.setOnClickListener {
            mediaPlayer.isLooping = true
            loopingBtn.visibility = View.GONE
            loopingStopBtn.visibility = View.VISIBLE
        }
        loopingStopBtn.setOnClickListener {
            mediaPlayer.isLooping = false
            loopingStopBtn.visibility = View.GONE
            loopingBtn.visibility = View.VISIBLE

        }
        // Get the audio manager system service
        val audioManager: AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager.setMediaVolume(5)
        seek_bar1.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seek_bar1.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        // Seek bar change listener
        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    mediaPlayer.seekTo(i * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

        seek_bar1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }

        })
        saveFile.setOnClickListener {
            val urlString = "https://afckstechnologies.in/AFCKS_RingTone/uploads/audio.mp3"
            DownloadAudioFromUrl(this).execute(urlString)
        }
    }

    // Method to initialize seek bar and audio stats
    private fun initializeSeekBar() {
        seek_bar.max = mediaPlayer.seconds

        runnable = Runnable {
            seek_bar.progress = mediaPlayer.currentSeconds

            tv_pass.text = "${mediaPlayer.currentSeconds} sec"
            val diff = mediaPlayer.seconds - mediaPlayer.currentSeconds
            tv_due.text = "$diff sec"

            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }
}

// Creating an extension property to get the media player time duration in seconds
val MediaPlayer.seconds: Int
    get() {
        return this.duration / 1000
    }
// Creating an extension property to get media player current position in seconds
val MediaPlayer.currentSeconds: Int
    get() {
        return this.currentPosition / 1000
    }

// Extension function to change media volume programmatically
fun AudioManager.setMediaVolume(volumeIndex: Int) {
    // Set media volume level
    this.setStreamVolume(
        AudioManager.STREAM_MUSIC, // Stream type
        volumeIndex, // Volume index
        AudioManager.FLAG_SHOW_UI// Flags
    )
}


// Extension property to get media maximum volume index
val AudioManager.mediaMaxVolume: Int
    get() = this.getStreamMaxVolume(AudioManager.STREAM_MUSIC)


// Extension property to get media/music current volume index
val AudioManager.mediaCurrentVolume: Int
    get() = this.getStreamVolume(AudioManager.STREAM_MUSIC)


// Extension function to show toast message
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}


class DownloadAudioFromUrl(val context: Context) : AsyncTask<String, String, String>() {

    override fun doInBackground(vararg p0: String?): String {

        var count: Int = 0
        try {
            val url = URL(p0[0])
            val conection: URLConnection = url.openConnection()
            conection.connect()
            // this will be useful so that you can show a tipical 0-100%
// progress bar
            val lenghtOfFile: Int = conection.getContentLength()
            // download the file
            val input: InputStream = BufferedInputStream(url.openStream(), 8192)
            // Output stream
            // imageView.setImageBitmap(bitmap);
            val root =
                Environment.getExternalStorageDirectory().toString()
            val myDir = File("$root/ClassMusic")
            myDir.mkdirs()
            val file = File(myDir, "audio.mp3")
            if (file.exists()) file.delete()
            val output: OutputStream = FileOutputStream(file)

            //  val output: OutputStream = FileOutputStream(Environment.getExternalStorageDirectory().toString().toString() + "/ClassMusic/audio.mp3")
            val data = ByteArray(1024)
            var total: Long = 0
            while (input.read(data).also({ count = it }) != -1) {
                total += count.toLong()
                // publishing the progress....
// After this onProgressUpdate will be called
                publishProgress("" + (total * 100 / lenghtOfFile).toInt())
                // writing data to file
                output.write(data, 0, count)
            }
            // flushing output
            output.flush()
            // closing streams
            output.close()
            input.close()
        } catch (e: Exception) {
            Log.e("Error: ", e.message)
        }
        return "Success"
    }
}

var mProgressDialog: ProgressDialog? = null
private val jsonObj: JSONObject? = null
private var jsonObject: JSONObject? = null
var jsonArray: JSONArray? = null
var trainerResponse: String? = ""
var status: Boolean? = null
var data: ArrayList<ClassMusicDAO?>? = null
var classMusicListAdpter: ClassMusicListAdpter? = null


//
//
val androidName = listOf<String>("Cupcake", "Donut", "Eclair", "Froyo",
    "Gingerbread", "Honeycomb", "Ice Cream Sandwich", "JellyBean", "Kitkat",
    "Lollipop", "Marshmallow", "Nougat", "Oreo")

class userAvailable(val context: Context) : AsyncTask<Void?, Void?, Void?>() {

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


        /*   val utility = WebServiceUtility(context)
           utility.getString(Config.URL_getAllClassMusic, json, object : WebServiceUtility.VolleyCallback {
                   @Throws(OperationApplicationException::class, RemoteException::class)
                   override fun onSuccess(result: String?) {
                       Log.d("data: ", result)
                       trainerResponse = result
                       data =  ArrayList<ClassMusicDAO?>()
                       if (trainerResponse!!.compareTo("") != 0) {
                           if (isJSONValid(trainerResponse)) {
                               try {
                                   jsonObject = JSONObject(trainerResponse)
                                  Log.d("value",""+jsonObject!!.getBoolean("status"))
                                   if(jsonObject!!.getBoolean("status").equals(true))
                                   {
                                       val jsonHelper = JsonHelper()
                                       data = jsonHelper.calssMusicList(trainerResponse)


                                   }


                               } catch (e: JSONException) { // TODO Auto-generated catch block
                                   e.printStackTrace()
                               }
                           }
                       }
                   }

                   override fun onRequestError(errorMessage: VolleyError?) {

                   }
               })


           return null*/


        val serviceAccess = WebClient()



        trainerResponse = serviceAccess.SendHttpPost(Config.URL_getAllClassMusic, json)
        Log.i("resp", "advertisementListResponse$trainerResponse")
        data =  ArrayList<ClassMusicDAO?>()
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
        if (data!!.size>0) {
            // Close the progressdialog
            mProgressDialog!!.dismiss()

       /*  //  classMusicListAdpter = ClassMusicListAdpter(androidName,context)
           // classMusicList!!.setAdapter(classMusicListAdpter)
          //  classMusicList!!.setLayoutManager(LinearLayoutManager(context))
            classMusicList = (context).findViewById<View>(R.id.classMusicList) as RecyclerView
            classMusicList?.layoutManager = LinearLayoutManager(context)
            classMusicList?.adapter = ClassMusicListAdpter(androidName,context)
            (classMusicList?.adapter as ClassMusicListAdpter).setMessage(androidName)
           // classMusicList!!.layoutManager=LinearLayoutManager(context, LinearLayout.VERTICAL,false)
           // classMusicList!!.adapter=ClassMusicListAdpter(androidName,context)
*/


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

