package com.example.lerningfiarbase;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class contactsAdepter extends RecyclerView.Adapter<contactsAdepter.viewHolder> {

    ArrayList<ModelContacts> contacts;
    Context context;

    public contactsAdepter(ArrayList<ModelContacts> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.layout_contacts,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        ModelContacts modelContacts = contacts.get(position);

        holder.name.setText(modelContacts.getName());
        holder.phone.setText(modelContacts.getPhoneNumber());
        Log.d("xxxxx", "onBindViewHolder: "+contacts.size());

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView name,phone;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.txt_name);
            phone = itemView.findViewById(R.id.txt_phone);
        }
    }
}
