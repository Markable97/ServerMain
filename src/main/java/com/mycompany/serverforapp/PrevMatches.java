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
public class PrevMatches {
    
     int id_match;
     String nameDivision;
     int idTour;
     String teamHome;
     int goalHome;
     int goalVisit;
     String teamVisit;
     transient String urlImageHome;
     transient String urlImageGuest;

    public PrevMatches(String nameDivision, int idTour, String teamHome, int goalHome, int goalVisit, String teamVisit) {
        this.nameDivision = nameDivision;
        this.idTour = idTour;
        this.teamHome = teamHome;
        this.goalHome = goalHome;
        this.goalVisit = goalVisit;
        this.teamVisit = teamVisit;
    }

    public PrevMatches(int id_match, String nameDivision, int idTour, String teamHome, int goalHome, int goalVisit, String teamVisit, String urlImageH, String urlImageG) {
        this.id_match = id_match;
        this.nameDivision = nameDivision;
        this.idTour = idTour;
        this.teamHome = teamHome;
        this.goalHome = goalHome;
        this.goalVisit = goalVisit;
        this.teamVisit = teamVisit;
        this.urlImageHome = urlImageH;
        this.urlImageGuest = urlImageG;
    }

    public PrevMatches(){
        
    }

    @Override
    public String toString() {
        return "PrevMatches{" + "id_match=" + id_match + ", nameDivision=" + nameDivision + ", idTour=" + idTour + ", teamHome=" + teamHome + ", goalHome=" + goalHome + ", goalVisit=" + goalVisit + ", teamVisit=" + teamVisit + '}';
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

    public int getGoalHome() {
        return goalHome;
    }

    public int getGoalVisit() {
        return goalVisit;
    }

    public String getTeamVisit() {
        return teamVisit;
    }

    String getUrlImageHome() {
      return urlImageHome;
    }

    String getUrlImageGuest() {
        return urlImageGuest;
    }
}
