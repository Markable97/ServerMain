package com.mycompany.serverforapp;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Markable
 */
public class DataBaseRequest {
    
    public static DataBaseRequest db;
    private int settingForApp;
    Connection connect;
    private DataBaseRequest(){
       String user = "root";
       String password = "7913194";
       String url = "jdbc:mysql://localhost:3306/football_main";
        try {
            this.connect = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
     
   public static synchronized DataBaseRequest getInstance(){
       if (db == null){
           db = new DataBaseRequest();
       }
       return db;
   }
    
    String message = "SUCCESS";
    //-------переменные для sql запросов---------
    private  String sqlTournamentTable = "SELECT name_division, \n" +
"	team_name, \n" +
"       games, \n" +
"       wins,\n" +
"       draws, \n" +
"       losses, \n" +
"       goals_scored, \n" +
"       goals_conceded, \n" +
"       sc_con, \n" +
"       points, \n" +
"       logo \n" +
"       FROM football_main.v_tournament_table\n" +
"       where id_division = ?;";
    private  String sqlPrevMatches = "SELECT id_match, name_division, \n" +
"	id_division, \n" +
"       id_tour, \n" +
"       team_home, \n" +
"       goal_home, \n" +
"       goal_guest, \n" +
"       team_guest, \n" +
"       date_format(m_date,'%d-%m-%y %H:%i') as m_date, \n" +
"       name_stadium, \n" +
"       staff_name,\n" +
"       logo_home,\n" +
"       logo_guest\n" +
"       FROM football_main.v_matches \n" +
"       where (to_days(curdate()) - to_days(m_date) ) >= 0 and (to_days(curdate()) - to_days(m_date)) < 8\n" +
"       and id_division = ?;";
    private String sqlNextMatches = "SELECT name_division, \n" +
"       id_tour, \n" +
"       team_home, \n" +
"       team_guest, \n" +
"       date_format(m_date,'%d-%m-%y %H:%i') as m_date, \n" +
"       name_stadium, \n" +
"       staff_name\n" +
"       FROM football_main.v_matches m\n" +
"       where curdate() < m_date and id_division = ?;";
    private  String sqlSquadInfo = "select team_name, \n" +
"	name, \n" +
"       name_amplua,\n" +
"       date_format(birthdate, '%d-%m-%Y') as birthdate,\n" +
"       number,\n" +
"       games,\n" +
"       goal,\n" +
"       penalty,\n" +
"       assist,\n" +
"       yellow_card,\n" +
"       red_card,\n" +
"       photo\n" +
"       from v_squad\n" +
"       where team_name = ?;";
    private  String sqlAllMatches = "SELECT id_match, name_division, \n" +
"	id_division, \n" +
"       id_tour, \n" +
"       team_home, \n" +
"       goal_home, \n" +
"       goal_guest, \n" +
"       team_guest, \n" +
"       date_format(m_date,'%d-%m-%y %H:%i') as m_date, \n" +
"       name_stadium, \n" +
"       staff_name,\n" +
"       logo_home,\n" +
"       logo_guest\n" +
"       FROM football_main.v_matches m\n" +
"       where team_home = ? or team_guest = ?\n" +
"       order by id_tour desc;";
    private  String sqlPlayersInMatch = "select pm.id_player, pm.name,\n" +
"	   pm.team_name,\n" +
"	   pm.number,\n" +
"       pm.count_goals,\n" +
"       pm.count_assist,\n" +
"       pm.penalty,\n" +
"       pm.penalty_out,\n" +
"       pm.yellow,\n" +
"       pm.red,\n" +
"       pm.own_goal \n" +
"       from v_players_in_matche pm\n" +
"       where id_match  = ?";
    //------------------------------------------
    private  String sqlInsertUsers = "insert into users\n" +
"set id_type = 1,\n" +
"    id_team = 1,\n" +
"    unique_id = ?,\n" +
"    name = ?,\n" +
"    email = ?,\n" +
"    encrypted_password = ?,\n" +
"    salt = ?,\n" +
"    created_user = ?;";
    private  String sqlFindUsers = "select \n" +
"	encrypted_password,\n" +
"       salt \n" +
"       id_type \n" +
"       from users\n" +
"       where email = ?;";
    //------Для подготовки запросов-------------
    private  PreparedStatement prTournamentTable;
    private  PreparedStatement prPrevMatches;
    private  PreparedStatement prNextMatches;
    private  PreparedStatement prSquadInfo;
    private  PreparedStatement prAllMatches;
    private  PreparedStatement prPlayerInMatch;
    private  PreparedStatement prInsertUsers;
    private  PreparedStatement prFindUsers;
    //------------------------------------------
    //------Курсоры или результаты запросов
    private  ResultSet rsTournamnetTable;
    private  ResultSet rsPrevMatches;
    private  ResultSet rsNextMathces;
    private  ResultSet rsSquadInfo;
    private  ResultSet rsAllMatches;
    private  ResultSet rsPlayerInMatch;
    private  ResultSet rsFindUsers;
    //------------------------------------------
    //-----Масимы классов для вытаскивания информации-----
    private  ArrayList<TournamentTable> tournamentTable = new ArrayList<TournamentTable>();
    private  ArrayList<PrevMatches> prevMatches = new ArrayList<>();
    private  ArrayList<NextMatches> nextMatches = new ArrayList<>();
    private  ArrayList<Player> squadInfo = new ArrayList<>();
    //------------------------------------------
    
    /*public DataBaseRequest(String name_team, String message, int id_match)throws SQLException{
        squadInfo.clear();
        prevMatches.clear();
        switch (message) {
            case "team":
                connection(name_team);
                break;
            case "player":
                connection_playerInMatch(id_match);
                break;
            default:
                connection_allMatches(name_team);
                break;
        }
    }*/
    /*public DataBaseRequest(UsersLogin user_info, String name, String email) throws SQLException{
        connection_register(user_info, name, email);
    }
    
    public DataBaseRequest(String email, String password) throws SQLException{
        connection_login(email, password);
    }*/
    
    
    void connection_login(String email, String passwordUser) throws SQLException{
        try {
            settingForApp = 0;
            prFindUsers = connect.prepareCall(sqlFindUsers);
            prFindUsers.setString(1, email);
            rsFindUsers = prFindUsers.executeQuery();
            checkUser(rsFindUsers, passwordUser);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
            message = ex.getMessage();
        }finally{
            rsFindUsers.close();
        }
    }
    
    void connection_register(UsersLogin user_info, String name, String email) throws SQLException{
        try {
            prInsertUsers = connect.prepareCall(sqlInsertUsers);
            prInsertUsers.setString(1, user_info.getUniqId());
            prInsertUsers.setString(2, name);
            prInsertUsers.setString(3, email);
            prInsertUsers.setString(4, user_info.getEncodePassword());
            prInsertUsers.setString(5, user_info.getSalt());
            Calendar dateNow = Calendar.getInstance();
            SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            prInsertUsers.setString(6, formatForDateNow.format(dateNow.getTime()));
            prInsertUsers.execute();
            message = "SUCCESS";
                  
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
            message = ex.getMessage();
        }finally{
            prInsertUsers.close();
        }
    }
    
    void connection_main_activity(int id_div) throws SQLException{
        try {
            tournamentTable.clear();
            prevMatches.clear();
            nextMatches.clear();
            //Вытаскиваем турнирную таблицу
            prTournamentTable = connect.prepareCall(sqlTournamentTable);
            prTournamentTable.setInt(1, id_div);
            rsTournamnetTable = prTournamentTable.executeQuery();
            getTournamentTable(rsTournamnetTable);
            //Сыгранные матчи
            prPrevMatches = connect.prepareCall(sqlPrevMatches);
            prPrevMatches.setInt(1, id_div);
            rsPrevMatches = prPrevMatches.executeQuery();
            //Будущие матчи
            getPrevMatches(rsPrevMatches);
            prNextMatches = connect.prepareCall(sqlNextMatches);
            prNextMatches.setInt(1, id_div);
            rsNextMathces = prNextMatches.executeQuery();
            getNextMatches(rsNextMathces);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            rsTournamnetTable.close();
            rsPrevMatches.close();
            rsNextMathces.close();
        }
    }
    
    void connection_squad_info(String name_team) throws SQLException{
        try {
            prSquadInfo = connect.prepareCall(sqlSquadInfo);
            prSquadInfo.setString(1, name_team);
            rsSquadInfo = prSquadInfo.executeQuery();
            getSquadInfo(rsSquadInfo);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            rsSquadInfo.close();
        }
    }
    
    void connection_allMatches(String name_team) throws SQLException{
        try {
            prAllMatches = connect.prepareCall(sqlAllMatches);
            prAllMatches.setString(1, name_team);
            prAllMatches.setString(2, name_team);
            rsAllMatches = prAllMatches.executeQuery();
            getAllMatches(rsAllMatches);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            rsAllMatches.close();
        }
    }
    
    void connection_playerInMatch(int id_match) throws SQLException{
        try {
            prPlayerInMatch = connect.prepareCall(sqlPlayersInMatch);
            prPlayerInMatch.setInt(1,id_match);
            rsPlayerInMatch = prPlayerInMatch.executeQuery();
            getPlayerInMatch(rsPlayerInMatch);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            rsPlayerInMatch.close();
        }
    }
    private  void getTournamentTable(ResultSet result){
        String queryOutput = "";
        try {
            while(result.next()){
                String nameDivision = result.getString("name_division");
                String teamName = result.getString("team_name");
                int games = result.getInt("games");
                int wins = result.getInt("wins");
                int draws = result.getInt("draws");
                int losses = result.getInt("losses");
                int goals_scored = result.getInt("goals_scored");
                int goals_conceded = result.getInt("goals_conceded");
                int sc_con = result.getInt("sc_con");
                int points = result.getInt("points");
                String logo = result.getString("logo");
                queryOutput += nameDivision + " " + teamName + " " + games  + " " + wins + " "  + draws + " "
                        + losses + " " + goals_scored + " " + goals_conceded + " "
                        + sc_con + " " + points + " " + logo + "\n";
                tournamentTable.add(new TournamentTable(nameDivision, teamName, games,  points, wins, draws, losses, 
                        goals_scored, goals_conceded/*, sc_con*/,  logo));
            }
            System.out.println("DataBaseRequest getTournamentTable(): output query from DB: \n" + queryOutput);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private  void getPrevMatches(ResultSet result){
        String queryOutput = "";
        try {
            while(result.next()){
                int id_match = result.getInt("id_match");
                String nameDivision = result.getString("name_division");
                int tour = result.getInt("id_tour");
                String teamHome = result.getString("team_home");
                int goalHome = result.getInt("goal_home");
                int goalGuest = result.getInt("goal_guest");
                String teamGuest = result.getString("team_guest");
                String mDate = result.getString("m_date");
                String stadium = result.getString("name_stadium");
                String logoHome = result.getString("logo_home");
                String logoGuest = result.getString("logo_guest");
                queryOutput +=id_match + " " + nameDivision + " " + tour + " " + teamHome + " " + goalHome + " " +
                        goalGuest + " " + teamGuest + " " + mDate + " " + stadium + " " + logoHome + " " + logoGuest + "\n";
                prevMatches.add(new PrevMatches(id_match, nameDivision, tour, teamHome, goalHome, goalGuest, teamGuest, logoHome, logoGuest));
            }
            System.out.println("DataBaseRequest getPrevMatches(): output query  from DB:" + queryOutput);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private  void getNextMatches(ResultSet result){
        String queryOutput = "";
        try {
            while(result.next()){
                String nameDivision = result.getString("name_division");
                int tour = result.getInt("id_tour");
                String t_home = result.getString("team_home");
                String t_guest = result.getString("team_guest");
                String m_date = result.getString("m_date");
                String stadium = result.getString("name_stadium");
                queryOutput += nameDivision + " " + tour + " " +t_home +  " " + t_guest + " " 
                            + m_date + " " + stadium + "\n";
                nextMatches.add(new NextMatches(nameDivision, tour, t_home, t_guest, m_date, stadium));
            }
            System.out.println("DataBaseRequest getNextMatchrs():output query  from DB:" + queryOutput);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private  void getSquadInfo(ResultSet result){
        String queryOutput = "";
        try {
            while(result.next()){
                String team_name = result.getString("team_name");
                String name = result.getString("name");
                String amplua = result.getString("name_amplua");
                String birthdate = result.getString("birthdate");
                int number = result.getInt("number");
                int games = result.getInt("games");
                int goal = result.getInt("goal");
                int penalty = result.getInt("penalty");
                int assist = result.getInt("assist");
                int yellow = result.getInt("yellow_card");
                int red = result.getInt("red_card");
                String photo = result.getString("photo");
                queryOutput+=team_name + " " + name + " " + amplua + " " + birthdate + " " + number + " " +
                        games + " " + goal + " " + penalty + " " + assist + " " +yellow+ " " + red + " " + photo + "\n";
                squadInfo.add(new Player(team_name,name,amplua,birthdate,number, games, goal, penalty, assist, yellow, red, photo) );
            }
            System.out.println("DataBaseRequest getSquadInfo():output query  from DB:" + queryOutput);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private  void getAllMatches(ResultSet result){
        String queryOutput = "";
        try {
            while(result.next()){
                int id_match = result.getInt("id_match");
                String division = result.getString("name_division");
                int tour = result.getInt("id_tour");
                String t_home = result.getString("team_home");
                int g_home = result.getInt("goal_home");
                int g_guest = result.getInt("goal_guest");
                String t_guest = result.getString("team_guest");
                String l_home = result.getString("logo_home");
                String l_guest = result.getString("logo_guest");
                queryOutput+=id_match + " " + division + " " + tour + " " + t_home + " " + g_home + " " + g_guest + " " +
                        t_guest + " " + l_home + " " + l_guest + "\n";
                prevMatches.add(new PrevMatches(id_match, division, tour, t_home, g_home, g_guest, t_guest, l_home, l_guest));
            }
            System.out.println("DataBaseRequest getAllMatches():output query  from DB: " + queryOutput);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private  void getPlayerInMatch(ResultSet result){
        String queryOutput = "";
        try{
            while(result.next()){
                int id = result.getInt("id_player");
                String name = result.getString("name");
                String team = result.getString("team_name");
                int number = result.getInt("number");
                int goal = result.getInt("count_goals"); 
                int penalty = result.getInt("penalty");
                int assist= result.getInt("count_assist");                
                int yellow = result.getInt("yellow");
                int red = result.getInt("red");
                int penalty_out = result.getInt("penalty_out");
                int own_goal = result.getInt("own_goal");
                queryOutput+=id + " " + name + " " + team + " " + number + " " + goal + " " + assist + " " + penalty + 
                        " " + penalty_out + " " + yellow + " " + red + " " + own_goal + "\n";
                squadInfo.add(new Player(number, team, name, number, goal, penalty, assist, yellow, red, penalty_out,
                        own_goal));
            }
            System.out.println("DataBaseRequest getPlayerInMatch():output query  from DB:" + queryOutput);
        }catch(SQLException ex){
             Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private  void checkUser(ResultSet result, String password){
        String encryptedPassword = null;
        String salt = "";
        try {
            while (result.next()){
                encryptedPassword = result.getString(1);
                salt = result.getString(2);
                settingForApp = result.getInt(3);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        UsersLogin ul = new UsersLogin(salt, password);
        System.out.println("encryptedPassword from db = " + encryptedPassword);
        System.out.println("encryptedPassword from user = " + ul.getHashPassword());
        if (ul.getHashPassword().equals(encryptedPassword)){
            message = "Password successfull";
        }else{
            message = "The password does not match";
        }
    }
    
    public  ArrayList<TournamentTable> getTournamentTable() {
        return tournamentTable;
    }
    
   public  ArrayList<PrevMatches> getPrevMatches(){
       return prevMatches;
   }

    public  ArrayList<NextMatches> getNextMatches() {
        return nextMatches;
    }

    public  ArrayList<Player> getSquadInfo() {
        return squadInfo;
    }

    public  String getMessage() {
        return message;
    }


    
}
