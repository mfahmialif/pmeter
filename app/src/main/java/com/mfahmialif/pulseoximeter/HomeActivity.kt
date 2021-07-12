package com.mfahmialif.pulseoximeter

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_home.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject

private const val GATT_MAX_MTU_SIZE = 517

class HomeActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    val btHandle = BluetoothHandle()
    var handlerLoopStatusBt = Handler(Looper.getMainLooper())
    var handlerLoopConnectBtMacAddr = Handler(Looper.getMainLooper())
    var statusAfterConnect = false
    var loaderActivity : LoaderActivity = LoaderActivity(this)

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        MainActivity.currentActivity.add(this)


        AndroidNetworking.initialize(getApplicationContext());

        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        // auto connect bt mac addr
        handlerLoopConnectBtMacAddr.post(object: Runnable{
            override fun run(){
                connectBTMacAddr()
                handlerLoopConnectBtMacAddr.postDelayed(this,10000)
            }
        })

        // change text status bluetooth
        changeTxtStatusBt()
        handlerLoopStatusBt.post(object : Runnable{
            override fun run() {
                changeTxtStatusBt()
                handlerLoopStatusBt.postDelayed(this,1000)
            }
        })

        var handlerLoopChangeText = Handler(Looper.getMainLooper())

        val btnRiwayat : ImageButton = findViewById(R.id.btn_riwayat1)
        val btnProfil : ImageButton = findViewById(R.id.btn_profil1)
        val btnMulai : Button = findViewById(R.id.btn_mulai)
        val pbSPO2 : ProgressBar = findViewById(R.id.progressBar1)
        val pbBPM : ProgressBar = findViewById(R.id.progressBar2)


        pbSPO2.setVisibility(View.GONE)
        pbBPM.setVisibility(View.GONE)

        btnRiwayat.setOnClickListener {
            btHandle.disableNotifyBT()
            handlerLoopChangeText.removeCallbacksAndMessages(null)
            startActivity(Intent(this, RiwayatActivity::class.java))
            finish()
        }
        btnProfil.setOnClickListener {
            btHandle.disableNotifyBT()
            handlerLoopChangeText.removeCallbacksAndMessages(null)
            startActivity(Intent(this, ProfilActivity::class.java))
            finish()
        }

        var cekMulai = true
        btnMulai.setOnClickListener {
            Log.w("tes", btHandle.companion.statusBT)
            val s = btHandle.companion.statusBT
            if (s == "off"){
                Log.w("tess", btHandle.companion.statusBT)
                var alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
                alertDialog.setTitle("Peringatan")
                alertDialog.setMessage("Belum terhubung ke bluetooth, klik ok untuk menghubungkan")
                alertDialog.setPositiveButton("OK"){_,_ ->
                    startActivity(Intent(this, PopUpBluetooth::class.java))
                    finish()
                }
                alertDialog.setNegativeButton("Batal"){_,_ -> }
                val alert : AlertDialog = alertDialog.create()
                alert.setCanceledOnTouchOutside(false)
                alert.show()

            }else{
                if (cekMulai == true){
                    pbSPO2.setVisibility(View.VISIBLE)
                    pbBPM.setVisibility(View.VISIBLE)
                    btHandle.enableNotifyBT()
                    handlerLoopChangeText.post(object : Runnable{
                        override fun run() {
                            changeTxt()
                            kirimData()
                            handlerLoopChangeText.postDelayed(this,1000)
                        }
                    })
                    val btnMulai = findViewById<Button>(R.id.btn_mulai)
                    btnMulai.setText("Selesai")
                    cekMulai = false
                }else{
                    pbSPO2.setVisibility(View.GONE)
                    pbBPM.setVisibility(View.GONE)
                    btHandle.disableNotifyBT()
                    handlerLoopChangeText.removeCallbacksAndMessages(null)
                    val btnMulai = findViewById<Button>(R.id.btn_mulai)
                    btnMulai.setText("Mulai")
                    cekMulai = true
                }

            }

        }
    }

    override fun onBackPressed() {
        toast("Tidak bisa menggunakan back button")
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.currentActivity.remove(this)
        handlerLoopStatusBt.removeCallbacksAndMessages(null)
    }

    private fun changeTxt(){
        val txtSpo2 = findViewById<TextView>(R.id.txt_spo2)
        txtSpo2.setText(btHandle.companion.spo2)
        val txtBpm = findViewById<TextView>(R.id.txt_bpm)
        txtBpm.setText(btHandle.companion.bpm)
    }

    private fun changeTxtStatusBt(){
        var statusBt = btHandle.companion.statusBT
        val txtStatusBt = findViewById<TextView>(R.id.txt_statusBT)
        if (statusBt == "off"){
            txtStatusBt.setText("Terputus")

            if (statusAfterConnect == true){
                var alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
                alertDialog.setTitle("Peringatan")
                alertDialog.setMessage("Terputus, hubungkan kembali ?")
                alertDialog.setPositiveButton("OK"){_,_ ->
                    statusAfterConnect = false
                    startActivity(Intent(this, PopUpBluetooth::class.java))
                    finish()
                }
                alertDialog.setNegativeButton("Tidak"){_,_ -> statusAfterConnect = false }
                val alert : AlertDialog = alertDialog.create()
                alert.setCanceledOnTouchOutside(false)
                alert.show()
            }
            statusAfterConnect = false

        }else{
            txtStatusBt.setText("Terhubung")
            statusAfterConnect = true
        }

    }

    private fun connectBTMacAddr(){
        val statusBt = btHandle.companion.statusBT
        if (statusBt == "off"){
            // connect with mac address bluetooth
            val addr = sharedPreferences.getString("DEVICE_ADDR","")
            if (addr != ""){
                val devbt = bluetoothAdapter?.getRemoteDevice(addr) as BluetoothDevice
                btHandle.connect(devbt, this)
            }
        }else{
            handlerLoopConnectBtMacAddr.removeCallbacksAndMessages(null)
        }
    }

    private fun boxMessage(pesan: String){
        var alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle("Pesan")
        alertDialog.setMessage(pesan)
        alertDialog.setPositiveButton("OK"){_,_ -> }
        val alert : AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    private fun kirimLokal(data: Data){
        var db = DatabaseHandler(context = this)
        db.insertData(data)
    }

    fun kirimData(){
        val currentDate = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formatted = currentDate.format(formatter)
        val datetimeFormatted = formatted

        val user = sharedPreferences.getString("USER","")
        val jsonUser = JSONObject(user)
        val email = jsonUser.getString("email")
        val nama = et_home_nama.text
        val pulse = btHandle.companion.spo2
        Log.w("tesapikirim", "$nama")
        val bpm = btHandle.companion.bpm

        Log.w("kirim-data", "mulai kirim")
        AndroidNetworking.get("https://pmeter.my.id/api/kirim-data-dtime?token=ikuzo66&petugas=$email&nama=$nama&pulse=$pulse&bpm=$bpm&dtime=$datetimeFormatted")
                .setTag("kirimData")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        Log.w("kirim-data", "selesai kirim")
                    }
                    override fun onError(error: ANError) {
                        // handle error
                        Log.w("tesapikirim", "$error")
                        val data = Data(email,nama.toString(),pulse.toDouble(),bpm.toDouble(),datetimeFormatted)
                        kirimLokal(data)
                    }
                })
    }
}