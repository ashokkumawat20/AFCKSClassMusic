package `in`.afckstechnologies.afcksclassmusic.adapter

import `in`.afckstechnologies.afcksclassmusic.R
import `in`.afckstechnologies.afcksclassmusic.models.ClassMusicDAO
import android.app.ProgressDialog
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.io.*
import java.net.URL
import java.net.URLConnection
import java.util.*


class ClassMusicListAdpter1(
    private val myAndroidOsList: ArrayList<ClassMusicDAO?>,
    private val context: Context

) : RecyclerView.Adapter<ClassMusicListAdpter1.MyViewHolder>() {

    var current: ClassMusicDAO? = null
    private var id: String? = ""
    private var tagId: Int? =null
    private var paylingFlag: String? = "0"
    private var pause: Boolean = false
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var runnable: Runnable
    private var handler: Handler = Handler()

    // declare the dialog as a member field of your activity


    val audioManager: AudioManager =
        context.getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager

    override fun getItemCount(): Int {
        return myAndroidOsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Get current position of item in recyclerview to bind data and assign values from list
        val myHolder = holder as ClassMusicListAdpter.MyViewHolder
        current = myAndroidOsList.get(position)
        myHolder.cat.setText(current!!.getCategory())
        myHolder.cat.tag = position

        myHolder.saveFile.tag = position
        myHolder.playBtn.tag = position
        myHolder.pauseBtn.tag = position

        myHolder.stopBtn.tag = position
        myHolder.loopingBtn.tag = position
        myHolder.loopingStopBtn.tag = position







        myHolder.seek_bar1.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
        myHolder.seek_bar1.setProgress(Integer.parseInt(current!!.getVolume()))
        myHolder.seek_bar1.tag = position

        myHolder.saveFile.setOnClickListener(View.OnClickListener { v ->
            val ID = v.tag as Int
            current = myAndroidOsList.get(ID)
            //Toast.makeText(context, "id is " + current.getId(), Toast.LENGTH_LONG).show();
            id = current!!.getId()
            //Toast.makeText(context, "id is " + current!!.getId(), Toast.LENGTH_LONG).show()
            val urlString =
                "https://afckstechnologies.in/AFCKS_RingTone/uploads/" + current!!.getRing_tone_path()
            DownloadAudioFromUrl(context, id).execute(urlString)
        })

        myHolder.playBtn.setOnClickListener(View.OnClickListener { v ->
            val ID = v.tag as Int

            current = myAndroidOsList.get(ID)

            //Toast.makeText(context, "id is " + current.getId(), Toast.LENGTH_LONG).show();
            id = current!!.getCategory()
            // Toast.makeText(context, "id is " + current!!.getId(), Toast.LENGTH_LONG).show()

            val filePath = Environment.getExternalStorageDirectory().absolutePath + "/ClassMusic" + File.separator + id + ".mp3"
            val uri1 = Uri.parse(filePath)
            try {


              /*  if (mediaPlayer.isPlaying ) {
                    pause = false
                    myHolder.seek_bar.setProgress(0)
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                    mediaPlayer.release()
                    handler.removeCallbacks(runnable)
                }
*/
                if (pause) {
                    mediaPlayer.seekTo(mediaPlayer.currentPosition)
                    mediaPlayer.start()
                    pause = false
                    Toast.makeText(context, "media playing", Toast.LENGTH_SHORT).show()
                } else {
                    // String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFCKS" + File.separator + "ashok.jpg";

                    if(paylingFlag.equals("0")) {

                        mediaPlayer = MediaPlayer.create(context, uri1)
                        audioManager.setMediaVolume(Integer.parseInt(current!!.getVolume()))
                        myHolder.seek_bar1.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
                        myHolder.seek_bar1.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
                        myHolder.seek_bar1.tag = position
                        mediaPlayer.start()
                        paylingFlag="1"
                        tagId=ID
                        current!!.setSelected(true)
                        Toast.makeText(context, "media playing", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        if (mediaPlayer.isPlaying || pause.equals(true)) {
                            mediaPlayer.pause()
                           /* current = myAndroidOsList.get(tagId!!)
                         if(current!!.isSelected())
                         {
                             current!!.setSelected(false)
                             myHolder.playBtn.isEnabled = true
                             myHolder.pauseBtn.isEnabled = false
                             myHolder.stopBtn.isEnabled = true
                             myHolder.playBtn.visibility = View.VISIBLE
                             myHolder.pauseBtn.visibility = View.GONE

                             notifyItemInserted(tagId!!);
                             notifyItemRangeChanged(tagId!!, myAndroidOsList.size);
                         }
                            current = myAndroidOsList.get(ID)*/
                            mediaPlayer = MediaPlayer.create(context, uri1)
                            audioManager.setMediaVolume(Integer.parseInt(current!!.getVolume()))
                            myHolder.seek_bar1.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
                            myHolder.seek_bar1.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
                            myHolder.seek_bar1.tag = position
                            mediaPlayer.start()
                            current!!.setSelected(true)
                            tagId=ID
                            Toast.makeText(context, "media playing", Toast.LENGTH_SHORT).show()

                        }
                    }

                }

              //  initializeSeekBar(myHolder)
                myHolder.seek_bar.max = mediaPlayer.seconds

                runnable = Runnable {
                    myHolder.seek_bar.progress = mediaPlayer.currentSeconds
                    myHolder.seek_bar.tag = position

                    myHolder.tv_pass.text = "${mediaPlayer.currentSeconds} sec"
                    myHolder.tv_pass.tag = position
                    val diff = mediaPlayer.seconds - mediaPlayer.currentSeconds
                    myHolder.tv_due.text = "$diff sec"
                    myHolder.tv_due.tag = position
                    handler.postDelayed(runnable, 1000)
                }
                handler.postDelayed(runnable, 1000)

                myHolder.playBtn.isEnabled = false
                myHolder.pauseBtn.isEnabled = true
                myHolder.stopBtn.isEnabled = true
                myHolder.loopingBtn.isEnabled = true
                myHolder.loopingStopBtn.isEnabled = true

                myHolder.playBtn.visibility = View.GONE
                myHolder.pauseBtn.visibility = View.VISIBLE

                myHolder.loopingBtn.visibility = View.VISIBLE

                myHolder.stopBtn.visibility = View.VISIBLE
                mediaPlayer.setOnCompletionListener {
                    myHolder.pauseBtn.visibility = View.GONE
                    myHolder.playBtn.visibility = View.VISIBLE
                    myHolder.playBtn.isEnabled = true
                    myHolder.pauseBtn.isEnabled = false
                    myHolder.stopBtn.isEnabled = false
                    Toast.makeText(context, "end", Toast.LENGTH_SHORT).show()
                }
            }catch (e: Exception) {
                // handler
                //Toast.makeText(context, "Please download file...", Toast.LENGTH_SHORT).show()
            }

        })

        myHolder.pauseBtn.setOnClickListener(View.OnClickListener { v ->
            val ID = v.tag as Int
            current = myAndroidOsList.get(ID)
            //Toast.makeText(context, "id is " + current.getId(), Toast.LENGTH_LONG).show();
            id = current!!.getId()
            //Toast.makeText(context, "id is " + current!!.getId(), Toast.LENGTH_LONG).show()
            mediaPlayer.pause()
            pause = true
            paylingFlag="2"
            myHolder.playBtn.isEnabled = true
            myHolder.pauseBtn.isEnabled = false
            myHolder.stopBtn.isEnabled = true
            myHolder.playBtn.visibility = View.VISIBLE
            myHolder.pauseBtn.visibility = View.GONE
            Toast.makeText(context, "media pause", Toast.LENGTH_SHORT).show()
        })

        myHolder.stopBtn.setOnClickListener(View.OnClickListener { v ->
            val ID = v.tag as Int
            current = myAndroidOsList.get(ID)
            //Toast.makeText(context, "id is " + current.getId(), Toast.LENGTH_LONG).show();
            id = current!!.getId()
            paylingFlag="0"
            //Toast.makeText(context, "id is " + current!!.getId(), Toast.LENGTH_LONG).show()
            if (mediaPlayer.isPlaying || pause.equals(true)) {
                pause = false
                myHolder.seek_bar.setProgress(0)
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
                handler.removeCallbacks(runnable)

                myHolder.pauseBtn.visibility = View.GONE
                myHolder.loopingBtn.visibility = View.GONE
                myHolder.loopingStopBtn.visibility = View.GONE
                myHolder.stopBtn.visibility = View.GONE
                myHolder.playBtn.visibility = View.VISIBLE

                myHolder.playBtn.isEnabled = true
                myHolder.pauseBtn.isEnabled = false
                myHolder.stopBtn.isEnabled = false

                myHolder.tv_pass.text = ""
                myHolder.tv_due.text = ""
                Toast.makeText(context, "media stop", Toast.LENGTH_SHORT).show()
            }
        })

        myHolder.loopingBtn.setOnClickListener(View.OnClickListener { v ->
            val ID = v.tag as Int
            current = myAndroidOsList.get(ID)
            //Toast.makeText(context, "id is " + current.getId(), Toast.LENGTH_LONG).show();
            id = current!!.getId()
            //Toast.makeText(context, "id is " + current!!.getId(), Toast.LENGTH_LONG).show()
            mediaPlayer.isLooping = true
            myHolder.loopingBtn.visibility = View.GONE
            myHolder.loopingStopBtn.visibility = View.VISIBLE
        })
        myHolder.loopingStopBtn.setOnClickListener(View.OnClickListener { v ->
            val ID = v.tag as Int
            current = myAndroidOsList.get(ID)
            //Toast.makeText(context, "id is " + current.getId(), Toast.LENGTH_LONG).show();
            id = current!!.getId()
            //Toast.makeText(context, "id is " + current!!.getId(), Toast.LENGTH_LONG).show()
            mediaPlayer.isLooping = false
            myHolder.loopingStopBtn.visibility = View.GONE
            myHolder.loopingBtn.visibility = View.VISIBLE
        })

        // Seek bar change listener
        myHolder.seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if(!paylingFlag.equals("0")) {
                    if (b) {
                        mediaPlayer.seekTo(i * 1000)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

        myHolder.seek_bar1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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

    }

    private fun updateAt(id: Int) {

    }

    private fun initializeSeekBar(myHolder: MyViewHolder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        myHolder.seek_bar.max = mediaPlayer.seconds
        runnable = Runnable {
            myHolder.seek_bar.progress = mediaPlayer.currentSeconds

            myHolder.tv_pass.text = "${mediaPlayer.currentSeconds} sec"
            val diff = mediaPlayer.seconds - mediaPlayer.currentSeconds
            myHolder.tv_due.text = "$diff sec"

            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.layout_class_music, parent, false)
        return MyViewHolder(v)
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var cat: TextView
        var saveFile: ImageView
        var playBtn: ImageView
        var pauseBtn: ImageView
        var stopNotBtn: ImageView
        var seek_bar: SeekBar
        var seek_bar1: SeekBar
        var tv_pass: TextView
        var tv_due: TextView
        var stopBtn: ImageView
        var loopingBtn: ImageView
        var loopingStopBtn: ImageView
        var loopingNotClickBtn: ImageView

        // create constructor to get widget reference
        init {
            cat = itemView.findViewById<View>(R.id.cat) as TextView
            saveFile = itemView.findViewById<View>(R.id.saveFile) as ImageView
            playBtn = itemView.findViewById<View>(R.id.playBtn) as ImageView
            pauseBtn = itemView.findViewById<View>(R.id.pauseBtn) as ImageView
            stopNotBtn = itemView.findViewById<View>(R.id.stopNotBtn) as ImageView
            seek_bar = itemView.findViewById<View>(R.id.seek_bar) as SeekBar
            seek_bar1 = itemView.findViewById<View>(R.id.seek_bar1) as SeekBar
            tv_pass = itemView.findViewById<View>(R.id.tv_pass) as TextView
            tv_due = itemView.findViewById<View>(R.id.tv_due) as TextView
            stopBtn = itemView.findViewById<View>(R.id.stopBtn) as ImageView
            loopingBtn = itemView.findViewById<View>(R.id.loopingBtn) as ImageView
            loopingStopBtn = itemView.findViewById<View>(R.id.loopingStopBtn) as ImageView
            loopingNotClickBtn = itemView.findViewById<View>(R.id.loopingNotClickBtn) as ImageView
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


    class DownloadAudioFromUrl(val context: Context, val id: String?) :
        AsyncTask<String, String, String>() {
        var mProgressDialog: ProgressDialog? = null
        @Override
        override fun onPreExecute() {
            super.onPreExecute();


// instantiate it within the onCreate method
            mProgressDialog = ProgressDialog(context)
            mProgressDialog!!.setMessage("Downloading...");
            mProgressDialog!!.setIndeterminate(true);
            mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog!!.setCancelable(true);
            mProgressDialog!!.show();
        }

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
                val file = File(myDir, "music_" + id + ".mp3")
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

        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate("" + values)
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog!!.isIndeterminate = false
            mProgressDialog!!.max = 100
            mProgressDialog!!.progress = values[0]!!.toInt()
        }

        override fun onPostExecute(result: String?) {
            mProgressDialog!!.dismiss();
            // Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()

        }
    }

}
