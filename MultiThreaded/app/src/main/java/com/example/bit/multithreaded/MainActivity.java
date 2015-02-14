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
   /*
    How AsyncTask Works

    We start by creating an instance of FileTask class and call execute function.
      new FileTask().execute("some string")
    For this assignment, a string will be passed in, but it can be whatever type you want.

    The type can be specified in AsyncTask<HERE, Some_Type, Some_Type>
    The 1st place is the place where your have to specify your type that will be
    passed into doInBackground function.

    Because we can pass multiple items through execution, we use [0] to access the passed data.

    When AsyncTask executes, it first calls onPreExecute(). doInBackground() follows right after
    it. Note that except doInBackground function, all other "override" functions are the main
    thread, aka UI thread. Main (UI) thread only needs to handle manipulating the UI stuff while
    the background thread handles creating and writing to a file and reading numbers from the file
    and add into an ArrayList.

    When doInBackground is finished, it will return the type that you have specified
    in AsyncTask<Some_Type, Some_Type, HERE>. It will be passed into onPostExecute method.
    Since the function is main thread, you do the UI work like displaying message or populating
    the list view.

    You shouldn't call those overridden methods manually because it will be called automatically.
    However,onProgressUpdate, which is also a main thread, can be called in background thread using
    publishProgress() method. Note that all other local methods that are being called in
    doInBackground(),such as createFile and loadFile belongs to background thread.
    Just think about it for a moment.

    AsyncTask<Some_Type, HERE, Some_Type>. The middle one is used by onProgressUpdate();

    Written by Bit Kim (2015 Feb 14 3:22 PM)
    */
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
            // creating file and loading file are background work
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
                    publishProgress(0); // calling onProgressUpdate
                    str = "clear";
                    break;
                default:
                    str = "error";
            }

            // pass this to onPostExecute(main thread)
            return str;
        }

        @Override
        protected void onPostExecute(String string) {
            // based on returned string,
            // now do the working on the UI since the background work has been completed
            // if you look carefully, works are all related to UI manipulations, such as displaying
            // message, setting the list view, and clearing the list view
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

                // wrap OutputStream with PrintStream just so you can
                // pass your string data without encoding to bytes code
                PrintStream printStream = new PrintStream(outputStream);

                for (int i = 0; i < 10; i++) {
                    // write a number then a new line character
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
                  // open numbers.txt from internal storage
                  File file = new File(MainActivity.this.getFilesDir(), filename);

                  FileReader fileReader = new FileReader(file);
                  BufferedReader bufferedReader = new BufferedReader(fileReader);

                  String line = null;

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

    MainActivity() {
        integerArrayAdapter = null;
    }

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

    /*
     ArrayAdapter

     ArrayAdapter converts data like an ArrayList to ListView and displays it, so it should be
     handled in the main thread(UI thread)
     */
    private void setListView(List<Integer> list) {
        integerArrayAdapter = new ArrayAdapter<Integer>(this, // context
                                                        android.R.layout.simple_list_item_1, // layout
                                                        list); // the list that contains numbers

        ListView listView = (ListView) findViewById(R.id.listView);
        // connect the adapter and the list
        // and this will do all the work like converting and displaying in the listview
        listView.setAdapter(integerArrayAdapter);
    }

    private void clearListView() {
        integerArrayAdapter.clear(); // clear
        integerArrayAdapter.notifyDataSetChanged(); // update
    }

    private ArrayAdapter<Integer> integerArrayAdapter;
}