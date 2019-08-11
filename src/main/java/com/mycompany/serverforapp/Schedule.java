/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.serverforapp;

/**
 *
 * @author march
 */
public class Schedule {
    String match_date;
    String match_time;
    int id_stadium;
    int id_tour;
    String name_stadium;
    int id_match;
    String name_division;
    String team_home;
    String team_guest;
    int busy_time;

    public Schedule(String match_date, String match_time, int id_stadium, int id_tour, 
            String name_stadium, int id_match, String name_division, String team_home, String team_guest,int busy_time) {
        this.match_date = match_date;
        this.match_time = match_time;
        this.id_stadium = id_stadium;
        this.id_tour = id_tour;
        this.name_stadium = name_stadium;
        this.id_match = id_match;
        this.name_division = name_division;
        this.team_home = team_home;
        this.team_guest = team_guest;
        this.busy_time = busy_time;
    }

    @Override
    public String toString() {
        return "Schedule{" + "match_date=" + match_date + ", match_time=" + match_time + 
                ", id_stadium=" + id_stadium + ", id_tour=" + id_tour + 
                ", name_stadium=" + name_stadium + 
                ", id_match=" + id_match + ", team_home=" + team_home + ", team_guest=" + 
                team_guest + ", busy_time=" + busy_time + '}' + "\n";
    }

    
    
}
