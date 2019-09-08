package com.mycompany.serverforapp;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Markable
 */
public class NextMatches {
    
    private String nameDivision;
    private int idMatch;
    private int idDivision;
    private int idTour;
    private String teamHome;
    private String teamVisit;
    private String date;
    private String nameStadium;
    String goal;

    public NextMatches(String nameDivision, int idTour, String teamHome, String teamVisit, String date, String nameStadium) {
        this.nameDivision = nameDivision;
        this.idTour = idTour;
        this.teamHome = teamHome;
        this.teamVisit = teamVisit;
        this.date = date;
        this.nameStadium = nameStadium;
    }

    public NextMatches(int idMatch, int idDivision, String nameDivision, int idTour, String teamHome, String teamVisit, String date, String nameStadium) {
        this.idMatch = idMatch;
        this.idDivision = idDivision;
        this.nameDivision = nameDivision;
        this.idTour = idTour;
        this.teamHome = teamHome;
        this.teamVisit = teamVisit;
        this.date = date;
        this.nameStadium = nameStadium;
    }

    public NextMatches(int idMatch, int idDivision, String nameDivision, int idTour, String teamHome, 
            String teamVisit, String date, String nameStadium, String goal) {
        this.idMatch = idMatch;
        this.idDivision = idDivision;
        this.nameDivision = nameDivision;
        this.idTour = idTour;
        this.teamHome = teamHome;
        this.teamVisit = teamVisit;
        this.date = date;
        this.nameStadium = nameStadium;
        this.goal = goal;
    }
    
    public String getNameDivision() {
        return nameDivision;
    }

    public int getIdTour() {
        return idTour;
    }

    public String getTeamHome() {
        return teamHome;
    }

    public String getTeamVisit() {
        return teamVisit;
    }

    public String getDate() {
        return date;
    }

    public String getNameStadium() {
        return nameStadium;
    }
    
}
