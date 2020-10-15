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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.navjot.football_league.Model.Player;
import com.navjot.football_league.Model.Team;
import com.navjot.football_league.Prevelant.Prevelant;
import com.squareup.picasso.Picasso;

public class MatchDetail extends AppCompatActivity {

    private String team1,team2,team1Id,team2Id,team1Logo,team2Logo,date,time,scheduleId;
    private Button declareResult;
    private TextView tvTeam1,tvTeam2,tvDate,tvTime;
    private ImageView ivTeam1Logo,ivTeam2Logo;
    private RecyclerView team1List,team2List;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference teamRef = db.collection("Teams");
    private CollectionReference player1Ref,player2Ref;
    private Query mQuery1,mQuery2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);

        team1 = getIntent().getStringExtra("team1");
        team2 = getIntent().getStringExtra("team2");
        team1Id = getIntent().getStringExtra("team1Id");
        team2Id = getIntent().getStringExtra("team2Id");
        team1Logo = getIntent().getStringExtra("team1Logo");
        team2Logo = getIntent().getStringExtra("team2Logo");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        scheduleId = getIntent().getStringExtra("scheduleId");

        getSupportActionBar().setTitle("Match Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        declareResult = findViewById(R.id.declare_result_btn);
        tvTeam1 = findViewById(R.id.team1_name);
        tvTeam2 = findViewById(R.id.team2_name);
        tvDate = findViewById(R.id.match_date);
        tvTime = findViewById(R.id.match_time);
        ivTeam1Logo = findViewById(R.id.team1_logo);
        ivTeam2Logo = findViewById(R.id.team2_logo);
        team1List = findViewById(R.id.team1_player_list);
        team2List = findViewById(R.id.team2_player_list);
        team1List.setHasFixedSize(true);
        team1List.setLayoutManager(new LinearLayoutManager(this));
        team2List.setHasFixedSize(true);
        team2List.setLayoutManager(new LinearLayoutManager(this));

        player1Ref = teamRef.document(team1Id).collection("Players");
        player2Ref = teamRef.document(team2Id).collection("Players");
        mQuery1 = player1Ref.orderBy("playerName", Query.Direction.ASCENDING);
        mQuery2 = player2Ref.orderBy("playerName", Query.Direction.ASCENDING);

        if(Prevelant.userType.equals("lManager")){
            declareResult.setVisibility(View.VISIBLE);
        }

        tvTeam1.setText(team1);
        tvTeam2.setText(team2);
        tvDate.setText(date);
        tvTime.setText(time);
        Picasso.get().load(team1Logo).into(ivTeam1Logo);
        Picasso.get().load(team2Logo).into(ivTeam2Logo);

        fetchTeamPlayers(mQuery1,team1List);
        fetchTeamPlayers(mQuery2,team2List);

    }

    private void fetchTeamPlayers(Query mQuery,RecyclerView mRecyclerView) {
        FirestoreRecyclerOptions<Player> options = new FirestoreRecyclerOptions.Builder<Player>()
                .setQuery(mQuery,Player.class).build();

        FirestoreRecyclerAdapter<Player, TeamDetails.PlayerViewHolder> adapter = new FirestoreRecyclerAdapter<Player, TeamDetails.PlayerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final TeamDetails.PlayerViewHolder holder, int position, @NonNull final Player model) {
                holder.playerName.setText(model.getPlayerName());
                holder.playerPosition.setText(model.getPlayerPosition());
                Picasso.get().load(model.getPlayerImage()).into(holder.playerImage);

            }
            @NonNull
            @Override
            public TeamDetails.PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_layout,parent,false);
                return new TeamDetails.PlayerViewHolder(view);
            }
        };
        //set adapter to recycler view
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
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
        if(Prevelant.userType.equals("lManager")){
            declareResult.setVisibility(View.VISIBLE);
        }
    }
}