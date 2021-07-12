package com.mfahmialif.pulseoximeter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RiwayatAdapter(val dataList : ArrayList<Data>) : RecyclerView.Adapter<RiwayatAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiwayatAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.riwayat_layout,parent,false)
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
        val textNama = itemView.findViewById(R.id.txt_nama_riwayat) as TextView
        val textData = itemView.findViewById(R.id.txt_pulse_bpm_riwayat) as TextView
        val textDatetime = itemView.findViewById(R.id.txt_datetime_riwayat) as TextView
    }

}