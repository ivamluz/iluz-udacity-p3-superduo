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
import barqsoft.footballscores.data.DatabaseContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainScreenFragment.class.getSimpleName();

    private static final int SCORES_LOADER = 0;
    private final String[] mFragmentDate = new String[1];
    private ScoresAdapter mScoresAdapter;

    public MainScreenFragment() {
    }

    private void updateScores() {
        Intent serviceStartIntent = new Intent(getActivity(), FootballDataService.class);
        getActivity().startService(serviceStartIntent);
    }

    public void setFragmentDate(String date) {
        mFragmentDate[0] = date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        updateScores();
        getLoaderManager().initLoader(SCORES_LOADER, null, this);

        mScoresAdapter = new ScoresAdapter(getActivity(), null, 0);
        mScoresAdapter.DETAIL_MATCH_ID = MainActivity.selectedMatchId;

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final ListView scoresList = (ListView) rootView.findViewById(R.id.scores_list);
        scoresList.setAdapter(mScoresAdapter);

        scoresList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder selected = (ViewHolder) view.getTag();
                mScoresAdapter.DETAIL_MATCH_ID = selected.matchId;
                MainActivity.selectedMatchId = (int) selected.matchId;
                mScoresAdapter.notifyDataSetChanged();
            }
        });

        // http://stackoverflow.com/a/9297055
        View empty = rootView.findViewById(R.id.empty_list_item);
        scoresList.setEmptyView(empty);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, mFragmentDate, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            cursor.moveToNext();
        }

        mScoresAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mScoresAdapter.swapCursor(null);
    }


}
