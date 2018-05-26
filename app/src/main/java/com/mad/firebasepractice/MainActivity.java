package com.mad.firebasepractice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Write a message to the database
        DatabaseReference mailReference = FirebaseDatabase.getInstance()
                .getReference("user"); // will be user on initialisation

        String userKey = mailReference.push().getKey();
        Log.d(TAG, "onCreate: user: " + userKey);

        writeNewUser(userKey,"Mitty", "mitty@mitty.com",
                FirebaseDatabase.getInstance().getReference());

        updateUsername(userKey, "Billy",
                FirebaseDatabase.getInstance().getReference());

        writeNewPost(userKey, "Billy", "Titled as hell",
                "so much text ",
                FirebaseDatabase.getInstance().getReference());

        // Read from the database
        mailReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void writeNewUser(String userId, String name, String email,
                              DatabaseReference database) {
        User user = new User(name, email);

        database.child("users").child(userId).setValue(user);
    }

    private void updateUsername(String userId, String newName, DatabaseReference database) {
        database.child("users").child(userId).child("username").setValue(newName);

    }
    private void writeNewPost(String userId, String username, String title, String body,
                              DatabaseReference databaseReference) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = databaseReference.child("posts").push().getKey();
        Post post = new Post(userId, username, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        databaseReference.updateChildren(childUpdates);
    }

}
