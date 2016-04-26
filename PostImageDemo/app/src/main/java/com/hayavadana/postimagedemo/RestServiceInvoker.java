package com.hayavadana.postimagedemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by kh499 on 11/12/15.
 */
public class RestServiceInvoker extends AsyncTask<String, Void, Void> {


    private String ContentFrmServer;
    private String ErrorMsg;
    private Activity curActivity;
    private java.lang.Class nextActivityClass;
    private ProgressDialog progress;
    private String operationType;
    private String jsonRespStr = "";
    private String svcURL;
    private String httpMethod;
    private String postRequestParams;
    private Context curContext;
    private boolean connIssue = false;
    private  ArrayList<PlateNumber> AllPlates;
    TextView plateTextView;

    public void setPostRequestParams(String postRequestParams) {
        this.postRequestParams = postRequestParams;
    }

    public void setAllPlates(ArrayList<PlateNumber> allPlates) {
        AllPlates = allPlates;
    }

    public void setCurrentContext(Context curCntxt) {
        this.curContext = curCntxt;
    }


    public void setHttpMethod(String meth) {
        httpMethod = meth;

    }

    public void setCurActivity(Activity curAct) {

        curActivity = curAct;
    }

    public void setTextViewRef(TextView tv) {

        plateTextView = tv;
    }

    public void setNextActivity(Class nextActClass) {

        nextActivityClass = nextActClass;
    }

    public void setOperationType(String opType) {
        operationType = opType;

    }

    public void setURL(String urlStr) {
        svcURL = urlStr;

    }

    protected void onPreExecute() {

        /*progress = new ProgressDialog(curActivity);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();
        */

    }

    // Call after onPreExecute method
    protected Void doInBackground(String... urls) {

        System.out.println("nampelli inside the do in background");

        System.out.println("inside background task");

        jsonRespStr = "";
        try {

            svcURL = "http://183.83.32.48:8080/RateOn/rest/alprService/send";

            URL url = new URL(svcURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(httpMethod);

            if (httpMethod == "POST") {
                System.out.println("before writing the json object string");
                conn.setRequestProperty("content-type", "text/plain; charset=utf-8");
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

                wr.writeBytes(postRequestParams);
                System.out.println("after writing the json object string");

            }

            //if (conn.getResponseCode() != 200) {
            //  throw new RuntimeException("Failed : HTTP error code : "
            //        + conn.getResponseCode());
            //}
            if (conn.getResponseCode() != 200) {
                System.out.println("Problem getting response from server..");
                connIssue = true;
            } else {

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String output;
                System.out.println("Output from Server .... \n");

                while ((output = br.readLine()) != null) {
                    jsonRespStr += output;
                    System.out.println(output);
                }
                System.out.println("SREENI received json string is :" + jsonRespStr.toString());



                JSONObject respJSON = new JSONObject(jsonRespStr);

                String plateString = (String)respJSON.optString("plate").toString();
                JSONObject respJSONplate = new JSONObject(plateString);
                System.out.println("OOOOOOO "+(String)respJSONplate.optString("results").toString());
                String resultsStr = (String)respJSONplate.optString("results").toString();

                JSONArray jsonArrForResults = new JSONArray(resultsStr);

                this.AllPlates.clear();
                boolean isMain = false;
                for (int i=0;i<jsonArrForResults.length();i++){
                    System.out.println("\n\n**********************************");

                    JSONObject jsonChildNode = jsonArrForResults.getJSONObject(i);
                    String plateNumber = jsonChildNode.optString("plate").toString();
                    System.out.println("Plate Number: "+plateNumber);

                    String confidence = jsonChildNode.optString("confidence").toString();
                    System.out.println("Confidence: "+confidence);

                    if (i==0){
                        isMain = true;
                    }
                    else {
                        isMain = false;
                    }
                    PlateNumber plateObj = new PlateNumber(isMain,plateNumber,confidence);
                    this.AllPlates.add(plateObj);

                    String candidatesStr = (String)jsonChildNode.optString("candidates").toString();
                    JSONArray jsonArrForCand = new JSONArray(candidatesStr);
                    for (int j=0;j<jsonArrForCand.length();j++){
                        System.out.println("*************CANDIDATE " + j + " *********************");

                        JSONObject jsonChildNodeCand = jsonArrForCand.getJSONObject(j);
                        String candPlateNumber = jsonChildNodeCand.optString("plate").toString();
                        System.out.println("Candidate Plate Number: "+candPlateNumber);

                        String canConfidence = jsonChildNodeCand.optString("confidence").toString();
                        System.out.println("Candidate Confidence: "+canConfidence);

                        PlateNumber tempPlateObj = new PlateNumber(false,candPlateNumber,canConfidence);
                        this.AllPlates.add(tempPlateObj);


                    }



                }
            }
            conn.disconnect();



        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {
            //showDialog("Unable to connect to server");
            connIssue = true;
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;


    }

    protected void onPostExecute(Void unused) {
        // NOTE: You can call UI Element here.

        System.out.println("INSIDE ONPOST EXECUTE");
//        progress.dismiss();

        if (connIssue) {
            // showDialog("Connectivity Issue", "Problem getting response from server..");
            connIssue = false;
            return;
        }
        if (jsonRespStr != "") try {
            processResponse();
            //Intent newIntent = new Intent(curActivity, nextActivityClass);
            //curActivity.startActivity(newIntent);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void processResponse() throws JSONException {

        //Intent intent = new Intent(curActivity, ImageCapture.class);
        //curActivity.startActivity(intent);

        if (AllPlates !=null && AllPlates.size() > 0){
            plateTextView.setText(AllPlates.get(0).getPlateNum());
        }
        System.out.println("ZZZZ: Size of array is :"+AllPlates.size());
    }


}