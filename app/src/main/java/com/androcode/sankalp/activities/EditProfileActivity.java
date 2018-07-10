package com.androcode.sankalp.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androcode.sankalp.R;
import com.androcode.sankalp.networks.CallHttpRequest;
import com.androcode.sankalp.networks.VolleyConnect;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private Context ctx;
    private TextView editPhoto, mobileno;
    private CFAlertDialog.Builder builder;
    private CircleImageView profilePic;
    private File destination;
    private Bundle bundle;
    private String mobno, URL, dpURL;
    private SharedPreferences pos;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest;
    private StringRequest stringRequest;
    private Intent intent;
    private ImageView save;
    private EditText name, gender, bloodGroup, age, emailid;
    private static final int CAM_REQ_CODE = 1;
    private static final int GAL_REQ_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ctx = this;

        pos = ctx.getSharedPreferences("userinfo", MODE_PRIVATE);
        mobno = pos.getString("mobileno", "");
        requestQueue = VolleyConnect.getInstance().getRequestQueue();
        profilePic = (CircleImageView) findViewById(R.id.profile_photo);
        editPhoto = (TextView) findViewById(R.id.edit_profile_pic);
        name = (EditText) findViewById(R.id.editName);
        age = (EditText) findViewById(R.id.editAge);
        gender = (EditText) findViewById(R.id.editGender);
        bloodGroup = (EditText) findViewById(R.id.editBloodGroup);
        emailid = (EditText) findViewById(R.id.editEmail);
        mobileno = (TextView) findViewById(R.id.editMobno);
        save = (ImageView) findViewById(R.id.saveChanges);

        editPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                saveChanges(name, age, gender, bloodGroup, emailid);
                intent = new Intent(ctx, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                bundle = ActivityOptions.makeCustomAnimation(ctx, R.anim.left_to_right, R.anim.right_to_left).toBundle();
                startActivity(intent, bundle);
                finish();
            }
        });

        setDetails();
    }

    private void showDialog() {
        builder = new CFAlertDialog.Builder(ctx)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle("Select photo from ")
                .addButton("Gallery", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, GAL_REQ_CODE);
                    dialog.dismiss();
                }).addButton("Camera", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAM_REQ_CODE);
                    dialog.dismiss();
                });
        builder.show();
    }

    private void createApplicationFolder(Intent data) {
        File folder = new File(android.os.Environment.getExternalStorageDirectory(), File.separator + "Pictures/Sankalp");
        folder.mkdirs();
        saveImageCamera(data);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK
                && resultCode != Activity.RESULT_CANCELED
                && requestCode == CAM_REQ_CODE
                && data != null) {
            createApplicationFolder(data);
        }
        if (resultCode == RESULT_OK
                && resultCode != Activity.RESULT_CANCELED
                && requestCode == GAL_REQ_CODE
                && data != null) {
            try {
                saveImageGallery(data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveImageCamera(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        destination = new File("sdcard/Pictures/Sankalp/", mobno + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        uploadPic(destination);
    }

    private void saveImageGallery(Uri data) throws IOException {
        Bitmap thumbnail = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        destination = new File("sdcard/Pictures/Sankalp", mobno + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        uploadPic(destination);
    }

    private void uploadPic(File destination) {
        String shareImageUrl[] = {"http://" + getString(R.string.ip) + ":8080/StudentSpot/UploadPic"};
        CallHttpRequest callHttpRequest = new CallHttpRequest(ctx, destination, "FileUpload");
        callHttpRequest.execute(shareImageUrl);
    }

    private void setDetails() {
        dpURL = "";
        // Picasso.with(ctx).load(dpURL).placeholder(R.drawable.no_dp_big).into(profilePic);
        getDetails();
    }

    private void getDetails() {
        URL = "http://192.168.1.10/fetchprofile.php?mobileno=" + mobno;

        jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject = response.optJSONObject(i);
                    mobileno.setText(jsonObject.optString("mobileno"));
                    name.setText(jsonObject.optString("name"));
                    gender.setText(jsonObject.optString("gender"));
                    bloodGroup.setText(jsonObject.optString("bloodgroup"));
                    emailid.setText(jsonObject.optString("emailid"));
                    age.setText(jsonObject.optString("age"));
                    // Toast.makeText(ctx, jsonObject.optString("age") + "", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(ctx, error + "", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void saveChanges(EditText nameE, EditText ageE, EditText genderE, EditText bloodgroupE, EditText emailidE) {
        URL = "http://192.168.1.10/savechanges.php?name=" + nameE.getText().toString()
                + "&age=" + ageE.getText().toString() + "&gender=" + genderE.getText().toString()
                + "&bloodgroup=" + bloodgroupE.getText().toString() + "&emailid=" + emailidE.getText().toString()
                + "&mobileno=" + mobno;

        stringRequest = new StringRequest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    name.setText(nameE.getText().toString());
                    age.setText(ageE.getText().toString());
                    gender.setText(genderE.getText().toString());
                    bloodGroup.setText(bloodgroupE.getText().toString());
                    emailid.setText(emailidE.getText().toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }
}
