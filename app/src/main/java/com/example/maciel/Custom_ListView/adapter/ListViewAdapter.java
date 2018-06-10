package com.example.maciel.Custom_ListView.adapter;

import
        android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maciel.Custom_ListView.models.Cliente;
import com.example.maciel.meuprimeiroapp.R;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<Cliente> {
    private static String TAG="clientesbd";

    public ListViewAdapter(Context context, List<Cliente> clientes) {
        super(context,0,clientes);

    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        Cliente cliente = getItem(position);

         convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_layout,parent,false);

        //Mapeia os textview
        TextView txtNome=(TextView)convertView.findViewById(R.id.tv_item_nome);
        TextView txtCpf=(TextView)convertView.findViewById(R.id.tv_item_cpf);
        TextView txtTelefone=(TextView)convertView.findViewById(R.id.tv_item_telefone);

        //Seta valores nos textview
        txtNome.setText(cliente.getNome());
        txtCpf.setText(cliente.getCpf());
        txtTelefone.setText(cliente.getTelefone());

        //setar imagem
        ImageView imv_imagem=convertView.findViewById(R.id.imv_item);

        if(cliente.getImagem() !=null){
            Bitmap bitmap= BitmapFactory.decodeByteArray(cliente.getImagem(),0,cliente.getImagem().length);
            imv_imagem.setImageBitmap(bitmap);

        }else{
            imv_imagem.setImageResource(R.drawable.foto_sombra);
        }

        // validacao para estava listando
       // Log.d(TAG,"Adapater View"+" "+cliente.getId()+" "+cliente.getNome()+" "+cliente.getCpf()+" "+cliente.getTelefone()+" "+cliente.getImagem());

        return convertView;
    }
}
