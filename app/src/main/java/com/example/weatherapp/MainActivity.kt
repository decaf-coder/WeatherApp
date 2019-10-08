package com.example.weatherapp

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import android.widget.Toast
import android.view.Gravity
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.location.FusedLocationProviderClient
import android.location.LocationListener
import android.location.LocationManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T








const val url1= "https://api.openweathermap.org/data/2.5/weather?q="
const val url2= "&APPID=1851ccff45dd11fd0a134049b170f468"
private lateinit var fusedLocationClient: FusedLocationProviderClient


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getLocation()

        buttonA.setOnClickListener {

        //save whatever is entered by user
            var city = enterLocation.text
            //check if they entered anything
            if (city.trim().isEmpty()) {
                val emptyToast = Toast.makeText(
                    applicationContext,
                    "You didn't enter anything :)",
                    Toast.LENGTH_SHORT
                )
                emptyToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                emptyToast.show()
            }
            else {
                var urlString = url1 + city + url2
                getInfo(urlString)
            }
        }



    }

    private fun getLocation(){
        fusedLocationClient = getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                // Try printing these two variables in your Toast
                var longitude = location.longitude
                var latitude = location.latitude

                val url3= "https://api.openweathermap.org/data/2.5/weather?lat="
                var urlString = url3 + latitude + "&lon="+ longitude + url2
                getInfo(urlString)

                //buttonA.text= latitude.toString() + "&" + longitude.toString()
            }
    }


     private fun getInfo(url: String ) {

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        //val url = "https://api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=1851ccff45dd11fd0a134049b170f468"
        val kelvin = 273.15

        // Request a string response from the provided URL.
        val stringReqy = StringRequest(Request.Method.GET, url,
            Listener<String> { response ->

                var strResp = response.toString()
                var json = JSONObject(strResp)

                var name= json.getString("name")
                display.text=(name)

                var main: JSONObject = JSONObject(response).getJSONObject("main")
                var tempy= ((main.getInt("temp"))- kelvin)
                var temper= (tempy.toInt()).toString() + "°C"
                display2.text= (temper)

                var mintemp= (main.getDouble("temp_min") - kelvin).toInt().toString() + "°C"
                var maxtemp= (main.getDouble("temp_max") - kelvin).toInt().toString() + "°C"
                var temps= "Min." + mintemp + " Max. " + maxtemp
                display3.text=(temps)

                var arr= json.getJSONArray("weather")
                var weather= arr.getJSONObject(0).getString("main")
                display4.text=(weather)
                var description= arr.getJSONObject(0).getString("description").capitalize()
                display5.text=(description)

                var humid= main.getDouble("humidity").toInt().toString() + "%\nHumidity"
                display6.text=(humid)

                var clouds: JSONObject = JSONObject(response).getJSONObject("clouds")
                var allclouds= clouds.getDouble("all").toInt().toString() + "%\nClouds"
                display7.text=(allclouds)

            },
            Response.ErrorListener {
                val errorToast = Toast.makeText(applicationContext,"That city doesn't exist :)",Toast.LENGTH_SHORT)
                errorToast.setGravity(Gravity.CENTER_VERTICAL,0,0)
                errorToast.show()
            })

         // Add the request to the RequestQueue.
        queue.add(stringReqy)
    }

}

