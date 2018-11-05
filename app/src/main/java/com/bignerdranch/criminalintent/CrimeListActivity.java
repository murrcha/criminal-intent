package com.bignerdranch.criminalintent;

import android.support.v4.app.Fragment;

/**
 * CrimeListActivity
 *
 * @author Ksenya Kaysheva (murrcha@me.com)
 * @since 05.11.2018
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
