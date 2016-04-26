package com.hayavadana.postimagedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class ActivityPlatesList extends AppCompatActivity {

    PlatesListAdapter platesListAdapter;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_plates_list);

        platesListAdapter = new PlatesListAdapter(ActivityPlatesList.this,R.layout.plate_list_item, ImageCapture.AllPlates);

        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(platesListAdapter);
    }
}
