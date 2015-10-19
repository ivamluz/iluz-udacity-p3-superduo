package barqsoft.footballscores;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import barqsoft.footballscores.service.FootballDataService;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ScoresAdapter mScoresAdapter;
    private static final int SCORES_LOADER = 0;
    private final String[] FRAGMENT_DATE = new String[1];

    public MainScreenFragment() {
    }

    private void update_scores() {
        Intent service_start = new Intent(getActivity(), FootballDataService.class);
        getActivity().startService(service_start);
    }

    public void setFRAGMENT_DATE(String date) {
        FRAGMENT_DATE[0] = date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        update_scores();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final ListView score_list = (ListView) rootView.findViewById(R.id.scores_list);
        mScoresAdapter = new ScoresAdapter(getActivity(), null, 0);
        score_list.setAdapter(mScoresAdapter);
        getLoaderManager().initLoader(SCORES_LOADER, null, this);
        mScoresAdapter.DETAIL_MATCH_ID = MainActivity.selectedMatchId;
        score_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder selected = (ViewHolder) view.getTag();
                mScoresAdapter.DETAIL_MATCH_ID = selected.matchId;
                MainActivity.selectedMatchId = (int) selected.matchId;
                mScoresAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, FRAGMENT_DATE, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        //Log.v(FetchScoreTask.LOG_TAG,"loader finished");
        //cursor.moveToFirst();
        /*
        while (!cursor.isAfterLast())
        {
            Log.v(FetchScoreTask.LOG_TAG,cursor.getString(1));
            cursor.moveToNext();
        }
        */

        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            i++;
            cursor.moveToNext();
        }
        //Log.v(FetchScoreTask.LOG_TAG,"Loader query: " + String.valueOf(i));
        mScoresAdapter.swapCursor(cursor);
        //mScoresAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mScoresAdapter.swapCursor(null);
    }


}
