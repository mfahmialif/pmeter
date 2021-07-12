package com.mfahmialif.pulseoximeter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_data_local.*
import org.jetbrains.anko.toast
import org.json.JSONObject

class DataLocalActivity : AppCompatActivity() {
    val dataList = ArrayList<Data>()
    var db = DatabaseHandler(context = this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_local)
        MainActivity.currentActivity.add(this)

        var loaderActivity : LoaderActivity = LoaderActivity(this)

        AndroidNetworking.initialize(getApplicationContext());

        rv_listDataLocal_datalocal.layoutManager = LinearLayoutManager(this)

        val rdata = db.readData()

        for (i in 0 until rdata.size){
            val data = Data(rdata.get(i).petugas,rdata.get(i).nama,rdata.get(i).pulse,rdata.get(i).bpm,rdata.get(i).dtime)
            dataList.add(data)
        }
        val adapter = DataLocalAdapter(dataList)
        rv_listDataLocal_datalocal.adapter = adapter

        btn_kirimkan_datalocal.setOnClickListener {
            loaderActivity.startLoadingDialog()
            if (dataList.size != 0){
                for (dl in dataList){
                    AndroidNetworking.get("https://pmeter.my.id/api/kirim-data-dtime?token=ikuzo66&petugas=${dl.petugas}&nama=${dl.nama}&pulse=${dl.pulse}&bpm=${dl.bpm}&dtime=${dl.dtime}")
                        .setTag("kirimData")
                        .setPriority(Priority.LOW)
                        .build()
                        .getAsJSONObject(object : JSONObjectRequestListener {
                            override fun onResponse(response: JSONObject) {
                                loaderActivity.dismissDialog()
                                kirimkan("Berhasil Dikirimkan",dl)
                            }
                            override fun onError(error: ANError) {
                                // handle error
                                loaderActivity.dismissDialog()
                            }
                        })
                }
            }else{
                toast("Tidak ada data")
            }

        }

        btn_hapus_datalocal.setOnClickListener {
            if (dataList.size != 0){
                boxMessage("Apakah anda yakin menghapus data tanpa dikirimkan ke database ?")
            }else{
                toast("Tidak ada data")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.currentActivity.remove(this)
        finish()
    }

    private fun kirimkan(pesan: String, data:Data){
        dataList.remove(data)
        db.deleteData(data.dtime)
        if (dataList.size == 0){
            var alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            alertDialog.setTitle("Pesan")
            alertDialog.setMessage(pesan)
            alertDialog.setPositiveButton("OK"){_,_ ->
                startActivity(Intent(this,LoginActivity::class.java))
            }
            val alert : AlertDialog = alertDialog.create()
            alert.setCanceledOnTouchOutside(false)
            alert.show()
        }

    }

    private fun boxMessage(pesan: String){
        var alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle("Pesan")
        alertDialog.setMessage(pesan)
        alertDialog.setPositiveButton("OK"){_,_ ->
            startActivity(Intent(this,LoginActivity::class.java))
        }
        alertDialog.setNegativeButton("Batal"){_,_ ->
            hapusData()
        }
        val alert : AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    private fun hapusData(){
        db.deleteAllData()
    }
}