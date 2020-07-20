package dev.yonathaniel.tenakatauni;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.yonathaniel.tenakatauni.db.MyDb;
import dev.yonathaniel.tenakatauni.models.UserModel;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class CaptureInfoActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    MyDb myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_info);

        myDb = new MyDb(this);

        // TODO: 7/19/2020
        initViews();
        setSupportActionBar(toolbar);
        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_captureinfo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.navList) {
            startActivity(new Intent(this, DisplayDataActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("name"))
            edtName.setText(savedInstanceState.getString("name"));

        if (savedInstanceState.containsKey("age"))
            edtAge.setText(savedInstanceState.getString("age"));

        if (savedInstanceState.containsKey("gender"))
            spinnerGender.setSelection(savedInstanceState.getInt("gender"));

        if (savedInstanceState.containsKey("maritalstatus"))
            spinnerStatus.setSelection(savedInstanceState.getInt("maritalstatus"));

        if (savedInstanceState.containsKey("height"))
            edtHeight.setText(savedInstanceState.getString("height"));

        if (savedInstanceState.containsKey("iq"))
            edtIq.setText(savedInstanceState.getString("iq"));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("name", String.valueOf(edtName.getText()));
        outState.putString("age", String.valueOf(edtAge.getText()));
        outState.putInt("gender", spinnerGender.getSelectedItemPosition());
        outState.putInt("maritalstatus", spinnerStatus.getSelectedItemPosition());
        outState.putString("height", String.valueOf(edtHeight.getText()));
        outState.putString("iq", String.valueOf(edtIq.getText()));
    }

    private Toolbar toolbar;
    private CircleImageView imgSelected;
    private FloatingActionButton fabCapture;
    private TextInputEditText edtName;
    private TextInputEditText edtAge;
    private Spinner spinnerStatus;
    private Spinner spinnerGender;
    private TextInputEditText edtHeight;
    private TextInputEditText edtIq;
    private FloatingActionButton fab;
    private ProgressBar pG;

    public void initViews() {
        toolbar = findViewById(R.id.toolbar);
        imgSelected = findViewById(R.id.imgSelected);
        fabCapture = findViewById(R.id.fabCapture);
        edtName = findViewById(R.id.edtName);
        edtAge = findViewById(R.id.edtAge);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        spinnerGender = findViewById(R.id.spinnerGender);
        edtHeight = findViewById(R.id.edtHeight);
        edtIq = findViewById(R.id.edtIq);
        fab = findViewById(R.id.fab);
        pG = findViewById(R.id.pG);
    }


    String path;
    File newFile;

    void init() {
        fabCapture.setOnClickListener(view -> {
            //Create folder !exist
            String folderPath = Environment.getExternalStorageDirectory() + "/tenakata";
            File folder = new File(folderPath);
            if (!folder.exists()) {
                File wallpaperDirectory = new File(folderPath);
                if (!wallpaperDirectory.mkdirs()) {
                    Snackbar.make(toolbar, "Unable to create directory", Snackbar.LENGTH_INDEFINITE).setAction("Exit", view1 -> finish()).show();
                    return;
                }
            }
            //create a new file
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg";
            newFile = new File(folderPath, timeStamp);

            // save image here
            path = newFile.getPath();
//            Uri relativePath = Uri.fromFile(newFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, relativePath);
            startActivityForResult(intent, 789);
        });

        fab.setOnClickListener(view -> getValues());

        getPermission();
    }

    private void getValues() {
        if (TextUtils.isEmpty(edtName.getText())) {
            edtName.setError("*");
            return;
        }
        if (TextUtils.isEmpty(edtAge.getText())) {
            edtAge.setError("*");
            return;
        }
        if (TextUtils.isEmpty(edtHeight.getText())) {
            edtHeight.setError("*");
            return;
        }
        if (TextUtils.isEmpty(edtIq.getText())) {
            edtIq.setError("*");
            return;
        }

        if (spinnerStatus.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Kindly select marital status", Toast.LENGTH_SHORT).show();
            return;
        }
        if (spinnerGender.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Kindly select gender", Toast.LENGTH_SHORT).show();
            return;
        }

        if (photo == null) {
            Toast.makeText(this, "Kindly capture image", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(path);
        if (!file.exists()) {
            Toast.makeText(this, "Kindly capture image", Toast.LENGTH_SHORT).show();
            return;
        }


        UserModel userModel = new UserModel();
        userModel.setPhoto(file.getName());
        userModel.setName(String.valueOf(edtName.getText()));
        userModel.setAge(Integer.parseInt(String.valueOf(edtAge.getText())));
        userModel.setHeight(Integer.parseInt(String.valueOf(edtHeight.getText())));
        userModel.setIqTest(Double.parseDouble(String.valueOf(edtIq.getText())));
        userModel.setMaritalStatus(String.valueOf(spinnerStatus.getSelectedItem()));
        userModel.setGender(String.valueOf(spinnerGender.getSelectedItem()));

        fab.setEnabled(false);
        pG.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            if (myDb.INSERT_DATA(userModel)) {
                resetVals();
                Toast.makeText(CaptureInfoActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                return;
            }

            fab.setEnabled(true);
            pG.setVisibility(View.GONE);
            new AlertDialog.Builder(CaptureInfoActivity.this)
                    .setMessage("Failed to save data")
                    .setCancelable(true)
                    .setNeutralButton("Dismiss", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        }, 3000);
    }

    private void resetVals() {
        fab.setEnabled(true);
        pG.setVisibility(View.GONE);
        edtName.setText("");
        edtAge.setText("");
        edtIq.setText("");
        edtHeight.setText("");
        spinnerGender.setSelection(0);
        spinnerStatus.setSelection(0);
        imgSelected.setImageResource(R.mipmap.default_useravatar);
    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PERMISSION_GRANTED)
            EasyPermissions.requestPermissions(this, "CaptureInfo", 4500,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            );
    }

    Bitmap photo;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 789 && resultCode == Activity.RESULT_OK) {
            assert data != null;
            assert data.getExtras() != null;
            photo = (Bitmap) data.getExtras().get("data");
            imgSelected.setImageBitmap(photo);
            FileOutputStream out;
            try {
                out = new FileOutputStream(newFile);
                photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (IOException e) {
                Log.e("TAG", Objects.requireNonNull(e.getMessage()));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (perms.contains(Manifest.permission.CAMERA)) {
            Toast.makeText(this, "You need camera permission", Toast.LENGTH_SHORT).show();
            getPermission();
        }
    }


}