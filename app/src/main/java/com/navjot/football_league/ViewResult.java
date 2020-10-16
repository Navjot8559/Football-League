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
import com.navjot.football_league.Model.Result;
import com.navjot.football_league.Model.Schedule;
import com.squareup.picasso.Picasso;

public class ViewResult extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference resultRef = db.collection("Results");
    private Query mQuery = resultRef.orderBy("matchDate", Query.Direction.ASCENDING);
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        getSupportActionBar().setTitle("Match Results");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mRecyclerView = findViewById(R.id.result_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirestoreRecyclerOptions<Result> options = new FirestoreRecyclerOptions.Builder<Result>()
                .setQuery(mQuery,Result.class).build();

        FirestoreRecyclerAdapter<Result,ResultViewHolder> adapter = new FirestoreRecyclerAdapter<Result, ResultViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ResultViewHolder holder, int position, @NonNull Result model) {
                int score1 = model.getTeam1Score();
                int score2 = model.getTeam2Score();

                holder.team1.setText(model.getTeam1());
                holder.team2.setText(model.getTeam2());
                holder.date.setText(model.getMatchDate());
                holder.time.setText(model.getMatchTime());
                holder.score.setText(score1 + "  VS  " + score2);
                Picasso.get().load(model.getTeam1Logo()).into(holder.team1Logo);
                Picasso.get().load(model.getTeam2Logo()).into(holder.team2Logo);

                if(score1>score2){
                    holder.result.setText(model.getTeam1() + " won by " +  String.valueOf(score1-score2) + " goals.");
                }else if(score1<score2){
                    holder.result.setText(model.getTeam2() + " won by " + String.valueOf(score2-score1) + " goals.");
                }else{
                    holder.result.setText("Match Tie");
                }

            }

            @NonNull
            @Override
            public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_layout,parent,false);
                ResultViewHolder holder = new ResultViewHolder(view);
                return holder;
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder{
        public TextView team1,team2,date,time,score,result;
        public ImageView team1Logo,team2Logo;
        private ItemClickListner mItemClickListner;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            team1 = itemView.findViewById(R.id.team1_name);
            team2 = itemView.findViewById(R.id.team2_name);
            date = itemView.findViewById(R.id.match_date);
            time = itemView.findViewById(R.id.match_time);
            team1Logo = itemView.findViewById(R.id.team1_logo);
            team2Logo = itemView.findViewById(R.id.team2_logo);
            result = itemView.findViewById(R.id.result);
            score = itemView.findViewById(R.id.score);
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