package com.example.sahabatmahasiswa;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EduSpend extends AppCompatActivity implements View.OnClickListener {

    private EditText edtUKT, edtNeeds, edtConsumption, edtRent, edtTransport, edtEmergency;
    private WebView webView;
    private Spinner dropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edu_spend);

        edtUKT = findViewById(R.id.edt_UKT);
        edtNeeds = findViewById(R.id.edt_Needs);
        edtConsumption = findViewById(R.id.edt_Consumption);
        edtRent = findViewById(R.id.edt_Rent);
        edtTransport = findViewById(R.id.edt_Transport);
        edtEmergency = findViewById(R.id.edt_Emergency);
        RelativeLayout btnHome1 = findViewById(R.id.rl_Home);
        ImageButton btnHome2 = findViewById(R.id.btn_Home);
        TextView btnHome3 = findViewById(R.id.tv_Home);
        RelativeLayout btnProfile1 = findViewById(R.id.rl_Profile);
        ImageButton btnProfile2 = findViewById(R.id.btn_Profile);
        TextView btnProfile3 = findViewById(R.id.tv_Profile);

        webView = findViewById(R.id.webView);

        btnHome1.setOnClickListener(v -> {
            startActivity(new Intent(EduSpend.this, Home.class));
            finish();
        });
        btnHome2.setOnClickListener(v -> {
            startActivity(new Intent(EduSpend.this, Home.class));
            finish();
        });
        btnHome3.setOnClickListener(v -> {
            startActivity(new Intent(EduSpend.this, Home.class));
            finish();
        });
        btnProfile1.setOnClickListener(v -> {
            startActivity(new Intent(EduSpend.this, Profile.class));
            finish();
        });
        btnProfile2.setOnClickListener(v -> {
            startActivity(new Intent(EduSpend.this, Profile.class));
            finish();
        });
        btnProfile3.setOnClickListener(v -> {
            startActivity(new Intent(EduSpend.this, Profile.class));
            finish();
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setBackgroundColor(Color.TRANSPARENT);

        dropdown = findViewById(R.id.spin_granularity);
        String[] items = new String[]{"Harian", "Bulanan", "Semester"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        findViewById(R.id.btn_Calculate).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_Calculate) {
            calculateAndShowChart();
        }
    }

    private void calculateAndShowChart() {
        String uktStr = edtUKT.getText().toString();
        String needsStr = edtNeeds.getText().toString();
        String consumptionStr = edtConsumption.getText().toString();
        String rentStr = edtRent.getText().toString();
        String transportStr = edtTransport.getText().toString();
        String emergencyStr = edtEmergency.getText().toString();

        double ukt = uktStr.isEmpty() ? 0 : Double.parseDouble(uktStr);
        double needs = needsStr.isEmpty() ? 0 : Double.parseDouble(needsStr);
        double consumption = consumptionStr.isEmpty() ? 0 : Double.parseDouble(consumptionStr);
        double rent = rentStr.isEmpty() ? 0 : Double.parseDouble(rentStr);
        double transport = transportStr.isEmpty() ? 0 : Double.parseDouble(transportStr);
        double emergency = emergencyStr.isEmpty() ? 0 : Double.parseDouble(emergencyStr);

        String granularity = dropdown.getSelectedItem().toString();

        switch (granularity) {
            case "Harian":
                ukt /= 180;
                needs /= 30;
                rent /= 30;
                emergency /= 30;
                break;
            case "Bulanan":
                ukt /= 6;
                consumption *= 30;
                rent = rent;
                transport *= 30;
                break;
            case "Semester":
                needs *= 6;
                consumption *= 180;
                rent *= 6;
                transport *= 180;
                emergency *= 6;
                break;
        }

        double total = ukt + needs + consumption + rent + transport + emergency;

        String data = "[['Expense', 'Amount'], ['UKT', " + ukt + "], ['Needs', " + needs + "], ['Consumption', " + consumption + "], ['Rent', " + rent + "], ['Transport', " + transport + "], ['Emergency', " + emergency + "]]";

        String totalString = "UKT                    : Rp" + String.format("%.2f", ukt) + "<br>" +
                "Kebutuhan Perkuliahan  : Rp" + String.format("%.2f", needs) + "<br>" +
                "Konsumsi               : Rp" + String.format("%.2f", consumption) + "<br>" +
                "Tempat Tinggal         : Rp" + String.format("%.2f", rent) + "<br>" +
                "Transportasi           : Rp" + String.format("%.2f", transport) + "<br>" +
                "Kebutuhan Darurat      : Rp" + String.format("%.2f", emergency) + "<br>" +
                "<br>" +
                "Total Keseluruhan      : Rp" + String.format("%.2f", total) + "<br>";

        String html = "<html>" +
                "<head>" +
                "<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>" +
                "<script type=\"text/javascript\">" +
                "google.charts.load('current', {'packages':['corechart']});" +
                "google.charts.setOnLoadCallback(drawChart);" +
                "function drawChart() {" +
                "  var data = google.visualization.arrayToDataTable(" + data + ");" +
                "  var options = { title: 'Expense Distribution' };" +
                "  var chart = new google.visualization.PieChart(document.getElementById('piechart'));" +
                "  chart.draw(data, options);" +
                "}" +
                "</script>" +
                "</head>" +
                "<body>" +
                "<div id=\"piechart\" style=\"width: 100%; height: 250px; background-color: transparent;\"></div>" +
                "<div style=\"padding: 5px; font-size: 16px;\">" + totalString + "</div>" +
                "</body>" +
                "</html>";

        webView.loadData(html, "text/html", null);
    }
}