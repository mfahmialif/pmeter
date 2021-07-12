package com.mfahmialif.pulseoximeter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class DataLocalAdapter(val dataList : ArrayList<Data>) : RecyclerView.Adapter<DataLocalAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.datalokal_layout,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dl : Data = dataList[position]
        holder.textNama.text = dl.nama.toString()
        holder.textData.text = "Pulse : ${dl.pulse} BPM : ${dl.bpm}"
        holder.textDatetime.text = dl.dtime.toString()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textNama = itemView.findViewById(R.id.txt_nama_datalokal) as TextView
        val textData = itemView.findViewById(R.id.txt_pulse_bpm_datalokal) as TextView
        val textDatetime = itemView.findViewById(R.id.txt_datetime_datalokal) as TextView
    }
}