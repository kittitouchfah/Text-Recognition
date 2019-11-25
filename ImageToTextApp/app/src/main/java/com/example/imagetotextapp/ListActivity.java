package com.example.imagetotextapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


import android.app.SearchManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class ListActivity extends AppCompatActivity {
    List<Model > modelList = new ArrayList<>();
    RecyclerView mRecyclerView;
    //layout manager for recycleview
    RecyclerView.LayoutManager layoutManager;


    //firestore instance
    FirebaseFirestore db;

    CustomAdapter adapter;

    ProgressDialog pd;

    Button mAddBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("List Data");


        //init firestore
        db = FirebaseFirestore.getInstance();

        pd = new ProgressDialog(this);
        //initialize views
        mRecyclerView = findViewById(R.id.recycler_view);
        mAddBtn = findViewById(R.id.addBtn);
        //set recycler view properties
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        //change default
        //show data in recyclerView
        showData();


        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListActivity.this,MainActivity.class));
                finish();
            }
        });

    }
    private void showData(){
        //set text of progress doalog
       pd.setTitle("Loading Data...");
        //show progress dialog
       pd.show();

        db.collection("Documents")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        modelList.clear();
                        //called when data is retrived

                        pd.dismiss();
                        //show data
                        for (DocumentSnapshot doc: task.getResult()){
                            Model model = new Model(doc.getString("id"),
                                    doc.getString("text"),
                                    doc.getString("user"),
                                    doc.getString("Date"),
                                    doc.getString("Filename"));
                            modelList.add(model);
                        }
                        //adapter
                        adapter = new CustomAdapter(ListActivity.this,modelList);
                        //set adapter to recyclerview
                        mRecyclerView.setAdapter(adapter);

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //called when there is any error while retire
                    }
                });
    }

    public void deleteData(int index){
        //set title of progress dialog
        pd.setTitle("Loading Data...");
        //show progress dialog
        pd.show();

        db.collection("Documents").document(modelList.get(index).getId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //called when deleted sucessfully
                        pd.dismiss();
                        Toast.makeText(ListActivity.this,"Deleted...",Toast.LENGTH_SHORT);
                        showData();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //called when there is any error
                        pd.dismiss();
                        //get and show error message
                        Toast.makeText(ListActivity.this,e.getMessage(),Toast.LENGTH_SHORT);
                    }
                });
    }



    private void searchData(String s) {
        //set title of progress bar
        pd.setTitle("Search...");
        //show progress bar when user click save button
        pd.show();
        db.collection("Documents").whereEqualTo("search",s.toLowerCase())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //called when searching is succeeded
                        modelList.clear();
                       pd.dismiss();
                        //show data
                        for (DocumentSnapshot doc: task.getResult()){
                            Model model = new Model(doc.getString("id"),
                                    doc.getString("text"),
                                    doc.getString("user"),
                                    doc.getString("Date"),
                                    doc.getString("Filename"));
                            modelList.add(model);
                        }
                        //adapter
                        adapter = new CustomAdapter(ListActivity.this,modelList);
                        //set adapter to recyclerview
                        mRecyclerView.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(ListActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

//menu


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //inflating menu_list.xml
        getMenuInflater().inflate(R.menu.menu_list,menu);
        //SeachView
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String s) {
                //called when we press search button
                searchData(s);//function call with string entered in searhview as parameter
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //called as and when we type even a single letter
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //handle other menu item clicksw here
        if(item.getItemId()== R.id.action_settings){
            Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
