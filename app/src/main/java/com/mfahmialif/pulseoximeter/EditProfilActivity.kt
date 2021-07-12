package com.mfahmialif.pulseoximeter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_edit_profil.*
import org.jetbrains.anko.toast
import org.json.JSONObject

class EditProfilActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profil)
        MainActivity.currentActivity.add(this)

        val btnGantiPassword : Button = findViewById(R.id.btn_editProfil_gantipassword)
        var loaderActivity : LoaderActivity = LoaderActivity(this)

        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        btnGantiPassword.setOnClickListener {
            val user = sharedPreferences.getString("USER","")
            val jsonUser = JSONObject(user)
            val email = jsonUser.getString("email")
            val pin = et_editProfil_pin.text
            val password = et_editProfil_password.text
            val rePassword = et_editProfil_repassword.text
            loaderActivity.startLoadingDialog()

            if (password.toString() == rePassword.toString()){
                AndroidNetworking.get("https://pmeter.my.id/api/edit-password?token=ikuzo66&email=$email&pin=$pin&password=$password&repassword=$rePassword")
                        .setTag("Login")
                        .setPriority(Priority.LOW)
                        .build()
                        .getAsJSONObject(object : JSONObjectRequestListener {
                            override fun onResponse(response: JSONObject) {
                                loaderActivity.dismissDialog()
                                val respon = response.getString("respon").toString()
                                boxMessage(respon)
                            }
                            override fun onError(error: ANError) {
                                loaderActivity.dismissDialog()
                            }
                        })
            }else{
                toast("Password dan Repassword tidak sama")
            }
//            startActivity(Intent(this, ProfilActivity::class.java))
//            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.currentActivity.remove(this)
        finish()
    }

    fun boxMessage(pesan: String){
        var alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle("Pesan")
        alertDialog.setMessage(pesan)
        alertDialog.setPositiveButton("OK"){_,_ ->
            if (pesan == "Sukses"){
                startActivity(Intent(this, ProfilActivity::class.java))
                finish()
            }
        }
        val alert : AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }
}