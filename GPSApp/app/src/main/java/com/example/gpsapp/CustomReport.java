package com.example.gpsapp;

// imports
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

/**
 * Custom view class that extends the view
 */
public class CustomReport extends View {
    // private variables used in the class
    private Paint paint;
    private int width;
    private int height;
    private int avg_speed_val, avg_altitude_val;
    private double total_distance, x_intervals, x_intervals_on_canvas;
    private double altitudes [];
    private ReportActivity refToReportActivity;
    private int padding = 100, axix_width = 5;
    private int highestY_limit = 0, x_axis_increments = 0, usable_height, usable_width, max_range;

    // default constructor for the class that takes in a context
    public CustomReport(Context c) {
            super(c);
            init();
    }

    // constructor that takes in a context and also a list of attributes
    // that were set through XML
    public CustomReport(Context c, AttributeSet as) {
            super(c, as);
            init();
    }


    // constructor that take in a context, attribute set and also a default
    // style in case the view is to be styled in a certian way
    public CustomReport(Context c, AttributeSet as, int default_style) {
            super(c, as, default_style);
            init();
    }

    // refactored init method as most of this code is shared by all the
    // constructors
    private void init() {

        // generating a new paint object
        paint = new Paint();
        paint.setAntiAlias(true);
        // setting the style of the paint to stroke
        paint.setStyle(Paint.Style.STROKE);

        // getting the context of the ReportActivity
        refToReportActivity = (ReportActivity) this.getContext();

        // calling methods from the report activity to get the passed results that was sent through the intent from the
        // mainActivity to the reportActivity
        avg_altitude_val = refToReportActivity.getAvg_alt_val();
        avg_speed_val = refToReportActivity.getAvg_speed_val();
        altitudes = refToReportActivity.get_altitudes();
        total_distance = refToReportActivity.get_total_distance();

        // calculating the width of usable canvas area when taking the padding into account
        usable_height = calculate_usable_height_range();
        usable_width = calculate_usable_width_range();

        // getting the highest x and y axis values from the data passed from the ReportActivity
        x_intervals = get_x_axix_increments();

        // calling the method that will the usable height within the graph space of the canvas
        get_y_limit();

    }

    /**
     * method called when the view draws the canvas
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // saving the canvas's position
        canvas.save();
        // setting the background of the canvas to black
        canvas.drawColor(Color.BLACK);

        // drawing the y axix
        // translating the canvas by the padding along the x axix and the diatnce of padding along the y axix
        canvas.translate(padding, padding);
        // calling the method that will draw the y axix, passing the method the canvas object
        draw_y_axix(canvas);
        // restoring the canvas to the saved location/position
        canvas.restore();

        // drawing the x axix
        // saving the canvas position
        canvas.save();
        // translating the canvas object
        canvas.translate(padding, (height - padding));
        // calling the method that will draw the x axix, passing the method the canvas object
        draw_x_axix(canvas);
        // restoring the canvas to the saved location/position
        canvas.restore();

        // drawing the average lines
        canvas.save();
        // translating the canvas object
        canvas.translate(padding, padding);
        // calling the method that will draw the average lines, passing the method the canvas object
        draw_averages_line(canvas);
        // restoring the canvas to the saved location/position
        canvas.restore();

        // drawing the altitudes line
        canvas.save();
        // translating the canvas object
        canvas.translate(padding, padding);
        // calling the method that will draw the average lines, passing the method the canvas object
        draw_of_altitudes_line(canvas);
        // restoring the canvas to the saved location/position
        canvas.restore();
    }

    /**
     * method that will draw the line the represents the x axix on the graph, the method will also draw the text associated with axix
     * @param canvas
     */
    public void draw_x_axix(Canvas canvas) {
        // drawing the line of the x axix
        canvas.drawLine(0, 0, (width - (padding * 2)),0, paint);
        // drawing the text associated with the axix
        canvas.drawText("Time", (width - (padding*2))/2, padding/2, paint);
    }

    /**
     * method that will draw the line the represents the y axix on the graph, the method will also draw the text associated with axix
     * @param canvas
     */
    public void draw_y_axix(Canvas canvas){
        // changing the color of the paint object
        paint.setColor(Color.WHITE);
        // setting the text size of the paint object
        paint.setTextSize(40);
        // setting the stroke of the paint object
        paint.setStrokeWidth(5);
        // drawing the line of the y axix
        canvas.drawLine(0, 0, 0, (height - (padding * 2)), paint);
        // drawing the text associated with the axix line
        canvas.drawText("Units", -padding,(height - (padding*2))/2, paint);
    }

    /**
     * method called to draw the average lines on the graph, the method will draw both the average altitudes and average speed collected from the mainActivity
     * @param canvas
     */
    public void draw_averages_line(Canvas canvas){

        // drawing the average altitude and speed
        if(highestY_limit < (width - (padding*2))){

            // setting the paint color
            paint.setColor(Color.RED);
            // drawing the line based on the variable storing the average altitude value
            canvas.drawLine(0, (height - (padding*2)) - avg_altitude_val,(width - (padding*2)),(height - (padding*2)) - avg_altitude_val, paint);
            // drawing the text associated with the line
            canvas.drawText("Average altitude: " + avg_altitude_val + "m",(width - (padding*2))/2,((height - (padding*2)) - avg_altitude_val) - (padding/2) , paint);

            // setting the color of the paint object
            paint.setColor(Color.BLUE);
            // drawing the line based on the variable storing the average speed value
            canvas.drawLine(0,(height - (padding*2)) - avg_speed_val,(width - (padding*2)),(height - (padding*2)) - avg_speed_val,paint);
            // drawing the text associated with the line
            canvas.drawText("Average speed: " + avg_speed_val + "km/h",(width - (padding*2))/2,((height - (padding*2)) - avg_speed_val) - (padding/2) , paint);
        }
        else{
            int y_axix_point = find_y_axix_value(avg_altitude_val);
        }
    }

    /**
     * method that will draw a line with individual points that are stored in the altitude array
     * @param canvas
     */
    public void draw_of_altitudes_line(Canvas canvas){

        // changing the paint object color to green
        paint.setColor(Color.GREEN);

        // variable used to store the previous point in the altitude array
        float prev_x = 0 ;
        float prev_y = (float) altitudes[0];

        // calculating the x axix intervals between lines on the usable canvas space
        x_intervals_on_canvas = (width - (padding*2)) / x_intervals;

        // looping through the altitudes array and drawing the lines taking into account the previous point stored
        for(int i = 1; i < altitudes.length; i++){
            // drawing the line taking into account the previous point stored in the above mentioned variables
            canvas.drawLine(prev_x,(height - (padding*2)) - prev_y,(float)(prev_x + x_intervals_on_canvas /*+ (padding*2)*/),(float) ((height - (padding*2)) - altitudes[i]),paint);
            // setting the previous variables to the current used point
            prev_x = prev_x + (float) x_intervals_on_canvas;
            prev_y = (float) altitudes[i];
        }
        // drawing the text associated with the line
        canvas.drawText("Altitudes",(width - (padding*2))/2,(float)((height - (padding*2)) - altitudes[0]) - (padding/2) , paint);
    }

    /**
     *  method that will generate the upper limit of the y axix of the usable area of the canvas
     */
    public void get_y_limit(){
        // variable that will hold the highest value
        double current_highest_value = 0;
        // checking for the highest value in the altitudes array
        for(int i = 0; i < altitudes.length; i++){
            if (altitudes[i] > current_highest_value){
                current_highest_value = (int) altitudes[i];
            }
        }

        // checking if the average altitude value is higher that the current highest value obtained in the altitudes array
        if(avg_altitude_val > current_highest_value){
            current_highest_value = avg_altitude_val;
        }

        // checking if the average speed value is higher that the current highest value obtained in the altitudes array
        if (avg_speed_val > current_highest_value){
            current_highest_value = avg_speed_val;
        }

        // returning the highest value
        highestY_limit = (int) current_highest_value;
    }

    /**
     * method that will return an int that represents the y value of the point in proportion to the available canvas space
     * @param y
     * @return
     */
    public int find_y_axix_value(int y){
        // variable that will hold the y axix value based on the canvas height
        int y_value;
        // finding the percent of the passed value in regards to the highest limit value
        int percentage_of_highest_limit = (y/highestY_limit);
        // turning the above value into a percent
        int as_a_percent = percentage_of_highest_limit * 100;
        // finding the value of the y variable in regards to the usable canvas area
        int value_of_usable_height = (percentage_of_highest_limit/100) * (height - (padding*2));
        // reversing the value to represent the point from the bottom of the graph
        y_value = (height - (padding*2)) - value_of_usable_height;
        // returning the value
        return y_value;
    }

    /**
     * method that will return a double which holds the increments that the graph will have when plotting the altitudes
     * @return
     */
    public double get_x_axix_increments(){
        return x_axis_increments = altitudes.length;
    }

    // returns an int of the usable area in regards to height on the canvas
    public int calculate_usable_height_range(){
        return height - (padding*2);
    }

    // returns an int of the usable area in regards to width on the canvas
    public int calculate_usable_width_range(){
        return width - (padding*2);
    }

    /**
     * method used to calculate the height and width of the canvas area
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = getMeasuredWidth();
        height = getMeasuredHeight();

    }

    /**
     * method used to calculate the percentage of a value within a particular range
     * @param min
     * @param max
     * @param value
     * @return
     */
    public int get_percentage(int min, int max, int value){
        return((value - min) / (max - min)) * 100;
    }
}