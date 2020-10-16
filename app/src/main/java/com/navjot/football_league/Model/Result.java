package com.navjot.football_league.Model;

public class Result {
    private String matchDate,matchTime,resultId,team1,team1Id,team1Logo,team2,team2Id,team2Logo,winner;
    private int team1Score,team2Score;

    public Result() {
    }

    public Result(String matchDate, String matchTime, String resultId, String team1, String team1Id, String team1Logo, String team2, String team2Id, String team2Logo, String winner, int team1Score, int team2Score) {
        this.matchDate = matchDate;
        this.matchTime = matchTime;
        this.resultId = resultId;
        this.team1 = team1;
        this.team1Id = team1Id;
        this.team1Logo = team1Logo;
        this.team2 = team2;
        this.team2Id = team2Id;
        this.team2Logo = team2Logo;
        this.winner = winner;
        this.team1Score = team1Score;
        this.team2Score = team2Score;
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

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam1Id() {
        return team1Id;
    }

    public void setTeam1Id(String team1Id) {
        this.team1Id = team1Id;
    }

    public String getTeam1Logo() {
        return team1Logo;
    }

    public void setTeam1Logo(String team1Logo) {
        this.team1Logo = team1Logo;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public String getTeam2Id() {
        return team2Id;
    }

    public void setTeam2Id(String team2Id) {
        this.team2Id = team2Id;
    }

    public String getTeam2Logo() {
        return team2Logo;
    }

    public void setTeam2Logo(String team2Logo) {
        this.team2Logo = team2Logo;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public int getTeam1Score() {
        return team1Score;
    }

    public void setTeam1Score(int team1Score) {
        this.team1Score = team1Score;
    }

    public int getTeam2Score() {
        return team2Score;
    }

    public void setTeam2Score(int team2Score) {
        this.team2Score = team2Score;
    }
}
