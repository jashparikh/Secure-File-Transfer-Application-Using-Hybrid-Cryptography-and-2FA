package com.example.filetransfer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filetransfer.R;
import com.example.filetransfer.model.Receive;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;

public class ReceiveAdapter extends FirebaseRecyclerAdapter<Receive, ReceiveAdapter.ReceiveViewHolder> {
    public ReceiverInterface receiverInterface;

    @Override
    public void updateOptions(@NonNull FirebaseRecyclerOptions<Receive> options) {
        super.updateOptions(options);
    }


    public ReceiveAdapter(@NonNull FirebaseRecyclerOptions<Receive> options, ReceiverInterface receiverInterface) {
        super(options);
        this.receiverInterface = receiverInterface;
    }


    @Override
    protected void onBindViewHolder(@NonNull ReceiveViewHolder holder, int position, @NonNull Receive model) {
        holder.senderName.setText(model.getSenderName());
        holder.fileName.setText(model.getFileName());
        holder.time.setText(model.getTime());
        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiverInterface.onClick(getRef(position), v, model);
            }
        });
    }

    @NonNull
    @Override
    public ReceiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receive_item, parent, false);
        return new ReceiveViewHolder(view);
    }


    static class ReceiveViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        ImageButton downloadButton;
        TextView senderName;
        TextView time;

        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.fileName);
            downloadButton = itemView.findViewById(R.id.downloadBtn);
            senderName = itemView.findViewById(R.id.senderName);
            time = itemView.findViewById(R.id.time);

        }
    }

    public interface ReceiverInterface {
        void onClick(DatabaseReference ref, View v, Receive model);
    }
}