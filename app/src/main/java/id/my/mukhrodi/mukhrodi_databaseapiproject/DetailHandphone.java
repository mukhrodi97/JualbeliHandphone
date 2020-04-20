package id.my.mukhrodi.mukhrodi_databaseapiproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import id.my.mukhrodi.mukhrodi_databaseapiproject.model.Handphone;

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

}
