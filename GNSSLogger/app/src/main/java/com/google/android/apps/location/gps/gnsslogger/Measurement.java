package com.google.android.apps.location.gps.gnsslogger;

import android.location.GnssClock;
import android.location.GnssMeasurement;
import android.os.StrictMode;
import  android.os.SystemClock;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by 张楷时 on 2017/4/13.
 */

public class Measurement {
    public static final int WEEK_SEC=604800;
    public static final int LIGHT_SPEED=299792458;


    public double getC1() {
        return C1;
    }

    public void setC1(double c1) {
        C1 = c1;
    }

    public double getL1() {
        return L1;
    }

    public void setL1(double l1) {
        L1 = l1;
    }

    public double getS1() {
        return S1;
    }

    public void setS1(double s1) {
        S1 = s1;
    }

    private double C1;
    private double L1;
    private double S1;
    //2private String sC1;

    public Measurement(GnssMeasurement mGnssMeasurement, GnssClock mGnssClock) {
        C1=Calculatepesudorange(mGnssMeasurement, mGnssClock);
        L1=mGnssMeasurement.getAccumulatedDeltaRangeMeters();
        S1=mGnssMeasurement.getCn0DbHz();
        //sC1=""+C1;
       //Log.d(TAG,sC1);
    }

    private double Calculatepesudorange(GnssMeasurement mGnssMeasurement, GnssClock mGnssClock){
        double Pesudorange;
        int GPSWeek;
        GPSWeek=(int)Math.floor(-mGnssClock.getFullBiasNanos()/1000000000/WEEK_SEC);
        long GPSWeekNanos = (long)GPSWeek*WEEK_SEC*1000000000;
        double tRxNanos=mGnssClock.getTimeNanos()-mGnssClock.getFullBiasNanos();
        if(tRxNanos<0)System.out.println("tRxNanos should be >=0");
        double tRxSec=(tRxNanos-mGnssMeasurement.getTimeOffsetNanos()-mGnssClock.getBiasNanos())/1000000000;
        double tTxSec=mGnssMeasurement.getReceivedSvTimeNanos()/1000000000;
        double prSec=CheckGpsWeekRollover(tRxSec,tTxSec);
        Pesudorange=prSec*LIGHT_SPEED;
        return Pesudorange;
    }

    private double CheckGpsWeekRollover(double tRxSec,double tTxSec){
        double prSec=tRxSec-tTxSec;
        boolean isRollover;
        isRollover=prSec>WEEK_SEC/2;
        if(isRollover){
            double prS=prSec;
            double delS=Math.round(prS/WEEK_SEC)*WEEK_SEC;
            prSec=prS-delS;
        }
        return prSec;
    }

    @Override
    public String toString() {
        StringBuilder builder=new StringBuilder(String.format("%lf,%lf,%lf",C1,L1,S1));
        return builder.toString();
    }
}
