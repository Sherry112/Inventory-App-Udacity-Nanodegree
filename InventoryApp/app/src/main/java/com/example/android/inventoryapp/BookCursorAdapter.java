package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
// Find fields to populate in inflated template
        TextView name = (TextView) view.findViewById(R.id.book_name);
        TextView priceOfOneBook = (TextView) view.findViewById(R.id.price_unit);
        TextView quantity = (TextView) view.findViewById(R.id.book_quantity);
        TextView supplier_name = (TextView) view.findViewById(R.id.book_supplier_name);
        TextView number = (TextView) view.findViewById(R.id.book_supplier_number);
        Button sale=(Button) view.findViewById(R.id.sale);

        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
        int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
        int numberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NUMBER);


        // Read the pet attributes from the Cursor for the current pet
        String bookName = cursor.getString(nameColumnIndex);
        String unitPrice = cursor.getString(priceColumnIndex);
        String bookQuantity=cursor.getString(quantityColumnIndex);
        String supplierName = cursor.getString(supplierNameColumnIndex);
        String supplierNumber=cursor.getString(numberColumnIndex);



      /* String changePrice= String.valueOf(unitPrice);
       String changeQuantity=String.valueOf(bookQuantity);
       String changeNumber=String.valueOf(supplierNumber);*/
        // Update the TextViews with the attributes for the current book
        name.setText(bookName);
        priceOfOneBook.setText(unitPrice+"USD");
        quantity.setText("Quantity : "+bookQuantity);
        supplier_name.setText("Supplier Name: "+supplierName);
        number.setText("Contact info:: "+supplierNumber);


        final int saleQuanityColumnIndeex=cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
        final int idColumnIndex=cursor.getInt(cursor.getColumnIndex(BookEntry._ID));
        final int saleQuantity=cursor.getInt(saleQuanityColumnIndeex);

        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saleQuantity>0){
                    int newSaleQuantity=saleQuantity;
                    newSaleQuantity=newSaleQuantity-1;
                    Uri saleUri= ContentUris.withAppendedId(BookEntry.CONTENT_URI, idColumnIndex);
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_BOOK_QUANTITY, newSaleQuantity);
                    context.getContentResolver().update(saleUri, values, null, null);
                    //Log.v("Adapter","value of quanuty:"+newSaleQuantity+" "+idColumnIndex);
                }
                else
                {
                    Toast.makeText(context, "Book is out of stock", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }
}