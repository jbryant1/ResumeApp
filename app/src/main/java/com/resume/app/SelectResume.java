package com.resume.app;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jbryant on 2/6/14.
 */
public class SelectResume extends ListActivity{

    private ProgressDialog pDialog;
    private RequestService service = new RequestService();

    ArrayList<HashMap<String,String>> resumeList;
    private static String localhost = "10.171.91.48";
    private static String url_all_resumes = "http://"+localhost+"/resume_app/grab_existing_resume.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RESUME = "resume";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";

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

                Intent i = new Intent(getApplicationContext(), EditResume.class);
                i.putExtra(TAG_ID, rid);
                startActivityForResult(i, 100);
            }
        });


    }

    class LoadAllResumes extends AsyncTask<String,String,String>{
        protected void onPreExecute(){
            pDialog = new ProgressDialog(SelectResume.this);
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
                    ListAdapter adapter = new SimpleAdapter(SelectResume.this, resumeList,R.layout.list_item, new String[] {TAG_ID, TAG_NAME},new int[] {R.id.id, R.id.name});
                    setListAdapter(adapter);
                }
            });
        }
    }
}
