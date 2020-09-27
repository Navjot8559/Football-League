package com.navjot.football_league;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference managerRef = db.collection("Teams");
    private Query mQuery = managerRef.orderBy("teamName", Query.Direction.ASCENDING);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);

        getSupportActionBar().setTitle("Teams");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        addTeam = findViewById(R.id.add_team_btn);
        mRecyclerView = findViewById(R.id.teams_list);
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
            protected void onBindViewHolder(@NonNull TeamsViewHolder holder, int position, @NonNull Team model) {
               holder.teamName.setText(model.getTeamName());
               Picasso.get().load(model.getTeamLogo()).into(holder.teamLogo);
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