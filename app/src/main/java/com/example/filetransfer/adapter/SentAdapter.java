package com.example.filetransfer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filetransfer.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;


public class SentAdapter extends FirebaseRecyclerAdapter<String, SentAdapter.StringViewHolder> {
    public SentInterface sentInterface;


    @Override
    public void updateOptions(@NonNull FirebaseRecyclerOptions<String> options) {
        super.updateOptions(options);
    }

    public SentAdapter(@NonNull FirebaseRecyclerOptions<String> options,SentInterface sentInterface) {
        super(options);
        this.sentInterface = sentInterface;
    }

    @Override
    protected void onBindViewHolder(@NonNull StringViewHolder holder, int position, @NonNull String model) {
        holder.email.setText(model);
        holder.reStringBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentInterface.onClick(v,model);
            }
        });
    }

    @NonNull
    @Override
    public DatabaseReference getRef(int position) {
        return super.getRef(position);
    }


    @NonNull
    @Override
    public StringViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_item, parent, false);
        return new StringViewHolder(view);
    }


    static class StringViewHolder extends RecyclerView.ViewHolder {
        Button reStringBtn;
        TextView email;

        public StringViewHolder(@NonNull View itemView) {
            super(itemView);
            reStringBtn = itemView.findViewById(R.id.reSentBtn);
            email = itemView.findViewById(R.id.email);

        }

    }

    public interface SentInterface {
        void onClick(View v, String model);
    }
}
