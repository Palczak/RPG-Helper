package pl.kalisz.pwsz.pup.jakubp.roleplaygamesassistant;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Currency;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase database;
    private DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            helper = DatabaseHelper.getInstance(this);
            database = helper.getWritableDatabase();
        } catch (SQLException e) {
            Toast toast = Toast.makeText(this, R.string.database_error, Toast.LENGTH_LONG);
            toast.show();
            finish();
        }

        findViewById(R.id.health).setOnClickListener((View v) -> {
            startActivity(new Intent(this, Health.class));
        });

        findViewById(R.id.ammunition).setOnClickListener((View v) -> {
            startActivity(new Intent(this, Ammunition.class));
        });

        findViewById(R.id.currency).setOnClickListener((View v) -> {
            startActivity(new Intent(this, Money.class));
        });
    }
}
