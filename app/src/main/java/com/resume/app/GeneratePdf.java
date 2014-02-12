package com.resume.app;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jbryant on 2/12/14.
 */
public class GeneratePdf extends ListActivity {
    private ProgressDialog pDialog;

    ArrayList<HashMap<String,String>> resumeList;
    private static String localhost = "10.171.91.48";
    private static String url_all_resumes = "http://"+localhost+"/resume_app/grab_existing_resume.php";
    private static String url_resume_details = "http://"+localhost+"/resume_app/existing_info.php";
    private static String FILE = "http://"+localhost+"/resume_app/pdf/FirstPdf.pdf";

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

    private Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLUE);
    private Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
    private Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

    private String selectId;

    JSONArray resumes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_resume);

        resumeList = new ArrayList<HashMap<String, String>>();

        new LoadAllResumes().execute();

        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String rid = ((TextView) view.findViewById(R.id.id)).getText().toString();
                selectId = rid;
                new GrabInfoGenerate(selectId).execute();
            }
        });
    }

    class GrabInfoGenerate extends AsyncTask<String, String, String>{

        String sid;
        JSONObject resume;

        protected GrabInfoGenerate(String id){
            this.sid = id;
        }

        protected void onPreExecute(){
            pDialog = new ProgressDialog(GeneratePdf.this);
            pDialog.setMessage("Generating PDF. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id",sid));
            JSONObject json = service.makeHttpRequest(url_resume_details, "GET", params);

            try{
                int success = json.getInt(TAG_SUCCESS);
                if(success == 1){
                    JSONArray prodObj = json.getJSONArray(TAG_RESUME);
                    resume = prodObj.getJSONObject(0);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url){

            try{
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(FILE));
                document.open();
                addResume(document, resume);
                document.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void addResume(Document doc, JSONObject resume) throws DocumentException{
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        try{
            preface.add(new Paragraph("Resume" + resume.getString(TAG_NAME), catFont));
            doc.add(preface);
            doc.newPage();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void addEmptyLine(Paragraph paragraph, int number){
        for(int i=0; i<number; i++){
            paragraph.add(new Paragraph(" "));
        }
    }

    class LoadAllResumes extends AsyncTask<String,String,String> {
        protected void onPreExecute(){
            pDialog = new ProgressDialog(GeneratePdf.this);
            pDialog.setMessage("Loading resumes. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json = service.makeHttpRequest(url_all_resumes, "GET", params);

            try{
                int success = json.getInt(TAG_SUCCESS);
                if(success == 1){
                    resumes = json.getJSONArray(TAG_RESUME);

                    for(int i=0; i < resumes.length(); i++){
                        JSONObject c = resumes.getJSONObject(i);
                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NAME);

                        HashMap<String, String> map = new HashMap<String,String>();

                        map.put(TAG_ID, id);
                        map.put(TAG_NAME, name);

                        resumeList.add(map);

                    }
                }
                else{
                    Intent i = new Intent(getApplicationContext(), EditResume.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url){
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(GeneratePdf.this, resumeList,R.layout.list_item, new String[] {TAG_ID, TAG_NAME},new int[] {R.id.id, R.id.name});
                    setListAdapter(adapter);
                }
            });
        }
    }
}
