package com.example.maciel.meuprimeiroapp.control;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.maciel.meuprimeiroapp.R;
import com.example.maciel.meuprimeiroapp.models.Cliente;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{


    private Spinner cbx_sexo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // popular spinner
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.cbx_sexo,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cbx_sexo.setAdapter(adapter);

    }
}
