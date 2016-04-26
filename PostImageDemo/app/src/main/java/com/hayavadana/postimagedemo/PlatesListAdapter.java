package com.hayavadana.postimagedemo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sridhar on 18/04/16.
 */
public class PlatesListAdapter extends ArrayAdapter {

    private final Activity context;
    private final ArrayList<PlateNumber> platesInfo;

    public PlatesListAdapter(Activity context, int res, ArrayList<PlateNumber> platesInfo) {
        super(context, res, platesInfo);
        this.context = context;
        this.platesInfo = platesInfo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.plate_list_item, null, true);
        TextView tv = (TextView )rowView.findViewById(R.id.tvPlateNumber);
        String tempstr = platesInfo.get(position).getPlateNum();
        tv.setText(tempstr);
        return rowView;
    }
}



