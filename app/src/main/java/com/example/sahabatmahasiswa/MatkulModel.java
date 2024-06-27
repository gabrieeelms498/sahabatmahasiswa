package com.example.sahabatmahasiswa;
public class MatkulModel {
    private String matkul;
    private String sks;
    private String smt;
    private String ruangan;
    private String dosen;
    private String start_time;
    private String end_time;
    private String day;

    public String getmatkulId() {
        return matkulId;
    }

    public void setmatkulId(String matkulId) {
        this.matkulId = matkulId;
    }

    private String matkulId;

    public MatkulModel() {
        // Empty constructor needed for Firestore
    }



    public MatkulModel(String matkul, String sks, String smt, String ruangan, String dosen, String start_time, String end_time, String day) {
        this.matkul = matkul;
        this.sks = sks;
        this.smt = smt;
        this.ruangan = ruangan;
        this.dosen = dosen;
        this.start_time = start_time;
        this.end_time = end_time;
        this.day = day;

    }

    public String getMatkul() {
        return matkul;
    }

    public void setMatkul(String matkul) {
        this.matkul = matkul;
    }

    public String getSks() {
        return sks;
    }

    public void setSks(String sks) {
        this.sks = sks;
    }

    public String getSmt() {
        return smt;
    }

    public void setSmt(String smt) {
        this.smt = smt;
    }

    public String getRuangan() {
        return ruangan;
    }

    public void setRuangan(String ruangan) {
        this.ruangan = ruangan;
    }

    public String getDosen() {
        return dosen;
    }

    public void setDosen(String dosen) {
        this.dosen = dosen;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}