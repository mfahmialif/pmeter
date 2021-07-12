package com.mfahmialif.pulseoximeter

class Data {
    var petugas : String = ""
    var nama : String = ""
    var pulse : Double = 0.0
    var bpm : Double = 0.0
    var dtime : String = ""

    constructor(petugas: String, nama:String, pulse: Double, bpm: Double, dtime: String){
        this.petugas = petugas
        this.nama = nama
        this.pulse = pulse
        this.bpm = bpm
        this.dtime = dtime

    }

    constructor(nama:String, dtime: String){
        this.nama = nama
        this.dtime = dtime

    }

    constructor(){
    }

}