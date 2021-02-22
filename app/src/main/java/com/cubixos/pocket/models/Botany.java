package com.cubixos.pocket.models;

public class Botany {

    private long timeStamp;//timestampLong
    private String timeDate;//TimeDate DDMMYYYHHMMSSAA
    private String status;//new/old
    //
    private String title;//filename set as File Name and remove extension
    private String description;
    private String facultyId;//auto selected according to subject
    private String studySubject;
    private String studyClass;
    private String studySemester;
    private String fileEdit;
    private String fileDownload;
    //
    private String fileName;//filename.extension
    private long fileSize;
    private long filePagesDuration;//pages for pdf/doc/xls/ppt, duration for Audio/Video
    private String fileExtension;
    private String fileMIMEType;//pdf/image/video/doc/ppt/xls
    private String fileUrl;//link of file
    private String fileThumbnailUrl;//link of file

    private String userID;

    public Botany() {
        //empty Constructor
    }

    public Botany(long timeStamp, String timeDate, String status, String title, String description, String facultyId, String studySubject, String studyClass, String studySemester, String fileEdit, String fileDownload, String fileName, long fileSize, long filePagesDuration, String fileExtension, String fileMIMEType, String fileUrl, String fileThumbnailUrl, String userID) {
        this.timeStamp = timeStamp;
        this.timeDate = timeDate;
        this.status = status;
        this.title = title;
        this.description = description;
        this.facultyId = facultyId;
        this.studySubject = studySubject;
        this.studyClass = studyClass;
        this.studySemester = studySemester;
        this.fileEdit = fileEdit;
        this.fileDownload = fileDownload;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePagesDuration = filePagesDuration;
        this.fileExtension = fileExtension;
        this.fileMIMEType = fileMIMEType;
        this.fileUrl = fileUrl;
        this.fileThumbnailUrl = fileThumbnailUrl;
        this.userID = userID;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getTimeDate() {
        return timeDate;
    }

    public String getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public String getStudySubject() {
        return studySubject;
    }

    public String getStudyClass() {
        return studyClass;
    }

    public String getStudySemester() {
        return studySemester;
    }

    public String getFileEdit() {
        return fileEdit;
    }

    public String getFileDownload() {
        return fileDownload;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getFilePagesDuration() {
        return filePagesDuration;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getFileMIMEType() {
        return fileMIMEType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getFileThumbnailUrl() {
        return fileThumbnailUrl;
    }

    public String getUserID() {
        return userID;
    }
}