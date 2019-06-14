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
import android.widget.Toast;

import java.util.Objects;

public class SelectedAmmunition extends AppCompatActivity {
    public static final String EXTRA_AMMUNITION_ID = "ammunitionId";
    private int id;
    private int amount;
    private  TextView amountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_ammunition);

        amountView = (TextView)findViewById(R.id.ammunition_selected_amount);
        findViewById(R.id.action_ammunition_shot_once).setOnClickListener((View v) -> shotOnce());
        findViewById(R.id.action_ammunition_shot_several).setOnClickListener((View v) -> dialogShotSeveral());
        findViewById(R.id.action_ammunition_set_amount).setOnClickListener((View v) -> dialogSetAmount());
    }

    @Override
    protected void onResume() {
        super.onResume();

        id = (int) Objects.requireNonNull(getIntent().getExtras()).get(EXTRA_AMMUNITION_ID);

        DatabaseHelper helper = DatabaseHelper.getInstance(this);
        SQLiteDatabase database = helper.getWritableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + Ammunition.TABLE_NAME + " WHERE _id=" + id, null);

        cursor.moveToFirst();
        amount = cursor.getInt(2);

        TextView amountText = findViewById(R.id.ammunition_selected_amount);
        amountText.setText(amount + "");

        cursor.close();
        database.close();
        helper.close();
    }

    private void dialogShotSeveral() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.ammunition_action_shot_several);
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(0 + "");
        builder.setView(input);

        builder.setPositiveButton(R.string.accept, (dialog, which) -> shotSeveral(Integer.parseInt(input.getText().toString())));

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void dialogSetAmount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.ammunition_action_set_amount);
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(amount + "");
        builder.setView(input);

        builder.setPositiveButton(R.string.accept, (dialog, which) -> setAmount(Integer.parseInt(input.getText().toString())));

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void shotOnce() {
        if (amount > 0) {
            DatabaseHelper helper = DatabaseHelper.getInstance(this);
            SQLiteDatabase database = helper.getWritableDatabase();

            ContentValues newAmount = new ContentValues();
            newAmount.put(Ammunition.AMOUNT, amount - 1);

            database.update(Ammunition.TABLE_NAME, newAmount, "_id=" + id, null);

            database.close();
            helper.close();
            amountView.setText((amount - 1)+"");
            amount--;
        }
    }

    private void shotSeveral(int shots) {
        if(amount > 0) {
            boolean incompleteVolleyFlag = false;
            if (shots > amount) {
                shots = amount;
                incompleteVolleyFlag = true;
            }
            DatabaseHelper helper = DatabaseHelper.getInstance(this);
            SQLiteDatabase database = helper.getWritableDatabase();

            ContentValues newAmount = new ContentValues();
            newAmount.put(Ammunition.AMOUNT, amount - shots);

            database.update(Ammunition.TABLE_NAME, newAmount, "_id=" + id, null);

            database.close();
            helper.close();
            amountView.setText((amount - shots)+"");
            amount -= shots;
            if (incompleteVolleyFlag) {
                Toast toast = Toast.makeText(this, R.string.ammunition_error_not_enough_ammunition, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void setAmount(int ammunition) {
        DatabaseHelper helper = DatabaseHelper.getInstance(this);
        SQLiteDatabase database = helper.getWritableDatabase();

        ContentValues newAmount = new ContentValues();
        newAmount.put(Ammunition.AMOUNT, ammunition);

        database.update(Ammunition.TABLE_NAME, newAmount, "_id=" + id, null);

        database.close();
        helper.close();
        amountView.setText(ammunition+"");
        amount = ammunition;
    }
}
