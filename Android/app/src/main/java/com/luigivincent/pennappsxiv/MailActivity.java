package com.luigivincent.pennappsxiv;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MailActivity extends AppCompatActivity {
    private static final Map<String, String> providerMap = new HashMap<>();
    static {
        providerMap.put("Alltell", "sms.alltelwireless.com");
        providerMap.put("AT&T", "txt.att.net");
        providerMap.put("Boost Mobile", "sms.myboostmobile");
        providerMap.put("Cricket Wireless", "mms.cricketwireless.net");
        providerMap.put("Project Fi", "msg.fi.google.com");
        providerMap.put("Republic Wireless", "text.republicwireless.com");
        providerMap.put("Sprint", "messaging.sprintpcs.com");
        providerMap.put("T-mobile", "tmomail.net");
        providerMap.put("U.S. Cellular", "email.uscc.net");
        providerMap.put("Verizon", "vzwpix.com");
        providerMap.put("Virgin Mobile", "vmobl.com");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);

        TextView nameField = (TextView) findViewById(R.id.nameField);
        TextView numberField = (TextView) findViewById(R.id.numberField);
        TextView emailField = (TextView) findViewById(R.id.emailField);
        TextView itemField = (TextView) findViewById(R.id.itemField);
        final TextView detailField = (TextView) findViewById(R.id.detailField);

        Button submitButton = (Button) findViewById(R.id.submitButton);
        ImageButton callButton = (ImageButton) findViewById(R.id.callButton);

        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        String number = intent.getStringExtra("number");
        final String email = intent.getStringExtra("email");
        final String item = intent.getStringExtra("item");
        String provider = intent.getStringExtra("provider");
        final String dialNumber = number.replaceAll("-", "");
        final String smsTarget = number + "@" + providerMap.get(provider);

        itemField.setText(item);

        boolean isPublic = intent.getStringExtra("public").equals("y");
        if (isPublic) {
            nameField.setText(name);

            numberField.setText(number);
            numberField.setTextAppearance(android.R.style.TextAppearance_Medium);
            numberField.setTextColor(ContextCompat.getColor(this, R.color.colorTextContrast));

            emailField.setText(email);
            emailField.setTextAppearance(android.R.style.TextAppearance_Small);
            emailField.setTextColor(ContextCompat.getColor(this, R.color.colorTextContrast));

            callButton.setVisibility(View.VISIBLE);
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri number = Uri.parse("tel:" + dialNumber);
                    Intent dial = new Intent(Intent.ACTION_CALL, number);
                    if ((ContextCompat.checkSelfPermission(v.getContext(), "android.permission.CALL_PHONE") == PackageManager.PERMISSION_GRANTED)) {
                        startActivity(dial);
                    } else {
                        Toast.makeText(MailActivity.this, "Call Permission required", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String recipient = smsTarget + "," + email;
                new SendMailTask(
                        MailActivity.this,
                        recipient,
                        "Alert concerning " + item,
                        "Hello, " + name + ",\n\n" + detailField.getText().toString()
                ).execute();
            }
        });
    }
}
