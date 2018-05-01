package com.example.maciel.meuprimeiroapp.control;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.maciel.meuprimeiroapp.adapter.ListViewAdapter;
import com.example.maciel.meuprimeiroapp.R;
import com.example.maciel.meuprimeiroapp.dao.ClienteDAO;
import com.example.maciel.meuprimeiroapp.models.Cliente;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener, ActionBar.TabListener{
    //instacia global
    private static final String TAG="clientesbd";
    private ClienteDAO clienteDAO;
    private static Cliente cliente = null;
    private EditText edt_nome;
    private EditText edt_cpf;
    private EditText edt_telefone;
    private ImageView imv_fotoperfil;
    private ListView lv_clientes;
    private Dialog mydialog;

    private static final String GETALL = "getAll";
    private static final String GETBYNAME = "getbyname";
    private static final String SAVE = "save";
    private static final String DELETE = "delete";
    private String nameFind = "";
    ActionBar.Tab tab0,tab1; //uma das abas da activity, quando em smartphone
    private byte[]imagem=null; //imagem do cliente

    //ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         //intacia dos obejtos
         cliente =  new Cliente();
         //instacia objeto de acesso ao BD
         clienteDAO= ClienteDAO.getInstance(this);

         if(isTablet(this)){
             setContentView(R.layout.activity_tablet);
             getSupportActionBar().setTitle("Colocar texto");//insere titulo para tela

            //mapeia os componentes da UI
             edt_nome = (EditText) findViewById(R.id.editText_nome);
             edt_cpf = (EditText)findViewById(R.id.editText_cpf);
             edt_telefone =(EditText) findViewById(R.id.editText_telefone);
             imv_fotoperfil = (ImageView) findViewById(R.id.imv_foto);
             lv_clientes=(ListView)findViewById(R.id.listview);
             //mapeia popup
             mydialog=new Dialog(this);

             //Formatando maskaras dos campos
             SimpleMaskFormatter smf_cpf= new SimpleMaskFormatter("NNN.NNN.NNN-NN");
             MaskTextWatcher mtw= new MaskTextWatcher(edt_cpf,smf_cpf);
             edt_cpf.addTextChangedListener(mtw);

             SimpleMaskFormatter smf_phone= new SimpleMaskFormatter("(NN)N-NNNN-NNNN");
             MaskTextWatcher mtw1= new MaskTextWatcher(edt_telefone,smf_phone);
             edt_telefone.addTextChangedListener(mtw1);


             lv_clientes.setOnItemClickListener(this);//adiciona lista de ouvintes

             new Task().execute(GETALL);//executa a operação GET em segundo plano

         }else{
             //preapara a actionbar
             getSupportActionBar().setTitle("Texto");//seta um titulo para aba
             getSupportActionBar().setNavigationMode(getSupportActionBar().NAVIGATION_MODE_TABS);//defini mode de navegacao

             /*Cria as abas e adiciona acitonbar */
             //tab0
             tab0 = getSupportActionBar().newTab().setText("Lista de Clientes");
             tab0.setTabListener(this);
             getSupportActionBar().addTab(tab0);

             //tab1
             tab1=getSupportActionBar().newTab().setText("Clientes");
             tab1.setTabListener(this);
             getSupportActionBar().addTab(tab1);
         }


    }
    /*tra enventos da abas */
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        switch (tab.getPosition()){
            case 0:
                //mapeia os componente da UI
                setContentView(R.layout.activity_smartphone_list);

                //insere titulo na acitonbar
                getSupportActionBar().setTitle("Clientes");

                //mapeia os componeste da UI
                lv_clientes=(ListView)findViewById(R.id.listview);
                lv_clientes.setOnItemClickListener(this);

                new Task().execute(GETALL);//executa operacao em segundo plano
            break;
            case 1:
                //mapeia os componente da UI
                setContentView(R.layout.activity_smartphone_inputs);

                //insere titulo na actionbar
                 getSupportActionBar().setTitle("Edição");

                //mapeia os componentes de activity_inputs.xml
                edt_nome = (EditText) findViewById(R.id.editText_nome);
                edt_cpf = (EditText)findViewById(R.id.editText_cpf);
                edt_telefone =(EditText) findViewById(R.id.editText_telefone);
                imv_fotoperfil = (ImageView) findViewById(R.id.imv_foto);
                mydialog=new Dialog(this);

                //Formatando maskaras dos campos
                SimpleMaskFormatter smf_cpf= new SimpleMaskFormatter("NNN.NNN.NNN-NN");
                MaskTextWatcher mtw= new MaskTextWatcher(edt_cpf,smf_cpf);
                edt_cpf.addTextChangedListener(mtw);

                SimpleMaskFormatter smf_phone= new SimpleMaskFormatter("(NN)N-NNNN-NNNN");
                MaskTextWatcher mtw1= new MaskTextWatcher(edt_telefone,smf_phone);
                edt_telefone.addTextChangedListener(mtw1);

            break;
        }

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }


    //infla o menu, mapeia e prepara a SearchView
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //infra menu
        getMenuInflater().inflate(R.menu.menu_actionbar,menu);

        //mapeia e insere o handler a fila de ouvintes da SearchView
        SearchView searchview= (SearchView) menu.findItem(R.id.menu_item_pesquisar).getActionView();
        searchview.setQueryHint("Digite um nome");
        searchview.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    //trata eventos dos itens do menu da ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //verifica se um tablet se for diferente leciona tab1
        if(!isTablet(this)){
            //seleciona a aba 1
            tab1.select();
        }

        switch (item.getItemId()){
            case R.id.menu_item_salvar:
                //Toast.makeText(this, "Salvar", Toast.LENGTH_SHORT).show();
                if(!edt_nome.getText().toString().isEmpty()&&
                !edt_cpf.getText().toString().isEmpty()&&
                !edt_telefone.getText().toString().isEmpty())
                {
                  //entrou no if

                    /*achar logica para "null", objeto esta como atributo "long"
                    if(cliente.getId()==null){ //se e inclusao
                        cliente= new Cliente();//apaga dados antigos
                    }*/

                    cliente.setNome(edt_nome.getText().toString());
                    cliente.setCpf(edt_cpf.getText().toString());
                    cliente.setTelefone(edt_telefone.getText().toString());

                    //tona Imageview para byte
                    imv_fotoperfil.buildDrawingCache();
                    Bitmap bitmap=imv_fotoperfil.getDrawingCache();
                     //esta perdendo resolucao da imagem
                    //bitmap= Bitmap.createScaledBitmap(bitmap,1600,1200,true);//reduz e aplica um filtro na imagem
                    byte[]img= getBitmapAsByteArray(bitmap);//converte para um fluxo de bytes
                    imagem = img; //coloca a imagem no objeto imagem (um array de bytes (byte[])


                    cliente.setImagem(imagem);

                    Log.d(TAG,"Cliente que sera salvo "+cliente.toString());

                    new Task().execute(SAVE);//executa a operação CREATE em segundo plano
                    new Task().execute(GETALL);//executa a operação GET em segundo plano  para atualizar a ListView

                }else{
                    Toast.makeText(this, "Preebcha todos o campos", Toast.LENGTH_SHORT).show();
                }


            break;

            case R.id.menu_item_excluir:
                if(cliente != null && !edt_nome.getText().toString().isEmpty() &&
                        !edt_cpf.getText().toString().isEmpty() &&
                        !edt_telefone.getText().toString().isEmpty()){
                    new Task().execute(DELETE); //executa a operação DELETE em segundo plano
                    new Task().execute(GETALL); //executa a operação GET em segundo plano para atualizar a ListView
                }else{
                    Toast.makeText(MainActivity.this, "Selecione um cliente na lista.", Toast.LENGTH_SHORT).show();
                }
            break;

            case R.id.menu_item_cencelar:
                Toast.makeText(this, "Cancelar", Toast.LENGTH_SHORT).show();
                limparForm();
            break;

            case  R.id.menu_item_develope:
               // Toast.makeText(this, "Developer", Toast.LENGTH_SHORT).show();
              //chama metodo para mostrar Popup
               ShowPoup();

            break;

        }
        //return true;
        return super.onOptionsItemSelected(item);
    }
    /*trata eventos da SearchView*/
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
        if(newText.equals("")){
            new Task().execute(GETALL);
        }else{
            new Task().execute(GETBYNAME); //executa a operação GET em segundo plano para atualizar a ListView
            nameFind = newText; //armazena em uma variável global para uso na task
        }

        return true;
    }
    //Limpa Formulario
    private void limparForm(){
        edt_nome.setText(null);
        edt_cpf.setText(null);
        edt_telefone.setText(null);
        edt_nome.requestFocus();
        imv_fotoperfil.setImageResource(R.drawable.foto_sombra);
        imagem=null;
        cliente = new Cliente();//apaga dados antigos

    }

    //trata o evento onClick de cada item da lista
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if(!isTablet(this)){
            tab1.select();//seleciona a aba 1
        }

        cliente=(Cliente) adapterView.getAdapter().getItem(position);//obtem dado cliente
        Log.d(TAG, cliente.toString());

        //carrega nos campos de input
        edt_nome.setText(cliente.getNome());
        edt_cpf.setText(cliente.getCpf());
        edt_telefone.setText(cliente.getTelefone());
        edt_nome.requestFocus();

        //validação para verificar se imagem não esta null
        if(cliente.getImagem() !=null){
            imv_fotoperfil.setImageBitmap(BitmapFactory.decodeByteArray(cliente.getImagem(),0,cliente.getImagem().length));
        }

    }

    /**
     * Método para carregar os dados do banco de dados para a ListView.
     */

    public void carregarListView(List<Cliente>clientes){
        //cria um objeto da classe ListAdapter, um adaptador List -> ListView
        ListViewAdapter adapter = new ListViewAdapter(this,clientes);
        //associa o adaptador a ListView
        lv_clientes.setAdapter(adapter);
    }

    /*
        Métodos para carregamento e tratamento da imagem da ImageView.
     */

    /**
     * Método que solicita ao sistema operacional a inicialização de uma Activity que saiba obter e devolver imagens.
     * @param view
     */
    public void selecionarImagem(View view){
        //cria uma Intent
        //primeiro argumento: ação ACTION_PICK "escolha um item a partir dos dados e retorne o seu URI"
        //segundo argumento: refina a ação para arquivos de imagem, retornando um URI
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //inicializa uma Activity. Neste caso, uma que forneca acesso a galeria de imagens do dispositivo.
        startActivityForResult(Intent.createChooser(intent,"Selecione uma imagem"),0);
    }

    /**
     * Método que recebe o retorno da Activity de galeria de imagens.
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Uri arquivoUri =data.getData();
            Log.d(TAG,"Uri da imagem"+arquivoUri);
            imv_fotoperfil.setImageURI(arquivoUri);
            //transforma em bytearray
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(arquivoUri));
                bitmap= Bitmap.createScaledBitmap(bitmap,200,200,true);//reduz e aplica um filtro na imagem
                byte[]img= getBitmapAsByteArray(bitmap);//converte para um fluxo de bytes
                imagem = img; //coloca a imagem no objeto imagem (um array de bytes (byte[])

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
    /**
     * Converte um Bitmap em um array de bytes (bytes[])
     * @param bitmap
     * @return byte[]
     */
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); //criam um stream para ByteArray
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream); //comprime a imagem
        return outputStream.toByteArray(); //retorna a imagem como um Array de Bytes (byte[])
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
       Abre PopUp

     */
    public void ShowPoup(){
        TextView txt_close;
        mydialog.setContentView(R.layout.custompopup);
        txt_close=mydialog.findViewById(R.id.txt_fechar);
        txt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydialog.dismiss();
            }
        });
        mydialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mydialog.show();

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
                //salva e chama listview no smartphone
                if(!isTablet(MainActivity.this)) {
                    tab0.select(); //seleciona a aba 0
                    //onTabSelected(tab1, null); //chama o tratador de eventos para carregar os componentes
                }
                Log.d(TAG, "Operação realizada.");
            }else{
                Toast.makeText(MainActivity.this, "Erro ao atualizar o contato. Contate o desenvolvedor.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
