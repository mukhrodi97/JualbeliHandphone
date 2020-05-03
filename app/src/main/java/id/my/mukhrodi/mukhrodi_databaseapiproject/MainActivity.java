package id.my.mukhrodi.mukhrodi_databaseapiproject;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import id.my.mukhrodi.mukhrodi_databaseapiproject.adapter.ListAdapterHandphone;
import id.my.mukhrodi.mukhrodi_databaseapiproject.model.Handphone;
import id.my.mukhrodi.mukhrodi_databaseapiproject.server.AsyncInvokeURLTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity<refresh> extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final String TAG = "MainActivity";
    public static final String urlDelete = "delete_phone.php";
    private ListView listView;
    private ActionMode actionMode;
    private ActionMode.Callback amCallback;
    private List<Handphone> listhp;
    private ListAdapterHandphone adapter;
    private Handphone selectedList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listview_main);
        amCallback = new ActionMode.Callback() {
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
            }
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.activity_main_action, menu);
                return true;
            }
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_menu_edit:
                        showUpdateForm();
                        break;
                    case R.id.action_menu_delete:
                        delete();
                        break;
                }
                mode.finish();
                return false;
            }
        };
        listhp = new ArrayList<Handphone>();
        loadDataHP();
    }
    private void showUpdateForm() {
        Intent in = new Intent(getApplicationContext(), FormHandphone.class);in.putExtra("id", selectedList.getId().toString());
        in.putExtra("nama", selectedList.getNama());
        in.putExtra("harga", selectedList.getHarga());
        startActivity(in);
    }
    private void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete " + selectedList.getNama() + " ?");
        builder.setTitle("Delete");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //listhp.remove(listhp.indexOf(selectedList));
                deleteData();
                Toast.makeText(getApplicationContext(), "deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.setIcon(android.R.drawable.ic_menu_delete);
        alert.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.option_menu_search);
        SearchView searchView = (SearchView)
                searchItem.getActionView();
        if (searchView == null) {
            MenuItemCompat.setShowAsAction(searchItem, MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_ALWAYS);
            MenuItemCompat.setActionView(searchItem, searchView = new SearchView(MainActivity.this));
        }
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text",
                null, null);
        TextView textView = (TextView) searchView.findViewById(id);
        textView.setTextColor(Color.WHITE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("nama");
        return true;}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_menu_new:
                Intent in = new Intent(getApplicationContext(),
                        FormHandphone.class);
                startActivity(in);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void processResponse(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);
            JSONArray jsonArray = jsonObj.getJSONArray("handphone");
            Log.d(TAG, "data length: " + jsonArray.length());
            Handphone handphone = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                handphone = new Handphone();
                handphone.setId(obj.getInt("id"));
                handphone.setNama(obj.getString("nama"));
                handphone.setHarga(obj.getString("harga"));
                this.listhp.add(handphone);
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
    }
    private void populateListView() {
        adapter = new ListAdapterHandphone(getApplicationContext(), this.listhp);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View v, int pos, long id) {
                if (actionMode != null) {
                    return false;
                }
                actionMode = startActionMode(amCallback);
                v.setSelected(true);
                selectedList = (Handphone) adapter.getItem(pos);
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int pos,long id) {
                selectedList = (Handphone) adapter.getItem(pos);
                Intent in = new Intent(getApplicationContext(), DetailHandphone.class);
                in.putExtra("id", selectedList.getId().toString());
                in.putExtra("nama", selectedList.getNama());
                in.putExtra("harga", selectedList.getHarga());
                startActivity(in);
            }
        });
    }
    public void loadDataHP() {
        try {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(0);
            AsyncInvokeURLTask task = new AsyncInvokeURLTask(nameValuePairs, new AsyncInvokeURLTask.OnPostExecuteListener() {
                @Override
                public void onPostExecute(String result) {
//TODO Auto-generated method stub
                    Log.d("TAG", "Login:" + result);
                    if (result.equals("timeout") || result.trim().equalsIgnoreCase("Tidak dapat Terkoneksi ke Data Base")) {
                        Toast.makeText(getBaseContext(), "Tidak Dapat Terkoneksi dengn Server", Toast.LENGTH_SHORT).show();
                    } else {
                        processResponse(result);
                        populateListView();
                    }
                }
            });
            task.showdialog = true;
            task.message = "Load Data HP Please Wait...";
            task.applicationContext = MainActivity.this;
            task.mNoteItWebUrl = "list_phone.php";
            task.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void deleteData() {
        try {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(0);
            nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(selectedList.getId())));
            AsyncInvokeURLTask task = new AsyncInvokeURLTask(nameValuePairs, new AsyncInvokeURLTask.OnPostExecuteListener() {
                @Override
                public void onPostExecute(String result) {
                    Log.d("TAG", "Login:" + result);
                    if (result.equals("timeout") ||result.trim().equalsIgnoreCase("Tidak dapat Terkoneksi ke Data Base")) {
                        Toast.makeText(getBaseContext(), "Tidak Dapat Terkoneksi dengn Server", Toast.LENGTH_SHORT).show();
                    } else {
                        processResponse(result);
                        populateListView();
                        goToMainActivity();
                    }
                }
            });
            task.showdialog = true;
            task.message = "Load Data HP Harap Tunggu..";
            task.applicationContext = MainActivity.this;
            task.mNoteItWebUrl = urlDelete;
            task.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void goToMainActivity() {
        Intent in = new Intent(getApplicationContext(), MainActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(in);
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return true;
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}