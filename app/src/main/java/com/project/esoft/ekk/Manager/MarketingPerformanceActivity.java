package com.project.esoft.ekk.Manager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.esoft.ekk.Constructor.DataTahunan;
import com.project.esoft.ekk.Constructor.Pegawai;
import com.project.esoft.ekk.R;

import java.util.ArrayList;

public class MarketingPerformanceActivity extends AppCompatActivity {


    private DatabaseReference database, ref;
    Pegawai pegawai;
    TextView namaPegawai, target, dari, terjual, durasi;
    String jenis, dura, uID;
    private int jual=1, trget=1;
    ProgressBar progress;
    Spinner jenisPenjualan;

    BarChart chart ;
    ArrayList<BarEntry> BARENTRY ;
    ArrayList<String> BarEntryLabels ;
    BarDataSet Bardataset ;
    BarData BARDATA ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing_performance);

        namaPegawai = findViewById(R.id.textViewNamaPeg);
        target = findViewById(R.id.textViewTargetPer);
        terjual = findViewById(R.id.textViewTerjualPer);
        dari = findViewById(R.id.textViewTerjual3Per);
        durasi = findViewById(R.id.textViewDurasi2Per);
        jenisPenjualan = findViewById(R.id.spinnerTypeJenis);
        progress = findViewById(R.id.progressBarPer);
        pegawai = (Pegawai) getIntent().getSerializableExtra("data");

        chart = findViewById(R.id.chartPerformance);

        BARENTRY = new ArrayList<>();

        BarEntryLabels = new ArrayList<String>();



        if (pegawai != null) {
            namaPegawai.setText("Nama Pegawai : "+pegawai.getNama().toString());
            uID = pegawai.getUid();
        }
        database = FirebaseDatabase.getInstance().getReference();
        ref = database.child("users").child(uID);
        jenis = "rumah";
        refreshData();

        jenisPenjualan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    jenis = "rumah";
                } else if(position==1){
                    jenis = "mobil";
                } else if(position==2){
                    jenis = "motor";
                }
                Log.e("jenisss", "onItemSelected: "+jenis );
                refreshData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        AddValuesToBARENTRY();

        AddValuesToBarEntryLabels();

    }

    public static Intent getActIntent(Activity activity) {
        return new Intent(activity, MarketingPerformanceActivity.class);
    }

    public void refreshData(){

        try {
            setDataRumah("durasi", durasi);
            setDataRumah("target", target);
            setDataRumah("terjual", terjual);
            setDataRumah("target", dari);
        } catch (Exception e){}

    }

    private  void setDataRumah(final String child, final  TextView text){
        try {
            database.child("users").child(uID).child(jenis).child(child).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dura = dataSnapshot.getValue(String.class);
                    text.setText(dura);
                    if (child.equals("target")) {
                        trget = Integer.parseInt(dura);
                    } else if (child.equals("terjual")) {
                        jual = Integer.parseInt(dura);
                    }
                    int test = jual * 100 / trget;
                    progress.setProgress(test);
                    int color = 0xFF3380D9;
                    progress.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
        }
        catch (Exception e){}
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

                Bardataset = new BarDataSet(BARENTRY, "Projects");

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
