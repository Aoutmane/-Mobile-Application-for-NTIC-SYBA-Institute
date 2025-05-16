package com.el_aouthmanie.nticapp.modules

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.el_aouthmanie.nticapp.modules.intities.Notification
import com.el_aouthmanie.nticapp.modules.intities.Seance
import com.el_aouthmanie.nticapp.modules.intities.User
import com.el_aouthmanie.nticapp.ui.screens.scheduleScreen.dayIndex
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale
import androidx.core.content.edit
import com.el_aouthmanie.nticapp.modules.intities.Admin
import com.el_aouthmanie.nticapp.modules.intities.Trainee
import com.el_aouthmanie.nticapp.modules.realmHandler.RealmManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType


@RequiresApi(Build.VERSION_CODES.O)
val NORMALE_END_TIME = LocalTime.parse("18:30")

//todo handle firebase and online data
object OnlineDataBase {
    private val client by lazy {
        OkHttpClient()
    }
    private val API_URL = "http://eplanner-syba.somee.com/Service1.asmx/ListeSeancesGrp"
    private val LOGIN_API_URL = "azzi-aoutmane.alwaysdata.net"

    private val REQUEST_BODY = "application/json; charset=utf-8"

    private val PREF_NAME = "loginInfo"

    private val KEY_IS_LOGGED_IN = "isLoged"

    private val KEY_NAME = "name"
    private val KEY_LASTNAME = "lname"
    private val KEY_GROUP = "group"
    private val KEY_ROLE = "role"
    private val PREF_LOGGIN = "login"

    private val NOTIFICATIONS = "nots"
    private val daysInApi = dayIndex.map { it.key.lowercase() }

    fun notificationsEnabled(ctx : Context) : Boolean {
        val sharedPreferences = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

       return  sharedPreferences.getBoolean(NOTIFICATIONS,true)
    }

    fun toggleNotifications(ctx : Context, new : Boolean){
        val sharedPreferences = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        sharedPreferences.edit {
            putBoolean(NOTIFICATIONS,new)
            apply()
        }

    }

    fun getLogin(ctx : Context) : String {
        val sharedPreferences = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        return sharedPreferences.getString(PREF_LOGGIN,"unknown").toString()
    }

    fun setLogin(ctx : Context, login : String){
        val sharedPreferences = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        sharedPreferences.edit {
            putString(PREF_LOGGIN,login)
            apply()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getNextClass(realm: Realm, scope: CoroutineScope): Seance? {


        val currentTime = LocalTime.now()
        val todayName = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale("fr"))

        val allSeances = realm.query<Seance>().find().toList()

        val onGoing = allSeances.firstOrNull {
            it.dayName.equals(todayName, true) &&
                    LocalTime.parse(it.startingTime) < currentTime &&
                    LocalTime.parse(it.endingTime) > currentTime
        }
        if (onGoing is Seance) {
            return onGoing
        }

        val nextClass = allSeances.filter {
            it.dayName.equals(todayName, true) &&
                    LocalTime.parse(it.startingTime) > currentTime
        }.minByOrNull {
            LocalTime.parse(it.startingTime)
        }

        if (nextClass is Seance) {
            return nextClass
        }

        // get the class in the next day

        val nextDayClass = allSeances
            .groupBy { it.dayName }
            .entries
            .firstOrNull {
                Log.d("Debug", "Day in API: ${it.key}, Today: $todayName")
                daysInApi.indexOf(it.key) > daysInApi.indexOf(todayName)
            }
            ?.value
            ?.sortedBy {
                LocalTime.parse(it.startingTime)
            }?.first()

        return nextDayClass
    }


    suspend fun syncClasses(
        grp: String, periode: String,
        realm: Realm, scope: CoroutineScope,
        onFailureResponse: () -> Unit = {},
        onComplete: () -> Unit
    ) {

        val json = """
    {
        "user": "$grp",
        "pass": "$grp",
        "groupe": "$grp",
        "periode": "$periode"
    }
    """.trimIndent()

        val request = Request.Builder()
            .url(API_URL)
            .post(json.toRequestBody(REQUEST_BODY.toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailureResponse()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    val jsonResponse = it.string()
                    scope.launch {
                        parseResponse(jsonResponse, realm)
                        onComplete()
                    }
                }
            }
        })
    }

    suspend fun parseResponse(jsonString: String, realm: Realm) {
        Log.d("onDb", "fetched data : $jsonString")
        val jsonObject = JSONObject(jsonString)
        val dataArray = JSONArray(jsonObject.getString("d"))

        realm.write {
            // Delete all existing Seance objects before inserting new data
            delete(Seance::class)

            for (i in 0 until dataArray.length()) {
                val seanceJson = dataArray.getJSONObject(i)

                val seance = Seance().apply {
                    id = seanceJson.optString("CodeSeance", "")
                    dayName = seanceJson.optString("Jour", "")
                    codeSeance = seanceJson.optString("CodeSeance", "")
                    nomMode = seanceJson.optString("Nom_mode", "")
                    classRoom = seanceJson.optString("NumSalle", "N/A")
                    teacher = seanceJson.optString("Formateur", "")
                    intitule = seanceJson.optString("Intitule", "")
                    moduleDetails = seanceJson.optString("Detail_module", "")
                    startingTime = seanceJson.optString("Heure_debut", "")
                    endingTime = seanceJson.optString("Heure_fin", "")
                }

                copyToRealm(
                    seance,
                    updatePolicy = UpdatePolicy.ALL
                ) // **Upsert to avoid duplicates**
            }
        }
    }

    suspend fun loadNotifications(realm : Realm) : List<Notification>{
        return realm.query<Notification>().find().toList()
    }

    fun saveLoginState(
        context: Context,
        user : User
    ) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        sharedPreferences.edit() {
            clear()
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_ROLE, user.role())
            putString(KEY_NAME, user.name)
            putString(KEY_LASTNAME, user.lastName)
            putString(KEY_GROUP, user.group)

            apply()
        }
    }



    fun getGroup(context: Context): String{
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        return sharedPreferences.getString(KEY_GROUP, "N/A") ?: "N/A"
    }

    fun getName(context: Context,last : Boolean = false): String{
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val name = sharedPreferences.getString(KEY_NAME, "N/A") ?: "N/A"
        val lastName = sharedPreferences.getString(KEY_LASTNAME, "N/A") ?: "N/A"
        return name.replaceFirstChar { it.uppercase() } + if (last) " ${lastName.uppercase()}" else ""

    }

    // Check login state from SharedPreferences
    fun isUserLoggedIn(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }


    suspend fun addAnnouncmentToHistory(
        realm: Realm,
        notification: Notification
    ){
        realm.write {
            copyToRealm(notification)
        }
    }



    //////////////////////////////////////////////////////////////////////////// [- API FUNCTIONS -] /////////////////////////////




    suspend fun loginUser(username: String, password: String): User? = withContext(Dispatchers.IO) {
        val url = HttpUrl.Builder()
            .scheme("http")
            .host(LOGIN_API_URL)
            .addPathSegment("login_service.php")
            .addQueryParameter("username", username)
            .addQueryParameter("password", password)
            .build()

        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@withContext null

            val body = response.body?.string() ?: return@withContext null

            val json = JSONObject(body)
            val name = json.optString("name", "")
            val lastName = json.optString("lastName", "")
            val group = json.optString("group", "")
            val isAdmin = json.optBoolean("admin", false)

            if (name.isBlank() || lastName.isBlank() || group.isBlank()) return@withContext null

            return@withContext if (isAdmin) Admin(name, lastName) else Trainee(name, lastName, group)
        }
    }

    fun sendNotificationToServer(
        group: String,
        title: String,
        body: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "https://azzi-aoutmane.alwaysdata.net/notification_service.php"
        val key = "svS2N0Ic2HVUv610d3fkihhX7sVxZ3"

        val json = JSONObject().apply {
            put("group", group)
            put("title", title)
            put("body", body)
            put("key", key)
        }

        val requestBody = json.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError("Request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        onError("Unexpected code ${it.code}")
                    } else {
                        onSuccess()
                    }
                }
            }
        })
    }


    suspend fun logout(ctx : Context){
        val prfs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        prfs.edit {
            clear()
            apply()
        }
        val rlm = RealmManager.realm

        rlm.write {
            delete(Notification::class)
            delete(Seance::class)

        }




    }
    suspend fun updatePassword(
        username: String,
        oldPassword: String,
        newPassword: String
    ): String = withContext(Dispatchers.IO) {
        val client = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("username", username)
            .add("old_password", oldPassword)
            .add("new_password", newPassword)
            .build()

        val request = Request.Builder()
            .url("https://azzi-aoutmane.alwaysdata.net/update_service.php")
            .post(formBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: return@withContext "Empty response from server."

                if (!response.isSuccessful) {
                    return@withContext "HTTP ${response.code}: $responseBody"
                }

                val json = JSONObject(responseBody)

                return@withContext when {
                    json.has("message") -> json.getString("message")
                    json.has("error") -> "Error: ${json.getString("error")}"
                    else -> "Unexpected response: $responseBody"
                }
            }
        } catch (e: Exception) {
            return@withContext "Exception: ${e.message}"
        }
    }
}
