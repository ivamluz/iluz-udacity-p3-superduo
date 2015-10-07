package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String ISBN_PREFIX = "978";
    private static final String LOG_TAG = AddBook.class.getSimpleName();
    private final int LOADER_ID = 1;
    private final String EAN_CONTENT = "eanContent";

    private View mRootView;
    private EditText mEanEditText;
    private Button mScanButton;
    private Button mSaveButton;
    private Button mDeleteButton;
    private TextView mBookTitleTextView;
    private TextView mBookSubtitleTextView;
    private TextView mBookAuthorsTextView;
    private ImageView mBookCoverImageView;
    private TextView mBookCategoriesTextView;

    public AddBook() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mEanEditText != null) {
            outState.putString(EAN_CONTENT, mEanEditText.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        cacheViewReferences();
        initViewComponents(savedInstanceState);

        return mRootView;
    }

    private void cacheViewReferences() {
        mEanEditText = (EditText) mRootView.findViewById(R.id.ean);
        mScanButton = (Button) mRootView.findViewById(R.id.scan_button);
        mSaveButton = (Button) mRootView.findViewById(R.id.save_button);
        mDeleteButton = (Button) mRootView.findViewById(R.id.delete_button);

        mBookTitleTextView = (TextView) mRootView.findViewById(R.id.bookTitle);
        mBookSubtitleTextView = (TextView) mRootView.findViewById(R.id.bookSubTitle);
        mBookAuthorsTextView = (TextView) mRootView.findViewById(R.id.authors);
        mBookCoverImageView = (ImageView) mRootView.findViewById(R.id.bookCover);
        mBookCategoriesTextView = (TextView) mRootView.findViewById(R.id.categories);
    }

    private void initViewComponents(Bundle savedInstanceState) {
        mEanEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ean = s.toString();
                //catch isbn10 numbers
                if (ean.length() == 10 && !ean.startsWith(ISBN_PREFIX)) {
                    ean = ISBN_PREFIX + ean;
                }
                if (ean.length() < 13) {
                    clearFields();
                    return;
                }
                //Once we have an ISBN, start a book intent
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.FETCH_BOOK);
                getActivity().startService(bookIntent);
                AddBook.this.restartLoader();
            }
        });

        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator.forSupportFragment(AddBook.this).initiateScan();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEanEditText.setText("");
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, mEanEditText.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                mEanEditText.setText("");
            }
        });

        if (savedInstanceState != null) {
            mEanEditText.setText(savedInstanceState.getString(EAN_CONTENT));
            mEanEditText.setHint("");
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), getString(R.string.scan_cancelled), Toast.LENGTH_LONG).show();
                Log.d(LOG_TAG, "Scan was cancelled.");
            } else {
                String barCode = result.getContents();
                mEanEditText.setText(barCode);
                Log.d(LOG_TAG, String.format("Scan completed with code: %s", barCode));
            }
        }
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mEanEditText.getText().length() == 0) {
            return null;
        }
        String eanStr = mEanEditText.getText().toString();
        if (eanStr.length() == 10 && !eanStr.startsWith(ISBN_PREFIX)) {
            eanStr = ISBN_PREFIX + eanStr;
        }
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        mBookTitleTextView.setText(bookTitle);

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        mBookSubtitleTextView.setText(bookSubTitle);

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String[] authorsArr = authors.split(",");
        mBookAuthorsTextView.setLines(authorsArr.length);
        mBookAuthorsTextView.setText(authors.replace(",", "\n"));

        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
            new DownloadImage(mBookCoverImageView).execute(imgUrl);
            mBookCoverImageView.setVisibility(View.VISIBLE);
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        mBookCategoriesTextView.setText(categories);

        mSaveButton.setVisibility(View.VISIBLE);
        mDeleteButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearFields() {
        mBookTitleTextView.setText("");
        mBookSubtitleTextView.setText("");
        mBookAuthorsTextView.setText("");
        mBookCategoriesTextView.setText("");
        mBookCoverImageView.setVisibility(View.INVISIBLE);
        mSaveButton.setVisibility(View.INVISIBLE);
        mDeleteButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }
}
