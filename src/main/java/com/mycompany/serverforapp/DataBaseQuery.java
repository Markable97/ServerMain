package com.mycompany.serverforapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Markable
 */
public class DataBaseQuery {
    
    private static int queryDivision;
    private static String queryTeam;
    private static String queryOutput="";
    
    private static String user = "root";
    private static String password = "7913194";
    private static String url = "jdbc:mysql://localhost:3306/main_football";
    
    private static Connection connect;
    private static Statement statment;
    
    private static PreparedStatement prepStateTable;
    private static PreparedStatement prepStateResultsPrevMatches;
    private static PreparedStatement prepStateCalendarNextMatches;
    private static PreparedStatement prepStatePlayerInfo;
    private static PreparedStatement prepStateAllMatchesTeam;
    
    private static ResultSet resultTournamnetTable; 
    private static ResultSet resultsPrevMatches; 
    private static ResultSet calendarNextMatches;
    private static ResultSet resultPlayerInfo;
    private static ResultSet resultAllMatches;
    
    private static ArrayList<TournamentTable> tournamentArray = new ArrayList<TournamentTable>();
    private static ArrayList<PrevMatches> prevMatchesArray = new ArrayList<>();
    private static ArrayList<NextMatches> nextMatchesArray = new ArrayList<>();
    private static ArrayList<Player> playerArray = new ArrayList<>();
    private static ArrayList<PrevMatches> allMatchesTeamArray = new ArrayList<>();
    //private static String outputQuery = "";

   
    public DataBaseQuery(int dataDivision, String dataTeam){
        queryDivision = dataDivision;
        queryTeam = dataTeam;
        tournamentArray.clear();
        prevMatchesArray.clear();
        nextMatchesArray.clear();
        playerArray.clear();
        allMatchesTeamArray.clear();
            //queryOutput="";
            connection(queryDivision, queryTeam);
        }
    
    
   ArrayList<TournamentTable> getTournamentTable(){
       return DataBaseQuery.tournamentArray;
   }
   
   ArrayList<PrevMatches> getResultsPrevMatches(){
       return DataBaseQuery.prevMatchesArray;
   }
   
   ArrayList<NextMatches> getCalendar(){
       return DataBaseQuery.nextMatchesArray;
   }
   
    ArrayList<Player> getPlayerArray(){
        return DataBaseQuery.playerArray;
    }
    ArrayList<PrevMatches> getAllMatches(){
        return DataBaseQuery.allMatchesTeamArray;
    }
    String getQueryOutput(){
        return queryOutput;
    }
    
   
    
    private static void connection(int qDiv, String qTeam){
        try {
            connect = DriverManager.getConnection(url, user, password);
            statment = connect.createStatement();
            //results = statment.executeQuery(query);
            prepStateTable = connect.prepareStatement("SELECT  name_division, team_name, games,point,wins,draws,losses,goals_scored,goals_conceded,logo\n" +
                    "FROM tournament_table j\n" +
                    "join divisions d on id_division = j.divisions_id_division\n" +
                    "join teams t on id_team = teams_id_team\n" +
                    "where j.divisions_id_division = ?\n" +
                    "order by point desc;");
            prepStateTable.setInt(1, qDiv);
            resultTournamnetTable = prepStateTable.executeQuery();
            //Teams team;
            tournamentTableByDivision(resultTournamnetTable);//
            
            prepStateResultsPrevMatches = connect.prepareStatement("SELECT name_division,id_tour,h.team_name,goal_home,goal_visitor, g.team_name, date\n" +
                    "FROM matches m\n" +
                    "join teams h on teams_id_teamHome = h.id_team\n" +
                    "join teams g on teams_id_teamVisitor = g.id_team\n" +
                    "join divisions d on m.divisions_id_division = id_division\n" +
                    "where  (to_days(curdate()) - to_days(date) ) >= 0 and (to_days(curdate()) - to_days(date)) < 8 and m.divisions_id_division = ?\n" +
                    "order by date;");
            prepStateResultsPrevMatches.setInt(1, qDiv);
            resultsPrevMatches = prepStateResultsPrevMatches.executeQuery();
            resultsPrevMatchesByDivision(resultsPrevMatches);
            
            prepStateCalendarNextMatches = connect.prepareStatement("SELECT  name_division, id_tour,h.team_name, g.team_name, DATE_FORMAT((date), \n" +
                    "CONCAT(' %d, ', ELT( MONTH((date)), '���.','���.','���.','���.','���.','���.','���.','���.','���.','���.','���.','���.'),' %H:%i')), name_stadium\n" +
                    "FROM matches m\n" +
                    "join teams h on teams_id_teamHome = h.id_team\n" +
                    "join teams g on teams_id_teamVisitor = g.id_team\n" +
                    "join stadiums s on stadiums_id_stadium = s.id_stadium\n" +
                    "join divisions d on m.divisions_id_division = id_division\n" +
                    "where date >= curdate() and m.divisions_id_division = ?\n" +
                    "order by date;");
            prepStateCalendarNextMatches.setInt(1,qDiv);     
            calendarNextMatches = prepStateCalendarNextMatches.executeQuery();
            calendarResults(calendarNextMatches);
            
            prepStatePlayerInfo = connect.prepareStatement("SELECT id_player, team_name, name, name_amplua, birthdate, number, games, goal, assist, yellow_card, red_card, photo \n" +
                    "FROM players p\n" +
                    "join amplua a on id_amplua = p.amplua_id_amplua\n" +
                    "join teams t on id_team = p.teams_id_team\n" +
                    "join players_statistics ps on ps.players_id_player = p.id_player\n" +
                    "where team_name like ? order by name_amplua;");
            prepStatePlayerInfo.setString(1, qTeam);
            resultPlayerInfo = prepStatePlayerInfo.executeQuery();
            playerInfo(resultPlayerInfo);
            
            prepStateAllMatchesTeam = connect.prepareStatement("SELECT name_division,id_tour,h.team_name,goal_home,"
                    + "goal_visitor, g.team_name, h.logo, g.logo\n" +
                    "FROM matches m \n" +
                    "join teams h on teams_id_teamHome = h.id_team \n" +
                    "join teams g on teams_id_teamVisitor = g.id_team\n" +
                    "join divisions d on m.divisions_id_division = id_division\n" +
                    "where (h.team_name like ? or g.team_name like ?) and m.goal_home is not null\n" +
                    "order by id_tour desc;");
            prepStateAllMatchesTeam.setString(1, qTeam);
            prepStateAllMatchesTeam.setString(2, qTeam);
            resultAllMatches = prepStateAllMatchesTeam.executeQuery();
            allMatchesForTeam(resultAllMatches);
            /*while(results.next()){
                int id = results.getInt("id_team");
                String team_name = results.getString("team_name");
                String date = results.getString("creation_date");
                int id_division = results.getInt("divisions_id_division");
                teamsAllArray.add(new Teams(id, team_name, date, id_division));
                //int number = result.getInt(6);
               // team = new Teams();
                queryOutput += id + " " + team_name + " " + date + " " + id_division +"\n";
            }
            System.out.println("[1]����� ������� �� ������ �� \n" + queryOutput);*/
            //System.out.println("[2]����� ������� �� ������ �� \n" + outputQuery);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
                connect.close();
            } catch (SQLException ex) {
                Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                statment.close();
            } catch (SQLException ex) {
                Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                resultTournamnetTable.close();
                //results.close();
            } catch (SQLException ex) {
                Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    //������� ���������� ������� ��������� �������
    private static void tournamentTableByDivision(ResultSet result){
        String queryOutput = "";
        
        try {
            while(result.next()){
                String nameDivision = result.getString("name_division");
                String teamName = result.getString("team_name");
                int games = result.getInt("games");
                int point = result.getInt("point");
                int wins = result.getInt("wins");
                int draws = result.getInt("draws");
                int losses = result.getInt("losses");
                int goals_scored = result.getInt("goals_scored");
                int goals_conceded = result.getInt("goals_conceded");
                String logo = result.getString("logo");
                tournamentArray.add(new TournamentTable(nameDivision, teamName, games, point, wins, draws, losses, goals_scored, goals_conceded, logo));
                queryOutput += nameDivision + " " + teamName + " " + games + " " + point + " " + wins + " "  + draws + " "
                        + losses + " "+ logo + "\n";
            }
           System.out.println("[1]����� ������� �� ������ �� \n" + queryOutput);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private static void resultsPrevMatchesByDivision(ResultSet result) {
       String queryOutput = "";
        try {
            while(result.next()){
                String nameDivision = result.getString("name_division");
                int idTour = result.getInt("id_tour");
                String teamHome = result.getString("h.team_name");
                int goalHome = result.getInt("goal_home");
                int goalVisit = result.getInt("goal_visitor");
                String teamVisit = result.getString("g.team_name");
                prevMatchesArray.add(new PrevMatches(nameDivision, idTour, teamHome, goalHome, goalVisit, teamVisit));
                queryOutput += nameDivision + " " + idTour + " " +teamHome + " " + goalHome 
                        + " " + goalVisit + " " + teamVisit + "\n";
            }
            //DataBaseQuery.queryOutput = queryOutput;
            System.out.println("[2]����� ������� �� ������ �� \n" + queryOutput);
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    private static void calendarResults(ResultSet result) {
        
            String queryOutput = "";
            try{
                while(result.next()){
                    String nameDivision = result.getString("name_division");
                    int idTour = result.getInt("id_tour");
                    String teamHome = result.getString("h.team_name");
                    String teamVisit = result.getString("g.team_name");
                    String date = result.getString(5);
                    String nameStadium = result.getString("name_stadium");
                    nextMatchesArray.add(new NextMatches(nameDivision, idTour, teamHome, teamVisit, date, nameStadium));
                    queryOutput += nameDivision + " " + idTour + " " +teamHome +  " " + teamVisit + " " 
                            + date + " " + nameStadium + "\n";
                }
                System.out.println("[3]����� ������� �� ������ �� \n" + queryOutput);
            }catch(SQLException ex){
                Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
    private static void playerInfo(ResultSet result){
        String query = "";
        try {
            while(result.next()){
                int idPlayer = result.getInt("id_player");
                String playerTeam = result.getString("team_name");
                String playerName = result.getString("name");
                String amplua = result.getString("name_amplua");
                String birhtday = result.getString("birthdate");
                int number = result.getInt("number");
                int games = result.getInt("games");
                int goal = result.getInt("goal");
                int assist = result.getInt("assist");
                int yellowCard = result.getInt("yellow_card");
                int redCard = result.getInt("red_card");
                String playerUrlImage = result.getString("photo");
                playerArray.add(new Player(idPlayer, playerTeam, playerName, amplua, birhtday, number, games, 
                        goal, assist, yellowCard, redCard, playerUrlImage));
            }
            System.out.println("[4]����� ������� �� ������ �� \n" + playerArray.toString());
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void allMatchesForTeam(ResultSet result){
        try {
            while(result.next()){
                String nameDivision = result.getString("name_division");
                int idTour = result.getInt("id_tour");
                String teamHome = result.getString("h.team_name");
                int goalHome = result.getInt("goal_home");
                int goalVisit = result.getInt("goal_visitor");
                String teamVisit = result.getString("g.team_name");
                String urlImageH = result.getString("h.logo");
                String urlImageG = result.getString("g.logo");
                allMatchesTeamArray.add(new PrevMatches(nameDivision, idTour, teamHome, goalHome, goalVisit, teamVisit, urlImageH, urlImageG) );
            }
            System.out.println("[5]����� ������� �� ������ �� \n" + allMatchesTeamArray.toString());
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
