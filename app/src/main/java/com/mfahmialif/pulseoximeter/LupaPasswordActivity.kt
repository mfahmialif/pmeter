package com.mfahmialif.pulseoximeter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_lupa_password.*
import org.jetbrains.anko.toast
import org.json.JSONObject

class LupaPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lupa_password)
        MainActivity.currentActivity.add(this)

        val btnResetPassword : Button = findViewById(R.id.btn_lupaPassword_resetPassword)
        var loaderActivity : LoaderActivity = LoaderActivity(this)

        btnResetPassword.setOnClickListener {
            val email = et_lupaPassword_email.text
            val pin = et_lupaPassword_pin.text
            val password = et_lupaPassword_password.text
            val rePassword = et_lupaPassword_rePassword.text
            loaderActivity.startLoadingDialog()

            if (password.toString() == rePassword.toString()){
                AndroidNetworking.get("https://pmeter.my.id/api/lupa-password?token=ikuzo66&email=$email&pin=$pin&password=$password&repassword=$rePassword")
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
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        val alert : AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }
}