package com.mfahmialif.pulseoximeter

import android.bluetooth.*
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class CustomAdapter(val btList: ArrayList<Bt>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    val btHandle = BluetoothHandle()
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.bt_layout,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bt : Bt = btList[position]
        holder.textNamabt.text = bt.result?.device?.name.toString()
        holder.textalamat.text = bt.result?.device?.address.toString()

        sharedPreferences = MainActivity.currentActivity.last().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("DEVICE_ADDR", bt.result?.device?.address.toString())
        editor.apply()
        Log.w("Save MAC Address", "Save MAC Address : ${bt.result?.device?.address.toString()}")

        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context, bt.result?.device?.name.toString(), Toast.LENGTH_SHORT).show()
            btHandle.connect(bt.result!!.device, holder.itemView.context)
        }
    }

    override fun getItemCount(): Int {
        return btList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textNamabt = itemView.findViewById(R.id.txt_namabt) as TextView
        val textalamat = itemView.findViewById(R.id.txt_alamat) as TextView
    }
}
//00002901-0000-1000-8000-00805f9b34fb
//00002902-0000-1000-8000-00805F9B34FB