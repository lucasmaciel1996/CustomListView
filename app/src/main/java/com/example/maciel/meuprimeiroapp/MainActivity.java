package com.example.maciel.meuprimeiroapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import models.User;

public class MainActivity extends AppCompatActivity {

    private Button buttonLimpar;
    private Button buttonConfirmar;
    private EditText edt_name;
    private EditText edt_cpf;
    User user =new User();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonConfirmar = (Button) findViewById(R.id.btn__confirma);
        buttonLimpar = (Button)findViewById(R.id.btn_limpar);
        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_cpf = (EditText) findViewById(R.id.edtcpf);

        buttonLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_name.setText(" ");
                edt_cpf.setText(" ");
            }
        });

        buttonConfirmar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                user.SetNome(edt_name.getText().toString());
                user.setCpf(edt_cpf.getText().toString());
                edt_name.setText(" ");
                edt_cpf.setText(" ");
            }
        });
    }
}
