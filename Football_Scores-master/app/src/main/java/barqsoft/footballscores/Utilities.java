package barqsoft.footballscores;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilities {
    public static final int SERIE_A = 357;
    public static final int PREMIER_LEGAUE = 354;
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int PRIMERA_DIVISION = 358;
    public static final int BUNDESLIGA = 351;

    public static String getLeague(int leagueId) {
        switch (leagueId) {
            case SERIE_A:
                return "Seria A";
            case PREMIER_LEGAUE:
                return "Premier League";
            case CHAMPIONS_LEAGUE:
                return "UEFA Champions League";
            case PRIMERA_DIVISION:
                return "Primera Division";
            case BUNDESLIGA:
                return "Bundesliga";
            default:
                return "Not known League Please report";
        }
    }

    public static String getMatchDay(int match_day, int league_num) {
        if (league_num == CHAMPIONS_LEAGUE) {
            if (match_day <= 6) {
                return "Group Stages, Match day : 6";
            } else if (match_day == 7 || match_day == 8) {
                return "First Knockout round";
            } else if (match_day == 9 || match_day == 10) {
                return "QuarterFinal";
            } else if (match_day == 11 || match_day == 12) {
                return "SemiFinal";
            } else {
                return "Final";
            }
        } else {
            return "Match day : " + String.valueOf(match_day);
        }
    }

    public static String getScores(int homeGoals, int awayGoals) {
        if (homeGoals < 0 || awayGoals < 0) {
            return " - ";
        } else {
            return String.valueOf(homeGoals) + " - " + String.valueOf(awayGoals);
        }
    }

    public static int getTeamCrestByTeamName(String teamName) {
        if (teamName == null) {
            return R.drawable.no_icon;
        }
        switch (teamName) { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal FC":
                return R.drawable.arsenal;
            case "Aston Villa FC":
                return R.drawable.aston_villa;
            case "Chelsea FC":
                return R.drawable.chelsea;
            case "Crystal Palace FC":
                return R.drawable.crystal_palace_fc;
            case "Everton FC":
                return R.drawable.everton_fc_logo1;
            case "Leicester City FC":
                return R.drawable.leicester_city_fc_hd_logo;
            case "Liverpool FC":
                return R.drawable.liverpool;
            case "Manchester City FC":
                return R.drawable.manchester_city;
            case "Manchester United FC":
                return R.drawable.manchester_united;
            case "Newcastle United FC":
                return R.drawable.newcastle_united;
            case "Queens Park Rangers": // FIXME: 19/10/15
                return R.drawable.queens_park_rangers_hd_logo;
            case "Southampton FC":
                return R.drawable.southampton_fc;
            case "Stoke City FC": // FIXME: 19/10/15
                return R.drawable.stoke_city;
            case "Sunderland AFC":
                return R.drawable.sunderland;
            case "Swansea City": // FIXME: 19/10/15
                return R.drawable.swansea_city_afc;
            case "Tottenham Hotspur FC": // FIXME: 19/10/15
                return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion FC":
                return R.drawable.west_bromwich_albion_hd_logo;
            case "West Ham United FC":
                return R.drawable.west_ham;
            default:
                return R.drawable.no_icon;
        }
    }
}
