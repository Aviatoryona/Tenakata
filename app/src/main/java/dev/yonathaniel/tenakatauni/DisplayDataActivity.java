package dev.yonathaniel.tenakatauni;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import dev.yonathaniel.tenakatauni.adapter.RecAdapter;
import dev.yonathaniel.tenakatauni.db.MyDb;
import dev.yonathaniel.tenakatauni.models.UserModel;

public class DisplayDataActivity extends AppCompatActivity {

    MyDb myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);

        myDb = new MyDb(this);

        initViews();
        setSupportActionBar(toolbar);
        init();
    }

    private void init() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new MarginDecoration(this, 8));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        swipeRefresh.setOnRefreshListener(this::fetchData);
        txtNoData.setOnClickListener(view -> fetchData());
        toolbar.setNavigationOnClickListener(view -> finish());

        new Handler().postDelayed(this::fetchData, 300);
    }

    private void fetchData() {
        Cursor cursor = myDb.READ_DATA();
        if (cursor == null) {
            swipeRefresh.setRefreshing(false);
            txtNoData.setVisibility(View.VISIBLE);
            pG.setVisibility(View.GONE);
            return;
        }

        List<UserModel> userModels = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                UserModel myModel = new UserModel();
                myModel.setId(cursor.getInt(0));
                myModel.setPhoto(cursor.getString(1));
                myModel.setName(cursor.getString(2));
                myModel.setMaritalStatus(cursor.getString(3));
                myModel.setGender(cursor.getString(4));
                myModel.setAge(cursor.getInt(5));
                myModel.setHeight(cursor.getInt(6));
                myModel.setIqTest(cursor.getDouble(7));
                userModels.add(myModel);
            }
        }

        if (userModels.isEmpty()) {
            swipeRefresh.setRefreshing(false);
            txtNoData.setVisibility(View.VISIBLE);
            pG.setVisibility(View.GONE);
            return;
        }

        recyclerView.setAdapter(new RecAdapter(userModels, this));
        swipeRefresh.setRefreshing(false);
        pG.setVisibility(View.GONE);
    }


    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private TextView txtNoData;
    private ProgressBar pG;

    public void initViews() {
        toolbar = findViewById(R.id.toolbar);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        recyclerView = findViewById(R.id.recyclerView);
        txtNoData = findViewById(R.id.txtNoData);
        pG = findViewById(R.id.pG);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_displaydata, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String[] strings = {"Pdf", "Word", "Excel"};
        if (item.getItemId() == R.id.navPrint) {
            new AlertDialog.Builder(this)
                    .setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strings), (dialogInterface, i) -> {

                    })
                    .setCancelable(true)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }
}