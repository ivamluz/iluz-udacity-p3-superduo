package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import it.jaschke.alexandria.api.BookListAdapter;
import it.jaschke.alexandria.api.Callback;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.utils.SoftKeyboardUtils;


public class ListOfBooks extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String BUNDLE_KEY_SEARCH_TERM = "books_list_search_term";
    private final int LOADER_ID = 10;
    private View mRootView;
    private BookListAdapter mBookListAdapter;

    private EditText mSearchEditText;
    private ImageButton mSearchButton;
    private ListView mBookListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private void cacheViewReferences() {
        mSearchEditText = (EditText) mRootView.findViewById(R.id.searchText);
        mSearchButton = (ImageButton) mRootView.findViewById(R.id.searchButton);
        mBookListView = (ListView) mRootView.findViewById(R.id.listOfBooks);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_list_of_books, container, false);

        cacheViewReferences();
        initViewComponents();

        return mRootView;
    }

    private void initViewComponents() {
        initSearchEditText();
        initSearchButton();
        initBooksListView();
    }

    private void initBooksListView() {
        Cursor cursor = getActivity().getContentResolver().query(
                AlexandriaContract.BookEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        // STUDENT NOTE: No need to close the cursor here. It's accomplished by onLoaderReset().

        mBookListAdapter = new BookListAdapter(getActivity(), cursor, 0);
        mBookListView.setAdapter(mBookListAdapter);
        mBookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = mBookListAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((Callback) getActivity())
                            .onItemSelected(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry._ID)));
                }
            }
        });
    }

    private void initSearchButton() {
        mSearchButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListOfBooks.this.restartLoader();
                    }
                }
        );
    }

    // STUDENT NOTE: Usability. Implement search triggering through soft keyboard.
    private void initSearchEditText() {
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView field, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    restartLoader();
                    SoftKeyboardUtils.hideSoftKeyboard(getActivity(), field.getWindowToken());

                    handled = true;
                }
                return handled;
            }
        });

        // STUDENT NOTE: keyboard was kept open when user was seeing the book list, focused on the
        // search field (making keyboard appear) and clicking a book.
        mSearchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                // http://stackoverflow.com/questions/15412943/hide-soft-keyboard-on-losing-focus
                if (view.getId() == mSearchEditText.getId() && !hasFocus) {
                    SoftKeyboardUtils.hideSoftKeyboard(getActivity(), view.getWindowToken());
                }
            }
        });
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        final String selection = AlexandriaContract.BookEntry.TITLE + " LIKE ? OR " + AlexandriaContract.BookEntry.SUBTITLE + " LIKE ? ";
        String searchString = mSearchEditText.getText().toString();

        if (searchString.length() > 0) {
            searchString = "%" + searchString + "%";
            return new CursorLoader(
                    getActivity(),
                    AlexandriaContract.BookEntry.CONTENT_URI,
                    null,
                    selection,
                    new String[]{searchString, searchString},
                    null
            );
        }

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mBookListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookListAdapter.swapCursor(null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.books);
    }

    // STUDENT NOTE: Save the query, so we are able to keep list state after device rotation.
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString(BUNDLE_KEY_SEARCH_TERM, mSearchEditText.getText().toString());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            String searchTerm = savedInstanceState.getString(BUNDLE_KEY_SEARCH_TERM);
            if (searchTerm != null && !"".equals(searchTerm)) {
                mSearchEditText.setText(searchTerm);
                restartLoader();
            }
        }
    }

    // STUDENT NOTE: Make sure list gets after refreshed when a book is deleted, for example.
    @Override
    public void onResume() {
        super.onResume();
        restartLoader();
    }
}
