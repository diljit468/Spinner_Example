package com.example.shopingdemo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "MainActivity";
    String jsonData = "",delivry="PickUp";
    Spinner spinnerName;
    TextView tvName, tvPrice, tvTotalPrice;
    ImageView ivItem;
    EditText etQty;
    RadioGroup radioGroup;
    JSONArray array;
    Button btnBuy;
    ProgressBar progressBar;
    double total=0;
    RadioButton delivery,pick;
    List<String> categories = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadData();
        init();
        try {
            getDataFromJson();
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: "+e );
        }

    }
    private void init() {
        spinnerName = findViewById(R.id.spinnerName);
        spinnerName.setOnItemSelectedListener(this);
        tvName = findViewById(R.id.tvName);
        tvPrice = findViewById(R.id.tvPrice);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        ivItem = findViewById(R.id.ivItem);
        etQty = findViewById(R.id.etQty);
        progressBar = findViewById(R.id.progressBar);
        btnBuy = findViewById(R.id.btnBuy);
        delivery = findViewById(R.id.delivery);
        pick = findViewById(R.id.pick);

        etQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(etQty.getText().toString().equals("")){
                    showToast("Enter Valid Quantity");
                    total=0;
                    tvTotalPrice.setText("Total Price $ : " + total);
                }else {
                    int qty = Integer.parseInt(etQty.getText().toString());
                    double price = Integer.parseInt(tvPrice.getText().toString());
                    total = price * qty;
                    if(delivry.equals("PickUp")){
                        double subTotal = total * 13 / 100;
                        double subTotal1 = total - subTotal;
                        tvTotalPrice.setText("Sub Total $ : " + subTotal1 + " 13% Tax include" + "\n" + " Total Price $ :" + total);
                    }else{
                    if (total > 100) {
                        double subTotal = total * 13 / 100;
                        double subTotal1 = total - subTotal;
                        tvTotalPrice.setText("Sub Total $ : " + subTotal1 + " 13% Tax include" + "\n" + " Total Price $ :" + total);
                    } else {
                        double subTotal = total * 13 / 100;
                        double subTotal1 = total - subTotal;
                        double deliveryFee=total * 10/ 100;
                        tvTotalPrice.setText("Delivery Fees $ : "+deliveryFee+"\n"+"Sub Total $ : " + subTotal1 + " 13% Tax include" + "\n" + " Total Price $ :" + (total+deliveryFee));
                    }
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(total<5){
                    showToast("Enter Valid Quantity");
                }else{
                    showToast("Order Placed Successfully");
                }
            }
        });
        RadioGroup rGroup = (RadioGroup)findViewById(R.id.radioGroup);
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked)
                {
                   if(checkedRadioButton.getText().equals("PickUp")){
                       delivery.setChecked(false);
                       pick.setChecked(true);
                       delivry="PickUp";
                   }else{
                       delivery.setChecked(true);
                       pick.setChecked(false);
                       delivry="delivry";
                   }
                }
            }
        });
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void getDataFromJson() throws JSONException {
        JSONObject object = new JSONObject(jsonData);
        Log.e(TAG, "getDataFromJson: "+object );
        array = object.getJSONArray("data");
        for (int i = 0; i < array.length(); i++) {
            JSONObject data = (JSONObject) array.get(i);
            categories.add(data.getString("name"));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinnerName.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();
        try {
            tvName.setText(item);
            etQty.setText("1");
            tvPrice.setText(array.getJSONObject(i).getString("price"));

            Glide.with(this)
                    .load(array.getJSONObject(i).getString("img"))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(ivItem);
            int qty = Integer.parseInt(etQty.getText().toString());
            double price = Integer.parseInt(tvPrice.getText().toString());
            total = price * qty;
            tvTotalPrice.setText("Total price $ : "+total);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    private void loadData() {
        jsonData ="{\n" +
                "\"data\":[\n" +
                "{\n" +
                "\"name\":\"Dettol Original Liquid Hand Wash\",\n" +
                "\"price\":\"10\"\n" +
                ",\"img\":\"https://i.ibb.co/9b1Gd55/a.jpg\"\n" +
                "},\n" +
                "{\n" +
                "\"name\":\"Dettol Original Liquid Hand Wash Refill\",\n" +
                "\"price\":\"14.5\"\n" +
                ",\"img\":\"https://i.ibb.co/jHHL5JL/b.jpg\"\n" +
                "},\n" +
                "{\n" +
                "\"name\":\"Dettol Liquid Soap Jar Skincare(900 ml)\",\n" +
                "\"price\":\"13.5\"\n" +
                ",\"img\":\"https://i.ibb.co/9bFhQ8g/c.jpg\"\n" +
                "},\n" +
                "{\n" +
                "\"name\":\"Parle Biscuits \",\n" +
                "\"price\":\"5\"\n" +
                ",\"img\":\"https://i.ibb.co/dpd9b3v/d.jpg\"\n" +
                "},\n" +
                "{\n" +
                "\"name\": \"McVitie's Digestive Biscuits\",\n" +
                "\"price\":\"8\"\n" +
                ",\"img\":\"https://i.ibb.co/DM9bVw5/e.jpg\"\n" +
                "},\n" +
                "{\n" +
                "\"name\":\"Cadbury Oreo Biscuits\",\n" +
                "\"price\":\"17\"\n" +
                ",\"img\":\"https://i.ibb.co/YdFDB0g/f.jpg\"\n" +
                "},\n" +
                "{\n" +
                "\"name\":\"Aesop Gentle Facial Cleansing Milk\",\n" +
                "\"price\":\"23\"\n" +
                ",\"img\":\"https://i.ibb.co/KVKpDfC/g.jpg\"\n" +
                "},\n" +
                "{\n" +
                "\"name\":\"Tiege Hanley's Daily Face Wash\",\n" +
                "\"price\":\"28\"\n" +
                ",\"img\":\"https://i.ibb.co/VpQHxBq/h.jpg\"\n" +
                "}\n" +
                "]\n" +
                "}";
    }
}
