package pl.kalisz.pwsz.pup.jakubp.roleplaygamesassistant;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.Objects;

public class Health extends AppCompatActivity {

    public static final String TABLE_NAME = "health";
    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String MAX = "max";
    public static final String CURRENT = "current";

    private DatabaseHelper helper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_health);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> dialogInsert());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper = DatabaseHelper.getInstance(this);
        database = helper.getWritableDatabase();

        ListView listViewHealth = findViewById(R.id.list_view_health);
        Cursor cursor = database.query(TABLE_NAME, new String[]{_ID, NAME, MAX, CURRENT}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.health_item, cursor, new String[]{"_id", "name", "max", "current"}, new int[]{R.id.health_id, R.id.health_name, R.id.health_max, R.id.health_current}, 0);
            listViewHealth.setAdapter(listAdapter);
        }

        listViewHealth.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, SelectedHealth.class);
            intent.putExtra(SelectedHealth.EXTRA_HEALTH_ID, (int) id);
            startActivity(intent);
        });

        listViewHealth.setOnItemLongClickListener((parent, view, position, id) -> {
            dialogDelete((int)id);
            return false;
        });
        helper.close();
        database.close();
    }


    private void dialogInsert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_dialog_input_health);
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(R.string.accept, (dialog, which) -> insert(input.getText().toString()));

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void dialogDelete(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_delete);

        builder.setPositiveButton(R.string.delete, (dialog, which) -> delete(id));

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void insert(String name) {
        helper = DatabaseHelper.getInstance(this);
        database = helper.getWritableDatabase();
        ContentValues newHealth = new ContentValues();
        newHealth.put(NAME, name);
        newHealth.put(MAX, 0);
        newHealth.put(CURRENT, 0);
        database.insert(TABLE_NAME, null, newHealth);
        finish();
        startActivity(this.getIntent());
        helper.close();
        database.close();
    }

    private void delete(int id) {
        helper = DatabaseHelper.getInstance(this);
        database = helper.getWritableDatabase();
        database.delete(TABLE_NAME, _ID + "=" + id, null);
        finish();
        startActivity(this.getIntent());
        helper.close();
        database.close();
    }
}