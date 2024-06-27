package com.example.sahabatmahasiswa;

public class FileModel {
    private String fileName;

    public String getFilesId() {
        return filesId;
    }

    public void setFilesId(String filesId) {
        this.filesId = filesId;
    }

    private String filesId;
    public FileModel() {
        // Diperlukan konstruktor kosong untuk Firebase
    }
    public FileModel(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
