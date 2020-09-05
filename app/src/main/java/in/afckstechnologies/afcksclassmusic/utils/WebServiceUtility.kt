package `in`.afckstechnologies.afcksclassmusic.utils

import android.content.Context
import android.content.OperationApplicationException
import android.os.RemoteException
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import org.json.JSONObject

class WebServiceUtility(var context: Context) {
    var result = ""
    fun getString(
        url: String?,
        jsonObjSend: JSONObject?,
        callback: VolleyCallback
    ): String {
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonObjSend,
            Response.Listener { response ->
                try {
                    if (response.getBoolean("status")) { //updating the status in sqlite
                        //  db.updateNameStatus(id, MainActivity.NAME_SYNCED_WITH_SERVER);
                        if (!response.isNull("dataList")) {
                            result = response.toString()
                            try {
                                callback.onSuccess(result)
                            } catch (e: OperationApplicationException) {
                                e.printStackTrace()
                            } catch (e: RemoteException) {
                                e.printStackTrace()
                            }
                        }
                        //sending the broadcast to refresh the list
                        // context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error -> callback.onRequestError(error) })
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            1000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        VolleySingleton.getInstance(context)!!.addToRequestQueue(jsonObjectRequest)
        return result
    }

    interface VolleyCallback {
        @Throws(OperationApplicationException::class, RemoteException::class)
        fun onSuccess(result: String?)

        fun onRequestError(errorMessage: VolleyError?) //void onJsonInvoke(String url, final VolleyCallback callback);
    }

}
