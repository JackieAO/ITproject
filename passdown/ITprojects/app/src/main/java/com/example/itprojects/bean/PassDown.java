package com.example.itprojects.bean;

/**
 * The class PassDown to store the information about a PassDown space.
 *
 * @author  Team Perfect World
 * @version 8.0
 * @since   2019-09-07
 */

public class PassDown {

    private String passdown_Name;
    private String passdown_Username;
    private String UserId;

    /**
     * Required empty public constructor.
     */

    public PassDown() {
    }

    public PassDown(String passdown_Name, String passdown_Username, String userId) {
        this.passdown_Name = passdown_Name;
        this.passdown_Username = passdown_Username;
        this.UserId = userId;
    }

    /**
     * Get the name of the Passdown space.
     *
     * @return pass down name
     *
     */

    public String getPassdown_Name() {
        return passdown_Name;
    }

    /**
     * Set the name of the Passdown space.
     *
     * @param passdown_Name       the new name to set.
     *
     */

    public void setPassdown_Name(String passdown_Name) {
        this.passdown_Name = passdown_Name;
    }

    /**
     * Get the user name of the Passdown space.
     *
     * @return user name
     *
     */

    public String getPassdown_Username() {
        return passdown_Username;
    }

    /**
     * Set the user name of the Passdown space.
     *
     * @param passdown_Username       the new user name to set.
     *
     */

    public void setPassdown_Username(String passdown_Username) {
        this.passdown_Username = passdown_Username;
    }

    /**
     * Get the user id of the Passdown space.
     *
     * @return user id
     *
     */

    public String getUserId() {
        return UserId;
    }

    /**
     * Set the user id of the Passdown space.
     *
     * @param userId      the user id to set.
     *
     */

    public void setUserId(String userId) {
        UserId = userId;
    }
}
