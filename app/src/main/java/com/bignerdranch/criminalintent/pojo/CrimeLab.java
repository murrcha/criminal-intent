package com.bignerdranch.criminalintent.pojo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.criminalintent.database.CrimeBaseHelper;
import com.bignerdranch.criminalintent.database.CrimeCursorWrapper;
import com.bignerdranch.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * CrimeLab
 *
 * @author Ksenya Kaysheva (murrcha@me.com)
 * @since 05.11.2018
 */
public class CrimeLab {

    private static CrimeLab crimeLab;
    private Context context;
    private SQLiteDatabase database;

    private CrimeLab(Context context) {
        this.context = context.getApplicationContext();
        this.database = new CrimeBaseHelper(this.context).getWritableDatabase();
    }

    public static CrimeLab get(Context context) {
        if (crimeLab == null) {
            crimeLab = new CrimeLab(context);
        }
        return crimeLab;
    }

    public void addCrime(Crime crime) {
        ContentValues values = getContentValues(crime);
        database.insert(CrimeTable.NAME, null, values);
    }

    public void updateCrime(Crime crime) {
        String uuid = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        database.update(CrimeTable.NAME, values,
                String.format("%s = ?", CrimeTable.Cols.UUID),
                new String[] {uuid});
    }

    public void deleteCrime(Crime crime) {
        String uuid = crime.getId().toString();
        database.delete(CrimeTable.NAME,
                String.format("%s = ?", CrimeTable.Cols.UUID),
                new String[] {uuid});
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime(final UUID id) {
        Crime crime = null;
        CrimeCursorWrapper cursor = queryCrimes(
                String.format("%s = ?", CrimeTable.Cols.UUID),
                new String[] {id.toString()});
        try {
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                crime = cursor.getCrime();
            }
        } finally {
            cursor.close();
        }
        return crime;
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().toString());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        values.put(CrimeTable.Cols.NUMBER, crime.getNumber());
        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = database.query(CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);
        return new CrimeCursorWrapper(cursor);
    }
}
