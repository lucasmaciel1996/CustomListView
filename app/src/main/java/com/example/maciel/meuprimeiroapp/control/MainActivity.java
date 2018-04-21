package com.example.maciel.meuprimeiroapp.control;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;

import com.example.maciel.meuprimeiroapp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet);
        getSupportActionBar().setTitle(R.string.app_name);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflate =getMenuInflater();
        inflate.inflate(R.menu.menu_actionbar,menu);

        SearchView mysearchview=(SearchView)menu.findItem(R.id.menuitem_search).getActionView();

        return true;
    }
}
