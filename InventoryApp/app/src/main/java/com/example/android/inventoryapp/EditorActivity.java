package com.example.android.inventoryapp;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

/**
 * Allows user to create a new book or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    /** Identifier for the book data loader */
    private static final int EXISTING_BOOK_LOADER = 0;
    private boolean mBookHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };


    private Uri mCurrentBookUri;
    /** EditText field to enter the book's name */
    private EditText mNameEditText;

    /** EditText field to enter the book's price */
    private EditText mPriceEditText;


    private EditText mQuantitytEditText;
    private EditText mSupplierName;
    private EditText mSupplierNumber;
    private Button increasingButton;
    private Button decreasingButton;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent=getIntent();
        mCurrentBookUri=intent.getData();
        if(mCurrentBookUri==null){
            setTitle("Add a Book");
            invalidateOptionsMenu();

        }
        else
        {
            setTitle("Edit a Book");
            getSupportLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_book_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mQuantitytEditText=(EditText)findViewById(R.id.edit_quantity);
        mSupplierName=(EditText) findViewById(R.id.supplier_name);
        mSupplierNumber=(EditText)findViewById(R.id.supplier_number);
        increasingButton=(Button)findViewById(R.id.plus_button);
        decreasingButton=(Button)findViewById(R.id.minus_button);


       mQuantitytEditText.setText("0");

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantitytEditText.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mSupplierNumber.setOnTouchListener(mTouchListener);


        ImageView callButton=(ImageView)findViewById(R.id.call);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number=mSupplierNumber.getText().toString().trim();
                Intent callIntent=new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" +number));
                startActivity(callIntent);
            }
        });

        increasingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mQuantitytEditText.getText().toString().trim())){
                    int quantity = Integer.parseInt(mQuantitytEditText.getText().toString().trim());
                    quantity++;
                    mQuantitytEditText.setText("" + quantity); }
            }
        });
        decreasingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mQuantitytEditText.getText().toString().trim())){
                    int quantity = Integer.parseInt(mQuantitytEditText.getText().toString().trim());
                    if(quantity<=0){
                        return;
                    }
                    quantity--;
                    mQuantitytEditText.setText("" + quantity); }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveBook();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void saveBook() {
        String bookName = mNameEditText.getText().toString().trim();
        String price = mPriceEditText.getText().toString().trim();
        String quantity = mQuantitytEditText.getText().toString().trim();
        String supplier_name = mSupplierName.getText().toString().trim();
        String number = mSupplierNumber.getText().toString().trim();

        int bookPrice;
        int bookQuantity;
        int supplier_number;


        ContentValues values = new ContentValues();
        // Check if this is supposed to be a new book
        // and check if all the fields in the editor are blank
        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(bookName) && TextUtils.isEmpty(price) &&
                TextUtils.isEmpty(String.valueOf(quantity)) && TextUtils.isEmpty(supplier_name) && TextUtils.isEmpty(number)) {
            // Since no fields were modified, we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        if (TextUtils.isEmpty(bookName)) {
            //mNameEditText.setError("Your book needs a name");

            Toast.makeText(this, "Please provide valid name for Book", Toast.LENGTH_LONG).show();
            return;
        }
        if (!TextUtils.isEmpty(price)) {
            bookPrice = Integer.parseInt(price);
            }
            else{
             bookPrice=0;
            Log.v("Message", "Item: " + price);
            Toast.makeText(this, "Please provide valid price for Book", Toast.LENGTH_LONG).show();
            return;}


        if (!TextUtils.isEmpty(quantity)) {
            bookQuantity = Integer.parseInt(quantity);
        }
        else{
            bookQuantity=0;
            Toast.makeText(this, "Please provide valid quantiy ordered of Book", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(supplier_name)) {
            Toast.makeText(this, "Please provide valid supplier name for your book", Toast.LENGTH_LONG).show();
            return;
        }
        if (!TextUtils.isEmpty(number)) {
            supplier_number = Integer.parseInt(number);}

            else{
            Toast.makeText(this, "Please provide valid supplier number for your book", Toast.LENGTH_SHORT).show();
            return;
        }
            values.put(BookEntry.COLUMN_BOOK_NAME, bookName);
            values.put(BookEntry.COLUMN_BOOK_PRICE, bookPrice);
            values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
            values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplier_name);
            values.put(BookEntry.COLUMN_SUPPLIER_NUMBER, supplier_number);


            // Show a toast message depending on whether or not the insertion was successful
            if (mCurrentBookUri == null) {
                Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                            Toast.LENGTH_SHORT).show();
                }

            } else {
                int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this, getString(R.string.editor_update_pet_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_update_pet_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }




    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_NUMBER,
                BookEntry.COLUMN_BOOK_QUANTITY
        };
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,         // Query the content URI for the current book
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished( Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int numberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NUMBER);
            String bookName = cursor.getString(nameColumnIndex);
            int unitPrice = cursor.getInt(priceColumnIndex);
            int unitQuantity=cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            int supplierNumber=cursor.getInt(numberColumnIndex);

            String strUnitPrice=String.valueOf(unitPrice);
            String strUnitQuantity=String.valueOf(unitQuantity);
            String strNumber=String.valueOf(supplierNumber);
            mNameEditText.setText(bookName);
            mPriceEditText.setText(strUnitPrice);
            mQuantitytEditText.setText(strUnitQuantity);
            mSupplierName.setText(supplierName);
            mSupplierNumber.setText(strNumber);
        }

    }

    @Override
    public void onLoaderReset( Loader<Cursor> loader) {

        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantitytEditText.setText("");
        mSupplierName.setText("");
        mSupplierNumber.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public void onBackPressed() {
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }
    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
