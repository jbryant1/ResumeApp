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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jbryant on 2/4/14.
 */
public class CreateResume extends Activity implements AsyncResponse{
    ProgressDialog pDialog;
    private String id ="";

    private static final String localhost = "10.171.91.48";
    private static String url_insert_name = "http://"+localhost+"/resume_app/insert_name.php";
    private static String url_insert_personal = "http://"+localhost+"/resume_app/insert_personal.php";
    private static String url_insert_employment = "http://"+localhost+"/resume_app/insert_employment.php";
    private static String url_insert_education = "http://"+localhost+"/resume_app/insert_education.php";


    RequestService service = new RequestService();
    EditText inputName;

    EditText inputPersonalName;
    EditText inputPersonalEmail;
    EditText inputPersonalPhone;
    EditText inputPersonalAddress;

    EditText inputEmploymentCompany;
    EditText inputEmploymentJob;
    EditText inputEmploymentDesc;

    EditText inputEduSchool;
    EditText inputEduLocation;
    EditText inputEduGPA;
    EditText inputEduMajor;
    EditText inputEduMinor;
    EditText inputEduDegree;


    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ID = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resume_name);
    }

    public void onResumeCreateClick(View v){
        new createResumeName().execute();
        Log.d("ID", "This is the id " + id);
    }

    public void onPersonalCreateClick(View v){
        new createPersonalInfo().execute();
    }

    public void onEducationCreateClick(View v){
        new createEducationInfo().execute();
    }

    public void onEmploymentCreateClick(View v){
        new createEmploymentInfo().execute();
        finish();
    }

    public void processFinish(String output){
        this.id = output;
        Log.d("Inside Process","THIS IS ID " + id);
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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(CreateResume.this)
                .setTitle("Confirm")
                .setMessage("You have not finished. You will be able to edit your progress")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    class createResumeName extends AsyncTask<String, String, String>{

        protected void onPreExecute(){
            pDialog = new ProgressDialog(CreateResume.this);
            pDialog.setMessage("Saving resume name. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
        }

        protected String doInBackground(String... args){
            inputName = (EditText) findViewById(R.id.inputResumeName);
            String name = checkNull(inputName);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", name));

            JSONObject json = service.makeHttpRequest(url_insert_name, "POST", params);

            try{
                int success = json.getInt(TAG_SUCCESS);

                if(success == 1){
                    id = json.getString(TAG_ID);
                }
                else{
                    id = "error";
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected  void onPostExecute(String file_url){
            pDialog.dismiss();
            if(id == "error"){
                new AlertDialog.Builder(CreateResume.this)
                    .setTitle("Error")
                    .setMessage("There was an error. Please try again.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = getIntent();
                            finish();
                            startActivity(i);
                        }
                    })
                .show();
            }
            else{
                Log.d("ID In Post:","ID " + id );
                processFinish(id);
                setContentView(R.layout.personl_info);
            }
        }
    }

    class createPersonalInfo extends AsyncTask<String, String, String>{

        String tempId = id;

        protected void onPreExecute(){
            pDialog = new ProgressDialog(CreateResume.this);
            pDialog.setMessage("Saving persnal info. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
        }

        protected String doInBackground(String... args){
            inputPersonalName = (EditText)findViewById(R.id.inputPersonalName);
            inputPersonalEmail = (EditText)findViewById(R.id.inputPersonalEmail);
            inputPersonalPhone = (EditText)findViewById(R.id.inputPersonalPhone);
            inputPersonalAddress = (EditText)findViewById(R.id.inputPersonalAddress);

            String name = checkNull(inputPersonalName);
            String email = checkNull(inputPersonalEmail);
            String phone = checkNull(inputPersonalPhone);
            String address = checkNull(inputPersonalAddress);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id));
            params.add(new BasicNameValuePair("name",name));
            params.add(new BasicNameValuePair("email",email));
            params.add(new BasicNameValuePair("phone",phone));
            params.add(new BasicNameValuePair("address",address));

            JSONObject json = service.makeHttpRequest(url_insert_personal, "POST", params);

            try{
                int success = json.getInt(TAG_SUCCESS);

                if(success != 1){
                    tempId = "error";
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected  void onPostExecute(String file_url){
            pDialog.dismiss();
            if(tempId == "error"){
                new AlertDialog.Builder(CreateResume.this)
                        .setTitle("Error")
                        .setMessage("There was an error. Please try again.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }
            else{
                setContentView(R.layout.education);
            }
        }
    }

    class createEducationInfo extends AsyncTask<String, String, String>{

        String tempId = id;

        protected void onPreExecute(){
            pDialog = new ProgressDialog(CreateResume.this);
            pDialog.setMessage("Saving education info. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
        }

        protected String doInBackground(String... args){
            inputEduSchool = (EditText)findViewById(R.id.inputEduSchool);
            inputEduLocation = (EditText)findViewById(R.id.inputEduLocation);
            inputEduGPA = (EditText)findViewById(R.id.inputEduGPA);
            inputEduMajor = (EditText)findViewById(R.id.inputEduMajor);
            inputEduMinor = (EditText)findViewById(R.id.inputEduMinor);
            inputEduDegree = (EditText)findViewById(R.id.inputEduDegree);

            String school = checkNull(inputEduSchool);
            String location = checkNull(inputEduLocation);
            String gpa = checkNull(inputEduGPA);
            String major = checkNull(inputEduMajor);
            String minor = checkNull(inputEduMinor);
            String degree = checkNull(inputEduDegree);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id));
            params.add(new BasicNameValuePair("school",school));
            params.add(new BasicNameValuePair("location",location));
            params.add(new BasicNameValuePair("gpa",gpa));
            params.add(new BasicNameValuePair("major",major));
            params.add(new BasicNameValuePair("minor",minor));
            params.add(new BasicNameValuePair("degree",degree));

            JSONObject json = service.makeHttpRequest(url_insert_education, "POST", params);

            try{
                int success = json.getInt(TAG_SUCCESS);

                if(success != 1){
                    tempId = "error";
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected  void onPostExecute(String file_url){
            pDialog.dismiss();
            if(tempId == "error"){
                new AlertDialog.Builder(CreateResume.this)
                        .setTitle("Error")
                        .setMessage("There was an error. Please try again.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }
            else{
                setContentView(R.layout.employment);
            }
        }
    }

    class createEmploymentInfo extends AsyncTask<String, String, String>{

        String tempId = id;

        protected void onPreExecute(){
            pDialog = new ProgressDialog(CreateResume.this);
            pDialog.setMessage("Saving education info. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
        }

        protected String doInBackground(String... args){
            inputEmploymentCompany = (EditText)findViewById(R.id.inputEmploymentCompany);
            inputEmploymentJob = (EditText)findViewById(R.id.inputEmploymentJob);
            inputEmploymentDesc = (EditText)findViewById(R.id.inputEmploymentDes);

            String company = checkNull(inputEmploymentCompany);
            String job = checkNull(inputEmploymentJob);
            String desc = checkNull(inputEmploymentDesc);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id));
            params.add(new BasicNameValuePair("company",company));
            params.add(new BasicNameValuePair("job",job));
            params.add(new BasicNameValuePair("desc",desc));

            JSONObject json = service.makeHttpRequest(url_insert_employment, "POST", params);

            try{
                int success = json.getInt(TAG_SUCCESS);

                if(success != 1){
                    tempId = "error";
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected  void onPostExecute(String file_url){
            pDialog.dismiss();
            if(tempId == "error"){
                new AlertDialog.Builder(CreateResume.this)
                        .setTitle("Error")
                        .setMessage("There was an error. Please try again.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }
        }
    }
}
