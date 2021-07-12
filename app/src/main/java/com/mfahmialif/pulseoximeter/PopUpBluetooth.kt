package com.mfahmialif.pulseoximeter

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_pop_up_bluetooth.*
import org.jetbrains.anko.toast

class PopUpBluetooth : AppCompatActivity() {
    private val REQUEST_ENABLE_BLUETOOTH : Int = 1
    val btHandle = BluetoothHandle()
    val bts = ArrayList<Bt>()
    var handlerLoop = Handler(Looper.getMainLooper())

    private val bleScanner = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            var cek = false
            for (b in bts){
                if ("${b.result?.device?.name}" == "${result?.device?.name}"){
                    cek = true
                }
            }
            if (cek == false && "${result?.device?.name}" != "null"){
                bts.add(Bt(result))
            }
            cek = false
        }
    }

    private val btAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.activity_pop_up_bluetooth)
        MainActivity.currentActivity.add(this)

        rv_BluetoothList.layoutManager = LinearLayoutManager(this)
        val btnNyalakanBt = findViewById<Button>(R.id.btn_nyalakanbt)

        if (btAdapter!!.isEnabled){
            btnNyalakanBt.setText("Matikan Bluetooth")
        }else{
            btnNyalakanBt.setText("Nyalakan Bluetooth")
        }

        btn_nyalakanbt.setOnClickListener {
            if (btAdapter!!.isEnabled){
                btAdapter!!.disable()
                toast("Bluetooth OFF")
                btnNyalakanBt.setText("Nyalakan Bluetooth")
            }else{
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent,REQUEST_ENABLE_BLUETOOTH)
            }
        }

        btn_pindai.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
                } else{
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
                }
            } else {
                memindai()
                handlerLoop.post(object : Runnable{
                    override fun run() {
                        if (btHandle.companion.statusBT == "on"){
                            pindahHome()
                        }
                        handlerLoop.postDelayed(this,1000)
                    }
                })
            }
        }
        btn_ok.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }


    }

    override fun onBackPressed() {
        toast("Tidak bisa menggunakan back button")
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.currentActivity.remove(this)
        handlerLoop.removeCallbacksAndMessages(null)
    }

    private fun pindahHome(){
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun memindai() {
        toast("Memindai bluetooth")
        bts.clear()
        btAdapter!!.bluetoothLeScanner.startScan(bleScanner)
        btn_pindai.isEnabled = false
        btn_pindai.isClickable = false
        Handler().postDelayed({
            btAdapter!!.bluetoothLeScanner.stopScan(bleScanner)
            toast("Selesai")
            btn_pindai.isEnabled = true
            btn_pindai.isClickable = true
            val adapter = CustomAdapter(bts)
            rv_BluetoothList.adapter = adapter
        },3000)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if((ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED)){
                        memindai()
                    }
                } else {
                    toast("Tidak dapat akses")
                }
                return
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_ENABLE_BLUETOOTH ->
                if (resultCode ==  Activity.RESULT_OK){
                    Toast.makeText(this, "Bluetooth  ON", Toast.LENGTH_SHORT).show()
                    val btnNyalakanBt : Button = findViewById(R.id.btn_nyalakanbt)
                    btnNyalakanBt.text = "Matikan Bluetooth"
                }else{
                    Toast.makeText(this, "Bluetooth  Tidak Bisa ON", Toast.LENGTH_SHORT).show()
                }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

}
