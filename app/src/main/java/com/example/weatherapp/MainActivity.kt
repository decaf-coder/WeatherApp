// October 9, 2019
// This app was created by Serena Drouillard
// This app retrieves live weather data for an inputted city and displays it to the user

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
import android.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


const val url1= "https://api.openweathermap.org/data/2.5/weather?q="
const val url2= "&APPID=1851ccff45dd11fd0a134049b170f468"
private lateinit var fusedLocationClient: FusedLocationProviderClient

//all of these are set to random numbers to initialize
var tempy= 0.0
var mintemp= 0.0
var maxtemp=0.0
private val RECORD_REQUEST_CODE = 101


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ask to get location here & display current location
        setupPermissions()

        //unit conversion button with on click listener
        setupUnitButton()

        // listener for the "my location" button
        // looks for permissions and uses current location
        myLocationButton.setOnClickListener{
            setupPermissions()
        }

        //listener for the get weather button
        buttonA.setOnClickListener {
            // save whatever is entered by user
            // this can be either a city or a city, country code
            var city = enterLocation.text
            //check if they entered anything
            if (city.trim().isEmpty()) { //user didn't enter anything
                val emptyToast = Toast.makeText(
                    applicationContext,
                    "You didn't enter anything :)",
                    Toast.LENGTH_SHORT
                )
                emptyToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                emptyToast.show()
            }
            //user did enter something
            else {
                var urlString = url1 + city + url2
                getInfo(urlString)
            }
        }
    }

    //this sets up the on click listener for the unit button and tells it what to do for each state
    // if units are already in celcius, the button will show Fahrenheit as the text, and vise-versa
    private fun setupUnitButton(){
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
    }

    //this checks if the users permissions are allowing location
    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
        //if they aren't giving the app permission to access location, then make request for permission
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
        //they are already giving the app permission so skip ahead to getting the location
        else
            getLocation()
    }

    //if we need to request permission from the user, saves the request code in variable RECORD_REQUEST_CODE
    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            RECORD_REQUEST_CODE)
    }

    //checks the grant results from the variable RECORD_REQUEST_CODE, if there are no grant results or if
    // the results are denied then the app will not display the users location
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //user said no, do nothing
                } else { //user gave permission, so go ahead and get it now
                    getLocation()
                }
            }
        }
    }

    //gets the users current location coordinates and sends them to getUnfo to retrieve information from OpenWeatherAPI
    private fun getLocation(){
        fusedLocationClient = getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                // user location coordinates
                var longitude = location.longitude
                var latitude = location.latitude

                //this calls the API using coordinates
                val url3= "https://api.openweathermap.org/data/2.5/weather?lat="
                var urlString = url3 + latitude + "&lon="+ longitude + url2
                getInfo(urlString)

                //makes the unit conversion button visible now, not before getLocation is called
                toggleButton.visibility= View.VISIBLE
            }
    }

    // gets the url and sends it to open weather API
    // receives a JSON and parses all necessary information and displays appropriately
    // example of url with city and country code : "https://api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=1851ccff45dd11fd0a134049b170f468"
    fun getInfo(url: String ) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val kelvin = 273.15 //used to convert from kelvin->celcius

        // Request a string response from the provided URL.
        // parses all response data and assigns to displays
        val stringReqy = StringRequest(Request.Method.GET, url,
            Listener<String> { response ->

                var strResp = response.toString()
                var json = JSONObject(strResp)

                var name= json.getString("name")

                var sys: JSONObject = JSONObject(response).getJSONObject("sys")
                var country= sys.getString("country")

                var fullname= name + ", " + country
                display.text=(fullname)

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

                //make the unit toggle button visible now
                toggleButton.visibility= View.VISIBLE
                // also set it back to off
                toggleButton.isChecked= false

            },
            // if the API call doesn't work due to improper input, a toast will appear to tell users to try again
            Response.ErrorListener {
                val errorToast = Toast.makeText(applicationContext,"That city doesn't exist, or you formatted your input wrong. It should be: [city] OR [city, country code] :)",Toast.LENGTH_LONG)
                errorToast.setGravity(Gravity.CENTER_VERTICAL,0,0)
                errorToast.show()
            })

         // Add the request to the RequestQueue.
        queue.add(stringReqy)
    }
}

