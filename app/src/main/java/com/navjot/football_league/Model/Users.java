package com.navjot.football_league.Model;

public class Users {
    private String name,phone,password,teamName,userType;

    public Users() {
    }

    public Users(String name, String phone, String password, String teamName,String userType) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.teamName = teamName;
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
