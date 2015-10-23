package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorAdapter {
    private static final int COL_HOME = 3;
    private static final int COL_AWAY = 4;
    private static final int COL_HOME_GOALS = 6;
    private static final int COL_AWAY_GOALS = 7;
    private static final int COL_DATE = 1;
    private static final int COL_LEAGUE = 5;
    private static final int COL_MATCH_DAY = 9;
    private static final int COL_ID = 8;
    private static final int COL_MATCH_TIME = 2;
    public double DETAIL_MATCH_ID = 0;

    public ScoresAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);

        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder mHolder = (ViewHolder) view.getTag();

        String homeTeamName = cursor.getString(COL_HOME);
        mHolder.homeTeamName.setText(homeTeamName);
        mHolder.homeTeamName.setContentDescription(context.getString(R.string.content_description_home_team_name, homeTeamName));

        String awayTeamName = cursor.getString(COL_AWAY);
        mHolder.awayTeamName.setText(awayTeamName);
        mHolder.awayTeamName.setContentDescription(context.getString(R.string.content_description_away_team_name, awayTeamName));

        String matchTime = cursor.getString(COL_MATCH_TIME);
        mHolder.date.setText(matchTime);
        mHolder.date.setContentDescription(context.getString(R.string.content_description_match_time, matchTime));

        String formattedScore = Utilities.getFormattedScore(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS), context);
        mHolder.score.setText(formattedScore);
        mHolder.score.setContentDescription(context.getString(R.string.content_description_match_score, formattedScore));

        mHolder.homeTeamCrest.setImageResource(Utilities.getTeamCrestByTeamName(homeTeamName));
        mHolder.homeTeamCrest.setContentDescription(context.getString(R.string.content_description_home_team_crest, homeTeamName));

        mHolder.awayTeamCrest.setImageResource(Utilities.getTeamCrestByTeamName(awayTeamName));
        mHolder.awayTeamCrest.setContentDescription(context.getString(R.string.content_description_away_team_crest, awayTeamName));

        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View detailView = layoutInflater.inflate(R.layout.detail_fragment, null);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);

        mHolder.matchId = cursor.getDouble(COL_ID);
        if (mHolder.matchId == DETAIL_MATCH_ID) {
            container.addView(detailView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));

            String formattedMatchDay = Utilities.getMatchPhase(cursor.getInt(COL_MATCH_DAY),
                    cursor.getInt(COL_LEAGUE), context);
            TextView matchDayTextView = (TextView) detailView.findViewById(R.id.matchday_textview);
            matchDayTextView.setText(formattedMatchDay);
            matchDayTextView.setContentDescription(formattedMatchDay);

            String leagueName = Utilities.getLeagueName(cursor.getInt(COL_LEAGUE), context);
            TextView league = (TextView) detailView.findViewById(R.id.league_textview);
            league.setText(leagueName);
            league.setContentDescription(context.getString(R.string.content_description_league_name, leagueName));

            Button shareButton = (Button) detailView.findViewById(R.id.share_button);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String shareText = context.getString(R.string.share_text, mHolder.homeTeamName.getText(), mHolder.score.getText(), mHolder.awayTeamName.getText());
                    context.startActivity(createShareIntent(shareText));
                }
            });
        } else {
            container.removeAllViews();
        }
    }

    @SuppressWarnings("deprecation")
    public Intent createShareIntent(String shareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        return shareIntent;
    }
}
