package com.zero.greenlist.ui.trashcan;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.zero.greenlist.R;

import java.util.ArrayList;
import java.util.List;

public class TrashFragment extends Fragment {

    private TrashViewModel trashViewModel;

    public View onCreateView (@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle savedInstanceState) {

        trashViewModel =
                ViewModelProviders.of(this).get(TrashViewModel.class);
        View root = inflater.inflate(R.layout.fragment_trashcan, container, false);

        final TextView textView = root.findViewById(R.id.navigation_trashcan);

        trashViewModel.getText().observe(this, new Observer<String>() {

            @Override
            public void onChanged(@Nullable String s) {
                downloadJSON();
            }

            /// START API ///
            private void downloadJSON() {

                class DownloadJSON extends AsyncTask<Void, Void, String> {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }


                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        try {
                            loadIntoListView(s);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected String doInBackground(Void... voids) {
                        try {
                            URL url = new URL("http://85.149.119.232/api/trashcan_service.php");
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                            StringBuilder sb = new StringBuilder();
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            String json;
                            while ((json = bufferedReader.readLine()) != null) {
                                sb.append(json + "\n");
                            }
                            return sb.toString().trim();
                        } catch (Exception e) {
                            return "";
                        }
                    }
                }
                DownloadJSON getJSON = new DownloadJSON();
                getJSON.execute();
            }

            private void loadIntoListView(String json) throws JSONException {
                JSONArray jsonArray = new JSONArray(json);
                int trashcan;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    trashcan = obj.getInt("measurement_cm");
                    System.out.println(trashcan);
                    pie(trashcan);
                }
            }
            /// END API ///

            public void pie(int trash) {
                int leeg = trash;
                int vol = 50 - trash;
                System.out.println(vol);
                System.out.println(leeg);

                List<PieEntry> value = new ArrayList<>(); //arraylist omdat geen vaste grote nodig heeft

                value.add(new PieEntry(leeg, "Leeg")); //arralist waarde voor leeg in %
                value.add(new PieEntry(vol, "Vol")); //arraylist waarde voor vol in %

                PieChart pieChart = getActivity().findViewById(R.id.piechart);
                pieChart.setUsePercentValues(true); //zorgt dat procenten gebruikt kunnen worden
                Description desc = new Description();
                desc.setText("Overzicht prullenbak");
                desc.setTextSize(50f);
                pieChart.setDescription(desc);
                pieChart.setHoleRadius(60f);
                pieChart.setTransparentCircleRadius(60f);


                PieDataSet pieDataSet = new PieDataSet(value, "Prullenbakinhoud");
                PieData pieData = new PieData(pieDataSet);
                pieChart.setData(pieData);
                pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
                pieChart.animateXY(1400, 1400);
                pieData.setValueTextSize(15f);
                pieData.setValueTextColor(Color.YELLOW);
                /// END PIECHART ///
            }
        });
        return root;
    }
}
