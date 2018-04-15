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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.maciel.meuprimeiroapp.dao.ClienteDAO;
import com.example.maciel.meuprimeiroapp.models.Cliente;

import java.io.ByteArrayOutputStream;
import java.nio.file.FileSystemNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnClickListener,SearchView.OnQueryTextListener,android.support.v7.app.ActionBar.TabListener {

    private static String TAG="clientes_db";
    private ClienteDAO clienteDAO;
    private EditText edt_nome;
    private EditText edt_cpf;
    private Spinner cbx_sexo;
    private EditText edt_telefene;
    private ListView lvClientes;
    private ImageView imvFoto;
    private static final String GETALL="getAll";
    private static final String GETBYNAME="getbyname";
    private static final String SAVE="save";
    private static final String DELETE="delete";
    private static Cliente cliente=null;
    private String nameFind="";
    ActionBar.Tab tab1,tab2;//uma das abas da activity, quando em smartphone
    private byte[]imagem=null; //imagem contato

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //obtém a instância do objeto de acesso ao banco de dados
        clienteDAO = ClienteDAO.getinstance(this);
        //constrói uma instância da classe de modelo
        cliente = new Cliente();

        if(isTablet(this)){
            setContentView(android.R.layout.activity_tablet);
            getSupportActionBar().setTitle(android.R.string.titulo_actionbar_list);//insere titulo para a janela

            //mapeia os componentes de UI
            edt_nome = (EditText) findViewById(R.id.editText_nome);
            edt_cpf = (EditText) findViewById(R.id.editText_sobrenome);
            edt_telefene= (EditText) findViewById(R.id.editText_telefone);
            imvFoto= (ImageView) findViewById(R.id.imageView);
            lvClientes = (ListView) findViewById(R.id.listView);
            lvClientes.setOnItemClickListener(this); //adiciona a lista de ouvintes
            new Task().execute(GETALL); //executa a operação GET em segundo plano

        }else{
            //prepara a ActionBar
            getSupportActionBar().setTitle(R.string.titulo_actionbar_list);//insere um título para a janela
            getSupportActionBar().setNavigationMode(getSupportActionBar().NAVIGATION_MODE_TABS);//define o modo de navegação por abas

            /*
                Cria as abas e as adiciona à ActionBar
            */
            //tab1
            tab1 = getSupportActionBar().newTab().setText("Lista de Clientes");
            tab1.setTabListener(MainActivity.this);
            getSupportActionBar().addTab(tab1);

            //tab2
            tab2 = getSupportActionBar().newTab().setText("Cliente");
            tab2.setTabListener(MainActivity.this);
            getSupportActionBar().addTab(tab2);
        }

    }
    /*
       Trata eventos das Tabs
    */
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        switch (tab.getPosition()){
            case 0:{
                //mapeia os componestes da UI
                setContentView(android.R.layout.activity_smartphone_list);

                //insere titulo ao action bar
                getSupportActionBar().setTitle("Clintes");

                //mapeias os componetes de  activity_list.xm
                //mapeia os componentes de activity_list.xml
                lvClientes = (ListView) findViewById(R.id.listView);
                lvClientes.setOnItemClickListener(MainActivity.this); //registra o tratador de eventos para cada item da ListView

                new Task().execute(GETALL); //executa a operação GET em segundo plano

                break;

            }
            case 1:{
                //mapeia os componentes da UI
                setContentView(R.layout.activity_smartphone_inputs);

                //insere um título na ActionBar
                getSupportActionBar().setTitle("Edição");

                //mapeia os componentes de activity_inputs.xml
                edt_nome = (EditText) findViewById(R.id.editText_nome);
                edt_cpf = (EditText) findViewById(R.id.editText_cpf);
                edt_telefene = (EditText) findViewById(R.id.editText_telefone);
                cbx_sexo = (Spinner) findViewById(R.id.editText_sexo);
                imvFoto = (ImageView) findViewById(R.id.imageView);

                break;
            }
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
    public boolean OnCreateOptionsMenu(Menu menu){
        //infla o menu
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        //mapeia e insere o handler a fila de ouvintes da SearchView
        SearchView mySearchView = (SearchView) menu.findItem(R.id.menuitem_search).getActionView();//obtem a SearchView
        mySearchView.setQueryHint("Digite um nome"); //coloca um hint na SearchView
        mySearchView.setOnQueryTextListener(MainActivity.this); //cadastra o tratador de eventos na lista de tratadores da SearchView


        return true;
    }


    //trata Eventod do MenuActionBar
    public boolean onOptionsItemSelected(MenuItem item){
        tab1.select();//seleciona tab1
        switch (item.getItemId()){
            case R.id.menuitem_salvar:
                if(!edt_nome.getText().toString().isEmpty() &&
                        !edt_cpf.getText().toString().isEmpty() &&
                        !edt_telefene.getText().toString().isEmpty()&&!cbx_sexo.getSelectedItem().toString().isEmpty()) {
                    if(cliente.getId() == null){ //se é uma inclusão
                        cliente = new Cliente(); //apaga dados antigos
                    }
                    cliente.setNome(edt_nome.getText().toString());
                    cliente.setCpf(edt_cpf.getText().toString());
                    cliente.setTelefone(edt_telefene.getText().toString());
                    cliente.setSexo(cbx_sexo.getSelectedItem().toString());
                    cliente.setImagem(imagem);
                    Log.d(TAG, "Contato que será salvo: " + cliente.toString());
                    new Task().execute(SAVE); //executa a operação CREATE em segundo plano
                    new Task().execute(GETALL); //executa a operação GET em segundo plano para atualizar a ListView
                }else{
                    Toast.makeText(MainActivity.this,"Preencha todos os Campos.",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menuitem_cancelar:
                LimparFormulario();
                break;
            case R.id.meniitem_excluir:
                if(cliente !=null && !edt_telefene.getText().toString().isEmpty()&&!edt_nome.getText().toString().isEmpty()&&!edt_cpf.getText().toString().isEmpty()
                &&!cbx_sexo.getSelectedItem().toString().isEmpty()){
                    new Task().execute(DELETE); //executa a operação DELETE em segundo plano
                    new Task().execute(GETALL); //executa a operação GET em segundo plano para atualizar a ListView

                }else{
                    Toast.makeText(MainActivity.this, "Selecione um contato na lista.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }
    //trata evendo SearchView

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }
    //trata evendo SearchView
    @Override
    public boolean onQueryTextChange(String newText) {
        if(!isTablet(MainActivity.this)){
            tab1.select();
            //seleciona a aba 1
            //onTabSelected(tab1, null); //chama o tratador de eventos para carregar os componentes
        }
        if(newText.equals("")){
            new Task().execute(GETALL);
        }else{
            new Task().execute(GETBYNAME); //executa a operação GET em segundo plano para atualizar a ListView
            nameFind = newText; //armazena em uma variável global para uso na task
        }

        return true;
    }
    //limpa o formulário
    private void limparFormulario(){
        edt_nome.setText(null);
        edt_cpf.setText(null);
        edt_telefene.setText(null);
        edt_nome.requestFocus();
        //cbx_sexo.setSelected(flas);
        imvFoto.setImageResource(R.drawable.foto_sombra);
        imagem = null;
        cliente = new Cliente(); //apaga dados antigos
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(!isTablet(MainActivity.this)) {
            tab2.select(); //seleciona a aba 2
            //onTabSelected(tab2, null); //chama o tratador de eventos para carregar os componentes
        }
        cliente = (Cliente) adapterView.getAdapter().getItem(i); //obtém o Contato
        Log.d(TAG, cliente.toString());
        //carrega nos campos de input
        edt_nome.setText(cliente.getNome());
        edt_cpf.setText(cliente.getCpf());
        edt_telefene.setText(cliente.getTelefone());
        edt_nome.requestFocus();
        //carrega a imagem no imageview
        if(cliente.imagem != null){
            imvFoto.setImageBitmap(BitmapFactory.decodeByteArray(cliente.getImagem(), 0, cliente.getImagem().length));
        }

    }
    /**
     * Método para carregar os dados do banco de dados para a ListView.
     */
    public void carregarListView(List<Cliente>clientes){
        //cria um objeto da classe ListAdapter, um adaptador List -> ListView
        ListAdapter dadosAdapter= new ListAdapter(this,clientes);
        //associa o adaptador a ListView
        lvClientes.setAdapter(dadosAdapter);
    }
       /*
        Métodos para carregamento e tratamento da imagem da ImageView.
     */

    /**
     * Método que solicita ao sistema operacional a inicialização de uma Activity que saiba obter e devolver imagens.
     * @param v
     */
    public void carregarImagem(View v){
        //cria uma Intent
        //primeiro argumento: ação ACTION_PICK "escolha um item a partir dos dados e retorne o seu URI"
        //segundo argumento: refina a ação para arquivos de imagem, retornando um URI
        Intent intent=new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //inicializa uma Activity. Neste caso, uma que forneca acesso a galeria de imagens do dispositivo
        startActivityForResult(Intent.createChooser(intent,"Selecione Uma Imagem"),0);
    }
    /**
     * Método que recebe o retorno da Activity de galeria de imagens.
     */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode ==RESULT_OK){
            Uri arquivoUri=data.getData();
            Log.d(TAG,"URI d imgagem: "+arquivoUri);
            imvFoto.setImageURI(arquivoUri);
            try{
                Bitmap bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(arquivoUri));
                bitmap=Bitmap.createScaledBitmap(bitmap,200,200,true);//reduz e aplica um filtro na imagem
                byte[] img= getBitmapAsByteArray(bitmap);//converte para um fluxo de bytes
                imagem = img;//coloca a imagem no objeto imagem (um array de bytes (byte[]))
            }catch (FileSystemNotFoundException e){
                e.printStackTrace();

            }
        }
    }
    /**
     * Converte um Bitmap em um array de bytes (bytes[])
     * @param bitmap
     * @return byte[]
     */
    public static  byte[]getBitmapAsByteArray(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();//criam um stream para ByteArray
        bitmap.compress(Bitmap.CompressFormat.PNG,0,outputStream);//Comprime a imagem
        return outputStream.toByteArray();//retorna a imagem como um Array de Bytes (byte[])
    }

    /**
     * Detecção do tipo de screen size.
     * @param context contexto da Activity
     * @return boolean
     */

    private static boolean isTablet(Context context){
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >=Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
    /*
        Classe interna para realizar as transações no banco de dados
     */
    private class  Task extends AsyncTask<String, Void, List<Cliente>> {

        long count =0L;//para armazenar o retorno do salvar e do excluir

        //executa Task em outra thred
        @Override
        protected List<Cliente> doInBackground(String... strings) {
            if(strings[0].equals(GETALL)){
                return clienteDAO.getAll();//get
            }else{
                if(strings[0].equals(GETBYNAME)){
                    return clienteDAO.getByname(nameFind);//get
                }else{
                    if(strings[0].equals(SAVE)){
                        count=clienteDAO.save(MainActivity.cliente);//create e update
                    }else{
                        if(strings[0].equals(DELETE)){
                            count=clienteDAO.delete(MainActivity.cliente);//delete
                        }
                    }

                }
            }
            return null;
        }
        //atualiza a View

        @Override
        protected void onPostExecute(List<Cliente> clientes) {
          if(clientes !=null){
              carregarListView(clientes);
          }else if(count>0){
              Toast.makeText(MainActivity.this, "Operação realizada.", Toast.LENGTH_SHORT).show();
              limparFormulario();
              if(!isTablet(MainActivity.this)){
                 tab1.select();
                  //onTabSelected(tab1, null); //chama o tratador de eventos para carregar os componentes
              }
              Log.d(TAG,"Operação Realizada.");
          }else{
              Toast.makeText(MainActivity.this, "Erro ao atualizar o contato. Contate o desenvolvedor.", Toast.LENGTH_SHORT).show();
          }
        }
    }


    @Override
    public void onClick(View v) {

    }

}
