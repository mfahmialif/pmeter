package com.mfahmialif.pulseoximeter

import android.app.Activity
import android.app.ActivityManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.*
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity(), ConnectionReceiver.ConnectionReceiverListener {
    lateinit var sharedPreferences: SharedPreferences
    val btHandle = BluetoothHandle()
    var db = DatabaseHandler(context = this)
    lateinit var loaderActivity : LoaderActivity

    companion object{
        val currentActivity = ArrayList<Context>()
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected){
            Log.w("tesdong3", "nice")
            toast("Kirim data yang tertunda")
            val rdata = db.readData()
            val sizeRdata = db.getSize()
            if (sizeRdata > 0){
                loaderActivity = LoaderActivity(MainActivity.currentActivity.last() as Activity)
                for (i in 0 until rdata.size){
                    AndroidNetworking.get("https://pmeter.my.id/api/kirim-data-dtime?token=ikuzo66&petugas=${rdata.get(i).petugas}&nama=${rdata.get(i).nama}&pulse=${rdata.get(i).pulse}&bpm=${rdata.get(i).bpm}&dtime=${rdata.get(i).dtime}")
                            .setTag("kirimData")
                            .setPriority(Priority.LOW)
                            .build()
                            .getAsJSONObject(object : JSONObjectRequestListener {
                                override fun onResponse(response: JSONObject) {
                                    kirimkan("Berhasil Dikirimkan",rdata.get(i))
                                }
                                override fun onError(error: ANError) {
                                    // handle error
                                }
                            })
                }
            }

        }else{
            boxMessage("Koneksi internet terputus")
        }
    }

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MainActivity.currentActivity.add(this)

        val window : Window = this@MainActivity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.hijauBack)

        val btnLanjutkan : Button = findViewById(R.id.btn_lanjutkan)
        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        // connect with mac address bluetooth
        val addr = sharedPreferences.getString("DEVICE_ADDR","")
        Log.w("BluetoothGattCallback", "Connect with MAC Address : $addr")
        if (addr != ""){
            val devbt = bluetoothAdapter?.getRemoteDevice(addr) as BluetoothDevice
            btHandle.connect(devbt, this)
        }


        // broadcast receiver
        baseContext.registerReceiver(ConnectionReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        ConnectApp.instance.setConnectionListener(this)

        btnLanjutkan.setOnClickListener {
            val rdata = db.readData()
            for (i in 0 until rdata.size){
                Log.w("tes-read-data", "${rdata.get(i).petugas.toString()} ${rdata.get(i).nama.toString()} ${rdata.get(i).pulse.toString()} ${rdata.get(i).bpm.toString()} ${rdata.get(i).dtime.toString()}")
            }
            if (rdata.size != 0){
                startActivity(Intent(this,DataLocalActivity::class.java))
            } else{
                startActivity(Intent(this, LoginActivity::class.java))
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.currentActivity.remove(this)
        finish()
    }

    private fun boxMessage(pesan: String){
        var alertDialog: AlertDialog.Builder = AlertDialog.Builder(MainActivity.currentActivity.last())
        alertDialog.setTitle("Pesan")
        alertDialog.setMessage(pesan)
        alertDialog.setPositiveButton("OK"){_,_ -> }
        val alert : AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    private fun kirimkan(pesan: String, data:Data){
        db.deleteData(data.dtime)
        val size = db.getSize()
        if (size < 1){
//            loaderActivity.dismissDialog()
//            boxMessage(pesan)
            toast("Selesai kirim data yang tertunda")
        }

    }
}



//            val currentDate = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                LocalDateTime.now()
//            } else {
//                TODO("VERSION.SDK_INT < O")
//            }
//            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//            val formatted = currentDate.format(formatter)
//            val datetimeFormatted = formatted

//            val data = Data("joni","budi",1.2,3.2,datetimeFormatted)
//            db.insertData(data)

//            for (i in 0 until rdata.size){
//                Log.w("tesdong3", "${rdata.get(i).petugas} \"${rdata.get(i).dtime}")
//            }