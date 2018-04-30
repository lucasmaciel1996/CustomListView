package com.example.maciel.meuprimeiroapp.control;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener, ActionBar.TabListener{
    //instacia global
    private static final String TAG="clientesbd";
    private ClienteDAO clienteDAO;

    private static Cliente cliente = null;
    private EditText edtnome,edtcpf,edttelefone;
    private ImageView imvimagem;

    private static final String GETALL = "getAll";
    private static final String GETBYNAME = "getbyname";
    private static final String SAVE = "save";
    private static final String DELETE = "delete";
    private String nameFind = "";

    private byte[]imagem=null;
    ListView listview;
    ActionBar.Tab tab0,tab1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //intacia dos obejtos
         cliente =  new Cliente();
         clienteDAO= ClienteDAO.getInstance(this);

         if(isTablet(this)){
             setContentView(R.layout.activity_tablet);

             edtnome = (EditText) findViewById(R.id.editText_nome);
             edtcpf = (EditText)findViewById(R.id.editText_cpf);
             edttelefone =(EditText) findViewById(R.id.editText_telefone);

             imvimagem = (ImageView) findViewById(R.id.imv_foto);

             listview=(ListView)findViewById(R.id.listview);
             listview.setOnItemClickListener(this);

             carregarListView(clienteDAO.getAll());

         }else{
             getSupportActionBar().setNavigationMode(getSupportActionBar().NAVIGATION_MODE_TABS);

             tab0 = getSupportActionBar().newTab().setText("Lista de Clientes");
             tab0.setTabListener(this);
             getSupportActionBar().addTab(tab0);

             tab1=getSupportActionBar().newTab().setText("Clientes");
             tab1.setTabListener(this);
             getSupportActionBar().addTab(tab1);
         }


    }
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        switch (tab.getPosition()){
            case 0:
                setContentView(R.layout.activity_smartphone_list);
                listview=(ListView)findViewById(R.id.listview);
                listview.setOnItemClickListener(this);

                carregarListView(clienteDAO.getAll());
            break;
            case 1:
                setContentView(R.layout.activity_smartphone_inputs);
                edtnome = (EditText) findViewById(R.id.editText_nome);
                edtcpf = (EditText)findViewById(R.id.editText_cpf);
                edttelefone =(EditText) findViewById(R.id.editText_telefone);

                imvimagem = (ImageView) findViewById(R.id.imv_foto);

            break;
        }

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    private static boolean isTablet(Context context){
        return (context.getResources().getConfiguration().screenLayout
               & Configuration.SCREENLAYOUT_SIZE_MASK)>=Configuration.SCREENLAYOUT_SIZE_LARGE;

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
                    //valida de que realmente salvou no BD
                    if(clienteDAO.save(cliente)>0){
                        Toast.makeText(this, "Salvando...", Toast.LENGTH_SHORT).show();
                        carregarListView(clienteDAO.getAll());
                        if(!isTablet(this)){
                            tab0.select();
                        }
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
        cliente = new Cliente();

    }

    public void carregarListView(List<Cliente>clientes){
        ListViewAdapter adapter = new ListViewAdapter(this,clientes);
        listview.setAdapter(adapter);
    }
    public void selecionarImagem(View view){
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent,"Selecione uma imagem"),0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Uri arquivoUri =data.getData();
            Log.d(TAG,"Uri da imagem"+arquivoUri);
            imvimagem.setImageURI(arquivoUri);
            //transforma em bytearray
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(arquivoUri));
                //converte em array de byte
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                //comprimindo em bytearray e eviando para outputstrem
                bitmap.compress(Bitmap.CompressFormat.PNG,0,outputStream);
                //seta valores no objeto
                cliente.setImagem(outputStream.toByteArray());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //Toast.makeText(this, "pesquisando", Toast.LENGTH_SHORT).show();
        if(!isTablet(this)){
            tab0.select();
        }
        carregarListView(clienteDAO.getbyName(newText));
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if(!isTablet(this)){
            tab1.select();
        }
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

    /*
        Classe interna para realizar as transações no banco de dados
     */
    private class Task extends AsyncTask<String, Void, List<Cliente>> {

        long count = 0L; //para armazenar o retorno do salvar e do excluir

        //executa a task em outra Thread
        @Override
        protected List<Cliente> doInBackground(String... strings) {
            if(strings[0].equals(GETALL)){
                return clienteDAO.getAll(); //get
            }else{
                if(strings[0].equals(GETBYNAME)){
                    return clienteDAO.getbyName(nameFind); //get
                }else{
                    if(strings[0].equals(SAVE)){
                        count = clienteDAO.save(MainActivity.cliente); //create e update
                    }else{
                        if(strings[0].equals(DELETE)){
                            count = clienteDAO.delete(MainActivity.cliente); //delete
                        }
                    }
                }
            }

            return null;
        }

        //atualiza a View
        @Override
        protected void onPostExecute(List<Cliente> clientes) {
            if(clientes != null){
                carregarListView(clientes);
            }else if(count > 0){
                Toast.makeText(MainActivity.this, "Operação realizada.", Toast.LENGTH_SHORT).show();
                limparForm();
                if(!isTablet(MainActivity.this)) {
                    tab1.select(); //seleciona a aba 1
                    //onTabSelected(tab1, null); //chama o tratador de eventos para carregar os componentes
                }
                Log.d(TAG, "Operação realizada.");
            }else{
                Toast.makeText(MainActivity.this, "Erro ao atualizar o contato. Contate o desenvolvedor.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
