package com.mfahmialif.pulseoximeter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import kotlinx.android.synthetic.main.activity_profil.*
import org.jetbrains.anko.toast
import org.json.JSONObject

class ProfilActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)
        MainActivity.currentActivity.add(this)

        val btnRiwayat : ImageButton = findViewById(R.id.btn_riwayat3)
        val btnHome : ImageButton = findViewById(R.id.btn_home3)
        val btnGantiPassword : Button = findViewById(R.id.btn_gantiPassword)
        val btnKeluar : Button = findViewById(R.id.btn_keluar)

        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val user = sharedPreferences.getString("USER","")
        val jsonUser = JSONObject(user)
        val email = jsonUser.getString("email")
        txt_profil_email.text = email.toString()

        btnRiwayat.setOnClickListener {
            startActivity(Intent(this, RiwayatActivity::class.java))
            finish()
        }
        btnHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        btnGantiPassword.setOnClickListener {
            startActivity(Intent(this, EditProfilActivity::class.java))
        }
        btnKeluar.setOnClickListener {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean("LOGIN", false)
            editor.apply()
            startActivity(Intent(this, LoginActivity::class.java))
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