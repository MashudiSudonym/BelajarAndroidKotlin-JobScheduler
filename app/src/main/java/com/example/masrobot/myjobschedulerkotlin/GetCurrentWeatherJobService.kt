package com.example.masrobot.myjobschedulerkotlin

import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.text.DecimalFormat

class GetCurrentWeatherJobService : JobService() {

    private val TAG = GetCurrentWeatherJobService::class.java.simpleName

    // API Key from openweathermap.org
    val APP_ID: String = "a3574a03b1808eafff99f6efd47bf3d9"

    val CITY: String = "Jepara"

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStartJob() Executed")
        getCurrentWeather(params)
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStopJob() Executed")
        return true
    }

    private fun getCurrentWeather(job: JobParameters?) {
        Log.d(TAG, "Running")
        val client = AsyncHttpClient()
        val url = "http://api.openweathermap.org/data/2.5/weather?q=$CITY&APPID=$APP_ID"
        Log.w(TAG, "getCurrentWeather: $url")
        client.get(url, object: AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                val result = String(responseBody!!)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val currentWeather = responseObject.getJSONArray("weather")
                            .getJSONObject(0)
                            .getString("main")
                    val description = responseObject.getJSONArray("weather")
                            .getJSONObject(0)
                            .getString("description")
                    val tempInKelvin = responseObject.getJSONObject("main")
                            .getDouble("temp")
                    val tempInCelcius = tempInKelvin - 273
                    val temprature = DecimalFormat("##.##").format(tempInCelcius)
                    val title = "Current Weather"
                    val message = "$currentWeather, $description with $temprature celcius"
                    val notifId = 100
                    showNotification(applicationContext, title, message, notifId)
                    jobFinished(job, false)
                }catch (e: Exception) {
                    jobFinished(job, true)
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                // ketika proses gagal, maka jobFinished diset dengan parameter true, yang artinya job perlu di reschedule
                jobFinished(job, true)
            }

        })
    }

    private fun showNotification(context: Context, title: String, message: String, notifId: Int) {
        val notificationManagerCompat = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.black))
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setSound(alarmSound)

        notificationManagerCompat.notify(notifId, builder.build())
    }
}