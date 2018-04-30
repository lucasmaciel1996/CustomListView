package com.example.maciel.meuprimeiroapp.dao;

import android.content.ContentResolver;
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
import java.util.zip.CheckedOutputStream;

/*Create Lucas Maciel 14/04/2018*/
public class ClienteDAO extends SQLiteOpenHelper {
    //configuração para o banco
    private static String TAG = "clientesbd";
    private static String NOME_BD = "clientes.sqlite";
    private static int VERSAO = 1;
    private static ClienteDAO clienteDAO;// Singleton


    private ClienteDAO(Context context) {
        super(context, NOME_BD, null, VERSAO);
        getWritableDatabase();

    }

    public static ClienteDAO getInstance(Context context) {

        if (clienteDAO == null) {
            clienteDAO = new ClienteDAO(context);
            return clienteDAO;
        }

        return clienteDAO;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists cliente"+
                "( _id integer primary key autoincrement, "+
                "nome text, "+
                "cpf text, "+
                "telefone text, "+
                "imagem blob);";

        Log.d(TAG,"Criando Tabela Aguarde...");
        db.execSQL(sql);
        Log.d(TAG,"Criando Tabela criada com Sucesso ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long save(Cliente cliente){
        SQLiteDatabase sqLiteDatabase= getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put("nome",cliente.getNome());
            values.put("cpf",cliente.getCpf());
            values.put("telefone",cliente.getTelefone());
            values.put("imagem",cliente.getImagem());

            if(cliente.getId()==0){
               return sqLiteDatabase.insert("cliente",null,values);
            }
            else{
                values.put("_id",cliente.getId());
                return sqLiteDatabase.update("cliente",values,"_id="+cliente.getId(),null);

            }

        }finally {
            sqLiteDatabase.close();
        }
        //return 0;
    }
    public List<Cliente>getAll(){
        SQLiteDatabase sqLiteDatabase= getWritableDatabase();
        try{
            //retorna lista com dados do banco
            return toList(sqLiteDatabase.rawQuery("select * from cliente",null));
        }finally {
            sqLiteDatabase.close();
        }
    }
    public long delete(Cliente cliente){
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        try{
            return sqLiteDatabase.delete("cliente","_id=?",new String[]{String.valueOf(cliente.getId())});

        }finally {

        }
    }

    public List<Cliente> toList(Cursor cursor){

        List<Cliente>clientes= new ArrayList<>();

        if(cursor.moveToFirst()){
            do{
                Cliente cliente= new Cliente();
                cliente.setId(cursor.getLong(cursor.getColumnIndex("_id")));
                cliente.setNome(cursor.getString(cursor.getColumnIndex("nome")));
                cliente.setCpf(cursor.getString(cursor.getColumnIndex("cpf")));
                cliente.setTelefone(cursor.getString(cursor.getColumnIndex("telefone")));
                cliente.setImagem(cursor.getBlob(cursor.getColumnIndex("imagem")));

                clientes.add(cliente);

            }while (cursor.moveToNext());
        }
        return clientes;
    }

}

