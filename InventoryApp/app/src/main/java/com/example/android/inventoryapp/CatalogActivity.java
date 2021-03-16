package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryapp.data.BookContract.BookEntry;


public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;
    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView bookListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);
       mCursorAdapter=new BookCursorAdapter(this,null);
       bookListView.setAdapter(mCursorAdapter);
       bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent intent=new Intent(CatalogActivity.this, EditorActivity.class);
               Log.i("CatalogActivity","Message"+"Opening new activity");
               Uri currentUri= ContentUris.withAppendedId(BookEntry.CONTENT_URI,id);
               intent.setData(currentUri);
               startActivity(intent);
           }
       });

        getSupportLoaderManager().initLoader(BOOK_LOADER,null,this);
    }



    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the books(library) database.
     */


    private void insertBook() {
        ContentValues values = new ContentValues();

        values.put(BookEntry.COLUMN_BOOK_NAME, "The Lord of the Rings");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 50);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 5);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "J. R. R. Tolkien");
        values.put(BookEntry.COLUMN_SUPPLIER_NUMBER, 903204000);

        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
        Log.v("CatalogActivity","Tag"+BookEntry.CONTENT_URI);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertBook();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_NUMBER,

        };

return new CursorLoader(this,BookEntry.CONTENT_URI,
        projection,
        null,
        null,
        null);
}

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mCursorAdapter.swapCursor(null);
    }

    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from Library database");
    }
}