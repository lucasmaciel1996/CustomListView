package com.example.maciel.meuprimeiroapp.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maciel.meuprimeiroapp.models.Cliente;

import java.util.List;

public class ListAdapter extends ArrayAdapter<Cliente> {

  public ListAdapter(Context context, List<Cliente> clientes){

      super(context,0,clientes);
  }
  public View getView(int position, View convertview, ViewGroup parent){
      //Ler irformações que usuario clicou
      Cliente cliente =getItem(position);

      //Checa se esta utulizando padrao ViewHolder
      if(convertview==null){
        convertview= LayoutInflater.from(getContext()).inflate(android.R.layout.adapter_layout,parent,false);
      }
      //mapeia view do layout do adaptador
      TextView tvNome = (TextView) convertview.findViewById(R.id.tv_item_nome);
      TextView tvTelfone = (TextView) convertview.findViewById(R.id.tv_item_telefone);
      TextView tvSexo = (TextView) convertview.findViewById(R.id.tv_item_sexo);
      TextView tvCpf = (TextView) convertview.findViewById(R.id.tv_item_cpf);

      // popula as views
      tvNome.setText(cliente.getNome());
      tvTelfone.setText(cliente.getTelefone());
      tvSexo.setText(cliente.getSexo());
      tvCpf.setText(cliente.getCpf());

      //maapeia view da imagem
      ImageView img_imagem=(ImageView)convertview.findViewById(R.id.imv_item);
      if(cliente.getImagem()!=null){
          //convert byte[] para bitmap
          Bitmap bitmap = BitmapFactory.decodeByteArray(cliente.getImagem(),,cliente.imagem.length);
          //carega imgem no imagemview da Listview
           img_imagem.setImageBitmap(bitmap);
      }else{
          //carega imagem padrao(se nao houver imagem no cursor)
          img_imagem.setImageResource(android.R.drawable.foto_sombra);
      }
      //retorna view completa para rendenizar no screen
      return convertview;
  }
}
