package com.mfahmialif.pulseoximeter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.loader.content.Loader
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_buat_akun.*
import org.jetbrains.anko.toast
import org.json.JSONObject

class BuatAkunActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_akun)
        MainActivity.currentActivity.add(this)

        val btnBuatAkun : Button = findViewById(R.id.btn_daftar_buatakun)

        btnBuatAkun.setOnClickListener {
            val email = et_daftar_email.text
            val password = et_daftar_password.text
            val rePassword = et_daftar_repassword.text
            val pin = et_daftar_pin.text
            var loaderActivity : LoaderActivity = LoaderActivity(this)

            if (password.toString() == rePassword.toString()){
                if (pin.length == 6){
                    loaderActivity.startLoadingDialog()
                    AndroidNetworking.get("https://pmeter.my.id/api/register?token=ikuzo66&email=$email&password=$password&repassword=$rePassword&pin=$pin")
                            .setTag("daftar")
                            .setPriority(Priority.LOW)
                            .build()
                            .getAsJSONObject(object : JSONObjectRequestListener {
                                override fun onResponse(response: JSONObject) {
                                    loaderActivity.dismissDialog()
                                    val respon = response.getString("respon").toString()
                                    if (respon == "Sukses"){
                                        boxMessage("Berhasil membuat akun")
                                    }else if(respon == "User sudah ada"){
                                        boxMessage("Akun sudah ada, silahkan coba masuk")
                                    }else{
                                        boxMessage(respon)
                                    }
                                }

                                override fun onError(error: ANError) {
                                    loaderActivity.dismissDialog()
                                }
                            })
                }else{
                    toast("PIN harus 6 digit angka")
                }
            }else{
                toast("Password dan RePassword tidak sama")
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
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        val alert : AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

}