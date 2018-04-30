package com.example.maciel.meuprimeiroapp.control;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.maciel.meuprimeiroapp.adapter.ListViewAdapter;
import com.example.maciel.meuprimeiroapp.R;
import com.example.maciel.meuprimeiroapp.dao.ClienteDAO;
import com.example.maciel.meuprimeiroapp.models.Cliente;

import java.util.List;

public class MainActivity extends AppCompatActivity  implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener{
    //instacia global
    private static final String TAG="clientesbd";
    private Cliente cliente;
    EditText edtnome,edtcpf,edttelefone;
    ImageView imvimagem;
    private ClienteDAO clienteDAO;
    ListView listview;
    private byte[]imagem=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet);

        //intacia dos obejtos
         cliente =  new Cliente();
         clienteDAO= ClienteDAO.getInstance(this);

         edtnome = (EditText) findViewById(R.id.editText_nome);
         edtcpf = (EditText)findViewById(R.id.editText_cpf);
         edttelefone =(EditText) findViewById(R.id.editText_telefone);

         imvimagem = (ImageView) findViewById(R.id.imv_foto);

         listview=(ListView)findViewById(R.id.listview);

         listview.setOnItemClickListener(this);

         carregarListView(clienteDAO.getAll());


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
                    cliente.setImagem(imagem);

                    Log.d(TAG,cliente.toString());
                    //valida de que realmente salvou no BD
                    if(clienteDAO.save(cliente)>0){
                        Toast.makeText(this, "Salvando...", Toast.LENGTH_SHORT).show();
                        carregarListView(clienteDAO.getAll());
                    }
                    limparForm();

                }else{
                    Toast.makeText(this, "Por favor informe os valores", Toast.LENGTH_SHORT).show();
                }


            break;

            case R.id.menu_item_excluir:
               // Toast.makeText(this, "Excluir", Toast.LENGTH_SHORT).show();
                if(cliente.getId() !=0){
                    if(clienteDAO.delete(cliente)>0){
                        Toast.makeText(this, "Excluido", Toast.LENGTH_SHORT).show();
                        carregarListView(clienteDAO.getAll());
                    }else{
                        Toast.makeText(this, "Não foi possivel excluir, contate o desenvolvedor do app", Toast.LENGTH_SHORT).show();
                    }

                }
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
        imvimagem.setImageResource(R.drawable.foto_sombra);

    }

    public void carregarListView(List<Cliente>clientes){
        ListViewAdapter adapter = new ListViewAdapter(this,clientes);
        listview.setAdapter(adapter);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        cliente=(Cliente) adapterView.getAdapter().getItem(position);

        //seta valores na tela
        edtnome.setText(cliente.getNome());
        edtcpf.setText(cliente.getCpf());
        edttelefone.setText(cliente.getTelefone());
        edtnome.requestFocus();

        //validação para verificar se imagem não esta null
        if(cliente.getImagem() !=null){
            imvimagem.setImageBitmap(BitmapFactory.decodeByteArray(cliente.getImagem(),0,cliente.getImagem().length));
        }

    }
}
