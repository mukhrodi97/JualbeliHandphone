package id.my.mukhrodi.mukhrodi_databaseapiproject;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import id.my.mukhrodi.mukhrodi_databaseapiproject.adapter.listAdapterHanphone;
import id.my.mukhrodi.mukhrodi_databaseapiproject.model.Handphone;
import id.my.mukhrodi.mukhrodi_databaseapiproject.server.AsyncInvokeURLTask;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final String TAG = "MainActivity";
    private ListView listView;
    private ActionMode actionMode;
    private ActionMode.Callback amCallback;
    private List<Handphone> listhp;
    private listAdapterHanphone adapter;
    private Handphone selectedList;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
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
        builder.setPositiveButton("Yes", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listhp.remove(selectedList);
                        Toast.makeText(getApplicationContext(), "deleted",
                                Toast.LENGTH_SHORT).show();
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
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
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
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("nama");
        return true;}

    public ComponentName getComponentName() {
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_menu_new) {
            Intent in = new Intent(getApplicationContext(),
                    FormHandphone.class);
            startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }

    public Context getApplicationContext() {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }
    private void populateListView() {
        adapter = new listAdapterHanphone(getApplicationContext(),
                this.listhp);
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
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
            ArrayList<NameValuePair> nameValuePairs = new
                    ArrayList<NameValuePair>(0);
            AsyncInvokeURLTask task = new AsyncInvokeURLTask(nameValuePairs, new AsyncInvokeURLTask.OnPostExecuteListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onPostExecute(String result) {
// TODO Auto-generated method stub
                    Log.d("TAG", "Login:" + result);
                    if (result.equals("timeout") ||result.trim().equalsIgnoreCase("Tidak dapat Terkoneksi ke Data Base")) {
                        Toast.makeText(getBaseContext(), "Tidak Dapat Terkoneksi dengn Server", Toast.LENGTH_SHORT).show();
                    } else {
                        processResponse(result);
                        populateListView();
                    }
                }
            });
            task.showdialog = true;
            task.message = "Load Data HP Harap Tunggu..";
            task.applicationContext = MainActivity.this;
            task.mNoteItWebUrl = "/select_all.php";
            task.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
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