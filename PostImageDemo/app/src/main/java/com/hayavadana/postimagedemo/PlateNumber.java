package com.hayavadana.postimagedemo;

/**
 * Created by Sridhar on 18/04/16.
 */
public class PlateNumber {
    public boolean isMainCand;



    public String plateNum;
    public String confidence;

    public PlateNumber(boolean isMainCand, String plateNum,String confidence ) {
        this.confidence = confidence;
        this.isMainCand = isMainCand;
        this.plateNum = plateNum;
    }

    public String getPlateNum() {
        return plateNum;
    }
}
