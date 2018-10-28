package com.sivector.android.appointmentapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class AppointmentActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    private Client client;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText companyNameEditText;
    private EditText reasonVisitEditText;
    private RadioGroup visitRadioGroup;
    private RadioButton businessButton;
    private RadioButton personalButton;
    private Button scheduleAppointmentButton;

    private SharedPreferences sharedPreferences;

    // Log D - Debugging purposes: Track variable state.
    private static final String APPOINTMENT_ACTIVITY = "AppointmentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        // get references to View objects.
        firstNameEditText = findViewById(R.id.fNameText);
        lastNameEditText = findViewById(R.id.lNameText);
        companyNameEditText = findViewById(R.id.cNameText);
        reasonVisitEditText = findViewById(R.id.reasonVisitText);
        businessButton = findViewById(R.id.businessButton);
        personalButton = findViewById(R.id.personalButton);

        // write up OnClickLister to scheduleAppointmentButton
        // Handle Schedule Appointment Button Click.
        scheduleAppointmentButton = findViewById(R.id.scheduleAppointButton);
        scheduleAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(AppointmentActivity.this, "Appointment Scheduled", Toast.LENGTH_LONG).show();
                getClientInformation();
                sendEmail();
                encryptDecryptClientInformation();
            }
        });

        // wire up Radio Group.
        visitRadioGroup = findViewById(R.id.visitRadioGroup);
        visitRadioGroup.setOnCheckedChangeListener(this);


        // get shared reference object. Create file, Make it only private to Application.
        // save application state on device orientation change.
        sharedPreferences = getSharedPreferences("CLIENTDATA", MODE_PRIVATE);
    }

    @Override
    protected void onPause() {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("FNAME", firstNameEditText.getText().toString());
        edit.putString("LNAME", lastNameEditText.getText().toString());
        edit.putString("CNAME", companyNameEditText.getText().toString());
        edit.putString("REASONVISIT", companyNameEditText.getText().toString());
        edit.commit();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String firstName = sharedPreferences.getString("FNAME", "");
        String lastName = sharedPreferences.getString("LNAME", "");
        String companyName = sharedPreferences.getString("CNAME", "" );
        String reasonforVisit = sharedPreferences.getString("RESONVISIT", "");
    }

    // Client Object state.
    public void getClientInformation(){

        client = new Client();
        client.setFirstName(firstNameEditText.getText().toString());
        client.setLastName(lastNameEditText.getText().toString());
        client.setCompanyName(companyNameEditText.getText().toString());
        client.setReasonForVisit(reasonVisitEditText.getText().toString());
        Log.d(APPOINTMENT_ACTIVITY, client.toString());

    }

    public void encryptDecryptClientInformation(){
        Encrypter encrypter = new Encrypter(client);
        encrypter.encrypt();
        encrypter.decrypt();
    }


    public void sendEmail(){
        String [] To = {"lmburu.lm@gmail.com"};
        PackageManager packageManager = getPackageManager();
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        if ( emailIntent.resolveActivity(packageManager) == null  ) {
            Log.d(APPOINTMENT_ACTIVITY, "No Component to Handle Email");
        }

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, To);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "You have an Appointment");
        emailIntent.putExtra(Intent.EXTRA_TEXT,
                "Your Appointment's Information: \n\n" +
                        "Name: "
                + client.getFirstName() + " "
                + client.getLastName()  + "\n" +
                        "Company Name: "
                + client.getCompanyName() + "\n" +
                        "Reason for Visit: "
                + client.getReasonForVisit()
        );

        startActivity(emailIntent);

    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.businessButton:
                personalButton.setAlpha(0.1f);
                break;
            case R.id.personalButton:
                businessButton.setAlpha(0.1f);
                break;
        }
    }
}
