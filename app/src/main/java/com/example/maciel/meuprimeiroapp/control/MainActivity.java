package com.example.maciel.meuprimeiroapp.control;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.maciel.meuprimeiroapp.R;
import com.example.maciel.meuprimeiroapp.models.Cliente;

public class MainActivity extends AppCompatActivity  implements SearchView.OnQueryTextListener{
    //instacia global
    private static final String TAG="clientesbd";
    private Cliente cliente;
    EditText edtnome,edtcpf,edttelefone;
    ImageView imvimagem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet);

        //intacia dos obejtos
         cliente =  new Cliente();
         edtnome = (EditText) findViewById(R.id.editText_nome);
         edtcpf = (EditText)findViewById(R.id.editText_cpf);
         edttelefone =(EditText) findViewById(R.id.editText_telefone);

         imvimagem = (ImageView) findViewById(R.id.imv_foto);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar,menu);

        SearchView searchview= (SearchView) menu.findItem(R.id.menu_item_pesquisar).getActionView();
        searchview.setQueryHint("Digite um nome");
        searchview.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_item_salvar:
                //Toast.makeText(this, "Salvar", Toast.LENGTH_SHORT).show();
                if(!edtnome.getText().toString().isEmpty()&&
                !edtcpf.getText().toString().isEmpty()&&
                !edttelefone.getText().toString().isEmpty())
                {
                  //entrou no if

                    cliente.setNome(edtnome.getText().toString());
                    cliente.setCpf(edtcpf.getText().toString());
                    cliente.setTelefone(edttelefone.getText().toString());

                    Log.d(TAG,cliente.toString());
                    limparForm();
                }else{
                    Toast.makeText(this, "Por favor informe os valores", Toast.LENGTH_SHORT).show();
                }


            break;

            case R.id.menu_item_excluir:
                Toast.makeText(this, "Excluir", Toast.LENGTH_SHORT).show();
                limparForm();

            break;

            case R.id.menu_item_cencelar:
                Toast.makeText(this, "Cancelar", Toast.LENGTH_SHORT).show();
                limparForm();
            break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void limparForm(){
        edtnome.setText(null);
        edtcpf.setText(null);
        edttelefone.setText(null);
        edtnome.requestFocus();
        imvimagem.setImageResource(R.mipmap.ic_user);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Toast.makeText(this, "pesquisando", Toast.LENGTH_SHORT).show();
        return true;
    }
}
