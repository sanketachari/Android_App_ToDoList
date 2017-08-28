/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.todolist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.android.todolist.data.TaskContract;

import java.util.HashSet;

import static android.R.attr.id;
import static android.R.id.input;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


public class AddTaskActivity extends AppCompatActivity {

    // Declare a member variable to keep track of a task's selected mPriority
    private int mPriority;
    private String mcurrentTask;
    private HashSet<String> tasks;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {

            Bundle extras = intentThatStartedThisActivity.getExtras();

            if (extras != null) {

                EditText task = ((EditText) findViewById(R.id.editTextTaskDescription));
                mcurrentTask = extras.getString("task");
                task.setText(mcurrentTask);

                switch (extras.getInt("priority")) {

                    case 1:
                        ((RadioButton) findViewById(R.id.radButton1)).setChecked(true);
                        mPriority = 1;
                        break;
                    case 2:
                        ((RadioButton) findViewById(R.id.radButton2)).setChecked(true);
                        mPriority = 2;
                        break;
                    case 3:
                        ((RadioButton) findViewById(R.id.radButton3)).setChecked(true);
                        mPriority = 3;
                        break;

                    default:
                        ((RadioButton) findViewById(R.id.radButton1)).setChecked(true);
                        mPriority = 1;
                }

                tasks = (HashSet<String>) extras.getSerializable("totalTasks");
            }
            else{
                // Initialize to highest mPriority by default (mPriority = 1)
                ((RadioButton) findViewById(R.id.radButton1)).setChecked(true);
                mPriority = 1;
            }

        }
    }


    /**
     * onClickAddTask is called when the "ADD" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onClickAddTask(View view) {
        // Not yet implemented
        // Check if EditText is empty, if not retrieve input and store it in a ContentValues object
        // If the EditText input is empty -> don't create an entry
        String input = ((EditText) findViewById(R.id.editTextTaskDescription)).getText().toString();
        if (input.length() == 0) {
            Toast.makeText(this, " Task is empty, please enter valid task ", Toast.LENGTH_LONG).show();
            return;
        }


        // Insert new task data via a ContentResolver
        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();
        // Put the task description and selected mPriority into the ContentValues
        contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, input);
        contentValues.put(TaskContract.TaskEntry.COLUMN_PRIORITY, mPriority);

        //Check whether edit text is present in database, if yes update it or insert it
        Cursor cursor = null, cursor2 = null;
        String[] projection= {TaskContract.TaskEntry._ID};
        String[] selectionArgs1 = {input};
        String[] selectionArgs2 = {mcurrentTask};
        String selection = TaskContract.TaskEntry.COLUMN_DESCRIPTION + "=?";


        if (mcurrentTask != null && mcurrentTask.equals(input)){

            Log.d("SQLlite", "Updating ");
            getContentResolver().update(TaskContract.TaskEntry.CONTENT_URI, contentValues,
                    selection, selectionArgs1);

            Log.d("SQLlite", "Updated ");
            finish();
        }
        else {

            try {
                Log.d("SQLlite", "Query1 Firing ");
                cursor = getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI,
                        new String[]{TaskContract.TaskEntry.COLUMN_DESCRIPTION},
                        selection,
                        new String[]{input},
                        TaskContract.TaskEntry.COLUMN_PRIORITY);
                Log.d("SQLlite", "Query1 returned");
            } catch (Exception e) {
                Log.d("SQLlite", "Failed to asynchronously load data.");
                e.printStackTrace();
            }

            if ((cursor == null || cursor.getCount() < 1)) {


                if (mcurrentTask == null || mcurrentTask.isEmpty()) {

                    Log.d("SQLlite", "Query inserting: " + input + "mcurrentTask  null");

                    // Insert the content values via a ContentResolver
                    getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentValues);

                    // Finish activity (this returns back to MainActivity)
                    finish();
                } else {

                    Log.d("SQLlite", "Query updateing: " + input + "mcurrentTask not null");

                    // Insert the content values via a ContentResolver
                    getContentResolver().update(TaskContract.TaskEntry.CONTENT_URI, contentValues,
                            selection, selectionArgs2);

                    // Finish activity (this returns back to MainActivity)
                    finish();
                }
            } else {
                Toast.makeText(this, "Entry already exists, try again", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * onPrioritySelected is called whenever a priority button is clicked.
     * It changes the value of mPriority based on the selected button.
     */
    public void onPrioritySelected(View view) {
        if (((RadioButton) findViewById(R.id.radButton1)).isChecked()) {
            mPriority = 1;
        } else if (((RadioButton) findViewById(R.id.radButton2)).isChecked()) {
            mPriority = 2;
        } else if (((RadioButton) findViewById(R.id.radButton3)).isChecked()) {
            mPriority = 3;
        }
    }
}
