package com.navjot.football_league.Model;

public class Team {
    private String teamId,teamName,managerName,teamLogo;
    private int teamSize,matches,wins,loses,ties,points;

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    public int getMatches() {
        return matches;
    }

    public void setMatches(int matches) {
        this.matches = matches;
    }

    public Team() {
    }

    public Team(String teamId, String teamName, String managerPhone, String managerName, String teamLogo,int teamSize,int matches, int wins, int loses, int ties, int points) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.managerName = managerName;
        this.teamLogo = teamLogo;
        this.teamSize = teamSize;
        this.matches = matches;
        this.wins = wins;
        this.loses = loses;
        this.ties = ties;
        this.points = points;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getTeamLogo() {
        return teamLogo;
    }

    public void setTeamLogo(String teamLogo) {
        this.teamLogo = teamLogo;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLoses() {
        return loses;
    }

    public void setLoses(int loses) {
        this.loses = loses;
    }

    public int getTies() {
        return ties;
    }

    public void setTies(int ties) {
        this.ties = ties;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
