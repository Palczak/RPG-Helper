package pl.kalisz.pwsz.pup.jakubp.roleplaygamesassistant;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SelectedHealth extends AppCompatActivity {

    public static final String EXTRA_HEALTH_ID = "healthId";
    private TextView currentView;
    private TextView maxView;
    private int id;
    private int current;
    private int max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_health);

        currentView = (TextView)findViewById(R.id.health_selected_current);
        maxView = (TextView)findViewById(R.id.health_selected_max);

        findViewById(R.id.action_health_set_max).setOnClickListener((View v) -> {
            dialogSetMax();
        });

        findViewById(R.id.action_health_take_hit).setOnClickListener((View v) -> {
            dialogTakeHit();
        });

        findViewById(R.id.action_health_heal).setOnClickListener((View v) -> {
            dialogHeal();
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        id = (int)getIntent().getExtras().get(EXTRA_HEALTH_ID);

        DatabaseHelper helper = DatabaseHelper.getInstance(this);
        SQLiteDatabase database = helper.getWritableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM "+Health.TABLE_NAME+" WHERE _id="+id,null);

        cursor.moveToFirst();
        max = cursor.getInt(2);
        current = cursor.getInt(3);

        TextView maxText = findViewById(R.id.health_selected_max);
        maxText.setText(max+"");
        TextView currentText = findViewById(R.id.health_selected_current);
        currentText.setText(current+"");

        database.close();
        helper.close();
    }

    private void dialogHeal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.health_action_heal);
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton(R.string.accept, (dialog, which) -> heal(Integer.parseInt(input.getText().toString())));

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void heal(int delta) {
        DatabaseHelper helper = DatabaseHelper.getInstance(this);
        SQLiteDatabase database = helper.getWritableDatabase();

        ContentValues newCurrent = new ContentValues();
        int finalValue;
        if(current < 0) {
            current = 0;
        }
        finalValue = current + delta;

        if(finalValue > max) {
            finalValue = max;
        }

        newCurrent.put(Health.CURRENT, finalValue);

        database.update(Health.TABLE_NAME, newCurrent, "_id="+id, null);

        database.close();
        helper.close();
        currentView.setText(finalValue+"");
        current = finalValue;
    }

    private void dialogTakeHit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.health_action_take_hit);
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton(R.string.accept, (dialog, which) -> takeHit(Integer.parseInt(input.getText().toString())));

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void takeHit(int delta) {
        DatabaseHelper helper = DatabaseHelper.getInstance(this);
        SQLiteDatabase database = helper.getWritableDatabase();

        ContentValues newCurrent = new ContentValues();
        newCurrent.put(Health.CURRENT, current - delta);

        database.update(Health.TABLE_NAME, newCurrent, "_id="+id, null);

        database.close();
        helper.close();

        currentView.setText((current-delta)+"");
        current -= delta;
    }

    private void dialogSetMax() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.health_action_set_max);
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(max+"");
        builder.setView(input);

        builder.setPositiveButton(R.string.accept, (dialog, which) -> setMax(Integer.parseInt(input.getText().toString())));

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void setMax(int newHp) {
        DatabaseHelper helper = DatabaseHelper.getInstance(this);
        SQLiteDatabase database = helper.getWritableDatabase();

        ContentValues newMax = new ContentValues();
        newMax.put(Health.MAX, newHp);

        database.update(Health.TABLE_NAME, newMax, "_id="+id, null);

        if(newHp < current) {
            ContentValues newCurrent = new ContentValues();
            newCurrent.put(Health.CURRENT, newHp);
            database.update(Health.TABLE_NAME, newCurrent, "_id="+id, null);
            currentView.setText(newHp+"");
            current = newHp;
        }

        database.close();
        helper.close();
        maxView.setText(newHp+"");
        max = newHp;
    }
}
