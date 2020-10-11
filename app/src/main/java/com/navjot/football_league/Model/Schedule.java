package com.navjot.football_league.Model;

public class Schedule {
    private String scheduleId,team1,team2,team1Id,team2Id,team1Logo,team2Logo,matchDate,matchTime,matchLocation;

    public Schedule(String scheduleId, String team1, String team2, String team1Id, String team2Id, String team1Logo, String team2Logo, String matchDate, String matchTime, String matchLocation) {
        this.scheduleId = scheduleId;
        this.team1 = team1;
        this.team2 = team2;
        this.team1Id = team1Id;
        this.team2Id = team2Id;
        this.team1Logo = team1Logo;
        this.team2Logo = team2Logo;
        this.matchDate = matchDate;
        this.matchTime = matchTime;
        this.matchLocation = matchLocation;
    }

    public Schedule() {
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public String getTeam1Id() {
        return team1Id;
    }

    public void setTeam1Id(String team1Id) {
        this.team1Id = team1Id;
    }

    public String getTeam2Id() {
        return team2Id;
    }

    public void setTeam2Id(String team2Id) {
        this.team2Id = team2Id;
    }

    public String getTeam1Logo() {
        return team1Logo;
    }

    public void setTeam1Logo(String team1Logo) {
        this.team1Logo = team1Logo;
    }

    public String getTeam2Logo() {
        return team2Logo;
    }

    public void setTeam2Logo(String team2Logo) {
        this.team2Logo = team2Logo;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public String getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    public String getMatchLocation() {
        return matchLocation;
    }

    public void setMatchLocation(String matchLocation) {
        this.matchLocation = matchLocation;
    }
}
