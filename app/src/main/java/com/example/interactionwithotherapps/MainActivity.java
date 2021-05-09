package com.example.interactionwithotherapps;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void tel(View view) {
        Uri number = Uri.parse("tel:5551234");
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
//        startActivity(callIntent);
        startActivityIfSafe(callIntent);
    }

    public void map(View view) {
        // Map point based on address
        Uri location = Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
        // Or map point based on latitude/longitude
        // Uri location = Uri.parse("geo:37.422219,-122.08364?z=14");  // z param is zoom level
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
//        startActivity(mapIntent);
        startActivityIfSafe(mapIntent);
    }

    public void web(View view) {
        Uri webpage = Uri.parse("http://www.android.com");
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
//        startActivity(webIntent);
        startActivityIfSafe(webIntent);
    }

    public void email(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // The intent does not have a URI, so declare the "text/plain" MIME type
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"jon@example.com"}); // recipients
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Email subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message text");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://path/to/email/attachment"));
        // You can also attach multiple items by passing an ArrayList of Uris
//        startActivity(emailIntent);
        startActivityIfSafeByChooser(emailIntent);
    }

    // カレンダーに予定を追加
    public void cal(View view) {
        Intent calendarIntent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2012, 0, 19, 7, 30);
        Calendar endTime = Calendar.getInstance();
        endTime.set(2012, 0, 19, 10, 30);
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());
        calendarIntent.putExtra(CalendarContract.Events.TITLE, "Ninja class");
        calendarIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "Secret dojo");
//        startActivity(calendarIntent);
        startActivityIfSafe(calendarIntent);
    }

    // 起動できるアプリがあるか確認後にIntentを実行する（アプリがなくて落ちるのを防ぐため）
    private void startActivityIfSafe(Intent intent) {
        PackageManager packageManager = getPackageManager();
        List activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;
        if (isIntentSafe) {
            startActivity(intent);
        }
    }

    private void startActivityIfSafeByChooser(Intent intent) {
        // Always use string resources for UI text.
        // This says something like "Share this photo with"
        String title = getResources().getString(R.string.chooser_title);
        // Create intent to show chooser
        Intent chooser = Intent.createChooser(intent, title);
        startActivityIfSafe(chooser);
    }

    static final int PICK_CONTACT_REQUEST = 1;  // The request code

    public void contact(View view) {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        // Show user only contacts w/ phone numbers
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    static final int PICK_TEXT_REQUEST = 2;   // The request code

    public void request(View view) {
        Intent pickTextIntent = new Intent(Intent.ACTION_SEND);
        pickTextIntent.setType("text/plain");
        pickTextIntent.putExtra(pickTextIntent.EXTRA_TEXT, "Sent text message.");
        startActivityForResult(pickTextIntent, PICK_TEXT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check which request it is that we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER };

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a saparate thread to avoid blocking
                // your app's UI thread. (For simplicity of the saple, this code doesn't do that.)
                // Consider using CoursorLoader to perform the query.
                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);

                // Do something with the phone number...
                TextView textView = (TextView) findViewById(R.id.hello_text);
                textView.setText("Phone number: " + number);
            }
        } else if (requestCode == PICK_TEXT_REQUEST) {
            if (resultCode == RESULT_OK) {
                String text = data.getStringExtra(Intent.EXTRA_TEXT);
                TextView textView = (TextView) findViewById(R.id.hello_text);
                textView.setText("Picked text: " + text);
            }
        }
    }
}