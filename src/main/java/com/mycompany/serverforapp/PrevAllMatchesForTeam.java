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
public class PrevAllMatchesForTeam {
    
    PrevMatches matches;
    String urlImageHome;
    String urlImageGuest;
    
    public PrevAllMatchesForTeam(String nameDivision, int idTour, String teamHome, 
            int goalHome, int goalVisit, String teamVisit, String urlImageHome, String urlImageGuest) {
        matches = new PrevMatches(nameDivision, idTour, teamHome, goalHome, goalVisit, teamVisit);
        this.urlImageHome = urlImageHome;
        this.urlImageGuest = urlImageGuest;
    }

    public String getUrlImageHome() {
        return urlImageHome;
    }

    public String getUrlImageGuest() {
        return urlImageGuest;
    }
    
    @Override
    public String toString() {
        return "AllMatches{" + "nameDivision=" + matches.nameDivision + ", idTour=" + matches.idTour + ", teamHome=" + matches.teamHome + 
                ", goalHome=" + matches.goalHome + ", goalVisit=" + matches.goalVisit + ", teamVisit=" + matches.teamVisit + 
                " urlImageHome=" + this.urlImageHome + " urlImageGuest=" + this.urlImageGuest + "}\n";
    }
    
}
