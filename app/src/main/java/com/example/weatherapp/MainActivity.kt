package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import com.android.volley.RequestQueue
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import org.json.JSONArray

const val url= "https://api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=1851ccff45dd11fd0a134049b170f468"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //button is ready to receive a string
        buttonA.setOnClickListener{
            //save whatever is entered by user **FOR LATER**
            //var city = enterLocation.text
            getInfo()
        }
    }

     private fun getInfo() {

        // Instantiate the RequestQueue.
      val queue = Volley.newRequestQueue(this)
       val url = "https://api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=1851ccff45dd11fd0a134049b170f468"

        // Request a string response from the provided URL.
        val stringReqy = StringRequest(Request.Method.GET, url,
            Listener<String> { response ->

                var strResp = response.toString()
                var json = JSONObject(strResp)

                var name= json.getString("name")
                display.text=(name)

                var main: JSONObject = JSONObject(response).getJSONObject("main")
                display2.text= (main.getDouble("temp").toString())

                var temps= (main.getDouble("temp_min").toString()) + " hi " + (main.getDouble("temp_max".toString()))
                display3.text=(temps)

                var arr= json.getJSONArray("weather")
                var weather= arr.getJSONObject(0).getString("main")
                display4.text=(weather)
                var description= arr.getJSONObject(0).getString("description")
                display5.text=(description)

                var humid= main.getDouble("humidity").toString() + "Humidity"
                display6.text=(humid)

                var clouds: JSONObject = JSONObject(response).getJSONObject("clouds")
                var allclouds= clouds.getDouble("all").toString() + "Clouds"
                display7.text=(allclouds)

            },
            Response.ErrorListener { display2.text = "That didn't work!" })

         // Add the request to the RequestQueue.
        queue.add(stringReqy)
    }

}

