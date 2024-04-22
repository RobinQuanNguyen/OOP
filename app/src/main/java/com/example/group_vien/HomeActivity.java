package com.example.group_vien;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import dagger.hilt.android.AndroidEntryPoint;

public class HomeActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.bottomNavView);
        fragmentManager = getSupportFragmentManager();


        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            int fragmentCount = fragmentManager.getBackStackEntryCount();
            if(fragmentCount < 2){
                if(itemId == R.id.nav_dashboard) {
                    replaceFragment(new Frag_Dashboard(),"frag_dashboard");
                    return true;
                } else if (itemId == R.id.nav_quiz) {
                    replaceFragment(new Frag_Quiz(),"frag_quiz");
                    return true;
                }
            } else {
                if(itemId == R.id.nav_dashboard) {
                    fragmentManager.getFragments().clear();
                    fragmentManager.popBackStack("frag_dashboard",0);
                    return true;
                } else if (itemId == R.id.nav_quiz) {
                    fragmentManager.getFragments().clear();
                    fragmentManager.popBackStack("frag_quiz",1);
                    return true;
                }
            }
            return false;
        });

        bottomNavigationView.setOnItemReselectedListener(item -> {

        });
        replaceFragment(new Frag_Dashboard(),"frag_dashboard");
    }
    private void replaceFragment(Fragment fragment, String tag) {
        FragmentTransaction Transaction = fragmentManager.beginTransaction();
        Transaction.replace(R.id.nav_fragment, fragment, tag);
        Transaction.addToBackStack(tag);
        Transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        fragmentManager.getFragments().clear();
        this.finish();
    }
}