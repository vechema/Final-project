package com.aptmini.jreacs.connexus;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateAGather extends FragmentActivity {
    Context context = this;
    static int startYear;
    static int startMonth;
    static int startDay;
    static int startHour;
    static int startMinute;
    static int endYear;
    static int endMonth;
    static int endDay;
    static int endHour;
    static int endMinute;
    String title;
    String address;
    double lat;
    double lng;
    List<String> numbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_a_gather);
        numbers = new ArrayList<String>();
        final Calendar c = Calendar.getInstance();
        startYear = c.get(Calendar.YEAR);
        startMonth = c.get(Calendar.MONTH);
        startDay = c.get(Calendar.DAY_OF_MONTH);
        startHour = c.get(Calendar.HOUR_OF_DAY);
        startMinute = c.get(Calendar.MINUTE);
    }

    //Define a fragment which will help us display a date picker dialog.
    //Default is current date
    public static class StartDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            System.out.println(year);
            System.out.println(month);
            System.out.println(day);
            startYear = year;
            startMonth = month;
            startDay = day;
        }
    }

    //Show the start-date picker dialog when the button is pressed
    public void showStartDatePickerDialog(View v) {
        DialogFragment newFragment = new StartDatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    //Define a fragment which will help us display a start time picker dialog.
    public static class StartTimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            System.out.println(hourOfDay);
            System.out.println(minute);
            startHour = hourOfDay;
            startMinute = minute;
        }
    }

    //Show the start-time picker dialog when the button is pressed
    public void showStartTimePickerDialog(View v) {
        DialogFragment newFragment = new StartTimePickerFragment();
        newFragment.show(getFragmentManager(),"timePicker");
    }

    //Define a fragment which will help us display a date picker dialog.
    //Default is start date
    public static class EndDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the start date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = startYear;
            int month = startMonth;
            int day = startDay;

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            System.out.println(year);
            System.out.println(month);
            System.out.println(day);
        }
    }

    //Show the start-date picker dialog when the button is pressed
    public void showEndDatePickerDialog(View v) {
        DialogFragment newFragment = new EndDatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    //Define a fragment which will help us display a start time picker dialog.
    public static class EndTimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = startHour + 1;
            if(hour == 24){
                hour = 0;
            }
            int minute = startMinute;

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            System.out.println(hourOfDay);
            System.out.println(minute);
        }
    }

    //Show the start-time picker dialog when the button is pressed
    public void showEndTimePickerDialog(View v) {
        DialogFragment newFragment = new EndTimePickerFragment();
        newFragment.show(getFragmentManager(),"timePicker");
    }


    //Make the gather!
    public void makeGather(View v){
        //get number(s) input
        EditText txtphoneNo = (EditText) findViewById(R.id.guests);
        String numberString = txtphoneNo.getText().toString();
        System.out.println("I AM DEBUGGING!!!!!");
        numbers.add(numberString);

        EditText txtAddress = (EditText) findViewById(R.id.gather_location);
        address = txtAddress.getText().toString();
        System.out.println(address);
        GeoPoint gatherPoint = getLocationFromAddress(address);
        lat = gatherPoint.lat;
        lng = gatherPoint.lng;

        System.out.println(lat);
        System.out.println(lng);

        //sendSMSMessage();
    }

    //Get latitude and longitude from the address input
    public GeoPoint getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new GeoPoint(location.getLatitude(),location.getLongitude());

            return p1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

        //Pass the Gather information to the backend
    //Step 1, get the upload URL
    private void getUploadURL(){
        AsyncHttpClient httpClient = new AsyncHttpClient();
        String request_url="http://gather.appspot.com/CreateGather";
        System.out.println(request_url);
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            String upload_url;

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {

                try {
                    JSONObject jObject = new JSONObject(new String(response));

                    upload_url = jObject.getString("upload_url");
                    postToServer(upload_url);

                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("Get_serving_url", "There was a problem in retrieving the url : " + e.toString());
            }
        });
    }

    //Send the Gather information out as a text message
    protected void sendSMSMessage() {
        Log.i("Send SMS", "");
        String phoneNo = numbers.get(0);
        String message = "You have been invited to a gather!";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    //Pass the Gather information to the backend
    //Step 2, input the information into the request parameters and send it to the backend.
    private void postToServer(String upload_url){
        RequestParams params = new RequestParams();
        params.put("startYear",startYear);
        params.put("startMonth",startMonth);
        params.put("startDay", startDay);
        params.put("startHour", startHour);
        params.put("startMinute", startMinute);
        params.put("endYear",endYear);
        params.put("endMonth",endMonth);
        params.put("endDay", endDay);
        params.put("endHour", endHour);
        params.put("endMinute", endMinute);
        params.put("title", title);
        params.put("address", address);
        params.put("numbers", numbers);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(upload_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                Log.w("async", "success!!!!");
                Toast.makeText(context, "Gather Created Successfully!", Toast.LENGTH_SHORT).show();
//                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
            }
        });
    }
}

