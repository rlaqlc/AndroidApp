package com.example.bit.scripturereference;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class DisplayActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        StringBuilder stringBuilder = new StringBuilder();

        Intent intent = getIntent();
        String message = intent.getStringExtra(InputActivity.BOOK);
        String message2 = intent.getStringExtra(InputActivity.CHAPTER);
        String message3 = intent.getStringExtra(InputActivity.VERSE);

        stringBuilder.append("Your favorite scripture is: ");
        stringBuilder.append(message);
        stringBuilder.append(" ");
        stringBuilder.append(message2);
        stringBuilder.append(":");
        stringBuilder.append(message3);

        String finalStr = stringBuilder.toString();

        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(finalStr);

        displayMessage(textView, finalStr);
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

    public void displayMessage(TextView textView, String finalStr) {
        textView = (TextView) findViewById(R.id.display);
        textView.setText(finalStr);
    }
}
