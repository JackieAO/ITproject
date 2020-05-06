package com.example.itprojects.bean;

import java.util.Date;

/**
 * The class Artifact to store the information about an artifact.
 *
 * @author  Team Perfect World
 * @version 8.0
 * @since   2019-09-07
 */

public class Artifact {

    private String title;
    private String desc;
    private String image;
    private Date uploadTime;
    private String key;
    private boolean status;
    private  String UserId;

    /**
     * Required empty public constructor.
     */

    public Artifact() {

    }

    public Artifact(String title, String desc, String image, Date uploadTime, String key, boolean status,String UserId) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.uploadTime = uploadTime;
        this.key = key;
        this.status=status;
        this.UserId=UserId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    /**
     * Get the key of the artifacts.
     *
     * @return key
     *
     */

    public String getKey() {
        return key;
    }

    /**
     * Set the key of the artifacts.
     *
     * @param key       the new key to set.
     *
     */

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Get the title of the artifacts.
     *
     * @return title
     *
     */

    public String getTitle() {
        return title;
    }

    /**
     * Set the title of the artifacts.
     *
     * @param title       the new title to set.
     *
     */

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the desc of the artifacts.
     *
     * @return desc
     *
     */

    public String getDesc() {
        return desc;
    }

    /**
     * Set the desc of the artifacts.
     *
     * @param desc      the new desc to set.
     *
     */

    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Get the image of the artifacts.
     *
     * @return image
     *
     */

    public String getImage() {
        return image;
    }

    /**
     * Set the image of the artifacts.
     *
     * @param image       the image to set.
     *
     */

    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Get the upload time of the artifacts.
     *
     * @return upload time
     *
     */

    public Date getUploadTime() {
        return uploadTime;
    }

    /**
     * Set the upload time of the artifacts.
     *
     * @param uploadTime       the upload time to set.
     *
     */

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}
