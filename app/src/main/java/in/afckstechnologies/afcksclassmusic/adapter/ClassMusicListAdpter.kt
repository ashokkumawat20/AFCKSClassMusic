package `in`.afckstechnologies.afcksclassmusic.adapter

import `in`.afckstechnologies.afcksclassmusic.R
import `in`.afckstechnologies.afcksclassmusic.models.ClassMusicDAO
import `in`.afckstechnologies.afcksclassmusic.models.MusicModelClass
import `in`.afckstechnologies.afcksclassmusic.utils.DatabaseHandler
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.util.*


class ClassMusicListAdpter(
    private val myAndroidOsList: ArrayList<ClassMusicDAO?>,
    private val context: Context

) : RecyclerView.Adapter<ClassMusicListAdpter.MyViewHolder>() {

    var current: ClassMusicDAO? = null
    private var id: String? = ""
    private var tagId: Int? = null
    private var paylingFlag: String? = "0"
    private var pause: Boolean = false
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var runnable: Runnable
    private var handler: Handler = Handler()
    //creating the instance of DatabaseHandler class
    val databaseHandler: DatabaseHandler = DatabaseHandler(context)
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
        myHolder.relVol.tag = position
        myHolder.relVol1.tag = position
        myHolder.deleteBtn.tag = position
        myHolder.lay.tag = position

        if (current!!.getLooping().equals("1")) {
            myHolder.loopingBtn.visibility = View.GONE
            myHolder.loopingStopBtn.visibility = View.VISIBLE
        } else {

            myHolder.loopingStopBtn.visibility = View.GONE
            myHolder.loopingBtn.visibility = View.VISIBLE
        }

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current!!.getVolume()!!.toInt(), 0);
        myHolder.seek_bar1.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
        myHolder.seek_bar1.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
        myHolder.seek_bar1.tag = position

        myHolder.playBtn.setOnClickListener(View.OnClickListener { v ->
            val ID = v.tag as Int

            current = myAndroidOsList.get(ID)
            current!!.setSelected(true)
            tagId = ID
            notifyDataSetChanged();
            //Toast.makeText(context, "id is " + current.getId(), Toast.LENGTH_LONG).show();
            id = current!!.getCategory()
            // Toast.makeText(context, "id is " + current!!.getId(), Toast.LENGTH_LONG).show()


            val filePath =
                Environment.getExternalStorageDirectory().absolutePath + "/ClassMusic" + File.separator + id + ".mp3"
            val uri1 = Uri.parse(filePath)
            try {
                if (pause) {
                    mediaPlayer.seekTo(mediaPlayer.currentPosition)
                    mediaPlayer.start()
                    pause = false
                    Toast.makeText(context, "media playing", Toast.LENGTH_SHORT).show()
                } else {
                    // String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFCKS" + File.separator + "ashok.jpg";

                    if (paylingFlag.equals("0")) {

                        mediaPlayer = MediaPlayer.create(context, uri1)
                        audioManager.setStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            current!!.getVolume()!!.toInt(),
                            0
                        );
                        myHolder.seek_bar1.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
                        myHolder.seek_bar1.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
                        myHolder.seek_bar1.tag = position
                        mediaPlayer.start()
                        paylingFlag = "1"
                        tagId = ID
                        if (current!!.getLooping().equals("1")) {
                            try {
                                mediaPlayer.isLooping = true

                            } catch (e: Exception) {
                                // handler
                                //Toast.makeText(context, "Please download file...", Toast.LENGTH_SHORT).show()
                            }
                        }
                        Toast.makeText(context, "media playing", Toast.LENGTH_SHORT).show()
                    } else {
                        if (mediaPlayer.isPlaying || pause.equals(true)) {
                            mediaPlayer.reset()// stops any current playing song
                            mediaPlayer = MediaPlayer.create(context, uri1)
                            audioManager.setStreamVolume(
                                AudioManager.STREAM_MUSIC,
                                current!!.getVolume()!!.toInt(),
                                0
                            );
                            myHolder.seek_bar1.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
                            myHolder.seek_bar1.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
                            myHolder.seek_bar1.tag = position
                            mediaPlayer.start()

                            if (current!!.getLooping().equals("1")) {
                                try {
                                    mediaPlayer.isLooping = true

                                } catch (e: Exception) {
                                    // handler
                                    //Toast.makeText(context, "Please download file...", Toast.LENGTH_SHORT).show()
                                }
                            }
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
                myHolder.playBtn.visibility = View.GONE
                myHolder.pauseBtn.visibility = View.VISIBLE

                mediaPlayer.setOnCompletionListener {
                    myHolder.pauseBtn.visibility = View.GONE
                    myHolder.playBtn.visibility = View.VISIBLE
                    myHolder.playBtn.isEnabled = true
                    myHolder.pauseBtn.isEnabled = false

                    Toast.makeText(context, "end", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // handler
                //Toast.makeText(context, "Please download file...", Toast.LENGTH_SHORT).show()
            }

        })
        if (tagId !== position) {

            myHolder.pauseBtn.visibility = View.GONE
            myHolder.playBtn.visibility = View.VISIBLE
            myHolder.playBtn.isEnabled = true
            myHolder.pauseBtn.isEnabled = false
            myHolder.lay.setBackgroundColor(Color.parseColor("#008577"))
        } else {
            myHolder.lay.setBackgroundColor(Color.parseColor("#00574B"))
        }
        myHolder.pauseBtn.setOnClickListener(View.OnClickListener { v ->
            val ID = v.tag as Int
            current = myAndroidOsList.get(ID)
            //Toast.makeText(context, "id is " + current.getId(), Toast.LENGTH_LONG).show();
            id = current!!.getId()
            //Toast.makeText(context, "id is " + current!!.getId(), Toast.LENGTH_LONG).show()
            mediaPlayer.pause()
            pause = true
            paylingFlag = "2"
            myHolder.playBtn.isEnabled = true
            myHolder.pauseBtn.isEnabled = false
            myHolder.stopBtn.isEnabled = true
            myHolder.playBtn.visibility = View.VISIBLE
            myHolder.pauseBtn.visibility = View.GONE
            Toast.makeText(context, "media pause", Toast.LENGTH_SHORT).show()
        })

        myHolder.stopBtn.setOnClickListener(View.OnClickListener { v ->
            val ID = v.tag as Int
            if (tagId === ID) {
                try {
                    current = myAndroidOsList.get(ID)
                    //Toast.makeText(context, "id is " + current.getId(), Toast.LENGTH_LONG).show();
                    id = current!!.getId()
                    paylingFlag = "0"
                    //Toast.makeText(context, "id is " + current!!.getId(), Toast.LENGTH_LONG).show()
                    if (mediaPlayer.isPlaying || pause.equals(true)) {
                        pause = false
                        myHolder.seek_bar.setProgress(0)
                        mediaPlayer.stop()
                        mediaPlayer.reset()
                        mediaPlayer.release()
                        handler.removeCallbacks(runnable)

                        myHolder.pauseBtn.visibility = View.GONE
                        myHolder.playBtn.visibility = View.VISIBLE

                        myHolder.playBtn.isEnabled = true
                        myHolder.pauseBtn.isEnabled = false

                        myHolder.tv_pass.text = ""
                        myHolder.tv_due.text = ""
                        Toast.makeText(context, "media stop", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    // handler
                    //Toast.makeText(context, "Please download file...", Toast.LENGTH_SHORT).show()
                }
            }
        })

        myHolder.loopingBtn.setOnClickListener(View.OnClickListener { v ->
            val ID = v.tag as Int
            current = myAndroidOsList.get(ID)
            //Toast.makeText(context, "id is " + current.getId(), Toast.LENGTH_LONG).show();
            id = current!!.getId()
            //Toast.makeText(context, "id is " + current!!.getId(), Toast.LENGTH_LONG).show()
            try {
                mediaPlayer.isLooping = true

            } catch (e: Exception) {
                // handler
                //Toast.makeText(context, "Please download file...", Toast.LENGTH_SHORT).show()
            }
            myHolder.loopingBtn.visibility = View.GONE
            myHolder.loopingStopBtn.visibility = View.VISIBLE
            val status = databaseHandler.updateLooping(
                MusicModelClass(
                    current!!.getId()!!.toInt(),
                    "",
                    "",
                    "1"
                )
            )


        })
        myHolder.loopingStopBtn.setOnClickListener(View.OnClickListener { v ->
            val ID = v.tag as Int
            current = myAndroidOsList.get(ID)
            //Toast.makeText(context, "id is " + current.getId(), Toast.LENGTH_LONG).show();
            id = current!!.getId()
            //Toast.makeText(context, "id is " + current!!.getId(), Toast.LENGTH_LONG).show()
            try {
                mediaPlayer.isLooping = false

            } catch (e: Exception) {
                // handler
                //Toast.makeText(context, "Please download file...", Toast.LENGTH_SHORT).show()
            }

            myHolder.loopingStopBtn.visibility = View.GONE
            myHolder.loopingBtn.visibility = View.VISIBLE
            val status = databaseHandler.updateLooping(
                MusicModelClass(
                    current!!.getId()!!.toInt(),
                    "",
                    "",
                    "0"
                )
            )

        })

        // Seek bar change listener
        myHolder.seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (!paylingFlag.equals("0")) {
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
                val ID = seekBar.tag as Int
                if (tagId === ID) {
                    current = myAndroidOsList.get(ID)
                    //Toast.makeText(context, "id is " + current.getId(), Toast.LENGTH_LONG).show();
                    id = current!!.getId()
                    current!!.setVolume("" + i)

                    if (b) {

                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
                        //  Toast.makeText(context, "Volume "+id, Toast.LENGTH_SHORT).show()
                        val status =
                            databaseHandler.updateMusic(
                                MusicModelClass(
                                    id!!.toInt(),
                                    "",
                                    "" + i,
                                    ""
                                )
                            )

                    }

                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }

        })
        myHolder.deleteBtn.setOnClickListener(View.OnClickListener { v ->
            val ID = v.tag as Int
            current = myAndroidOsList.get(ID)
            //Toast.makeText(context, "id is " + current.getId(), Toast.LENGTH_LONG).show();
            id = current!!.getId()
            val musicname = current!!.getCategory()
            //Toast.makeText(context, "id is " + current!!.getId(), Toast.LENGTH_LONG).show()
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            val view: View = LayoutInflater.from(context).inflate(R.layout.alert_layout, null)
            builder.setView(view)
            val edt = view.findViewById<EditText>(R.id.passcode)
            builder.setPositiveButton("Submit",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    val passcode = edt.text.toString()
                    if (!passcode.equals("")) {
                        if (passcode.equals("1516")) {
                            val filePath =
                                Environment.getExternalStorageDirectory().absolutePath + "/ClassMusic" + File.separator + musicname + ".mp3"

                            try { // delete the original file
                                File(filePath).delete()
                                val status = databaseHandler.deleteMusic(
                                    MusicModelClass(
                                        current!!.getId()!!.toInt(),
                                        "",
                                        "",
                                        ""
                                    )
                                )

                                removeAt(ID)
                            } catch (e: java.lang.Exception) {
                                Log.e("tag", e.message)
                            }

                        } else {
                            Toast.makeText(
                                context,
                                "Please Enter Correct Passcode",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(context, "Please Enter Passcode", Toast.LENGTH_LONG).show()
                    }
                })


            builder.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })


            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        })
    }

    private fun removeAt(id: Int) {
        myAndroidOsList.removeAt(id)
        notifyItemRemoved(id)
        notifyItemRangeChanged(id, myAndroidOsList.size)
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
        var seek_bar: SeekBar
        var seek_bar1: SeekBar
        var tv_pass: TextView
        var tv_due: TextView
        var stopBtn: ImageView
        var loopingBtn: ImageView
        var loopingStopBtn: ImageView

        var deleteBtn: ImageView

        var relVol: RelativeLayout
        var relVol1: RelativeLayout
        var lay: LinearLayout

        // create constructor to get widget reference
        init {
            cat = itemView.findViewById<View>(R.id.cat) as TextView
            saveFile = itemView.findViewById<View>(R.id.saveFile) as ImageView
            playBtn = itemView.findViewById<View>(R.id.playBtn) as ImageView
            pauseBtn = itemView.findViewById<View>(R.id.pauseBtn) as ImageView
            seek_bar = itemView.findViewById<View>(R.id.seek_bar) as SeekBar
            seek_bar1 = itemView.findViewById<View>(R.id.seek_bar1) as SeekBar
            tv_pass = itemView.findViewById<View>(R.id.tv_pass) as TextView
            tv_due = itemView.findViewById<View>(R.id.tv_due) as TextView
            stopBtn = itemView.findViewById<View>(R.id.stopBtn) as ImageView
            loopingBtn = itemView.findViewById<View>(R.id.loopingBtn) as ImageView
            loopingStopBtn = itemView.findViewById<View>(R.id.loopingStopBtn) as ImageView

            deleteBtn = itemView.findViewById<View>(R.id.deleteBtn) as ImageView
            relVol = itemView.findViewById<View>(R.id.relVol) as RelativeLayout
            relVol1 = itemView.findViewById<View>(R.id.relVol1) as RelativeLayout
            lay = itemView.findViewById<View>(R.id.lay) as LinearLayout
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


}
