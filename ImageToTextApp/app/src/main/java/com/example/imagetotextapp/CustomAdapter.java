package com.example.imagetotextapp;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {
    ListActivity listActivity;
    List<Model> modelList;
    Context context;

    public  CustomAdapter(ListActivity listActivity,List<Model> modelList){
        this.listActivity = listActivity;
        this.modelList = modelList;

        }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.model_layout,viewGroup,false);

        ViewHolder viewHolder = new ViewHolder(itemView);
        //handle item clicks here
        viewHolder.setmClickListener(new ViewHolder.ClickListener(){
            @Override
                    public void onItemLongClick(View view,int position){
                        //this will bee called when user click item


                        //show data in toast on clicking
                        String filename = modelList.get(position ).getFilename();
                        String text = modelList.get(position).getText();
                        String user = modelList.get(position ).getUser();
                        String date = modelList.get(position ).getDate();

                        Toast.makeText(listActivity,text+"\n"+user,Toast.LENGTH_SHORT).show();


            }
            @Override
                    public void onItemClick(View view, final int position){
                        //this will be called when user long click item

                        //Creating AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(listActivity);
                //options to display in dialog
                String[] options = {"Update","Delete"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if(which == 0){
                            //update is clicked
                            //get data
                            String id = modelList.get(position).getId();
                            String filename = modelList.get(position).getFilename();
                            String text = modelList.get(position).getText();

                            //intent to start activity
                            Intent intent = new Intent(listActivity, MainActivity.class);

                            //put data in intent
                            intent.putExtra("pId",id);
                            intent.putExtra("pFilename",filename);
                            intent.putExtra("pText",text);
                            //start activity
                            listActivity.startActivity(intent);

                        }
                        if(which == 1){
                          //delete is clicked
                            listActivity.deleteData(position);
                        }
                    }
                }).create().show();
            }
        });


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        //bind views / set data
        viewHolder.mFilename.setText(modelList.get(i).getFilename());
        viewHolder.mText.setText(modelList.get(i).getText());
        viewHolder.mUser.setText(modelList.get(i).getUser());
        viewHolder.mDate.setText(modelList.get(i).getDate());
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}
