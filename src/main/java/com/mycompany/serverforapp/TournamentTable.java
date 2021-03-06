package com.mycompany.serverforapp;


import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Markable
 */
public class TournamentTable implements Serializable {

    String nameLeague;
    private String divisionName;
    int idTeam;
    private String teamName;
    private int games;
    private int points;
    private int wins;
    private int draws;
    private int losses;
    private int goalScored;
    private int goalConceded;
    private int sc_con;
    private String imageBase64;
    private transient String urlImage;
   
    public TournamentTable(String division, String team, int games,int wins, int draws, int losses,
            int goalScored, int goalConceded, int sc_con, int points, String urlImage){
        this.divisionName = division;
        this.teamName = team;
        this.games = games;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.goalScored = goalConceded;
        this.goalConceded = goalConceded;
        this.sc_con = sc_con;
        this.points = points;
        this.urlImage = urlImage;
    }
    
    public TournamentTable(String division, String team, int games, int point, int wins, int draws, int losses,
            int goalScored, int goalConceded,String urlImage){
        setDivision(division);
        setTeam(team);
        setPoint(point);
        setGames(games);
        setWins(wins);
        setDraws(draws);
        setLosses(losses);
        setGoalScored(goalScored);
        setGoalConceded(goalConceded);
        setUrlImage(urlImage);
        
    }

    private void setDivision(String division) {
        this.divisionName = division;
     }

    private void setTeam(String team) {
        this.teamName = team;
    }

    private void setGames(int games) {
        this.games = games;
    }
    private void setPoint(int point) {
        this.points = point;
    }
    private void setWins(int wins) {
        this.wins = wins;
    }

    private void setDraws(int draws) {
        this.draws = draws;
    }

    private void setLosses(int losses) {
        this.losses = losses;
    }
    
    private void setGoalScored(int goalScored) {
        this.goalScored = goalScored;
    }
    
    private void setGoalConceded(int goalConceded) {
        this.goalConceded = goalConceded;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
    
    public String getDivisionName() {
        return divisionName;
    }
    
    public String getTeamName() {
        return teamName;
    }

    public int getGames() {
        return games;
    }

    public int getPoint() {
        return points;
    }

    public int getWins() {
        return wins;
    }

    public int getDraws() {
        return draws;
    }

    public int getLosses() {
        return losses;
    }
    
    public int getGoalScored() {
        return goalScored;
    }

    public int getGoalConceded() {
        return goalConceded;
    }

    public String getUrlImage() {
        return urlImage;
    }
    private void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}
