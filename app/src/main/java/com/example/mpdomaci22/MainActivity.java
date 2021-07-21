package com.example.mpdomaci22;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editMovie;
    TextView movieName;
    TextView director;
    TextView genre;
    TextView plot;
    String searchName;
    ProgressBar progressBar;
    ImageView poster;
    ListViewBaseAdapter adapter;
    ListView listRatings;
    ArrayList<ListViewBean> ratingsPrep;
    protected String apiKey = "5297070a";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editMovie = findViewById(R.id.editMovie);
        movieName = findViewById(R.id.txtName);
        director = findViewById(R.id.txtDirector);
        genre = findViewById(R.id.txtGenre);
        plot = findViewById(R.id.txtPlot);
        poster = findViewById(R.id.imgPoster);
        progressBar = findViewById(R.id.prBar);
        plot.setMovementMethod(new ScrollingMovementMethod());

        listRatings = findViewById(R.id.listRatings);
        ratingsPrep = new ArrayList<ListViewBean>();

    }

    public void search(View view) {
        searchName = editMovie.getText().toString();

        if(searchName.isEmpty()){
            Toast.makeText(this,"Morate uneti ime filma",Toast.LENGTH_LONG).show();
            return;
        }


        Movie movie = new Movie();
        movie.execute();


    }

    public class Movie extends AsyncTask<String, Integer, JSONObject> {

        @SuppressLint("WrongThread")
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            if (jsonObject == null || (jsonObject.has("Error"))) {
                Toast.makeText(getApplicationContext(), "Traženi film nije pronađen", Toast.LENGTH_LONG).show();
                publishProgress(0);
                return;
            } else {
                try {
                    if (jsonObject.has("Title")) {
                        movieName.setText(jsonObject.getString("Title"));
                        ratingsPrep.clear();
                    }
                    if (jsonObject.has("Director")) {
                        director.setText(jsonObject.getString("Director"));
                    }
                    if (jsonObject.has("Plot")) {
                        plot.setText(jsonObject.getString("Plot"));
                        //plot.setJustificationMode(1);
                    }
                    if (jsonObject.has("Genre")) {
                        genre.setText(jsonObject.getString("Genre"));
                    }
                    if (jsonObject.has("Ratings")) {
                        JSONArray ratings = new JSONArray(jsonObject.getString("Ratings"));

                        for (int i = 0; i < ratings.length(); i++) {
                            JSONObject object = ratings.getJSONObject(i);

                            ratingsPrep.add(new ListViewBean(object.getString("Source"), object.getString("Value")));
                        }
                        //adapter = new SimpleAdapter(getApplicationContext(),listRatingsPrep,R.layout.list_item,new String[]{"Source","Score"},new int[]{R.id.txtSource,R.id.txtScore});

                        adapter = new ListViewBaseAdapter(ratingsPrep, getApplicationContext());
                        listRatings.setAdapter(adapter);
                    }
                    if (jsonObject.has("Poster")) {
                        Picasso.get().load(jsonObject.getString("Poster")).into(poster);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected JSONObject doInBackground(String... strings) {

            try {
                URL url = new URL("http://www.omdbapi.com/?t=" + searchName + "&apikey=" + apiKey);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                System.out.println("http://www.omdbapi.com/?t=" + searchName + "&apikey=" + apiKey);
                publishProgress(20);

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                publishProgress(80);


                if (connection.getResponseCode() == 200) {
                    JSONObject json = new JSONObject(bufferedReader.readLine());
                    publishProgress(100);

                    return json;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }
    }

    public class ListViewBaseAdapter extends BaseAdapter {
        public ArrayList<ListViewBean> arraylistListener;
        private List<ListViewBean> mListenerList;
        Context mContext;

        public ListViewBaseAdapter(List<ListViewBean> mListenerList, Context context) {

            mContext = context;
            this.mListenerList = mListenerList;
            this.arraylistListener = new ArrayList<ListViewBean>();
            this.arraylistListener.addAll(mListenerList);

        }

        public class ViewHolder {

            TextView source;
            TextView score;

        }

        @Override
        public int getCount() {
            return mListenerList.size();
        }

        @Override
        public Object getItem(int position) {
            return mListenerList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.list_item, null);
                holder = new ViewHolder();
                holder.source = (TextView) view.findViewById(R.id.txtSource);
                holder.score = (TextView) view.findViewById(R.id.txtScore);


                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();

            }
            try {
                holder.source.setText(mListenerList.get(position).getSource());
                holder.score.setText(mListenerList.get(position).getScore());
            } catch (Exception ex) {


            }

            return view;
        }

    }
}