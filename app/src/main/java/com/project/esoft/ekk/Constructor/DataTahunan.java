package com.project.esoft.ekk.Constructor;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class DataTahunan implements Serializable {

    private String bulan;
    private String jumlah;

    public DataTahunan(){

    }

    public String getBulan() {
        return bulan;
    }

    public void setBulan(String bulan) {
        this.bulan = bulan;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    @Override
    public String toString() {
        return " "+bulan+"\n" +
                " "+ jumlah ;
    }

    public DataTahunan(String bulan, String jumlah){
        this.bulan = bulan;
        this.jumlah = jumlah;
    }
}