package com.project.esoft.ekk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.esoft.ekk.ActivityUser.MarketingActivity;
import com.project.esoft.ekk.ActivityUser.UnverifiedActivity;
import com.project.esoft.ekk.Adapter.AdapterPegawai;
import com.project.esoft.ekk.Admin.DaftarPegawaiActivity;
import com.project.esoft.ekk.Constructor.Pegawai;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {


    private EditText noId;
    private EditText nameAdd;
    private EditText passwordAdd;
    private EditText passwordReenter;

    private CircleImageView photo;


    private Spinner spinnerType;

    Button submitButton;

    TextView changePhoto;

    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference ref;
    private FirebaseUser user;

    FirebaseStorage storage;
    StorageReference storageReference;

    StorageReference pathReference;
    String jenis;


    private Uri filePath;
    String uID;
    public static final String TAG_JENISID = "jenisid";
    Pegawai pegawai;

    private final int PICK_IMAGE_REQUEST = 71;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            jenis = getIntent().getStringExtra(TAG_JENISID);
        }catch (Exception e){

        }

        if(jenis.equals("marketing")){
            setTheme(R.style.AppThemeMarketing);
        }else if(jenis.equals("manager")){
            setTheme(R.style.AppThemeManager);
        }else if(jenis.equals("admin")){
            setTheme(R.style.AppTheme2);
        }

        setContentView(R.layout.activity_setting);
        noId = findViewById(R.id.textIDSetting);
        nameAdd = findViewById(R.id.textNameSetting);
        passwordAdd = findViewById(R.id.textPasswordSetting);
        passwordReenter = findViewById(R.id.textReenterSetting);

        changePhoto = findViewById(R.id.text_change_photo_setting2);

        photo = findViewById(R.id.image_profile_setting2);

        submitButton = findViewById(R.id.buttonSubmitSetting);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        ref = mDatabase.child("users");

        user = FirebaseAuth.getInstance().getCurrentUser();
        uID = user.getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        pathReference = storageReference.child("imagesProfile/"+uID.toString());

        ref.child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                pegawai = dataSnapshot.getValue(Pegawai.class);
                    pegawai.setUid(dataSnapshot.getKey());
                    noId.setText(pegawai.getNoid());
                    nameAdd.setText(pegawai.getNama());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                System.out.println(databaseError.getDetails()+" "+databaseError.getMessage());
            }
        });


        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(SettingActivity.this)
                        .load(uri.toString())
                        .into(photo);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });



        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(noId.getText().toString().isEmpty()||
                        nameAdd.getText().toString().isEmpty()||
                        passwordAdd.getText().toString().isEmpty())
                {
                    Toast.makeText(SettingActivity.this, "Field cannot be empty", Toast.LENGTH_LONG).show();

                }
                else if(!passwordAdd.getText().toString().equals(passwordReenter.getText().toString()))
                {
                    Toast.makeText(SettingActivity.this, "Password did not match", Toast.LENGTH_LONG).show();
                }
                else if(passwordAdd.getText().length()<8)
                {
                    Toast.makeText(SettingActivity.this, "Password minimal 8 karakter", Toast.LENGTH_LONG).show();
                }
                else{
                    ref.child(uID).child("noid").setValue(noId.getText().toString());
                    ref.child(uID).child("nama").setValue(nameAdd.getText().toString());
                    user.updatePassword(passwordAdd.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TESTPASS", "Password updated");
                                if(filePath==null){
                                    onBackPressed();
                                }
                            } else {
                                if(filePath==null){
                                    onBackPressed();
                                }
                            }
                        }
                    });
                    user.updatePassword(passwordReenter.getText().toString());
                    uploadImage();

                }
            }
        });

        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                photo.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("imagesProfile/"+uID);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(SettingActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SettingActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
        else{
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
