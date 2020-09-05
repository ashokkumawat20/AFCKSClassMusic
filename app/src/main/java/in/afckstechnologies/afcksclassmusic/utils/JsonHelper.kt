package `in`.afckstechnologies.afcksclassmusic.utils

import `in`.afckstechnologies.afcksclassmusic.models.ClassMusicDAO
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.util.*

public class JsonHelper {

    private val classMusicDAOArrayList: ArrayList<ClassMusicDAO?> = ArrayList<ClassMusicDAO?>()
    private var classMusicDAO: ClassMusicDAO? = null

    fun calssMusicList(ListResponse: String?): ArrayList<ClassMusicDAO?>? { // TODO Auto-generated method stub
        Log.d("scheduleListResponse", ListResponse)
        try {
            val jsonObject = JSONObject(ListResponse)
            if (!jsonObject.isNull("dataList")) {
                val jsonArray = jsonObject.getJSONArray("dataList")
                for (i in 0 until jsonArray.length()) {
                    val `object` = jsonArray.getJSONObject(i)
                    classMusicDAO = ClassMusicDAO()
                    classMusicDAO!!.setId(`object`.getString("id"))
                    classMusicDAO!!.setCategory(`object`.getString("category"))
                    classMusicDAO!!.setRing_tone_path(`object`.getString("ring_tone_path"))
                    classMusicDAO!!.setVolume(`object`.getString("volume"))
                    classMusicDAO!!.setDate_time(`object`.getString("date_time"))
                    classMusicDAOArrayList.add(classMusicDAO)
                }
            }
        } catch (e: JSONException) { // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return classMusicDAOArrayList
    }
}