package com.bignerdranch.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.criminalintent.Crime;
import com.bignerdranch.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

/**
 * CrimeCursorWrapper
 *
 * @author Ksenya Kaysheva (murrcha@me.com)
 * @since 05.11.2018
 */
public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuid = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));

        Crime crime = new Crime(UUID.fromString(uuid));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        return crime;
    }
}
