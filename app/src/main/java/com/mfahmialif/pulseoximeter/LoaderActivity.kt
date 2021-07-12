package com.mfahmialif.pulseoximeter

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater

class LoaderActivity {
    lateinit var activity: Activity
    lateinit var dialog: AlertDialog

    constructor(myActivity: Activity){
        activity = myActivity
    }

    fun startLoadingDialog() {
        var builder : AlertDialog.Builder = AlertDialog.Builder(activity)
        var inflater : LayoutInflater = activity.getLayoutInflater()
        builder.setView(inflater.inflate(R.layout.loader,null))
        builder.setCancelable(true)
        dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    fun dismissDialog(){
        dialog.dismiss()
    }
}