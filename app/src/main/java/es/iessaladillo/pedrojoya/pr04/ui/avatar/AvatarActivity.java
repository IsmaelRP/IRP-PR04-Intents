package es.iessaladillo.pedrojoya.pr04.ui.avatar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import es.iessaladillo.pedrojoya.pr04.R;
import es.iessaladillo.pedrojoya.pr04.data.local.Database;
import es.iessaladillo.pedrojoya.pr04.data.local.model.Avatar;
import es.iessaladillo.pedrojoya.pr04.utils.ResourcesUtils;

public class AvatarActivity extends AppCompatActivity {

    public static final String EXTRA_AVATAR = "EXTRA_AVATAR";
    private Avatar avatar;

    private Database database;

    private ImageView imgAvatar1;
    private ImageView imgAvatar2;
    private ImageView imgAvatar3;
    private ImageView imgAvatar4;
    private ImageView imgAvatar5;
    private ImageView imgAvatar6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);
        getIntentData();
        initViews();
        switchCats2(avatar.getImageResId());
    }

    private void initViews() {
        database = Database.getInstance();

        imgAvatar1 = ActivityCompat.requireViewById(this, R.id.imgAvatar1);
        imgAvatar2 = ActivityCompat.requireViewById(this, R.id.imgAvatar2);
        imgAvatar3 = ActivityCompat.requireViewById(this, R.id.imgAvatar3);
        imgAvatar4 = ActivityCompat.requireViewById(this, R.id.imgAvatar4);
        imgAvatar5 = ActivityCompat.requireViewById(this, R.id.imgAvatar5);
        imgAvatar6 = ActivityCompat.requireViewById(this, R.id.imgAvatar6);

        imgAvatar1.setOnClickListener(v -> configureIntent(imgAvatar1));
        imgAvatar2.setOnClickListener(v -> configureIntent(imgAvatar2));
        imgAvatar3.setOnClickListener(v -> configureIntent(imgAvatar3));
        imgAvatar4.setOnClickListener(v -> configureIntent(imgAvatar4));
        imgAvatar5.setOnClickListener(v -> configureIntent(imgAvatar5));
        imgAvatar6.setOnClickListener(v -> configureIntent(imgAvatar6));
    }

    private void configureIntent(ImageView img) {

        switch (img.getId()) {
            case R.id.imgAvatar1:
                avatar = database.queryAvatar(1);
                break;
            case R.id.imgAvatar2:
                avatar = database.queryAvatar(2);
                break;
            case R.id.imgAvatar3:
                avatar = database.queryAvatar(3);
                break;
            case R.id.imgAvatar4:
                avatar = database.queryAvatar(4);
                break;
            case R.id.imgAvatar5:
                avatar = database.queryAvatar(5);
                break;
            case R.id.imgAvatar6:
                avatar = database.queryAvatar(6);
                break;
        }
        sendIntent(avatar);
    }

    public void onClick(View v) {
        switchCats1(v.getId());
        sendIntent(avatar);
    }

    private void switchCats1(int id) {
        switch (id) {
            case R.id.imgAvatar1:
                avatar = database.queryAvatar(1);
                break;
            case R.id.imgAvatar2:
                avatar = database.queryAvatar(2);
                break;
            case R.id.imgAvatar3:
                avatar = database.queryAvatar(3);
                break;
            case R.id.imgAvatar4:
                avatar = database.queryAvatar(4);
                break;
            case R.id.imgAvatar5:
                avatar = database.queryAvatar(5);
                break;
            case R.id.imgAvatar6:
                avatar = database.queryAvatar(6);
                break;
        }
    }

    private void switchCats2(int id) {
        Database database = Database.getInstance();

        if (id == database.queryAvatar(1).getImageResId()) {
            selectImageView(imgAvatar1);
        } else if (id == database.queryAvatar(2).getImageResId()) {
            selectImageView(imgAvatar2);
        } else if (id == database.queryAvatar(3).getImageResId()) {
            selectImageView(imgAvatar3);
        } else if (id == database.queryAvatar(4).getImageResId()) {
            selectImageView(imgAvatar4);
        } else if (id == database.queryAvatar(5).getImageResId()) {
            selectImageView(imgAvatar5);
        } else if (id == database.queryAvatar(6).getImageResId()) {
            selectImageView(imgAvatar6);
        }

    }


    // DO NO TOUCH
    private void selectImageView(ImageView imageView) {
        imageView.setAlpha(ResourcesUtils.getFloat(this, R.dimen.selected_image_alpha));
    }

    // DO NOT TOUCH
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static void startForResult(Activity activity, int requestCode, Avatar avatar) {
        Intent intent = new Intent(activity, AvatarActivity.class);
        intent.putExtra(EXTRA_AVATAR, avatar);
        activity.startActivityForResult(intent, requestCode);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_AVATAR)) {
            avatar = intent.getParcelableExtra(EXTRA_AVATAR);
        } else {
            throw new IllegalArgumentException("Activity cannot find extras " + EXTRA_AVATAR);
        }
    }

    private void sendIntent(Avatar avatar) {
        Intent result = new Intent();
        result.putExtra(EXTRA_AVATAR, avatar);
        setResult(RESULT_OK, result);
        finish();
    }

}
