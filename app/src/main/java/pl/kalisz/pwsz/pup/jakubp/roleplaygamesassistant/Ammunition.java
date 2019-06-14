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

public class Ammunition extends AppCompatActivity {

    public static final String TABLE_NAME = "ammunition";
    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String AMOUNT = "amount";

    private DatabaseHelper helper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ammunition);
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
        ListView listViewAmmunition = findViewById(R.id.list_view_ammunition);
        Cursor cursor = database.query(TABLE_NAME, new String[]{_ID, NAME, AMOUNT}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.ammunition_item, cursor, new String[]{"_id", "name", "amount"}, new int[]{R.id.ammunition_id, R.id.ammunition_name, R.id.ammunition_amount}, 0);
            listViewAmmunition.setAdapter(listAdapter);
        }

        listViewAmmunition.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, SelectedAmmunition.class);
            intent.putExtra(SelectedAmmunition.EXTRA_AMMUNITION_ID, (int) id);
            startActivity(intent);
        });

        listViewAmmunition.setOnItemLongClickListener((parent, view, position, id) -> {
            dialogDelete((int)id);
            return false;
        });

        helper.close();
        database.close();
    }

    private void dialogInsert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_dialog_input_ammunition);
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
        ContentValues newRow = new ContentValues();
        helper = DatabaseHelper.getInstance(this);
        database = helper.getWritableDatabase();
        newRow.put(NAME, name);
        newRow.put(AMOUNT, 0);
        database.insert(TABLE_NAME, null, newRow);

        helper.close();
        database.close();
        finish();
        startActivity(getIntent());
    }

    private void delete(int id) {
        helper = DatabaseHelper.getInstance(this);
        database = helper.getWritableDatabase();
        database.delete(TABLE_NAME, _ID + "=" + id, null);
        helper.close();
        database.close();
        finish();
        startActivity(getIntent());
    }
}
