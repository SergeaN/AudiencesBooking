package com.etu.audiencesbooking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutFragment extends Fragment {

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.aboutus);
        Element versionElement = new Element();
        versionElement.setTitle("Version 1.2");
        return new AboutPage(getContext())
                .isRTL(false)
                .enableDarkMode(false)
                .setDescription(getString(R.string.descripiton))
                .setImage(R.drawable.full_profile_icon)
                .addItem(versionElement)
                .addGroup("Connect with us")
                .addEmail("saanukhin@stud.etu.ru")
                .addWebsite("https://etu.ru/")
                .addFacebook("the.medy")
                .addTwitter("sergeanpro")
                .addYoutube("UCnKhcV7frITmrYbIU5MrMZw")
                .addYoutube("spbetu", "University")
                .addGitHub("SergeaN", "Sergei")
                .addGitHub("Emiliano43", "Emil")
                .addInstagram("serggean")
                .create();
    }
}
