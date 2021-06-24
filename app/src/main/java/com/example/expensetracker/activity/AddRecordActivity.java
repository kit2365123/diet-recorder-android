package com.example.expensetracker.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.example.expensetracker.R;
import com.example.expensetracker.data.AppDatabase;
import com.example.expensetracker.data.Keyword;
import com.example.expensetracker.data.KeywordDao;
import com.example.expensetracker.data.Record;
import com.example.expensetracker.ml.FoodRecognitionModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Database;

import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

public class AddRecordActivity extends AppCompatActivity {

    // activity elements
    private EditText nameText, priceText;
    private ImageView recordImage;
    private Button option1, option2, option3, option4, option5;
    private String selectedTitle;
    private String itemType = "";
    private Bitmap originalBitmap;
    private Bitmap displayBitmap;
    // options for model interpreter
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();
    private Interpreter tflite;
    // holds all the possible labels for model
    private List<String> labelList;
    // holds the probabilities of each label for quantized graphs
    private byte[][] labelProbArray = null;
    // array that holds the labels with the highest probabilities
    private String[] topLables = null;
    // array that holds the highest probabilities
    private String[] topConfidence = null;
    // holds the selected image data as bytes
    private ByteBuffer imgData = null;
    // int array to hold image data
    private int[] imageIntValues;
    private ArrayList<Text.TextBlock> textBlockList = new ArrayList<>();
    private ArrayList<Keyword> keywordLists = new ArrayList<>();

    // input image dimensions for the Inception Model
    private int IMG_SIZE_X = 299;
    private int IMG_SIZE_Y = 299;
    private int PIXEL_SIZE = 3;
    private static final int RESULTS_TO_SHOW = 5;
    private String MODEL_FILE_NAME = "inception_quant.tflite";

    // priority queue that will hold the top results from the CNN
    private PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });

    // for permission requests
    public static final int REQUEST_PERMISSION = 300;

    // request code for permission requests to the os for image
    public static final int REQUEST_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);
//
//        option1 = findViewById(R.id.option1);
//        option2 = findViewById(R.id.option2);
//        option3 = findViewById(R.id.option3);
//        option4 = findViewById(R.id.option4);
//        option5 = findViewById(R.id.option5);
           recordImage = findViewById(R.id.record_image);

        // set the image to the image view
        //Uri uri = (Uri)getIntent().getParcelableExtra("image_uri");
        Uri uri;
        if (getIntent().hasExtra("image_uri")) {
            uri = (Uri)getIntent().getParcelableExtra("image_uri");
            try {
                originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                recordImage.setImageBitmap(originalBitmap);

                recognizeText(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // initialize array that holds image data
        imageIntValues = new int[IMG_SIZE_X * IMG_SIZE_Y];

        //initialize the graph and labels
        try{
            tflite = new Interpreter(loadModelFile(), tfliteOptions);
            labelList = loadLabelList();
        } catch (Exception ex){
            ex.printStackTrace();
        }

        imgData = ByteBuffer.allocateDirect(IMG_SIZE_X * IMG_SIZE_Y * PIXEL_SIZE);
        imgData.order(ByteOrder.nativeOrder());

        // initialize probabilities array
        labelProbArray= new byte[1][labelList.size()];
        // initialize array to hold top labels
        topLables = new String[RESULTS_TO_SHOW];
        // initialize array to hold top probabilities
        topConfidence = new String[RESULTS_TO_SHOW];
        recordImage = findViewById(R.id.record_image);
        nameText = findViewById(R.id.input_name);
        classifyImg();
        priceText = findViewById(R.id.input_price);

//        option1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                nameText.setText(option1.getText());
//            }
//        });
//        option2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                nameText.setText(option2.getText());
//            }
//        });
//        option3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                nameText.setText(option3.getText());
//            }
//        });
//        option4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                nameText.setText(option4.getText());
//            }
//        });
//        option5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                nameText.setText(option5.getText());
//            }
//        });

        ImageButton saveButton = findViewById(R.id.save_button);
        // save button listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEmpty(nameText) && !isEmpty(priceText)) {
                    addRecordToDB();
                }
            }
        });

        ImageButton cancelButton = findViewById(R.id.cancel_button);
        // cancel button listener
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    // classify the object in the image
    private void classifyImg() {

        List<Category> probability = new ArrayList<>();
        try {
            FoodRecognitionModel model = FoodRecognitionModel.newInstance(getApplicationContext());
            // Creates inputs for reference.
            TensorImage image = TensorImage.fromBitmap(originalBitmap);
            // Runs model inference and gets result.
            FoodRecognitionModel.Outputs outputs = model.process(image);
            probability = outputs.getProbabilityAsCategoryList();
            int index = 0;
            float p = probability.get(0).getScore();
            for (int i = 0; i < probability.size(); i++) {
                Log.d("Prob", probability.get(i).getLabel() + ": " + probability.get(i).getScore() );
                if(probability.get(i).getScore() > p) {
                    p = probability.get(i).getScore();
                    index = i;
                }
            }
            itemType = probability.get(index).getLabel();
            Log.d("Label", probability.get(index).getLabel());
           // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            e.printStackTrace();// TODO Handle the exception
        }

       Log.d("Probi", String.valueOf(probability));

        //convertBitmapToByteBuffer(bitmap);
        //tflite.run(imgData, labelProbArray);
        // add all results to priority queue
        for (int i = 0; i < labelList.size(); ++i) {
            sortedLabels.add(new AbstractMap.SimpleEntry<>(labelList.get(i), (labelProbArray[0][i] & 0xff) / 255.0f));
            if (sortedLabels.size() > RESULTS_TO_SHOW) {
                sortedLabels.poll();
            }
        }

        // get top results from priority queue
        final int size = sortedLabels.size();
        Log.d("T", String.valueOf(size));
        for (int i = 0; i < size; ++i) {
            Map.Entry<String, Float> label = sortedLabels.poll();
            topLables[i] = label.getKey();
            topConfidence[i] = String.format("%.0f%%",label.getValue()*100);
        }

//        String option1Text = topLables[RESULTS_TO_SHOW - 1].substring(0, 1).toUpperCase() + topLables[RESULTS_TO_SHOW - 1].substring(1);
//        String option2Text = topLables[RESULTS_TO_SHOW - 2].substring(0, 1).toUpperCase() + topLables[RESULTS_TO_SHOW - 2].substring(1);
//        String option3Text = topLables[RESULTS_TO_SHOW - 3].substring(0, 1).toUpperCase() + topLables[RESULTS_TO_SHOW - 3].substring(1);
//        String option4Text = topLables[RESULTS_TO_SHOW - 4].substring(0, 1).toUpperCase() + topLables[RESULTS_TO_SHOW - 4].substring(1);
//        String option5Text = topLables[RESULTS_TO_SHOW - 5].substring(0, 1).toUpperCase() + topLables[RESULTS_TO_SHOW - 5].substring(1);

//        option1.setText(option1Text);
//        option2.setText(option2Text);
//        option3.setText(option3Text);
//        option4.setText(option4Text);
//        option5.setText(option5Text);
    }


    // add a record to database
    private void addRecordToDB() {
        // create a new record
        final Record record = new Record();
        selectedTitle = nameText.getText().toString();
        record.setItemName(selectedTitle);
        record.setPrice(Float.parseFloat(priceText.getText().toString()));
        Date date = Calendar.getInstance().getTime();
        record.setCreateTime(date);

        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Log.d("Date", df.format(date));

        BitmapDrawable bitmapDrawable = (BitmapDrawable)recordImage.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos);
        String imageByteArray = android.util.Base64.encodeToString(bos.toByteArray(), android.util.Base64.DEFAULT);
        record.setImageByteArray(imageByteArray);
        record.setItemType(itemType.toLowerCase());
        // store the record in the database
        @SuppressLint("StaticFieldLeak")
        class SaveRecordTask extends AsyncTask<Void, Void, Void> {

            // insert title, type, keywords
            @Override
            protected Void doInBackground(Void... voids) {
                AppDatabase.getDatabase(getApplicationContext()).recordDao().insertRecord(record);
                int id = AppDatabase.getDatabase(getApplicationContext()).recordDao().getRecordIdByDate(record.getCreateTime());
                KeywordDao keywordDao = AppDatabase.getDatabase(getApplicationContext()).keywordDao();
                Keyword title = new Keyword();
                Keyword type = new Keyword();
                title.setRecordId(id);
                title.setWord(selectedTitle);
                type.setRecordId(id);
                type.setWord(itemType.toLowerCase());
                for(Keyword k : keywordLists) {
                    k.setRecordId(id);
                    Log.d("Keyword", k.getWord() + " " + k.getRecordId());
                    keywordDao.insertKeyword(k);
                }
                keywordDao.insertKeyword(title);
                keywordDao.insertKeyword(type);
                Log.d("Record id", id + "");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    Toast toast = Toast.makeText(getApplicationContext(),"Record Saved",Toast.LENGTH_SHORT);
                    toast.show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
            }

        }
        new SaveRecordTask().execute();
    }

    // check the edit text view is empty or not
    private boolean isEmpty(EditText text) {
        if (text.getText().toString().trim().length() > 0)
            return false;
        return true;
    }

    // loads tflite grapg from file
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd(MODEL_FILE_NAME);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // loads the labels from the label txt file in assets into a string array
    private List<String> loadLabelList() throws IOException {
        List<String> labelList = new ArrayList<String>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(this.getAssets().open("labels.txt")));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    // resizes bitmap to given dimensions
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    // converts bitmap to byte array which is passed in the tflite graph
    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        bitmap.getPixels(imageIntValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // loop through all pixels
        int pixel = 0;
        for (int i = 0; i < IMG_SIZE_X; ++i) {
            for (int j = 0; j < IMG_SIZE_Y; ++j) {
                final int val = imageIntValues[pixel++];
                // get rgb values from intValues where each int holds the rgb values for a pixel.
                imgData.put((byte) ((val >> 16) & 0xFF));
                imgData.put((byte) ((val >> 8) & 0xFF));
                imgData.put((byte) (val & 0xFF));
            }
        }
    }

    private void recognizeText(Uri uri) {
        String[] resultArr = null;
        InputImage image = null;
        try {
            image = InputImage.fromFilePath(getApplicationContext(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TextRecognizer recognizer = TextRecognition.getClient();
        final Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                for (Text.TextBlock block : visionText.getTextBlocks()) {
                                    textBlockList.add(block);
                                    Rect boundingBox = block.getBoundingBox();
                                    //canvas.drawRect(boundingBox, paint);
                                    String text = block.getText();
                                    Log.d("Text", text);
                                    Keyword keyword = new Keyword();
                                    keyword.setWord(text);
                                    keywordLists.add(keyword);
                                }
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });

    }


    @SuppressLint("ClickableViewAccessibility")
    private void setRectOnTouchListener(ImageView imageView) {
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    Log.d(x+ "", y+"");
                    for(Text.TextBlock i : textBlockList) {
                        Log.d("Image Height", String.valueOf(i.getBoundingBox().width()));
                        if(i.getBoundingBox().contains(x, y)) {
                            Log.d("Text2", i.getText());
                        }
                    }
                }

                return true;
            }
        });
    }

}