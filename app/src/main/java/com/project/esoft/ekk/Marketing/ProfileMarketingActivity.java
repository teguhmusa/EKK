package com.project.esoft.ekk.Marketing;

import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.esoft.ekk.ActivityUser.MarketingActivity;
import com.project.esoft.ekk.Adapter.AdapterApproval;
import com.project.esoft.ekk.Constructor.DataPenjualan;
import com.project.esoft.ekk.Constructor.DataTahunan;
import com.project.esoft.ekk.Constructor.History;
import com.project.esoft.ekk.Manager.ApprovalRequestActivity;
import com.project.esoft.ekk.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileMarketingActivity extends AppCompatActivity {

    public static final String TAG_NAMA = "nama";
    public static final String TAG_UID = "uid";

    TextView namaView, terjualRumah, targetRumah,
            terjualMobil, targetMobil,
            terjualMotor, targetMotor,
            bRumah, bMobil, bMotor;

    CircleImageView profilePict;

    DataPenjualan dataRumah, dataMobil, dataMotor;

    DatabaseReference database, ref;

    RatingBar ratingBar;

    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference pathReference;

    float ratingPenjual;

    String uID, nama, bonusRumah, bonusMobil, bonusMotor;

    BarChart chart ;
    ArrayList<BarEntry> BARENTRY ;
    ArrayList<String> BarEntryLabels ;
    BarDataSet Bardataset ;
    BarData BARDATA ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_marketing);

        uID = getIntent().getStringExtra(TAG_UID);
        nama = getIntent().getStringExtra(TAG_NAMA);

        namaView = findViewById(R.id.namaProfilePegawai);
        terjualRumah = findViewById(R.id.terjualTextRumah);
        terjualMobil = findViewById(R.id.terjualTextMobil);
        terjualMotor = findViewById(R.id.terjualTextMotor);
        targetRumah = findViewById(R.id.dariTextRumah);
        targetMobil = findViewById(R.id.dariTextMobil);
        targetMotor = findViewById(R.id.dariTextMotor);

        bRumah = findViewById(R.id.bonusRumah);
        bMobil = findViewById(R.id.bonusMobil);
        bMotor = findViewById(R.id.bonusMotor);

        profilePict = findViewById(R.id.imageProfilePegawai);

        ratingBar = findViewById(R.id.ratingBarProfile);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(0xFFE9D32E, PorterDuff.Mode.SRC_ATOP);

        ratingBar.setClickable(false);
        ratingBar.setFocusable(false);
        ratingBar.setFocusableInTouchMode(false);
        ratingBar.setIsIndicator(true);
        chart = findViewById(R.id.chartProfile);

        BARENTRY = new ArrayList<>();

        BarEntryLabels = new ArrayList<String>();




        namaView.setText(nama);

        database = FirebaseDatabase.getInstance().getReference();
        ref = database.child("users").child(uID);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        pathReference = storageReference.child("imagesProfile/"+uID.toString());

        ref.child("rumah").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataRumah = dataSnapshot.getValue(DataPenjualan.class);
                dataRumah.setTypeJual(dataSnapshot.getKey());
                terjualRumah.setText(dataRumah.getTerjual().toString());
                targetRumah.setText(dataRumah.getTarget().toString());
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        ref.child("mobil").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataMobil = dataSnapshot.getValue(DataPenjualan.class);
                dataMobil.setTypeJual(dataSnapshot.getKey());
                terjualMobil.setText(dataMobil.getTerjual().toString());
                targetMobil.setText(dataMobil.getTarget().toString());
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        ref.child("motor").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataMotor = dataSnapshot.getValue(DataPenjualan.class);
                dataMotor.setTypeJual(dataSnapshot.getKey());
                terjualMotor.setText(dataMotor.getTerjual().toString());
                targetMotor.setText(dataMotor.getTarget().toString());
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        ref.child("rating").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String dura = dataSnapshot.getValue(String.class);
                ratingPenjual = Float.parseFloat(dura);
                ratingBar.setRating(ratingPenjual);
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        ref.child("bonusrumah").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bonusRumah= dataSnapshot.getValue(String.class);
                bRumah.setText(bonusRumah);
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        ref.child("bonusmobil").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bonusMobil= dataSnapshot.getValue(String.class);
                bMobil.setText(bonusMobil);
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        ref.child("bonusmotor").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bonusMotor= dataSnapshot.getValue(String.class);
                bMotor.setText(bonusMotor);
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ProfileMarketingActivity.this)
                        .load(uri.toString())
                        .into(profilePict);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });

        AddValuesToBARENTRY();

        AddValuesToBarEntryLabels();

        Log.e("d", "onCreate: " );


    }

    public void AddValuesToBARENTRY(){

        ref.child("datatahunan").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 1;
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    try {
                        DataTahunan dataTahunan = noteDataSnapshot.getValue(DataTahunan.class);
                        dataTahunan.setBulan(noteDataSnapshot.getKey());
                        String data = dataTahunan.getJumlah().toString();
                        Log.e("SIDATAA", "onDataChange: "+data );
                        int angka = Integer.parseInt(data);
                        BARENTRY.add(new BarEntry(i, angka, angka));
                        i++;
                    } catch (Exception e) {
                        Log.e("SIDATAA", "onDataChange: "+e );
                    }
                }

                Bardataset = new BarDataSet(BARENTRY, "");

                BARDATA = new BarData(Bardataset);

                Bardataset.setColors(ColorTemplate.COLORFUL_COLORS);

                chart.setData(BARDATA);

                chart.animateY(3000);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getDetails()+" "+databaseError.getMessage());
            }
        });
    }

    public void AddValuesToBarEntryLabels(){

        BarEntryLabels.add("January");
        BarEntryLabels.add("February");
        BarEntryLabels.add("March");
        BarEntryLabels.add("April");
        BarEntryLabels.add("May");
        BarEntryLabels.add("June");
    }
}
