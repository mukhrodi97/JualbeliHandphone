package id.my.mukhrodi.mukhrodi_databaseapiproject.server;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AsyncInvokeURLTask extends AsyncTask<Void, Void, String> {
    public String mNoteItWebUrl = "www.siakad-altie.com";
    private ArrayList<NameValuePair> mParams;
    private OnPostExecuteListener mPostExecuteListener = null;
    private ProgressDialog dialog;
    public boolean showdialog =false;
    public String message ="Proses Data";
    //Harus diganti dengan IP Server yang digunakan
    public String url_server ="https://mukhrodi.000webhostapp.com/xphone/";
    @SuppressLint("StaticFieldLeak")
    public Context applicationContext;
    public static interface OnPostExecuteListener{
        void onPostExecute(String result);
    }
    public AsyncInvokeURLTask(ArrayList<NameValuePair> nameValuePairs, OnPostExecuteListener postExecuteListener) throws Exception {
        mParams = nameValuePairs;
        mPostExecuteListener = postExecuteListener;
        if (mPostExecuteListener == null)
            throw new Exception("Param cannot be null.");
    }
    @Override
    public void onPreExecute() {
        if (showdialog)
            this.dialog =
                    ProgressDialog.show(applicationContext,message, "Silakan Menunggu...",
                            true);
    }
    @Override
    public void onPostExecute(String result) {
        if (mPostExecuteListener != null){
            try {
                //JSONObject json = new JSONObject(result);
                if (showdialog)this.dialog.dismiss();
                mPostExecuteListener.onPostExecute(result);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public String doInBackground(Void... params) {
        String result = "timeout";
// Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url_server+mNoteItWebUrl);
        try {// Add parameters
            httppost.setEntity(new UrlEncodedFormEntity(mParams));
// Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            if (entity != null){
                InputStream inStream = entity.getContent();
                result = convertStreamToString(inStream);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    private static String convertStreamToString(InputStream is){
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}