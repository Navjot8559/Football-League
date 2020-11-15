package com.navjot.football_league;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.navjot.football_league.Model.Managers;
import com.navjot.football_league.Model.Player;
import com.navjot.football_league.Model.Team;
import com.navjot.football_league.Prevelant.Prevelant;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeamDetails extends AppCompatActivity {

    private String teamId;
    private ImageView teamLogo;
    private TextView teamName,teamManager;
    private RecyclerView mRecyclerView;
    private Button addPlayer,teamStats;
    private ProgressDialog mProgressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference teamRef = db.collection("Teams");
    private CollectionReference playerRef;
    private Query mQuery;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_details);

        teamId = getIntent().getStringExtra("teamId");
        playerRef = teamRef.document(teamId).collection("Players");
        mQuery = playerRef.orderBy("playerName", Query.Direction.ASCENDING);

        getSupportActionBar().setTitle("Team Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        teamLogo = findViewById(R.id.team_logo);
        teamName = findViewById(R.id.team_name);
        teamManager = findViewById(R.id.team_manager);
        addPlayer = findViewById(R.id.add_player_btn);
        teamStats = findViewById(R.id.view_stats_btn);
        builder = new AlertDialog.Builder(this);
        mProgressDialog = new ProgressDialog(this);
        mRecyclerView = findViewById(R.id.players_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(Prevelant.userType.equals("lManager") || Prevelant.userPhone.equals(teamId)){
            addPlayer.setVisibility(View.VISIBLE);
        }

        addPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddPlayer.class);
                intent.putExtra("teamId",teamId);
                startActivity(intent);
            }
        });

        teamStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Statistics.class);
                intent.putExtra("teamId",teamId);
                startActivity(intent);
            }
        });

        fetchTeamDetails();

        FirestoreRecyclerOptions<Player> options = new FirestoreRecyclerOptions.Builder<Player>()
                .setQuery(mQuery,Player.class).build();

        FirestoreRecyclerAdapter<Player,PlayerViewHolder> adapter = new FirestoreRecyclerAdapter<Player, PlayerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PlayerViewHolder holder, int position, @NonNull final Player model) {
                holder.playerName.setText(model.getPlayerName());
                holder.playerPosition.setText(model.getPlayerPosition());
                Picasso.get().load(model.getPlayerImage()).into(holder.playerImage);

                if(Prevelant.userType.equals("lManager") || Prevelant.userPhone.equals(teamId)){
                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            builder.setMessage("Do you want to remove this Player?");
                            builder.setTitle("Alert");
                            builder.setCancelable(false);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mProgressDialog.setTitle("Removing");
                                    mProgressDialog.setMessage("please wait...");
                                    mProgressDialog.setCanceledOnTouchOutside(false);
                                    mProgressDialog.show();

                                    playerRef.document(model.getPlayerId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                teamRef.document(teamId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            DocumentSnapshot documentSnapshot = task.getResult();
                                                            Team team = documentSnapshot.toObject(Team.class);
                                                            int teamSize = team.getTeamSize();
                                                            teamSize--;
                                                            teamRef.document(teamId).update("teamSize",teamSize);
                                                            mProgressDialog.dismiss();
                                                            Toast.makeText(TeamDetails.this,"player removed...", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        mProgressDialog.dismiss();
                                                        Toast.makeText(TeamDetails.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            mProgressDialog.dismiss();
                                            Toast.makeText(TeamDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            }

            @NonNull
            @Override
            public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_layout,parent,false);
                return new PlayerViewHolder(view);
            }
        };
        //set adapter to recycler view
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void fetchTeamDetails() {
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("please wait...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        teamRef.document(teamId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    mProgressDialog.dismiss();
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        Team team = documentSnapshot.toObject(Team.class);
                        Picasso.get().load(team.getTeamLogo()).into(teamLogo);
                        teamName.setText(team.getTeamName());
                        teamManager.setText("Managed By : " + team.getManagerName());
                    }else{
                        Toast.makeText(TeamDetails.this, "team not exists...", Toast.LENGTH_SHORT).show();
                        addPlayer.setVisibility(View.GONE);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressDialog.dismiss();
                Toast.makeText(TeamDetails.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder{

        public TextView playerName,playerPosition;
        public CircleImageView playerImage;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.player_name);
            playerPosition = itemView.findViewById(R.id.player_position);
            playerImage = itemView.findViewById(R.id.player_image);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Prevelant.userType.equals("lManager") || Prevelant.userPhone.equals(teamId)){
            addPlayer.setVisibility(View.VISIBLE);
        }
    }
}