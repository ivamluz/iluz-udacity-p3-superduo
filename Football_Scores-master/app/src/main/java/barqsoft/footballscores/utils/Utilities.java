package barqsoft.footballscores.utils;

import android.content.Context;

import barqsoft.footballscores.R;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilities {
    private static final int SERIE_A = 357;
    private static final int PREMIER_LEGAUE = 354;
    private static final int CHAMPIONS_LEAGUE = 405;
    private static final int PRIMERA_DIVISION = 358;
    private static final int BUNDESLIGA = 351;

    public static String getLeagueName(int leagueId, Context context) {
        validateContext(context);

        switch (leagueId) {
            case SERIE_A:
                return context.getString(R.string.league_name_serie_a);
            case PREMIER_LEGAUE:
                return context.getString(R.string.league_name_premier_league);
            case CHAMPIONS_LEAGUE:
                return context.getString(R.string.league_name_champions_league);
            case PRIMERA_DIVISION:
                return context.getString(R.string.league_name_primera_division);
            case BUNDESLIGA:
                return context.getString(R.string.league_name_bundesliga);
            default:
                return context.getString(R.string.league_name_unknown);
        }
    }

    public static String getMatchPhase(int matchDay, int leagueId, Context context) {
        validateContext(context);

        if (leagueId != CHAMPIONS_LEAGUE) {
            return context.getString(R.string.match_phase_general_group_stages, matchDay);
        }

        if (matchDay <= 6) {
            return context.getString(R.string.match_phase_champions_league_group_stages, matchDay);
        } else if (matchDay == 7 || matchDay == 8) {
            return context.getString(R.string.match_phase_first_knockout_round);
        } else if (matchDay == 9 || matchDay == 10) {
            return context.getString(R.string.match_phase_quarter_finals);
        } else if (matchDay == 11 || matchDay == 12) {
            return context.getString(R.string.match_phase_semi_finals);
        } else {
            return context.getString(R.string.match_phase_final);
        }
    }

    private static void validateContext(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context should not be null!");
        }
    }

    public static String getFormattedScore(int homeGoals, int awayGoals, Context context) {
        validateContext(context);

        if (isValidScore(homeGoals, awayGoals)) {
            return context.getString(R.string.match_score, homeGoals, awayGoals);
        } else {
            return context.getString(R.string.match_score, "", "");
        }
    }

    private static boolean isValidScore(int homeGoals, int awayGoals) {
        return (homeGoals >= 0 && awayGoals >= 0);
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
