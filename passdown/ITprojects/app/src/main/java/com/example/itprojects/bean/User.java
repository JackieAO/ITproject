package com.example.itprojects.bean;

/**
 * The class User to store the information about a user.
 *
 * @author  Team Perfect World
 * @version 8.0
 * @since   2019-09-07
 */

public class User {

    private String email;
    private String image;
    private String surName;
    private String firstName;
    private String Passdown_Name;
    private String Invited_Code;

    /**
     * Required empty public constructor.
     */

    public User() {
    }

    public User(String email, String image, String surName,
                String firstName, String passdown_Name,String Invited_Code) {
        this.email = email;
        this.image = image;
        this.surName = surName;
        this.firstName = firstName;
        this.Passdown_Name = passdown_Name;
        this.Invited_Code=Invited_Code;
    }

    public String getInvited_Code() {
        return Invited_Code;
    }

    public void setInvited_Code(String invited_Code) {
        Invited_Code = invited_Code;
    }

    /**
     * Get the email of the User.
     *
     * @return user email
     *
     */

    public String getEmail() {
        return email;
    }

    /**
     * Set the email of the User.
     *
     * @param email      the email to set.
     *
     */

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the photo of the User.
     *
     * @return image
     *
     */

    public String getImage() {
        return image;
    }

    /**
     * Set the photo of the User.
     *
     * @param image      the image to set.
     *
     */

    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Get the surname of the User.
     *
     * @return user surname
     *
     */

    public String getSurName() {
        return surName;
    }

    /**
     * Set the surname of the User.
     *
     * @param surName      the surname to set.
     *
     */

    public void setSurName(String surName) {
        this.surName = surName;
    }

    /**
     * Get the first name of the User.
     *
     * @return user first name
     *
     */

    public String getFirstName() {
        return firstName;
    }

    /**
     * Set the first name of the User.
     *
     * @param firstName      the first name to set.
     *
     */

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get the pass down name of the User.
     *
     * @return passdown name
     *
     */

    public String getPassdown_Name() {
        return Passdown_Name;
    }

    /**
     * Set the pass down space name of the User.
     *
     * @param passdown_Name      the pass down space name to set.
     *
     */

    public void setPassdown_Name(String passdown_Name) {
        this.Passdown_Name = passdown_Name;
    }
}
