package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by iluz on 21/10/15.
 */
public class ScoresWidgetRemoteViewsService extends RemoteViewsService {
    private static final int LEAGUE_COL_INDEX = 0;
    // not used in the widget, but included for practice in the future, may we need this.
    private static final int MATCH_DAY_COL_INDEX = 1;
    private static final int MATCH_TIME_COL_INDEX = 2;
    private static final int MATCH_ID_COL_INDEX = 3;
    private static final int HOME_TEAM_COL_INDEX = 4;
    private static final int HOME_TEAM_GOALS_COL_INDEX = 5;
    private static final int AWAY_TEAM_COL_INDEX = 6;
    private static final int AWAY_TEAM_GOALS_COL_INDEX = 7;
    private static final String[] PROJECTION = {
            DatabaseContract.scores_table.LEAGUE_COL,
            DatabaseContract.scores_table.MATCH_DAY_COL,
            DatabaseContract.scores_table.MATCH_TIME_COL,
            DatabaseContract.scores_table.MATCH_ID_COL,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
    };
    private static final String LOG_TAG = ScoresWidgetRemoteViewsService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor dataset = null;

            @Override
            public void onCreate() {
                // empty method
            }

            @Override
            public void onDataSetChanged() {
                if (dataset != null) {
                    Log.d(LOG_TAG, "Dataset is null.");
                    dataset.close();
                }

                Uri uri = DatabaseContract.scores_table.buildScoreWithDate();

                String dateFormat = getString(R.string.data_query_date_format);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
                String currentDay = simpleDateFormat.format(new Date());

                String selection = null;
                String[] selectionArgs = {currentDay};
                String sortOrder = null;

                dataset = getContentResolver().query(uri, PROJECTION, selection, selectionArgs, sortOrder);
            }

            @Override
            public void onDestroy() {
                try {
                    dataset.close();
                } finally {
                    dataset = null;
                }
            }

            @Override
            public int getCount() {
                return dataset == null ? 0 : dataset.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (!isValidPosition(position)) {
                    Log.e(LOG_TAG, String.format("%s is not a valid position", position));
                    return null;
                }

                RemoteViews widgetRow = new RemoteViews(getPackageName(), R.layout.widget_scores_list_item);
                Context context = getApplicationContext();

                int leagueId = Integer.valueOf(dataset.getString(LEAGUE_COL_INDEX));
                String leagueName = Utilities.getLeagueName(leagueId, context);
                widgetRow.setTextViewText(R.id.widget_league_name, leagueName);
                widgetRow.setContentDescription(R.id.widget_league_name, getString(R.string.content_description_league_name, leagueName));

                String homeTeamName = dataset.getString(HOME_TEAM_COL_INDEX);
                widgetRow.setTextViewText(R.id.widget_home_team_name, homeTeamName);
                widgetRow.setContentDescription(R.id.widget_home_team_name, getString(R.string.content_description_home_team_name, homeTeamName));

                widgetRow.setImageViewResource(R.id.widget_home_team_crest, Utilities.getTeamCrestByTeamName(homeTeamName));
                widgetRow.setContentDescription(R.id.widget_home_team_crest, getString(R.string.content_description_home_team_crest, homeTeamName));

                String matchTime = dataset.getString(MATCH_TIME_COL_INDEX);
                widgetRow.setTextViewText(R.id.widget_match_time, matchTime);
                widgetRow.setContentDescription(R.id.widget_match_time, getString(R.string.content_description_match_time, matchTime));

                String score = Utilities.getFormattedScore(dataset.getInt(HOME_TEAM_GOALS_COL_INDEX), dataset.getInt(AWAY_TEAM_GOALS_COL_INDEX), context);
                widgetRow.setTextViewText(R.id.widget_score, score);
                widgetRow.setContentDescription(R.id.widget_score, getString(R.string.content_description_match_score, score));

                String awayTeamName = dataset.getString(AWAY_TEAM_COL_INDEX);
                widgetRow.setTextViewText(R.id.widget_away_team_name, awayTeamName);
                widgetRow.setContentDescription(R.id.widget_away_team_name, getString(R.string.content_description_away_team_name, awayTeamName));

                widgetRow.setImageViewResource(R.id.widget_away_team_crest, Utilities.getTeamCrestByTeamName(awayTeamName));
                widgetRow.setContentDescription(R.id.widget_away_team_crest, getString(R.string.content_description_away_team_crest, awayTeamName));


                return widgetRow;
            }

            private boolean isValidPosition(int position) {
                return (position != AdapterView.INVALID_POSITION && dataset != null && dataset.moveToPosition(position));
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_scores_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (dataset.moveToPosition(position))
                    return dataset.getLong(MATCH_ID_COL_INDEX);

                return AdapterView.INVALID_POSITION;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}