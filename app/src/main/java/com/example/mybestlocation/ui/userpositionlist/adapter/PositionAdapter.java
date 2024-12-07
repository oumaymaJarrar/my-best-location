package com.example.mybestlocation.ui.userpositionlist.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybestlocation.DatabaseHelper;
import com.example.mybestlocation.Position;
import com.example.mybestlocation.R;

import java.util.List;

public class PositionAdapter extends RecyclerView.Adapter<PositionAdapter.PositionViewHolder> {

    private List<Position> positionList;
    private Context context;
    private DatabaseHelper databaseHelper;

    public PositionAdapter(Context context, List<Position> positionList) {
        this.context = context;
        this.positionList = positionList;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public PositionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_position, parent, false);
        return new PositionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PositionViewHolder holder, int position) {
        Position pos = positionList.get(position);
        holder.pseudoTextView.setText(pos.getPseudo());
        holder.phoneNumberTextView.setText(pos.getNumber());
        holder.latitudeTextView.setText(pos.getLatitude());
        holder.longitudeTextView.setText(pos.getLongitude());

        // Set actions for edit, delete, and send SMS
        holder.editButton.setOnClickListener(v -> editPosition(pos));
        holder.deleteButton.setOnClickListener(v -> deletePosition(pos.getIdPosition()));
        holder.smsButton.setOnClickListener(v -> sendSMS(pos.getNumber(), pos.getLongitude(), pos.getLatitude()));
    }

    @Override
    public int getItemCount() {
        return positionList.size();
    }

    private void editPosition(Position position) {
        // Create an AlertDialog for editing pseudo and phone number
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);


        // Inflate the custom dialog layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_position, null);
        builder.setView(dialogView);

        // Create the AlertDialog (without default buttons)
        android.app.AlertDialog dialog = builder.create();

        // Find input fields and buttons in the custom layout
        EditText pseudoEditText = dialogView.findViewById(R.id.pseudoEditText);
        EditText phoneEditText = dialogView.findViewById(R.id.phoneNumberEditText);
        EditText latitudeEditText = dialogView.findViewById(R.id.latitudeEditText);
        EditText longitudeEditText = dialogView.findViewById(R.id.longitudeEditText);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button backButton = dialogView.findViewById(R.id.backButton);

        // Pre-fill the fields with current data
        pseudoEditText.setText(position.getPseudo());
        phoneEditText.setText(position.getNumber());
        latitudeEditText.setText(position.getLatitude());
        longitudeEditText.setText(position.getLongitude());

        // Save button logic
        saveButton.setOnClickListener(v -> {
            // Update the position details in the database
            String newPseudo = pseudoEditText.getText().toString().trim();
            String newPhone = phoneEditText.getText().toString().trim();

            // Update in database
            databaseHelper.updatePosition(position.getIdPosition(), newPseudo, newPhone);

            // Update in-memory list and notify RecyclerView
            position.setPseudo(newPseudo);
            position.setNumber(newPhone);
            notifyDataSetChanged();

            // Close the dialog
            dialog.dismiss();
        });

        // Back button logic
        backButton.setOnClickListener(v -> {
            // Close the dialog without saving
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }



    private void deletePosition(int positionId) {
        // Delete the position from the database
        databaseHelper.deletePosition(positionId);
        // Refresh the list after deletion
        positionList.removeIf(position -> position.getIdPosition() == positionId);
        notifyDataSetChanged();
    }

    private void sendSMS(String phoneNumber, String longitude, String latitude) {
        // Send SMS to the phone number
        Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        smsIntent.putExtra("hERE ", "Here is my postion : Longitude," + longitude + "Latitude:" + latitude);
        context.startActivity(smsIntent);
    }

    public static class PositionViewHolder extends RecyclerView.ViewHolder {

        TextView pseudoTextView, phoneNumberTextView, latitudeTextView, longitudeTextView;
        ImageButton editButton, deleteButton, smsButton;
        public PositionViewHolder(@NonNull View itemView) {
            super(itemView);
            pseudoTextView = itemView.findViewById(R.id.pseudoTextView);
            phoneNumberTextView = itemView.findViewById(R.id.phoneNumberTextView);
            latitudeTextView = itemView.findViewById(R.id.latitudeTextView);
            longitudeTextView = itemView.findViewById(R.id.longitudeTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            smsButton = itemView.findViewById(R.id.smsButton);
        }
    }
}
