package com.resume.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TabHost;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jbryant on 2/4/14.
 */
public class EditResume extends Activity{
    String id;

    private ProgressDialog pDialog;
    private RequestService service = new RequestService();

    EditText txtName;
    EditText txtPhone;
    EditText txtEmail;
    EditText txtAddress;
    EditText txtSchool;
    EditText txtLocation;
    EditText txtGpa;
    EditText txtMajor;
    EditText txtMinor;
    EditText txtDegree;
    EditText txtCompany;
    EditText txtPosition;
    EditText txtDescription;


    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RESUME = "resume";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_SCHOOL = "school";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_GPA = "gpa";
    private static final String TAG_MAJOR = "major";
    private static final String TAG_MINOR = "minor";
    private static final String TAG_DEGREE = "degree";
    private static final String TAG_COMPANY = "company";
    private static final String TAG_POSITION = "position";
    private static final String TAG_DESCRIPTION = "description";

    private static final String url_resume_details = "http://10.171.91.51/resume_app/existing_info.php";

    protected void onCreate(Bundle savedBundle){
        super.onCreate(savedBundle);
        setContentView(R.layout.display_resume);

        TabHost tabs = (TabHost)findViewById(R.id.tabHost);
        tabs.setup();

        TabHost.TabSpec personalTab = tabs.newTabSpec("personal");
        personalTab.setContent(R.id.personal);
        personalTab.setIndicator("Personal Info");
        tabs.addTab(personalTab);

        TabHost.TabSpec educationTab = tabs.newTabSpec("education");
        educationTab.setContent(R.id.education);
        educationTab.setIndicator("Education");
        tabs.addTab(educationTab);

        TabHost.TabSpec employmentTab = tabs.newTabSpec("employment");
        employmentTab.setContent(R.id.employment);
        employmentTab.setIndicator("Employment");
        tabs.addTab(employmentTab);

        Intent i = getIntent();
        id = i.getStringExtra(TAG_ID);
        new ResumeDetails().execute();

    }

    private void displayText(JSONObject resume){
        try{
            txtName = (EditText) findViewById(R.id.inputName);
            txtPhone = (EditText) findViewById(R.id.inputPhone);
            txtEmail = (EditText) findViewById(R.id.inputEmail);
            txtAddress = (EditText) findViewById(R.id.inputAddress);
            txtSchool = (EditText) findViewById(R.id.inputSchool);
            txtLocation = (EditText) findViewById(R.id.inputLocation);
            txtGpa = (EditText) findViewById(R.id.inputGPA);
            txtMajor = (EditText) findViewById(R.id.inputMajor);
            txtMinor = (EditText) findViewById(R.id.inputMinor);
            txtDegree = (EditText) findViewById(R.id.inputDegree);
            txtCompany = (EditText) findViewById(R.id.inputCompany);
            txtPosition = (EditText) findViewById(R.id.inputPos);
            txtDescription = (EditText) findViewById(R.id.inputDes);

            txtName.setText(resume.getString(TAG_NAME));
            txtPhone.setText(resume.getString(TAG_PHONE));
            txtEmail.setText(resume.getString(TAG_EMAIL));
            txtAddress.setText(resume.getString(TAG_ADDRESS));
            txtSchool.setText(resume.getString(TAG_SCHOOL));
            txtLocation.setText(resume.getString(TAG_LOCATION));
            txtGpa.setText(resume.getString(TAG_GPA));
            txtMajor.setText(resume.getString(TAG_MAJOR));
            txtMinor.setText(resume.getString(TAG_MINOR));
            txtDegree.setText(resume.getString(TAG_DEGREE));
            txtCompany.setText(resume.getString(TAG_COMPANY));
            txtPosition.setText(resume.getString(TAG_POSITION));
            txtDescription.setText(resume.getString(TAG_DESCRIPTION));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    class ResumeDetails extends AsyncTask<String,String,String>{

        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(EditResume.this);
            pDialog.setMessage("Loading resume details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args){
            int success;
            try{
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("Id", id));

                JSONObject json = service.makeHttpRequest(url_resume_details, "GET", params);
                success = json.getInt(TAG_SUCCESS);

                if(success == 1){
                    JSONArray prodObj = json.getJSONArray(TAG_RESUME);
                    final JSONObject  resume = prodObj.getJSONObject(0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displayText(resume);

                        }
                    });
                }
                else{
                    pDialog = new ProgressDialog(EditResume.this);
                    pDialog.setMessage("There was an error");
                    pDialog.show();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url){
            pDialog.dismiss();
        }
    }

    class UpdatePersonal extends AsyncTask<String, String, String>{

        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(EditResume.this);
            pDialog.setMessage("Updating information. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args){

            return null;
        }
    }


}
