package com.example.gpsapp;

// imports
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;



// class definition
public class ReportActivity extends Activity {

    // variables used by the class
    private double total_distance;
    private float speeds [];
    private double altitudes [];
    private TextView total_dist, min_speed, max_speed, avg_speed, min_alt, max_alt, avg_alt;
    private int min_speed_val, max_speed_val, avg_speed_val, min_alt_val, max_alt_val, avg_alt_val;
    private Button back_button;

    /**
     * method called on the creation of the class
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // the code below is run before calling the super, otherwise the custom view will load with null data points

        // getting the data from the intent
        total_distance = getIntent().getExtras().getDouble("total_distance");
        speeds = getIntent().getExtras().getFloatArray("speeds");
        altitudes = getIntent().getExtras().getDoubleArray("altitudes");

        // calculating the values
        max_speed_val = cal_max(speeds);
        min_speed_val = cal_min(speeds);
        min_alt_val = cal_min(altitudes);
        max_alt_val = cal_max(altitudes);
        avg_speed_val = cal_average(speeds);
        avg_alt_val = cal_average(altitudes);

        // call the super class method and set the content for this activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // pulliing the view from the activity_report xml file
        total_dist = findViewById(R.id.total_distance);
        min_speed = findViewById(R.id.min_speed);
        max_speed = findViewById(R.id.max_speed);
        avg_speed = findViewById(R.id.average_speed);
        min_alt = findViewById(R.id.min_alt);
        max_alt = findViewById(R.id.max_alt);
        avg_alt = findViewById(R.id.avergae_alt);
        back_button = findViewById(R.id.back_button);

        // updating the views
        total_dist.setText("Total distance: " + (int)total_distance + "m");
        min_speed.setText("Min speed: " + min_speed_val);
        max_speed.setText("Max speed: " + max_speed_val);
        avg_speed.setText("Average speed: " + avg_speed_val);
        min_alt.setText("Min altitude: " + min_alt_val);
        max_alt.setText("Max altitude: " + max_alt_val);
        avg_alt.setText("Average altitude: " + avg_alt_val);

        // adding an listener to the back button which will return to the main activity
        back_button.setOnClickListener(new View.OnClickListener() {

            // overridden on click method to return a result to the starter of this
            // activity
            public void onClick(View v) {
                // generating a new intent
                Intent result = new Intent(Intent.ACTION_VIEW);
                // adding a result
                result.putExtra("Result", 1);
                // setting the result
                setResult(RESULT_OK, result);
                // moving back to the mainActivity
                finish();
            }
        });

    }

    /**
     * method that will return a float array of the speeds recorded in the mainActivity
     * @return
     */
    public float[] get_speeds(){
        return speeds;
    }

    /**
     * method that will return a double array of altitudes that where recorded in the mainActivity
     * @return
     */
    public double [] get_altitudes(){
        return altitudes;
    }


    /**
     * method that will return a double array of distances that where recorded in the mainActivity
     * @return
     */
    public double get_total_distance(){
        return (Double) total_distance;
    }


    /**
     * method that will return an int of average speed that where recorded in the mainActivity
     * @return
     */
    public int getAvg_speed_val(){
        return avg_speed_val;
    }


    /**
     * method that will return an int of the average altitude that where recorded in the mainActivity
     * @return
     */
    public int getAvg_alt_val(){
        return avg_alt_val;
    }


    /**
     * method that will return an int of the maximum value in the passed array
     * @return
     */
    // Method for getting the maximum value
    public int cal_max(float[] inputArray){
        float maxValue = inputArray[0];
        for(int i=1;i < inputArray.length;i++){
            if(inputArray[i] > maxValue){
                maxValue = inputArray[i];
            }
        }

        return (int) maxValue;
    }


    /**
     * method that will return an int of the minimum value in the passed array
     * @return
     */
    // Method for getting the minimum value
    public int cal_min(float[] inputArray){
        float minValue = inputArray[0];
        for(int i=1;i<inputArray.length;i++){
            if(inputArray[i] < minValue){
                minValue = inputArray[i];
            }
        }
        return (int) minValue;
    }


    /**
     * method that will return an int of the maximum value in the passed array
     * @return
     */
    // Method for getting the maximum value
    public int cal_max(double[] inputArray){
        double maxValue = inputArray[0];
        for(int i=1;i < inputArray.length;i++){
            if(inputArray[i] > maxValue){
                maxValue = inputArray[i];
            }
        }

        return (int) maxValue;
    }


    /**
     * method that will return an int of the minimum value in the passed array
     * @return
     */
    // Method for getting the minimum value
    public int cal_min(double[] inputArray){
        double minValue = inputArray[0];
        for(int i=1;i<inputArray.length;i++){
            if(inputArray[i] < minValue){
                minValue = inputArray[i];
            }
        }
        return (int) minValue;
    }

    /**
     * method that will return an int of the average value in the passed double array
     * @return
     */
    public int cal_average(double[] inputArray){
        double temp = 0;
        for(int i = 0; i < inputArray.length; i++){
            temp += inputArray[i];
        }
        double total = temp / inputArray.length;
        return (int) total;
    }

    /**
     * method that will return an int of the average value in the passed float array
     * @return
     */
    public int cal_average(float[] inputArray){
        float temp = 0;
        for(int i = 0; i < inputArray.length; i++){
            temp += inputArray[i];
        }
        double total = temp / inputArray.length;
        return (int) total;
    }

    /**
     * method called when the menu bar is generated for the activity
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // inflate the menu and return true
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * method called when an item is selected on the bottom menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}