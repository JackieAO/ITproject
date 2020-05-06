package com.example.itprojects.bean;

import java.util.Date;

public class Update {
    private String Username;
    private Date Upload;
    private String UserImgae;
    private String Title;
    private String Desc;
    private String ArtifactImage;
    private boolean status;
    private  String UserId;
    private String key;

    public Update(String username, Date upload, String userImgae
            ,String title, String desc, String artifactImage
            ,boolean status, String userId, String key) {
        this.Username = username;
        this.Upload = upload;
        this.UserImgae = userImgae;
        this.Title = title;
        this.Desc = desc;
        this.ArtifactImage = artifactImage;
        this.status = status;
        this.UserId = userId;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public Date getUpload() {
        return Upload;
    }

    public void setUpload(Date upload) {
        Upload = upload;
    }

    public String getUserImgae() {
        return UserImgae;
    }

    public void setUserImgae(String userImgae) {
        UserImgae = userImgae;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getArtifactImage() {
        return ArtifactImage;
    }

    public void setArtifactImage(String artifactImage) {
        ArtifactImage = artifactImage;
    }
}
