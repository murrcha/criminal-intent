package com.bignerdranch.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bignerdranch.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * CrimeBaseHelper
 *
 * @author Ksenya Kaysheva (murrcha@me.com)
 * @since 05.11.2018
 */
public class CrimeBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(String.format("create table %s "
                + "( _id integer primary key autoincrement, "
                + "%s, %s, %s, %s, %s )",
                CrimeTable.NAME,
                CrimeTable.Cols.UUID,
                CrimeTable.Cols.TITLE,
                CrimeTable.Cols.DATE,
                CrimeTable.Cols.SOLVED,
                CrimeTable.Cols.SUSPECT));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
