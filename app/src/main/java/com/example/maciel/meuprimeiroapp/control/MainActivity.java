package com.example.maciel.meuprimeiroapp.control;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.maciel.meuprimeiroapp.dao.ClienteDAO;
import com.example.maciel.meuprimeiroapp.models.Cliente;

import java.util.ArrayList;

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

     //falta terminal 15/04



    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
