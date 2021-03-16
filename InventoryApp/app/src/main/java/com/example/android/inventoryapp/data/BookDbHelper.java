package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.inventoryapp.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="library.db";
    private static final int DATABASE_VERSION=1;
    public BookDbHelper(Context context){
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BOOKS_TABLE=  "CREATE TABLE " + BookEntry.TABLE_NAME +"("+
                BookEntry._ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                BookEntry.COLUMN_BOOK_NAME +" TEXT, "+
                BookEntry.COLUMN_BOOK_PRICE +" INTEGER , "+
                BookEntry.COLUMN_BOOK_QUANTITY +" INTEGER , "+
                BookEntry.COLUMN_SUPPLIER_NAME +" TEXT, "+
                BookEntry.COLUMN_SUPPLIER_NUMBER + " INTEGER DEFAULT 0);";
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
