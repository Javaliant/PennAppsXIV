package com.luigivincent.pennappsxiv;

/**
 * Created by Luigi on 9/10/2016.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

//Class is extending AsyncTask because this class is going to perform a networking operation
public class SendMailTask extends AsyncTask<Void, Void, Void> {

    //Declaring Variables
    private Context context;

    //Information to send email
    private String email;
    private String number;
    private String subject;
    private String message;

    //Progressdialog to show while sending email
    private ProgressDialog progressDialog;

    //Class Constructor
    public SendMailTask(Context context, String email, String subject, String message){
        //Initializing variables
        this.context = context;
        String[] destinations = email.split(",");
        this.email = destinations[0];
        this.number = destinations[1];
        this.subject = subject;
        this.message = message;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        progressDialog = ProgressDialog.show(context, "Sending message", "Please wait...", false, false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismissing the progress dialog
        progressDialog.dismiss();
        //Showing a success message
        Toast.makeText(context, "Message sent successfully", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, SuccessActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("luigipvincent@gmail.com", "q2s5ccec9");
                    }
                });

        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(session);

            //Setting sender address
            mm.setFrom(new InternetAddress("luigipvincent@gmail.com"));
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(number));
            mm.setSubject(subject);
            mm.setText(message);
            Transport.send(mm);

        } catch (MessagingException e) {
            Log.d("error sending mail", "doInBackground: ");
        }
        return null;
    }
}
