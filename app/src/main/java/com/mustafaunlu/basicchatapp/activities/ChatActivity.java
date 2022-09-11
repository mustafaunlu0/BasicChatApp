package com.mustafaunlu.basicchatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mustafaunlu.basicchatapp.R;
import com.mustafaunlu.basicchatapp.adapter.ChatAdapter;
import com.mustafaunlu.basicchatapp.databinding.ActivityChatBinding;
import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private ChatAdapter chatAdapter;
    private ArrayList<String> chatMessages = new ArrayList<>();

    private static final String ONESIGNAL_APP_ID = "0e7ae6f1-27cf-4f97-a5ba-690f0216a936";



    private ActivityChatBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pushNotificationWithOneSignal();

        mAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        chatAdapter=new ChatAdapter(chatMessages);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(chatAdapter);


        //getData() -> eklenecek verileri çekmek için
        getData();

        //Get user
        getUser();




    }
    private void pushNotificationWithOneSignal(){
        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        // promptForPushNotifications will show the native Android notification permission prompt.
        // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
        OneSignal.promptForPushNotifications();
    }

    private void getUser(){

        OSDeviceState deviceState=OneSignal.getDeviceState();
        if(deviceState != null){
            String userId=deviceState.getUserId();
            Map<String,Object> userIds=new HashMap<>();
            userIds.put("userID",userId);

            firebaseFirestore.collection("UserIDs").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if(task.isSuccessful()){
                        ArrayList<String> playerIDs=new ArrayList<>();

                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            Map<String,Object> map=queryDocumentSnapshot.getData();
                            String userId= (String) map.get("userID");
                            playerIDs.add(userId);

                        }
                        if(!playerIDs.contains(userIds)){
                            firebaseFirestore.collection("UserIDs").add(userIds);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                }
            });
        }

    }
    public void send(View view){


        FirebaseUser user=mAuth.getCurrentUser();
        String messageToSend=binding.messageEditText.getText().toString();

        //verileri paketle
        Map<String,Object> map=new HashMap<>();
        map.put("message",messageToSend);
        map.put("email",user.getEmail());
        map.put("time", Timestamp.now());

        firebaseFirestore.collection("Messages").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(), "recorded!", Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });


        binding.messageEditText.setText("");
        getData();


        //One Signal

        firebaseFirestore.collection("UserIDs").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                        Map<String,Object> map=queryDocumentSnapshot.getData();
                        String userID= (String) map.get("userID");


                        try {
                            OneSignal.postNotification(new JSONObject("{'contents': {'en':'"+messageToSend+"'}, 'include_player_ids' : ['"+userID+"']}"),null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        });





    }

    private void getData() {

        firebaseFirestore.collection("Messages").orderBy("time", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                chatMessages.clear();
                for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                    Map<String,Object> dataFromMessages=queryDocumentSnapshot.getData();
                    String mail= (String) dataFromMessages.get("email");
                    String message = (String) dataFromMessages.get("message");

                    chatMessages.add(mail +": "+message);
                    chatAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.toProfileActivity){
            //Intent
            Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
            startActivity(intent);

        }
        else if(item.getItemId() == R.id.signOut){
            //Sign out
            mAuth.signOut();
            Intent intent=new Intent(getApplicationContext(),SignActivity.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding=null;
    }
}