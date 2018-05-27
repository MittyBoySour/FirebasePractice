package com.mad.firebasepractice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String USER_FILTER = "user";
    private static final String USER_MAIL_FILTER = "mail";
    private User currentUser;
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerView mMailRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup views
        mMailRecyclerView = (RecyclerView) findViewById(R.id.mail_list_recycler);
        //mLinearLayoutManager.setStackFromEnd(true);
        mMailRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // setup DB and references
        mDatabaseReference = FirebaseDatabase.getInstance()
                .getReference();
        setupTestTree();
        // currentUser = new User("");
        DatabaseReference userMailQuery = mDatabaseReference.child(USER_FILTER)
                .child(currentUser.userId).child(USER_MAIL_FILTER);
        Log.d(TAG, "onCreate: userMailQuery.toString(): " + userMailQuery.toString());

        // setup recycler
        SnapshotParser<Mail> parser = getMailSnapshotParser();
        FirebaseRecyclerOptions<Mail> options = getMailRecyclerOptions(userMailQuery, parser);
        // TODO: move with viewholder to new class
        mFirebaseAdapter = getFirebasebaseRA(options);
        mMailRecyclerView.setAdapter(mFirebaseAdapter);

    }

    private void setupTestTree() {
        User testUser1 = new User("TestUser1");
        writeNewUser(testUser1);
        User testUser2 = new User("TestUser2");
        writeNewUser(testUser2);
        addUserToContacts(testUser1, testUser2, "TestFriend2");
        Mail mailFromUser1 = new Mail(testUser1.userId, testUser2.userId,
                "Mail 1 title", "Mail 1 message");
        sendNewMailToUser(mailFromUser1);
        addUserToContacts(testUser2, testUser1, "TestFriend1");
        Mail mailFromUser2 = new Mail(testUser2.userId, testUser1.userId,
                "Re: Mail 1 title", "Thank you for Mail 1");
        sendNewMailToUser(mailFromUser2);
        currentUser = testUser1;
    }

    private void writeNewUser(User user) {
        // assumes check for userName validity has been performed
        mDatabaseReference.child("user").setValue(user);
    }

    private void addUserToContacts(User user, User contact, String nickname) {
        mDatabaseReference.child("user").child(user.userId)
                .child("contact").child(contact.userId)
                .child("nickname").setValue(nickname);
    }

    private void sendNewMailToUser(Mail mail) {
        if (mail.recipientId.equalsIgnoreCase("")) { return; }
        String mailKey = mDatabaseReference.child("user").child(mail.recipientId)
                .child("mail").push().getKey();
        mDatabaseReference.child("user").child(mail.recipientId)
                .child("mail").child(mailKey).setValue(mail); // check that user still exists first
    }

    private SnapshotParser<Mail> getMailSnapshotParser() {
        return new SnapshotParser<Mail>() {
            @Override
            public Mail parseSnapshot(DataSnapshot dataSnapshot) {
                Mail mail = dataSnapshot.getValue(Mail.class);
                return mail;
            }
        };
    }

    private FirebaseRecyclerOptions<Mail> getMailRecyclerOptions(
            DatabaseReference query, SnapshotParser<Mail> parser) {
        return new FirebaseRecyclerOptions.Builder<Mail>()
                .setQuery(query, parser)
                .build();
    }

    private FirebaseRecyclerAdapter<Mail, MailViewHolder> getFirebasebaseRA(
            FirebaseRecyclerOptions<Mail> options) {
        return new FirebaseRecyclerAdapter<Mail, MailViewHolder>(options) {
            @Override
            public MailViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MailViewHolder(inflater.inflate(R.layout.mail_item, viewGroup,
                        false));
            }

            @Override
            protected void onBindViewHolder(final MailViewHolder viewHolder,
                                            int position,
                                            Mail mail) {
                viewHolder.titleTv.setText(mail.mailTitle);
                viewHolder.senderTV.setText(mail.senderId);
            }
        };
    }

    private class MailViewHolder extends RecyclerView.ViewHolder {

        public TextView senderTV;
        public TextView titleTv;
        // make separate views for status

        public MailViewHolder(View view) {
            super(view);
            senderTV = findViewById(R.id.mail_sender);
            titleTv = findViewById(R.id.mail_title);
        }
    }
}
