package `in`.afckstechnologies.afcksclassmusic.utils

import android.content.Context
import android.util.Log
import org.apache.http.HttpVersion
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.conn.ClientConnectionManager
import org.apache.http.conn.scheme.PlainSocketFactory
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.scheme.SchemeRegistry
import org.apache.http.conn.ssl.SSLSocketFactory
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager
import org.apache.http.params.BasicHttpParams
import org.apache.http.params.HttpConnectionParams
import org.apache.http.params.HttpParams
import org.apache.http.params.HttpProtocolParams
import org.apache.http.protocol.HTTP
import org.apache.http.util.EntityUtils
import org.json.JSONObject
import java.security.KeyStore
import java.util.*

class WebClient {
    var context: Context? = null
    var TAG = "ServiceAccess"
    var response = ""
    var baseURL = ""
    fun SendHttpPost(URL: String?, jsonObjSend: JSONObject): String {
        try {
            val client = newHttpClient
            //HttpClient client = new DefaultHttpClient();
            val post = HttpPost(URL)
            // HttpGet post = new HttpGet(URL);
            post.setHeader("Content-type", "application/json; charset=UTF-8")
            post.setHeader("Accept", "application/json")
            post.entity = StringEntity(jsonObjSend.toString(), "UTF-8")
            val httpClient = DefaultHttpClient()
            HttpConnectionParams.setSoTimeout(
                httpClient.params,
                10 * 1000
            )
            HttpConnectionParams.setConnectionTimeout(
                httpClient.params,
                10 * 1000
            )
            val response = client.execute(post)
            Log.i(TAG, "resoponse$response")
            val entity = response.entity
            return EntityUtils.toString(entity)
        } catch (e: Exception) { // TODO: handle exception
            Log.i(TAG, "exception$e")
        }
        Log.i(TAG, "response$response")
        return response
    }

    val newHttpClient: HttpClient
        get() = try {
            val trustStore =
                KeyStore.getInstance(KeyStore.getDefaultType())
            trustStore.load(null, null)
            val sf = MySSLSocketFactory(trustStore)
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
            val params: HttpParams = BasicHttpParams()
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1)
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8)
            val registry = SchemeRegistry()
            registry.register(Scheme("http", PlainSocketFactory.getSocketFactory(), 80))
            registry.register(Scheme("https", sf, 443))
            val ccm: ClientConnectionManager = ThreadSafeClientConnManager(params, registry)
            DefaultHttpClient(ccm, params)
        } catch (e: Exception) {
            DefaultHttpClient()
        }

    companion object {
        fun convertArrayListToStringWithComma(arrayList: ArrayList<String?>): String { //The string builder used to construct the string
            val commaSepValueBuilder = StringBuilder()
            //Looping through the list
            for (i in arrayList.indices) { //append the value into the builder
                commaSepValueBuilder.append(arrayList[i])
                //if the value is not the last element of the list
//then append the hash(H) as well
                if (i != arrayList.size - 1) {
                    commaSepValueBuilder.append(",")
                }
            }
            return commaSepValueBuilder.toString()
        }

        fun convertStringwithCommaToArrayList(stringValue: String): ArrayList<String> {
            return ArrayList(
                Arrays.asList(
                    *stringValue.split(",").toTypedArray()
                )
            )
        }
    }
}

