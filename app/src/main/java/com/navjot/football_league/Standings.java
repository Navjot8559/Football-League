package com.navjot.football_league;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.navjot.football_league.Model.ItemClickListner;
import com.navjot.football_league.Model.Team;

public class Standings extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference teamRef = db.collection("Teams");
    private Query mQuery = teamRef.orderBy("points", Query.Direction.DESCENDING);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standings);

        getSupportActionBar().setTitle("Standings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecyclerView = findViewById(R.id.standings_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirestoreRecyclerOptions<Team> options = new FirestoreRecyclerOptions.Builder<Team>()
                .setQuery(mQuery,Team.class).build();

        FirestoreRecyclerAdapter<Team,StandingsViewHolder> adapter = new FirestoreRecyclerAdapter<Team, StandingsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull StandingsViewHolder holder, int position, @NonNull Team model) {
                holder.teamName.setText(model.getTeamName());
                holder.win.setText(String.valueOf(model.getWins()));
                holder.lose.setText(String.valueOf(model.getLoses()));
                holder.tie.setText(String.valueOf(model.getTies()));
                holder.points.setText(String.valueOf(model.getPoints()));
            }

            @NonNull
            @Override
            public StandingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.standing_layout,parent,false);
                StandingsViewHolder holder = new StandingsViewHolder(view);
                return holder;
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class StandingsViewHolder extends RecyclerView.ViewHolder{

        public TextView teamName,win,lose,tie,points;

        public StandingsViewHolder(@NonNull View itemView) {
            super(itemView);
            teamName = itemView.findViewById(R.id.team);
            win = itemView.findViewById(R.id.win);
            lose = itemView.findViewById(R.id.lose);
            tie = itemView.findViewById(R.id.tie);
            points = itemView.findViewById(R.id.points);
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