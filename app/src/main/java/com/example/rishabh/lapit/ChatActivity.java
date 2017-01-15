package com.example.rishabh.lapit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ChatActivity extends AppCompatActivity {

    Button add_room;
    EditText room_name;
    ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_rooms = new ArrayList<>();

    private DatabaseReference rootChat;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        add_room = (Button) findViewById(R.id.add_room);
        room_name = (EditText) findViewById(R.id.room_name);
        listView = (ListView) findViewById(R.id.listView);

        rootChat = FirebaseDatabase.getInstance().getReference().child("Chat");
        mAuth = FirebaseAuth.getInstance();

        arrayAdapter = new ArrayAdapter<String>(ChatActivity.this,android.R.layout.simple_list_item_1,list_of_rooms);

        listView.setAdapter(arrayAdapter);

        add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String,Object> map = new HashMap<String, Object>();
                map.put(room_name.getText().toString(),"");
                rootChat.updateChildren(map);

            }
        });

        rootChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();
                while(i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());

                }
                list_of_rooms.clear();
                list_of_rooms.addAll(set);

                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent chatRoomIntent = new Intent(ChatActivity.this,ChatRoomActivity.class);
                chatRoomIntent.putExtra("room_name",((TextView)view).getText().toString());
                chatRoomIntent.putExtra("user_name",mAuth.getCurrentUser().getDisplayName());
                startActivity(chatRoomIntent);

            }
        });


    }
}
