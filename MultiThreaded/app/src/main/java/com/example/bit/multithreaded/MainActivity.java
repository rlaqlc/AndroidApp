package com.example.bit.multithreaded;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    public class FileTask extends AsyncTask<String, Integer, String> {
        FileTask() {
            list = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... action) {
            String str  = null;
            String filename = "numbers.txt";

            // first determine which action was passed in
            // action[0] == string data that was passed in
            switch (action[0]) {
                case "create":
                    str = "create";
                    createFile(filename);
                    break;
                case "load":
                    str = "load";
                    try {
                        loadFile(filename);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case "clear":
                    publishProgress(0);
                    str = "clear";
                    break;
                default:
                    str = "error";
            }

            // to onPostExecute(main thread)
            return str;
        }

        @Override
        protected void onPostExecute(String string) {
            // based on returned string,
            // now do the working on the UI since the background work has been completed
            switch (string) {
                case "create":
                    MainActivity.this.displayToast("\"numbers.txt\" successfully created.");
                    break;
                case "load":
                    MainActivity.this.setListView(list);
                    MainActivity.this.displayToast("\"numbers.txt\" successfully loaded.");
                    break;
                case "clear":
                    MainActivity.this.clearListView();
                    MainActivity.this.displayToast("All cleared.");
                    break;
                default:
                    MainActivity.this.displayToast("Oops! Something went wrong.");
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            setProgressPercent(progress[0]);
        }

        private void createFile(String filename) {
            // create a numbers.txt file on internal storage
            // internal storage is in MainActivity, so call getFilesDir method from there
            File file = new File(MainActivity.this.getFilesDir(), filename);
            String newline = "\n";

            // now write 1 through 10
            try {
                FileOutputStream outputStream;
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                PrintStream printStream = new PrintStream(outputStream);

                for (int i = 0; i < 10; i++) {
                    printStream.print(i + 1);
                    printStream.print(newline);
                    Thread.sleep(300);

                    // update the progress bar
                    publishProgress((i + 1) * 10);
                }
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void loadFile(String filename)
                                               throws FileNotFoundException {
            try {
                  File file = new File(MainActivity.this.getFilesDir(), filename);

                  FileReader fileReader = new FileReader(file);
                  BufferedReader bufferedReader = new BufferedReader(fileReader);

                  String line = null;
                  int count = 1;

                  // read numbers.txt line by line
                  for (int i = 0; (line = bufferedReader.readLine()) != null; i++) {
                      list.add(i + 1); // add to ArrayList
                      Thread.sleep(300);
                      // update the progress bar for each line
                      publishProgress((i + 1) * 10);
                  }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private List<Integer> list;
    } // end of FileTask class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void create(View view) {
        String create = "create";
        new FileTask().execute("create");
    }

    public void load(View view) {
        String load = "load";
        new FileTask().execute(load);
    }

    public void clear(View view) {
        String clear = "clear";
        new FileTask().execute(clear);
    }

    private void displayToast(String string) {
        Context context = getApplicationContext();
        CharSequence text = string;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);

        toast.show();
    }

    private void setProgressPercent(Integer percent) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(percent);
    }

    private void setListView(List<Integer> list) {
        integerArrayAdapter = new ArrayAdapter<Integer>(this,
                                                        android.R.layout.simple_list_item_1,
                                                        list);
        // connect the adapter and the list
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(integerArrayAdapter);
    }

    private void clearListView() {

        integerArrayAdapter.clear(); // clear
        integerArrayAdapter.notifyDataSetChanged(); // update
    }

    private ArrayAdapter<Integer> integerArrayAdapter;
}