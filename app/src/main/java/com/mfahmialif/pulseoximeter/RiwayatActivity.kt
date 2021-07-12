package com.mfahmialif.pulseoximeter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import kotlinx.android.synthetic.main.activity_data_local.*
import kotlinx.android.synthetic.main.activity_riwayat.*
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject

class RiwayatActivity : AppCompatActivity() {
    val dataList = ArrayList<Data>()
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)
        MainActivity.currentActivity.add(this)

        val btnHome : ImageButton = findViewById(R.id.btn_home2)
        val btnProfil : ImageButton = findViewById(R.id.btn_profil2)

        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        AndroidNetworking.initialize(getApplicationContext());

        val user = sharedPreferences.getString("USER","")
        val jsonUser = JSONObject(user)
        val email = jsonUser.getString("email")
        rv_listriwayat_riwayat.layoutManager = LinearLayoutManager(this)
//        dataList.add(Data("Fahmi","f"))
//        val adapter = RiwayatAdapter(dataList)
//        rv_listriwayat_riwayat.adapter = adapter

        AndroidNetworking.get("https://pmeter.my.id/api/data-petugas?token=ikuzo66&petugas=$email")
                .setTag("dataPetugas")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(object : JSONArrayRequestListener {
                    override fun onResponse(response: JSONArray) {
                        Log.w("datapetugas", "$response")
                        var respon = JSONArray(response.toString())

                        for (n in 0..(respon.length()) -1){
                            val petugas = respon.getJSONObject(n).getString("petugas").toString()
                            val nama = respon.getJSONObject(n).getString("nama").toString()
                            val pulse = respon.getJSONObject(n).getString("pulse").toDouble()
                            val bpm = respon.getJSONObject(n).getString("bpm").toDouble()
                            val dtime = respon.getJSONObject(n).getString("datetime").toString()
                            dataList.add(Data(petugas,nama,pulse,bpm,dtime))
                        }
                        val adapter = RiwayatAdapter(dataList)
                        rv_listriwayat_riwayat.adapter = adapter
                    }
                    override fun onError(error: ANError) {
                        // handle error
                        Log.w("datapetugase", "$error")

                    }
                })
        btnHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        btnProfil.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        toast("Tidak bisa menggunakan back button")
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.currentActivity.remove(this)
        finish()
    }
}