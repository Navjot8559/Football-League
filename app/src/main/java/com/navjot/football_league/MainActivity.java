package com.navjot.football_league;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.navjot.football_league.Model.ItemClickListner;
import com.navjot.football_league.Model.Schedule;
import com.navjot.football_league.Model.Team;
import com.navjot.football_league.Prevelant.Prevelant;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private String userType;
    private AlertDialog.Builder builder;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference teamRef = db.collection("Schedules");
    private Query mQuery = teamRef.orderBy("matchDate", Query.Direction.ASCENDING);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        userType = getIntent().getStringExtra("userType");
        builder = new AlertDialog.Builder(this);
        mRecyclerView = findViewById(R.id.match_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        if(userType.equals("guest")){
            navigationView.getMenu().findItem(R.id.nav_schedule).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
        }else{

            navigationView.getMenu().findItem(R.id.nav_schedule).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
        }

        FirestoreRecyclerOptions<Schedule> options = new FirestoreRecyclerOptions.Builder<Schedule>()
                .setQuery(mQuery,Schedule.class).build();

        FirestoreRecyclerAdapter<Schedule,ScheduleViewHolder> adapter = new FirestoreRecyclerAdapter<Schedule, ScheduleViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position, @NonNull final Schedule model) {
                holder.team1.setText(model.getTeam1());
                holder.team2.setText(model.getTeam2());
                holder.date.setText(model.getMatchDate());
                holder.time.setText(model.getMatchTime());
                Picasso.get().load(model.getTeam1Logo()).into(holder.team1Logo);
                Picasso.get().load(model.getTeam2Logo()).into(holder.team2Logo);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       Intent intent = new Intent(getApplicationContext(),MatchDetail.class);
                       intent.putExtra("team1Id",model.getTeam1Id());
                       intent.putExtra("team2Id",model.getTeam2Id());
                       intent.putExtra("team1",model.getTeam1());
                       intent.putExtra("team2",model.getTeam2());
                       intent.putExtra("team1Logo",model.getTeam1Logo());
                       intent.putExtra("team2Logo",model.getTeam2Logo());
                       intent.putExtra("date",model.getMatchDate());
                       intent.putExtra("time",model.getMatchTime());
                       intent.putExtra("scheduleId",model.getScheduleId());
                       startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_layout,parent,false);
                ScheduleViewHolder holder = new ScheduleViewHolder(view);
                return holder;
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.nav_logout){
            builder.setMessage("Do You Want to Logout?");
            builder.setTitle("Alert");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Paper.book().destroy();
                    Prevelant.userType = "guest";
                    Prevelant.userPhone = "userPhone";
                    finish();
                    Intent intent = new Intent(getApplicationContext(),SelectUserType.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Logged Out...", Toast.LENGTH_SHORT).show();
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
        }else if(id == R.id.nav_tmanager){
            startActivity(new Intent(getApplicationContext(),TeamManagers.class));
        }else if(id == R.id.nav_login){
            startActivity(new Intent(getApplicationContext(),SelectUserType.class));
            finish();
        }else if(id == R.id.nav_teams){
           startActivity(new Intent(getApplicationContext(),Teams.class));
        }else if(id == R.id.nav_schedule){
           startActivity(new Intent(getApplicationContext(),CreateSchedule.class));
        }else if(id == R.id.nav_results){
            startActivity(new Intent(getApplicationContext(),ViewResult.class));
        }else if(id == R.id.nav_standings){
            startActivity(new Intent(getApplicationContext(),Standings.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView team1,team2,date,time;
        public ImageView team1Logo,team2Logo;
        private ItemClickListner mItemClickListner;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            team1 = itemView.findViewById(R.id.team1_name);
            team2 = itemView.findViewById(R.id.team2_name);
            date = itemView.findViewById(R.id.match_date);
            time = itemView.findViewById(R.id.match_time);
            team1Logo = itemView.findViewById(R.id.team1_logo);
            team2Logo = itemView.findViewById(R.id.team2_logo);
        }

        @Override
        public void onClick(View view) {
            mItemClickListner.onClick(view,getAdapterPosition(),false);
        }

        public void setItemClickListner(ItemClickListner itemClickListner) {
            mItemClickListner = itemClickListner;
        }
    }
}