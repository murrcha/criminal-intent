package com.bignerdranch.criminalintent;


import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bignerdranch.criminalintent.utils.PictureUtils;

import java.io.File;
import java.util.Objects;

/**
 * PhotoFragment
 *
 * @author Ksenya Kaysheva (murrcha@me.com)
 * @since 07.11.2018
 */
public class PhotoFragment extends DialogFragment {

    private static final String FILE = "file";

    public static PhotoFragment newInstance(File file) {
        Bundle args = new Bundle();
        args.putSerializable(FILE, file);
        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        File photoFile = (File) Objects.requireNonNull(getArguments()).getSerializable(FILE);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_photo, null);
        ImageView imageView = view.findViewById(R.id.photo_detail_view);
        Bitmap bitmap = PictureUtils.getScaledBitmap(
                Objects.requireNonNull(photoFile).getPath(),
                Objects.requireNonNull(getActivity()));
        imageView.setImageBitmap(bitmap);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }
}
