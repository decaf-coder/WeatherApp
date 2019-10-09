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
import android.view.View
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


const val url1= "https://api.openweathermap.org/data/2.5/weather?q="
const val url2= "&APPID=1851ccff45dd11fd0a134049b170f468"
private lateinit var fusedLocationClient: FusedLocationProviderClient
var tempy= 0.0
var mintemp= 0.0
var maxtemp=0.0


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ask to get location here
        setupPermissions()


        toggleButton.setOnClickListener {
            if (toggleButton.isChecked) {
                tempy= (tempy *9/5) + 32
                var temper= (tempy.toInt()).toString() + "°F"
                display2.text= temper

                mintemp= (mintemp *9/5) + 32
                var mintempS= mintemp.toInt().toString() + "°F"
                maxtemp= (maxtemp *9/5) + 32
                var maxtempS= mintemp.toInt().toString() + "°F"

                var temps= "Min." + mintempS + " Max. " + maxtempS
                display3.text=(temps)
            }

            if(!(toggleButton.isChecked)){
                tempy= (tempy-32) * 5/9
                var temper= (tempy.toInt()).toString() + "°C"
                display2.text= temper

                mintemp= (mintemp-32) * 5/9
                var mintempS= mintemp.toInt().toString() + "°C"
                maxtemp= (maxtemp-32) * 5/9
                var maxtempS= mintemp.toInt().toString() + "°C"

                var temps= "Min." + mintempS + " Max. " + maxtempS
                display3.text=(temps)
            }
         }

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


    private val RECORD_REQUEST_CODE = 101

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            RECORD_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    //Log.i(TAG, "Permission has been denied by user")
                } else {
                    getLocation()
                    //Log.i(TAG, "Permission has been granted by user")
                }
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

                toggleButton.visibility= View.VISIBLE
                //buttonA.text= latitude.toString() + "&" + longitude.toString()
            }
    }


      fun getInfo(url: String ) {

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
                 tempy= ((main.getInt("temp"))- kelvin) //convert to celcius automatically at first
                var temper= (tempy.toInt()).toString() + "°C"
                display2.text= (temper)

                mintemp= (main.getDouble("temp_min") - kelvin)
                var mintempS= mintemp.toInt().toString() + "°C"
                maxtemp= (main.getDouble("temp_max") - kelvin)
                var maxtempS= maxtemp.toInt().toString() + "°C"
                var temps= "Min." + mintempS + " Max. " + maxtempS
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

                toggleButton.visibility= View.VISIBLE

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

