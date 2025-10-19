package common;

import java.io.Serializable;

public class FileTransfer implements Serializable {
    private static final long serialVersionUID = 2L;

    private String sender;
    private String recipient;
    private String fileName;
    private long fileSize;
    private byte[] fileData; // Sadržaj fajla kao niz bajtova

    public FileTransfer(String sender, String recipient, String fileName, byte[] fileData) {
        this.sender = sender;
        this.recipient = recipient;
        this.fileName = fileName;
        this.fileData = fileData;
        this.fileSize = fileData.length;
    }

    // ----------- Getteri su neophodni -----------
    
    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public byte[] getFileData() {
        return fileData;
    }
    
    // Nema potrebe za setteri-ma
}