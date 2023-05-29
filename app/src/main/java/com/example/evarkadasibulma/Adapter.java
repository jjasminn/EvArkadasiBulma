package com.example.evarkadasibulma;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.UserViewHolder> {

    private List<User> userList;

    public Adapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new UserViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText(user.getName());
        holder.statusTextView.setText(user.getStatus());

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView itemImage;
        TextView nameTextView, statusTextView;

        UserViewHolder(View view) {
            super(view);
            itemImage = view.findViewById(R.id.itemImage);
            nameTextView = view.findViewById(R.id.fullName);
            statusTextView = view.findViewById(R.id.status);
            view.setOnClickListener((View.OnClickListener) this);
        }
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            User selectedUser = userList.get(position);

            Intent intent = new Intent(v.getContext(), Profile.class);
            intent.putExtra("user", selectedUser);
            v.getContext().startActivity(intent);
        }

    }
}
