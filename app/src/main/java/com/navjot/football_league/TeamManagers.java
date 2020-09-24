package com.navjot.football_league;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.navjot.football_league.Model.ItemClickListner;
import com.navjot.football_league.Model.Managers;
import com.navjot.football_league.Prevelant.Prevelant;

public class TeamManagers extends AppCompatActivity {

    private Button addTeamManager;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference managerRef = db.collection("tManager");
    private Query mQuery = managerRef.orderBy("name", Query.Direction.ASCENDING);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_managers);

        getSupportActionBar().setTitle("Team Managers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        addTeamManager = findViewById(R.id.add_team_manager);
        mRecyclerView = findViewById(R.id.managers_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(Prevelant.userType.equals("lManager")){
            addTeamManager.setVisibility(View.VISIBLE);
        }

        addTeamManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        FirestoreRecyclerOptions<Managers> options = new FirestoreRecyclerOptions.Builder<Managers>()
                .setQuery(mQuery,Managers.class).build();

        FirestoreRecyclerAdapter<Managers,ManagersViewHolder> adapter = new FirestoreRecyclerAdapter<Managers, ManagersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ManagersViewHolder holder, int position, @NonNull final Managers model) {
                holder.managerName.setText(model.getName());
                holder.managerPhone.setText(model.getPhone());
                holder.managerTeam.setText(model.getTeamName());

                if(Prevelant.userType.equals("lManager")){
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CharSequence options[] = new CharSequence[]{
                                    "Remove","Cancel"
                            };
                            final AlertDialog.Builder builder = new AlertDialog.Builder(TeamManagers.this);
                            builder.setTitle("Remove Manager");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                   if(i == 0){
                                      managerRef.document(model.getPhone()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(TeamManagers.this, "Manager Removed", Toast.LENGTH_SHORT).show();
                                            }
                                          }
                                      }).addOnFailureListener(new OnFailureListener() {
                                          @Override
                                          public void onFailure(@NonNull Exception e) {
                                              Toast.makeText(TeamManagers.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                          }
                                      });
                                   }
                                   if(i == 1){
                                      dialogInterface.cancel();
                                   }
                                }
                            });
                            builder.show();
                        }
                    });
                }
            }

            @NonNull
            @Override
            public ManagersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.managers_layout,parent,false);
                return new ManagersViewHolder(view);
            }
        };
        //set adapter to recycler view
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public static class ManagersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView managerName,managerPhone,managerTeam;
        private ItemClickListner mItemClickListner;

        public ManagersViewHolder(@NonNull View itemView) {
            super(itemView);
            managerName = itemView.findViewById(R.id.manager_name);
            managerPhone = itemView.findViewById(R.id.manager_phone);
            managerTeam = itemView.findViewById(R.id.manager_team);
        }

        @Override
        public void onClick(View view) {
            mItemClickListner.onClick(view,getAdapterPosition(),false);
        }

        public void setItemClickListner(ItemClickListner itemClickListner) {
            mItemClickListner = itemClickListner;
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}