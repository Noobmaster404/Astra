package com.example.astra.Auth;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.astra.Navigation.Cart.CartFragment;
import com.example.astra.Navigation.HomeFragment;
import com.example.astra.Navigation.ProfileFragment;
import com.example.astra.Navigation.SearchFragment;
import com.example.astra.R;
import com.example.astra.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;//Относится к bottom navigation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Из-за edgeToEdge у меня неправильно работал navigation bar
        //EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());//Относится к bottom navigation
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        replaceFragment(new HomeFragment());//Относится к bottom navigation

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home){
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.nav_profile){
                replaceFragment(new ProfileFragment());
            } else if (itemId == R.id.nav_cart){
                replaceFragment(new CartFragment());
            } else if (itemId == R.id.nav_search){
                replaceFragment(new SearchFragment());
            }
            return true;
        });

    }
    
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }//Относится к bottom navigation

}