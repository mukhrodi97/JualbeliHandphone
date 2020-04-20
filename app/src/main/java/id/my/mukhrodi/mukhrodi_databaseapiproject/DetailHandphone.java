package id.my.mukhrodi.mukhrodi_databaseapiproject;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import id.my.mukhrodi.mukhrodi_databaseapiproject.model.Handphone;
import id.my.mukhrodi.mukhrodi_databaseapiproject.server.AsyncInvokeURLTask;

public class DetailHandphone extends AppCompatActivity {
    public static final String urlDelete = "delete_phone.php";
    private EditText textNama, textHarga;
    private Handphone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(Bundle savedInstanceState);
        setContentView(R.layout.activity_detail_handphone);
        handphone =  new Handphone();
        initView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void initView(){
        textNama = (EditText) findViewById(R.id.add_new_nama);
        textHarga = (EditText) findViewById(R.id.add_new_harga);
        String id = getIntent().getStringExtra("id");
        String nama = getIntent().getStringExtra("nama");
        String harga = getIntent().getStringExtra("harga");
        textNama.setText(nama);
        textHarga.setText(harga);
        handphone.setId(Integer.valueOf(id));
        handphone.setNama(nama);
        handphone.setHarga(harga);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                goToMainActivity();
                break;
            case R.id.action_menu_edit:
                Intent in = new Intent(getApplicationContext(),FormHandphone.class);
                in.putExtra("id", handphone.getId().toString());
                in.putExtra("nama", handphone.getNama());
                in.putExtra("harga", handphone.getharga());
                startActivity(in);
                break;
            case R.id.action_menu_delete:
                urlDelete();
                break;
        }
        return  super.onOptionsItemSelected(item);
    }
    private void goToMainActivity() {
        Intent in = new Intent(getApplicationContext(),MainActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        StartActivity(in);
    }
    private  void urlDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete"+handphone.getNama()+"?");
        builder.setTitle("Delete");
        builder.setPositiveButton("Yes", new DatePickerDialog.OnClickListener() {
            public void  onClick(DialogInterface dialog, int which) {
                deleteData();
                Toast.makeText(getApplicationContext(), "deleted",Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alert = builder.create();
        alert.setIcon(android.R.drawable.ic_menu_delete);
        alert.show();
    }
public void deleteData() {
        try {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new AsyncInvokeURLTask(nameValuePairs,
                    new AsyncInvokeURLTask.OnPostExecuteListener() {
                        @Override
                        public void onPostExecute(String result) {
                            Log.d("TAG", "Delete :" + result);
                            if (result.equals("timeout") ||
                                    result.trim().equalsIgnoreCase("Tidak dapat Terkoneksi ke Data Base")){
                                Toast.makeText(getBaseContext(), "Tidak Dapat Terkoneksi dengan Server", Toast.LENGTH_SHORT).show();
                            }else{
                                goToMainActivity();
                            }
                        }
                    });
            task.showdialog=true;
            task.message="Proses Delete Data Harapan Tuggu..";
            task.applicationContext =DetailHandphone.this;
            task.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
