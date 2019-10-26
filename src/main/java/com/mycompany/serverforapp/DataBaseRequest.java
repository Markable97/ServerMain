package com.mycompany.serverforapp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.java.com.mycompany.serverforapp.DataBaseSetting;

/**
 *
 * @author Markable
 */
public class DataBaseRequest {
    
    
    private int settingForApp;
    Connection connect;
    DataBaseRequest(DataBaseSetting setting){
       try{
           this.connect = setting.dataSource.getConnection();
       }catch (SQLException ex) {
            System.out.println("Database connection failure: " + ex.getMessage());
       }
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
"       FROM v_tournament_table\n" +
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
"       logo_guest,\n" +
"       played \n" +
"       FROM v_matches \n" +
"       where (to_days(curdate()) - to_days(m_date) ) >= 0 and (to_days(curdate()) - to_days(m_date)) < 8\n" +
"       and id_division = ? and goal_home is not null;";
    private String sqlNextMatches = "SELECT name_division, \n" +
"       id_tour, \n" +
"       team_home, \n" +
"       team_guest, \n" +
"       date_format(m_date,'%d-%m-%y %H:%i') as m_date, \n" +
"       name_stadium, \n" +
"       staff_name,\n" +
"       logo_home,\n" +
"       logo_guest\n" +
"       FROM v_matches m\n" +
"       where curdate() < m_date and id_division = ?"
            + " order by m_date;";
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
"       logo_guest,\n" +
"       played \n" +            
"       FROM v_matches m\n" +
"       where team_home = ? or team_guest = ?\n" +
"       order by id_tour asc;" ;
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
"       salt, \n" +
"       id_type \n" +
"       from users\n" +
"       where email = ?;";
    private String sqlGetTour = "select id_match, id_division, name_division, id_tour, team_home, team_guest, date_format(m_date, '%Y-%m-%d %H:%i') m_date, name_stadium\n" +
"from v_matches \n" +
"where id_season = 4 \n" +
"and goal_home is null and goal_guest is null and id_tour = ? and id_division = ?";
    private String sqlCountStadiums = "select count(1) cnt_stadium\n" +
"from(    \n" +
"select distinct id_stadium from dayofmatch\n" +
"where match_date = ?) t";
    private String sqlNameStadiums = "select distinct name_stadium, id_stadium \n" +
"from v_dayofmatch\n" +
"where match_date = ?";
    private String sqlScheduleTime = "select match_date, match_time, id_stadium, \n" +
"	   id_tour, name_stadium, id_match, name_division, \n" +
"       team_home,team_guest, busy_time \n" +
"from v_dayofmatch \n" +
"where match_date = ?;";
    private String sqlInsertSchedule = " update dayofmatch "
            + " set id_tour = ?, "
                + " id_match = ?,"
                + " busy_time = ? "
            + " where match_time = ? "
            + " and match_date = ? "
            + " and id_stadium = ?;";
    private String sqlGetTourForAddResult = ""
            + " select id_match, id_division, name_division, id_tour, team_home, team_guest, "
            + " date_format(m_date, '%Y-%m-%d %H:%i') m_date, name_stadium, played, goal_home, goal_guest "
            + " from v_matches "
            + " where id_season = 4 "
            + " and id_division = ? "
            + " and m_date between ? and ?";
    private String sqlGetProtocolTeam = "" + 
            "select s.id_player, s.name, s.id_team, s.team_name, tmp.id_match, tmp.in_game,"
            + " tmp.count_goals, tmp.count_assist, tmp.yellow, tmp.red, tmp.penalty, "
            + "tmp.penalty_out, tmp.own_goal " +
            "from\n" +
            "(\n" +
            "select m.id_match, m.in_game, s.id_player, m.count_goals, m.count_assist, m.yellow, m.red, m.penalty, m.penalty_out, m.own_goal \n" +
            "from v_squad s\n" +
            "join players_in_match m on s.id_player = m.id_player\n" +
            "where id_match = ?) tmp\n" +
            "right join v_squad s on tmp.id_player = s.id_player\n" +
            "where s.team_name = ? "
            + " order by name";
    private String sqlSetResultMatch = " "
            + " update matches "
            + " set goal_home = ?, "
            + " goal_guest = ?, "
            + " played = 1 "
            + " where id_match = ?";
    private String sqlInsertPlayerInMatch = ""
            + " insert into players_in_match "
            + " set id_match = ?, "
            + " id_player = ?, "
            + " count_goals = ?, "
            + " count_assist = ?, "
            + " yellow = ?, "
            + " red = ?, "
            + " penalty = ?, "
            + " penalty_out = ?, "
            + " own_goal = ?,"
            + " in_game = 1";
    private String sqlDeletePlayerInMatch = ""
            + " delete from players_in_match "
            + " where id_match = ?;";
    private String sqlUpdateResultMatch = " "
            + " update matches "
            + " set goal_home = ?, "
            + " goal_guest = ? "
            + " where id_match = ?;";
    //------Для подготовки запросов-------------
    private  PreparedStatement preparetStatement;
    private  PreparedStatement prTournamentTable;
    private  PreparedStatement prPrevMatches;
    private  PreparedStatement prNextMatches;
    private  PreparedStatement prSquadInfo;
    private  PreparedStatement prAllMatches;
    private  PreparedStatement prPlayerInMatch;
    private  PreparedStatement prInsertUsers;
    private  PreparedStatement prFindUsers;
    private  PreparedStatement prGetTour;
    private  PreparedStatement prCntStadiums;
    //------------------------------------------
    //------Курсоры или результаты запросов
    private  ResultSet resultSet;
    private  ResultSet rsTournamnetTable;
    private  ResultSet rsPrevMatches;
    private  ResultSet rsNextMathces;
    private  ResultSet rsSquadInfo;
    private  ResultSet rsAllMatches;
    private  ResultSet rsPlayerInMatch;
    private  ResultSet rsFindUsers;
    private  ResultSet rsGetTour;
    private  ResultSet rsCntStadiums;
    //------------------------------------------
    //-----Масимы классов для вытаскивания информации-----
    private  ArrayList<TournamentTable> tournamentTable = new ArrayList<TournamentTable>();
    private  ArrayList<PrevMatches> prevMatches = new ArrayList<>();
    private  ArrayList<NextMatches> nextMatches = new ArrayList<>();
    private  ArrayList<Player> squadInfo = new ArrayList<>();
    //------------------------------------------
    public String updateResults(PrevMatches match, ArrayList<Player> players)throws SQLException {
        boolean f = false;
        try {
            connect.setAutoCommit(false);
            preparetStatement = connect.prepareStatement(sqlUpdateResultMatch);
            preparetStatement = connect.prepareStatement(sqlSetResultMatch);
            preparetStatement.setInt(1, match.getGoalHome());
            preparetStatement.setInt(2, match.getGoalVisit());
            preparetStatement.setInt(3, match.getId_match());
            preparetStatement.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            try {
                preparetStatement.close();
            } catch (SQLException ex) {
                Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Удаление предыдущих игроков
        preparetStatement = connect.prepareStatement(sqlDeletePlayerInMatch);
        preparetStatement.setInt(1, match.getId_match());
        preparetStatement.executeUpdate();
        preparetStatement.close();
        //добавление новых
        try{
            preparetStatement = connect.prepareStatement(sqlInsertPlayerInMatch);
            for(Player p : players){
                preparetStatement.setInt(1, match.getId_match());
                preparetStatement.setInt(2, p.getIdPlayer());
                preparetStatement.setInt(3, p.getGoal());
                preparetStatement.setInt(4, p.getAssist());
                preparetStatement.setInt(5, p.getYellowCard());
                preparetStatement.setInt(6, p.getRedCard());
                preparetStatement.setInt(7, p.getPenalty());
                preparetStatement.setInt(8, p.getPenalty_out());
                preparetStatement.setInt(9, p.getOwn_goal());
                preparetStatement.execute();
            }
            connect.commit();
            f = true;
        }catch (SQLException ex){
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            preparetStatement.close();
        }

        if(f){
            return "SUCCESS";
        }else{
            return "bad DB";
        }
    }
    
    public String setResults(PrevMatches match, ArrayList<Player> players){
        boolean f1, f2 = false;
        try {
            connect.setAutoCommit(false);
            preparetStatement = connect.prepareStatement(sqlSetResultMatch);
            preparetStatement.setInt(1, match.getGoalHome());
            preparetStatement.setInt(2, match.getGoalVisit());
            preparetStatement.setInt(3, match.getId_match());
            preparetStatement.executeUpdate();
            f1 = true;
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
            f1 = false;
        } finally{
            try {
                preparetStatement.close();
            } catch (SQLException ex) {
                Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            preparetStatement = connect.prepareStatement(sqlInsertPlayerInMatch);
            for(Player p : players){
                preparetStatement.setInt(1, match.getId_match());
                preparetStatement.setInt(2, p.getIdPlayer());
                preparetStatement.setInt(3, p.getGoal());
                preparetStatement.setInt(4, p.getAssist());
                preparetStatement.setInt(5, p.getYellowCard());
                preparetStatement.setInt(6, p.getRedCard());
                preparetStatement.setInt(7, p.getPenalty());
                preparetStatement.setInt(8, p.getPenalty_out());
                preparetStatement.setInt(9, p.getOwn_goal());
                preparetStatement.execute();
            }
            f2 = true;
            try {
                connect.commit();
            } catch (SQLException ex) {
                Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
            f2 = false;
            try {
                connect.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }finally{
            try {
                preparetStatement.close();
            } catch (SQLException ex) {
                Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(f1 && f2){
            return "SUCCESS";
        }else{
            return "bad DB";
        }
    }
    
    public void setSchedule(ArrayList<Schedule> schedule) throws SQLException{
        int cnt = 0;
        connect.setAutoCommit(false);
        try {
            preparetStatement = connect.prepareStatement(sqlInsertSchedule);
            for(Schedule s : schedule){
                preparetStatement.setInt(1, s.getId_tour());
                preparetStatement.setInt(2, s.getId_match());
                preparetStatement.setInt(3, s.getBusy_time());
                preparetStatement.setString(4, s.getMatch_time());
                preparetStatement.setString(5, s.getMatch_date());
                preparetStatement.setInt(6, s.getId_stadium());
                preparetStatement.executeUpdate();
                System.out.println("Расписание добавдено: " + s.toString());
                cnt++;
            }
             connect.commit();        
        } catch (SQLException ex) {          
             connect.rollback();
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            System.out.println("Все матчей добавлено: " + cnt);
            preparetStatement.close();
        }
        if(cnt == schedule.size()){
            message = "SUCCESS";
        }else{
            message = "bad";
        }
        
    }
    
    ArrayList<Schedule> getSchedule(String date) throws SQLException{
        ArrayList<Schedule> list = new ArrayList<>();
        try {
            preparetStatement = connect.prepareStatement(sqlScheduleTime);
            preparetStatement.setString(1, date);
            resultSet = preparetStatement.executeQuery();
            while(resultSet.next()){
                String match_date = resultSet.getString(1);
                String match_time = resultSet.getString(2);
                int id_stadium = resultSet.getInt(3);
                int id_tour = resultSet.getInt(4);
                String name_stadium = resultSet.getString(5);
                int id_match = resultSet.getInt(6);;
                String name_division = resultSet.getString(7);
                String team_home = resultSet.getString(8);
                String team_guest = resultSet.getString(9);
                int busy_time = resultSet.getInt(10);
                list.add(new Schedule(match_date, match_time, id_stadium, id_tour, 
                        name_stadium, id_match, name_division,team_home, team_guest, busy_time));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            resultSet.close();
            preparetStatement.close();
        }
        System.out.println("Scheduke from DB: \n" + list.toString());
        return list;
    }
    
    ArrayList<Stadiums> getNameStadium(String date) throws SQLException{
        ArrayList<Stadiums> stadiums = new ArrayList<>();
        try {
            preparetStatement = connect.prepareStatement(sqlNameStadiums);
            preparetStatement.setString(1, date);
            resultSet = preparetStatement.executeQuery();
            while(resultSet.next()){
                String name = resultSet.getString(1);
                int id = resultSet.getInt(2);
                stadiums.add(new Stadiums(id, name));
            }
            System.out.println("Stadium list from DB: \n" + stadiums.toString());
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            resultSet.close();
            preparetStatement.close();
        }
        return stadiums;
    }
    
    int getCntStadium(String date) throws SQLException{
        try {
            prCntStadiums = connect.prepareStatement(sqlCountStadiums);
            prCntStadiums.setString(1, date);
            rsCntStadiums = prCntStadiums.executeQuery();
            int cnt = 0;
            while(rsCntStadiums.next()){
                cnt = rsCntStadiums.getInt(1);
            }
            return cnt;
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            rsCntStadiums.close();
            prCntStadiums.close();
        }
        return 0;
    }
    
    void connection_login(String email, String passwordUser) throws SQLException{
        try {
            settingForApp = 0;
            prFindUsers = connect.prepareStatement(sqlFindUsers);
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
            prInsertUsers = connect.prepareStatement(sqlInsertUsers);
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
    
    void connection_main_activity(int id_div) throws SQLException, IOException{
        try {
            tournamentTable.clear();
            prevMatches.clear();
            nextMatches.clear();
            //Вытаскиваем турнирную таблицу
            prTournamentTable = connect.prepareStatement(sqlTournamentTable);
            prTournamentTable.setInt(1, id_div);
            rsTournamnetTable = prTournamentTable.executeQuery();
            getTournamentTable(rsTournamnetTable);
            //Сыгранные матчи
            prPrevMatches = connect.prepareStatement(sqlPrevMatches);
            prPrevMatches.setInt(1, id_div);
            rsPrevMatches = prPrevMatches.executeQuery();
            //Будущие матчи
            getPrevMatches(rsPrevMatches);
            prNextMatches = connect.prepareStatement(sqlNextMatches);
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
            prSquadInfo = connect.prepareStatement(sqlSquadInfo);
            prSquadInfo.setString(1, name_team);
            rsSquadInfo = prSquadInfo.executeQuery();
            getSquadInfo(rsSquadInfo);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            rsSquadInfo.close();
        }
    }
    
    void connection_allMatches(String name_team) throws SQLException, IOException{
        prevMatches.clear();
        try {
            prAllMatches = connect.prepareStatement(sqlAllMatches);
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
            prPlayerInMatch = connect.prepareStatement(sqlPlayersInMatch);
            prPlayerInMatch.setInt(1,id_match);
            rsPlayerInMatch = prPlayerInMatch.executeQuery();
            getPlayerInMatch(rsPlayerInMatch);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            rsPlayerInMatch.close();
        }
    }
    
    void connectiom_playersProtocol(String teamName, int idMatch){
         String queryOutput = "";
        try {
            preparetStatement = connect.prepareStatement(sqlGetProtocolTeam);
            preparetStatement.setInt(1, idMatch);
            preparetStatement.setString(2, teamName);
            resultSet = preparetStatement.executeQuery();
            squadInfo.clear();
            while(resultSet.next()){
                int idPlayer = resultSet.getInt("id_player");
                String team = resultSet.getString("team_name");
                String name = resultSet.getString("name");
                int inGame = resultSet.getInt("in_game");
                int goal = resultSet.getInt("count_goals");
                int assist = resultSet.getInt("count_assist");
                int yellow = resultSet.getInt("yellow");
                int red = resultSet.getInt("red");
                int penalty = resultSet.getInt("penalty");
                int penaltyOut = resultSet.getInt("penalty_out");
                int ownGoal = resultSet.getInt("own_goal");
                queryOutput += idPlayer + " " + team + " " + team + " "; 
                Player player = new Player(idPlayer, team, name, inGame, goal, assist, yellow, red);
                player.setPenalty(penalty);
                player.setPenalty_out(penaltyOut);
                player.setOwn_goal(ownGoal);
                squadInfo.add(player);
            }
            System.out.println("From DB " + squadInfo.toString());
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void connection_getTour(int id_division, int id_tour) throws SQLException{
        try{
            prGetTour = connect.prepareStatement(sqlGetTour);
            prGetTour.setInt(1, id_tour);
            prGetTour.setInt(2, id_division);
            rsGetTour = prGetTour.executeQuery();
            getTour(rsGetTour, 0);
            
        }catch (SQLException ex){
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            rsGetTour.close();
            prGetTour.close();
        }
    }
    
    void connection_getTourAddResults(int id_division, String m_date) throws SQLException{
        String date1 = m_date + " 00:00";
        String date2 = m_date + " 23:59";
        try{
            preparetStatement = connect.prepareStatement(sqlGetTourForAddResult);
            preparetStatement.setInt(1, id_division);
            preparetStatement.setString(2, date1);
            preparetStatement.setString(3, date2);
            resultSet = preparetStatement.executeQuery();
            getTour(resultSet, 1);
        }catch(SQLException ex){
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            resultSet.close();
            preparetStatement.close();
        }
    }
    
     private  void getTournamentTable(ResultSet result) throws IOException{
        tournamentTable.clear();
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
                String imageBase64 = getBase64Image(logo, nameDivision);
                queryOutput += nameDivision + " " + teamName + " " + games  + " " + wins + " "  + draws + " "
                        + losses + " " + goals_scored + " " + goals_conceded + " "
                        + sc_con + " " + points + " " + logo + "\n";
                TournamentTable table = new TournamentTable(nameDivision, teamName, games,  points, wins, draws, losses, 
                        goals_scored, goals_conceded/*, sc_con*/,  logo);
                table.setImageBase64(imageBase64);
                tournamentTable.add(table);
            }
            System.out.println("DataBaseRequest getTournamentTable(): output query from DB: \n" + queryOutput);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private  void getPrevMatches(ResultSet result) throws IOException{
        String queryOutput = "";
        prevMatches.clear();
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
                String logoHomeBase64 = getBase64Image(logoHome, nameDivision);
                String logoGuestBase64 = getBase64Image(logoGuest, nameDivision);
                int played = result.getInt("played");
                queryOutput +=id_match + " " + nameDivision + " " + tour + " " + teamHome + " " + goalHome + " " +
                        goalGuest + " " + teamGuest + " " + mDate + " " + stadium + " " + logoHome + " " + logoGuest + "\n";
                PrevMatches matches = new PrevMatches(id_match, nameDivision, tour, teamHome, goalHome, goalGuest, teamGuest, logoHome, logoGuest);
                matches.setImages(logoHomeBase64, logoGuestBase64);
                matches.played = played;
                prevMatches.add(matches);
            }
            System.out.println("DataBaseRequest getPrevMatches(): output query  from DB:" + queryOutput);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private  void getNextMatches(ResultSet result) throws IOException{
        String queryOutput = "";
        nextMatches.clear();
        try {
            while(result.next()){
                String nameDivision = result.getString("name_division");
                int tour = result.getInt("id_tour");
                String t_home = result.getString("team_home");
                String t_guest = result.getString("team_guest");
                String m_date = result.getString("m_date");
                String stadium = result.getString("name_stadium");
                String logoHome = result.getString("logo_home");
                String logoGuest = result.getString("logo_guest");
                String logoHomeBase64 = getBase64Image(logoHome, nameDivision);
                String logoGuestBase64 = getBase64Image(logoGuest, nameDivision);
                queryOutput += nameDivision + " " + tour + " " +t_home +  " " + t_guest + " " 
                            + m_date + " " + stadium + "\n";
                NextMatches matches = new NextMatches(nameDivision, tour, t_home, t_guest, m_date, stadium);
                matches.setImages(logoHomeBase64, logoGuestBase64);
                nextMatches.add(matches);
            }
            System.out.println("DataBaseRequest getNextMatchrs():output query  from DB:" + queryOutput);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private  void getSquadInfo(ResultSet result){
        String queryOutput = "";
        squadInfo.clear();
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
    
    private  void getAllMatches(ResultSet result) throws IOException{
        String queryOutput = "";
        prevMatches.clear();
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
                String logoHomeBase64 = getBase64Image(l_home, division);
                String logoGuestBase64 = getBase64Image(l_guest, division);
                int played = result.getInt("played");
                queryOutput+=id_match + " " + division + " " + tour + " " + t_home + " " + g_home + " " + g_guest + " " +
                        t_guest + " " + l_home + " " + l_guest + "\n";
                PrevMatches match = new PrevMatches(id_match, division, tour, t_home, g_home, g_guest, t_guest, l_home, l_guest);
                match.setImages(logoHomeBase64, logoGuestBase64);
                match.played = played;
                prevMatches.add(match);
            }
            System.out.println("DataBaseRequest getAllMatches():output query  from DB: " + queryOutput);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private  void getPlayerInMatch(ResultSet result){
        String queryOutput = "";
        squadInfo.clear();
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
    
    private void getTour(ResultSet result, int option){
        String queryOutput = "";
        nextMatches.clear();
        try {
            while(result.next()){
                int id_match = result.getInt("id_match");
                int id_division = result.getInt("id_division");
                int id_tour = result.getInt("id_tour");
                String name_division = result.getString("name_division");
                String team_home = result.getString("team_home");
                String team_guest = result.getString("team_guest");
                String m_date = result.getString("m_date");
                String name_stadium = result.getString("name_stadium");
                if(option == 1){ //если 1 то добавляем еще один параметр
                 int played = result.getInt("played");   
                 int goal_home = result.getInt("goal_home");
                 int goal_guest = result.getInt("goal_guest");
                 queryOutput += id_match + " "  + id_division + " "+ id_tour + " " + team_home + " " + team_guest + 
                        " " + m_date + " " + name_stadium + " " + played + "\n";
                    nextMatches.add(new NextMatches(id_match, id_division, name_division, id_tour,
                            team_home, team_guest, m_date, name_stadium, played, goal_home, goal_guest));
                }else{
                    queryOutput += id_match + " "  + id_division + " "+ id_tour + " " + team_home + " " + team_guest + 
                        " " + m_date + " " + name_stadium /*+ " " + goal*/ + "\n";
                    nextMatches.add(new NextMatches(id_match, id_division, name_division, id_tour, team_home, team_guest, m_date, name_stadium));
                }
            }
            System.out.println("Список матчей тура: \n" + queryOutput);
        } catch (SQLException ex) {
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
    
    public void closeConnection(){
        try {
            connect.close();
            System.out.println("Close connection DataBase");
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseRequest.class.getName()).log(Level.SEVERE, null, ex);
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

    public int getSettingForApp() {
        return settingForApp;
    }

    String getBase64Image(String logo, String divivsion) throws FileNotFoundException, IOException{
        String pathBig = "D:\\Pictures\\"+divivsion+"\\"; 
        //String pathBig = "/home/mark/Shares/Pictures/+divivsion+/";
        File image = new File(pathBig + logo); 
        if(image.exists()){
            System.out.println("Файлы существует " + image.getName());
                    //String nameImage = tournamentArray.get(i).getUrlImage().replace(".png",""); 
                    //out.writeUTF(nameImage);
                    byte[] byteArrayBig = new byte[(int)image.length()];
                    BufferedInputStream streamBig = new BufferedInputStream(new FileInputStream(image));
                    streamBig.read(byteArrayBig, 0, byteArrayBig.length);
                    streamBig.close();
                    //out.writeInt(byteArrayBig.length);
                    return Base64.getEncoder().encodeToString(byteArrayBig);
        }else{
            System.out.println("Файл "+logo+" не сущуствует!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            return "";
        }
        
    }
}
