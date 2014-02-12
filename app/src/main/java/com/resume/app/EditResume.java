package com.resume.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
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
    EditText txtJob;
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
    private static final String TAG_JOB = "job";
    private static final String TAG_DESCRIPTION = "description";

    private String button;
    private static final String localhost = "10.171.91.48";
    private static final String url_resume_details = "http://"+localhost+"/resume_app/existing_info.php";
    private static final String url_update_info = "http://"+localhost+"/resume_app/update_existing_info.php";


    protected void onCreate(Bundle savedBundle){
        super.onCreate(savedBundle);
        setContentView(R.layout.display_resume);

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
        txtJob = (EditText) findViewById(R.id.inputJob);
        txtDescription = (EditText) findViewById(R.id.inputDes);

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

    private String checkNull(EditText text){
        String input;
        if(text.getText() == null){
            input = "";
        }
        else{
            input = text.getText().toString();
        }

        return input;
    }

    private String checkString(String tag){
        if(tag == null || tag.equals("null")){
            tag = "";
        }

        return tag;
    }

    private void displayText(JSONObject resume){
        try{
            txtName.setText(checkString(resume.getString(TAG_NAME)));
            txtPhone.setText(checkString(resume.getString(TAG_PHONE)));
            txtEmail.setText(checkString(resume.getString(TAG_EMAIL)));
            txtAddress.setText(checkString(resume.getString(TAG_ADDRESS)));
            txtSchool.setText(checkString(resume.getString(TAG_SCHOOL)));
            txtLocation.setText(checkString(resume.getString(TAG_LOCATION)));
            txtGpa.setText(checkString(resume.getString(TAG_GPA)));
            txtMajor.setText(checkString(resume.getString(TAG_MAJOR)));
            txtMinor.setText(checkString(resume.getString(TAG_MINOR)));
            txtDegree.setText(checkString(resume.getString(TAG_DEGREE)));
            txtCompany.setText(checkString(resume.getString(TAG_COMPANY)));
            txtJob.setText(checkString(resume.getString(TAG_JOB)));
            txtDescription.setText(checkString(resume.getString(TAG_DESCRIPTION)));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateInfo(View v){
        button = "personal";
        new UpdateInfo().execute();
    }


    class ResumeDetails extends AsyncTask<String,String,String>{

        JSONObject resume;

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
                Log.d("ID In Edit", "Id " + id);
                JSONObject json = service.makeHttpRequest(url_resume_details, "GET", params);
                success = json.getInt(TAG_SUCCESS);

                if(success == 1){
                    JSONArray prodObj = json.getJSONArray(TAG_RESUME);
                    resume = prodObj.getJSONObject(0);

                }
                else{
                    //Error
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url){
            displayText(resume);
            pDialog.dismiss();
        }
    }

    class UpdateInfo extends AsyncTask<String, String, String>{

        Boolean error = false;

        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(EditResume.this);
            pDialog.setMessage("Updating information. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args){
            String name = checkNull(txtName);
            String email = checkNull(txtEmail);
            String phone = checkNull(txtPhone);
            String address = checkNull(txtAddress);
            String school = checkNull(txtSchool);
            String location = checkNull(txtLocation);
            String gpa = checkNull(txtGpa);
            String major = checkNull(txtMajor);
            String minor = checkNull(txtMinor);
            String degree = checkNull(txtDegree);
            String company = checkNull(txtCompany);
            String job = checkNull(txtJob);
            String desc = checkNull(txtDescription);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("phone", phone));
            params.add(new BasicNameValuePair("address", address));
            params.add(new BasicNameValuePair("school", school));
            params.add(new BasicNameValuePair("location", location));
            params.add(new BasicNameValuePair("gpa", gpa));
            params.add(new BasicNameValuePair("major", major));
            params.add(new BasicNameValuePair("minor", minor));
            params.add(new BasicNameValuePair("degree", degree));
            params.add(new BasicNameValuePair("company", company));
            params.add(new BasicNameValuePair("job", job));
            params.add(new BasicNameValuePair("desc", desc));

            JSONObject json = service.makeHttpRequest(url_update_info, "POST", params);

            try{
                int success = json.getInt(TAG_SUCCESS);
                if(success != 1){
                    error = true;
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url){
            if(error){
                new AlertDialog.Builder(EditResume.this)
                        .setTitle("Error")
                        .setMessage("There was an error. Please try again.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }
            pDialog.dismiss();
        }
    }


}
