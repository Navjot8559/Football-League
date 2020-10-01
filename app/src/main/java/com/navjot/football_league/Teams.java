package com.navjot.football_league;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.navjot.football_league.Model.Team;
import com.navjot.football_league.Prevelant.Prevelant;
import com.squareup.picasso.Picasso;

public class Teams extends AppCompatActivity {

    private Button addTeam;
    private RecyclerView mRecyclerView;
    private AlertDialog.Builder builder;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference teamRef = db.collection("Teams");
    private Query mQuery = teamRef.orderBy("teamName", Query.Direction.ASCENDING);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);

        getSupportActionBar().setTitle("Teams");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        addTeam = findViewById(R.id.add_team_btn);
        mRecyclerView = findViewById(R.id.teams_list);
        builder = new AlertDialog.Builder(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(Prevelant.userType.equals("lManager")){
            addTeam.setVisibility(View.VISIBLE);
        }

        addTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AddTeam.class));
            }
        });

        FirestoreRecyclerOptions<Team> options = new FirestoreRecyclerOptions.Builder<Team>()
                .setQuery(mQuery,Team.class).build();

        FirestoreRecyclerAdapter<Team,TeamsViewHolder> adapter = new FirestoreRecyclerAdapter<Team, TeamsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TeamsViewHolder holder, int position, @NonNull final Team model) {
               holder.teamName.setText(model.getTeamName());
               Picasso.get().load(model.getTeamLogo()).into(holder.teamLogo);

               if(Prevelant.userType.equals("lManager")){
                   holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                       @Override
                       public boolean onLongClick(View view) {
                           builder.setMessage("Do you want to remove this Team?");
                           builder.setTitle("Alert");
                           builder.setCancelable(false);
                           builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                   teamRef.document(model.getTeamId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if(task.isSuccessful()){
                                               Toast.makeText(Teams.this, "team removed...", Toast.LENGTH_SHORT).show();
                                           }
                                       }
                                   }).addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception e) {
                                           Toast.makeText(Teams.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                       }
                                   });
                               }
                           });
                           builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                   dialogInterface.cancel();
                               }
                           });
                           AlertDialog alertDialog = builder.create();
                           alertDialog.show();
                           return true;
                       }
                   });
               }

               holder.itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                      Intent intent = new Intent(getApplicationContext(),TeamDetails.class);
                      intent.putExtra("teamId",model.getTeamId());
                      intent.putExtra("teamName",model.getTeamName());
                      startActivity(intent);
                   }
               });
            }

            @NonNull
            @Override
            public TeamsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teams_layout,parent,false);
                TeamsViewHolder holder = new TeamsViewHolder(view);
                return holder;
            }
        };

        //set adapter to recycler view
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public static class TeamsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView teamName;
        public ImageView teamLogo;
        private ItemClickListner mItemClickListner;

        public TeamsViewHolder(@NonNull View itemView) {
            super(itemView);
            teamName = itemView.findViewById(R.id.team_name);
            teamLogo = itemView.findViewById(R.id.team_logo);
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