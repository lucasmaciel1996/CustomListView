package com.example.maciel.meuprimeiroapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.maciel.meuprimeiroapp.models.Cliente;

import java.util.ArrayList;
import java.util.List;

/*Create Lucas Maciel 14/04/2018*/
public class ClienteDAO extends SQLiteOpenHelper {
    //configuração para o banco
    private static String TAG="clientes_bd";
    private static String NOME_BD="clientes.sqlite";
    private static int VERSAO=1;
    private static ClienteDAO clienteDAO=null;// Singleton


    public ClienteDAO(Context context){
        super(context, NOME_BD, null, VERSAO);
    }

    public static ClienteDAO getinstance(Context context){
      if(clienteDAO ==null){
        clienteDAO= new ClienteDAO(context);
        return clienteDAO;
      }else{
          return clienteDAO;
      }
    }
     /*Inicio do ciclo de vida do DB*/

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql="create table if not exists cliente"+
                "( _id interger primary key autoincrement,"+
                   "nome text,"+
                   "cpf text,"+
                   "sexo text,"+
                   "telefone text,"+
                   "imagem blob);";
        Log.d(TAG,"Criando Tabeta Aguarde");
        sqLiteDatabase.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Caso mude a versão do banco de dados, podemos executar um SQL aqui
        // exemplo:
        Log.d("aulas", "Upgrade da versão " + oldVersion + " para "
                + newVersion + ", destruindo tudo.");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS cliente");
        onCreate(sqLiteDatabase); // chama onCreate e recria o banco de dados
        Log.i("aulas", "Executou o script de upgrade da tabela contatos.");
    }
    /*Metodos CRUD*/
  public long save(Cliente cliente){
      SQLiteDatabase db =getWritableDatabase();//Abre Conexao com banco
      try {
          //Dupla com Chave, valor
          ContentValues values = new ContentValues();
          values.put("nome", cliente.getNome());
          values.put("cpf", cliente.getCpf());
          values.put("sexo", cliente.getSexo());
          values.put("telefone", cliente.getTelefone());
          values.put("imagem", cliente.getImagem());

          //Inicio da operação
          if (cliente.getId() == null) {
              //insert no DB
              return db.insert("cliente", null, values);
          } else {
              //update no DB
              values.put("_id", cliente.getId());
              return db.update("clinte", values, "_id=" + cliente.getId(), null);
          }
      }catch(SQLException e){
          e.printStackTrace();
      }finally {
          db.close();
      }
     return 0;
  }
  //delete cliente
    public long delete(Cliente cliente){
      SQLiteDatabase db= getWritableDatabase();//Abre conecao com BD
      try{
        return db.delete("cliente","_id=?",new String[]{String.valueOf(cliente.getId())});

      }finally {
        db.close();
      }
    }
    //Lista todos os registro do BD
    public List<Cliente>getAll(){
      SQLiteDatabase db= getWritableDatabase();
      try{
          //select * from cliente
          return toList(db.rawQuery("SELECT * FROM cliente",null));
      }finally {
          db.close();
      }
    }

    //Lista todos o registro por nome do BD
    public List<Cliente>getByname(String nome){
        SQLiteDatabase db= getWritableDatabase();
        try{
            //select * from cliente
            return toList(db.rawQuery("SELECT * FROM cliente where nome LIKE'"+nome+"%'",null));
        }finally {
            db.close();
        }
    }
    //Convert do curso para Lista
    private List<Cliente>toList(Cursor c){
      List<Cliente>clientes= new ArrayList<Cliente>();

      if(c.moveToFirst()){
          do{
              Cliente cliente = new Cliente();

             //recupera valor
              cliente.setId(c.getLong(c.getColumnIndex("_id")));
              cliente.setNome(c.getString(c.getColumnIndex("nome")));
              cliente.setCpf(c.getString(c.getColumnIndex("cpf")));
              cliente.setSexo(c.getString(c.getColumnIndex("sexo")));
              cliente.setTelefone(c.getString(c.getColumnIndex("telefone")));
              cliente.setImagem(c.getBlob(c.getColumnIndex("imagem")));

              clientes.add(cliente);
          }while(c.moveToNext());
      }
      return clientes;
    }
}
