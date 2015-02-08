package com.example.bit.scripturereference;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class InputActivity extends ActionBarActivity {
    public final static String BOOK = "com.example.bit.scripturereference.BOOK";
    public final static String CHAPTER = "com.example.bit.scripturereference.CHAPTER";
    public final static String VERSE = "com.example.bit.scripturereference.VERSE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_input, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendMessages(View view) {
        Intent intent = new Intent(this, DisplayActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String book = editText.getText().toString();
        intent.putExtra(BOOK, book);
        intent.putExtra(CHAPTER, book);
        intent.putExtra(VERSE, book);
        startActivity(intent);
    }
}