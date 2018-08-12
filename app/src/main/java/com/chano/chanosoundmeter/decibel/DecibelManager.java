package com.chano.chanosoundmeter.decibel;

import android.media.MediaRecorder;
import android.util.Log;

public class DecibelManager {

    private static double mEMA = 0.0;
    private static final double EMA_FILTER = 0.6;

    MediaRecorder mediaRecorder;

    public boolean setRecorder(){
        if (mediaRecorder == null)
        {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile("/dev/null");
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (java.io.IOException ioe) {
                Log.e("[Monkey]", "IOException: " + Log.getStackTraceString(ioe));
                return false;
            } catch (java.lang.SecurityException e) {
                Log.e("[Monkey]", "SecurityException: " + Log.getStackTraceString(e));
                return false;
            }
        }
        return true;
    }

    public void stopRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    public float soundDb(float ampl){
        return  20 * (float)Math.log10(/*getAmplitudeEMA() /*/ ampl);
    }
    public float getAmplitude() {
        if (mediaRecorder != null)
            return  mediaRecorder.getMaxAmplitude();
        else
            return 0;

    }
    public double getAmplitudeEMA() {
        double amp =  getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }

    public float setFormatDB(float decibel){
        String format = String.format("%.01f",decibel);
        return Float.parseFloat(format);
    }
}
