package com.mfahmialif.pulseoximeter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences
    var isLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        MainActivity.currentActivity.add(this)

        val btnMasuk : Button = findViewById(R.id.btn_login_masuk)
        val btnBuatAkun : Button = findViewById(R.id.btn_buatAkun)
        var txtLupaPasswod : TextView = findViewById(R.id.txt_lupaPassword)
        var loaderActivity : LoaderActivity = LoaderActivity(this)

        AndroidNetworking.initialize(getApplicationContext());

        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        isLogin = sharedPreferences.getBoolean("LOGIN",false)
        if (isLogin){
            val user = sharedPreferences.getString("USER","")
            val jsonUser = JSONObject(user)
            val email = jsonUser.getString("email")
            val password = jsonUser.getString("password")
            loaderActivity.startLoadingDialog()

            AndroidNetworking.get("https://pmeter.my.id/api/login?token=ikuzo66&email=$email&password=$password")
                    .setTag("Login")
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            loaderActivity.dismissDialog()
                            val respon = response.getString("respon").toString()
                            boxMessage(respon, email, password)
                        }
                        override fun onError(error: ANError) {
                            loaderActivity.dismissDialog()
                        }
                    })
        }


        btnMasuk.setOnClickListener {
            val email = et_login_email.text
            val password = et_login_password.text
            loaderActivity.startLoadingDialog()
            AndroidNetworking.get("https://pmeter.my.id/api/login?token=ikuzo66&email=$email&password=$password")
                    .setTag("Login")
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            loaderActivity.dismissDialog()
                            val respon = response.getString("respon").toString()
                            boxMessage(respon, email.toString(), password.toString())
                        }
                        override fun onError(error: ANError) {
                            loaderActivity.dismissDialog()
                        }
                    })
        }

        btnBuatAkun.setOnClickListener {
            startActivity(Intent(this, BuatAkunActivity::class.java))

        }

        txtLupaPasswod.setOnClickListener {
            startActivity(Intent(this, LupaPasswordActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.currentActivity.remove(this)
        finish()
    }

    fun boxMessage(pesan: String, email: String, password: String){
        var alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle("Pesan")
        alertDialog.setMessage(pesan)
        alertDialog.setPositiveButton("OK"){_,_ ->
            if (pesan == "Sukses"){
                val email = email
                val password = password
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                val user = "{'email':$email,'password':$password}"
                editor.putString("USER", user)
                editor.putBoolean("LOGIN", true)
                editor.apply()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
        val alert : AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }
}

//            AndroidNetworking.get("https://pmeter.my.id/api/user?token=ikuzo66")
//                    .setTag("test")
//                    .setPriority(Priority.LOW)
//                    .build()
//                    .getAsJSONArray(object : JSONArrayRequestListener {
//                        override fun onResponse(response: JSONArray) {
//                            Log.w("tesapi", "$response")
//                            Log.i("tesapi", response[0].toString())
//
//                            var tes1 = JSONArray(response.toString())
//                            var username = tes1.getJSONObject(0).getString("username")
//
//                            Log.i("tesapi", username.toString())
//
//                            for (ts in 0..(tes1.length()) -1){
//                                Log.i("tesapi", tes1.getJSONObject(ts).getString("username").toString())
//                            }
//                        }
//
//                        override fun onError(error: ANError) {
//                            // handle error
//                        }
//                    })