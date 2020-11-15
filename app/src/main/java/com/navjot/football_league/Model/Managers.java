package com.navjot.football_league.Model;

public class Managers {
    private String name,phone,teamName,managerImage;

    public Managers() {
    }

    public Managers(String name, String phone, String teamName,String managerImage) {
        this.name = name;
        this.phone = phone;
        this.teamName = teamName;
        this.managerImage = managerImage;
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

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getManagerImage() {
        return managerImage;
    }

    public void setManagerImage(String managerImage) {
        this.managerImage = managerImage;
    }
}

