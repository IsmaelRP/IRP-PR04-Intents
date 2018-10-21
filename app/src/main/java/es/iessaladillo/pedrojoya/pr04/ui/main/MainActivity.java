package es.iessaladillo.pedrojoya.pr04.ui.main;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import es.iessaladillo.pedrojoya.pr04.R;
import es.iessaladillo.pedrojoya.pr04.data.local.Database;
import es.iessaladillo.pedrojoya.pr04.data.local.model.Avatar;
import es.iessaladillo.pedrojoya.pr04.ui.avatar.AvatarActivity;
import es.iessaladillo.pedrojoya.pr04.utils.KeyboardUtils;
import es.iessaladillo.pedrojoya.pr04.utils.SnackbarUtils;
import es.iessaladillo.pedrojoya.pr04.utils.ValidationUtils;

@SuppressWarnings("WeakerAccess")
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_AVATAR = "EXTRA_AVATAR";
    public static final int RC_AVATAR = 1;
    private Avatar avatar;

    private final Database database = Database.getInstance();

    private ImageView imgAvatar;
    private TextView lblAvatar;

    private TextView lblName;
    private TextView lblEmail;
    private TextView lblPhoneNumber;
    private TextView lblAddress;
    private TextView lblWeb;

    private EditText txtName;
    private EditText txtEmail;
    private EditText txtPhoneNumber;
    private EditText txtAddress;
    private EditText txtWeb;

    private ImageView imgEmail;
    private ImageView imgPhonenumber;
    private ImageView imgAddress;
    private ImageView imgWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        avatar = database.queryAvatar(1);

        imgAvatar = ActivityCompat.requireViewById(this, R.id.imgAvatar);
        imgAvatar.setImageResource(database.getDefaultAvatar().getImageResId());
        imgAvatar.setTag(database.getDefaultAvatar().getImageResId());
        imgAvatar.setOnClickListener(v -> AvatarActivity.startForResult(MainActivity.this, RC_AVATAR, avatar));

        lblAvatar = ActivityCompat.requireViewById(this, R.id.lblAvatar);
        lblAvatar.setText(database.getDefaultAvatar().getName());
        lblAvatar.setOnClickListener(v -> AvatarActivity.startForResult(MainActivity.this, RC_AVATAR, avatar));

        lblName = ActivityCompat.requireViewById(this, R.id.lblName);
        txtName = ActivityCompat.requireViewById(this, R.id.txtName);
        txtName.setOnFocusChangeListener((v, hasFocus) -> txtSwapBold(lblName));
        txtName.addTextChangedListener(new GenericTextWatcher(txtName));

        lblEmail = ActivityCompat.requireViewById(this, R.id.lblEmail);
        txtEmail = ActivityCompat.requireViewById(this, R.id.txtEmail);
        txtEmail.setOnFocusChangeListener((v, hasFocus) -> txtSwapBold(lblEmail));
        txtEmail.addTextChangedListener(new GenericTextWatcher(txtEmail));

        lblPhoneNumber = ActivityCompat.requireViewById(this, R.id.lblPhonenumber);
        txtPhoneNumber = ActivityCompat.requireViewById(this, R.id.txtPhonenumber);
        txtPhoneNumber.setOnFocusChangeListener((v, hasFocus) -> txtSwapBold(lblPhoneNumber));
        txtPhoneNumber.addTextChangedListener(new GenericTextWatcher(txtPhoneNumber));

        lblAddress = ActivityCompat.requireViewById(this, R.id.lblAddress);
        txtAddress = ActivityCompat.requireViewById(this, R.id.txtAddress);
        txtAddress.setOnFocusChangeListener((v, hasFocus) -> txtSwapBold(lblAddress));
        txtAddress.addTextChangedListener(new GenericTextWatcher(txtAddress));

        lblWeb = ActivityCompat.requireViewById(this, R.id.lblWeb);
        txtWeb = ActivityCompat.requireViewById(this, R.id.txtWeb);
        txtWeb.setOnFocusChangeListener((v, hasFocus) -> txtSwapBold(lblWeb));
        txtWeb.addTextChangedListener(new GenericTextWatcher(txtWeb));

        txtWeb.setOnEditorActionListener((v, actionId, event) -> {
            save();
            return true;
        });

        imgEmail = ActivityCompat.requireViewById(this, R.id.imgEmail);
        imgEmail.setOnClickListener(v -> sendEmail());

        imgPhonenumber = ActivityCompat.requireViewById(this, R.id.imgPhonenumber);
        imgPhonenumber.setOnClickListener(v -> dial());

        imgAddress = ActivityCompat.requireViewById(this, R.id.imgAddress);
        imgAddress.setOnClickListener(v -> maps());

        imgWeb = ActivityCompat.requireViewById(this, R.id.imgWeb);
        imgWeb.setOnClickListener(v -> searchWeb());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == RC_AVATAR) {
            if (data != null && data.hasExtra(AvatarActivity.EXTRA_AVATAR)) {
                avatar = data.getParcelableExtra(EXTRA_AVATAR);

                imgAvatar.setImageResource(avatar.getImageResId());
                imgAvatar.setTag(avatar.getImageResId());
                lblAvatar.setText(avatar.getName());
            }
        }
    }

    private void sendEmail(){
        Intent intent;
        String address = txtEmail.getText().toString();

        if (!isWrongEmail()){
            intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + address));
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                KeyboardUtils.hideSoftKeyboard(this);
                SnackbarUtils.snackbar(imgEmail, getString(R.string.error_email), Snackbar.LENGTH_SHORT);
            }
        }
    }

    private void dial(){
        Intent intent;
        String phoneNumber = txtPhoneNumber.getText().toString();

        if (!isWrongPhonenumber()){
            intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                KeyboardUtils.hideSoftKeyboard(this);
                SnackbarUtils.snackbar(imgPhonenumber, getString(R.string.error_phonenumber), Snackbar.LENGTH_SHORT);
            }
        }
    }

    private void maps(){
        Intent intent;
        String address = txtAddress.getText().toString();

        if (!isWrongAddress()){
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("geo:0,0?q=" + address));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                KeyboardUtils.hideSoftKeyboard(this);
                SnackbarUtils.snackbar(imgAddress, getString(R.string.error_address), Snackbar.LENGTH_SHORT);
            }
        }
    }

    private void searchWeb(){
        Intent intent;
        String web = txtWeb.getText().toString();

        if (!isWrongWeb()){
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(web));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                KeyboardUtils.hideSoftKeyboard(this);
                SnackbarUtils.snackbar(imgAddress, getString(R.string.error_address), Snackbar.LENGTH_SHORT);
            }
        }
    }


    private void txtSwapBold(TextView txt) {
        if (txt.getTypeface().isBold()) {
            txt.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        } else {
            txt.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
    }

    private class GenericTextWatcher implements TextWatcher {

        private final View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.txtName:
                    isWrongName();
                    break;
                case R.id.txtEmail:
                    isWrongEmail();
                    break;
                case R.id.txtPhonenumber:
                    isWrongPhonenumber();
                    break;
                case R.id.txtAddress:
                    isWrongAddress();
                    break;
                case R.id.txtWeb:
                    isWrongWeb();
                    break;
            }
        }
    }

    private boolean isWrongName() {
        boolean isWrong;
        if (txtName.getText().toString().length() <= 0) {
            txtName.setError((getString(R.string.main_invalid_data)));
            lblName.setTextColor(getResources().getColor(R.color.colorError));
            lblName.setEnabled(false);
            isWrong = true;
        } else {
            txtName.setError(null);
            lblName.setTextColor(getResources().getColor(R.color.colorBlack));
            lblName.setEnabled(true);
            isWrong = false;
        }
        return isWrong;
    }

    private boolean isWrongEmail() {
        boolean isWrong;
        if (!ValidationUtils.isValidEmail(txtEmail.getText().toString())) {
            txtEmail.setError((getString(R.string.main_invalid_data)));
            imgEmail.setEnabled(false);
            lblEmail.setTextColor(getResources().getColor(R.color.colorError));
            lblEmail.setEnabled(false);
            isWrong = true;
        } else {
            txtAddress.setError(null);
            imgEmail.setEnabled(true);
            lblEmail.setTextColor(getResources().getColor(R.color.colorBlack));
            lblEmail.setEnabled(true);
            isWrong = false;
        }
        return isWrong;
    }

    private boolean isWrongPhonenumber() {
        boolean isWrong;
        if (!ValidationUtils.isValidPhone(txtPhoneNumber.getText().toString())) {
            txtPhoneNumber.setError((getString(R.string.main_invalid_data)));
            imgPhonenumber.setEnabled(false);
            lblPhoneNumber.setTextColor(getResources().getColor(R.color.colorError));
            lblPhoneNumber.setEnabled(false);
            isWrong = true;
        } else {
            txtPhoneNumber.setError(null);
            imgPhonenumber.setEnabled(true);
            lblPhoneNumber.setTextColor(getResources().getColor(R.color.colorBlack));
            lblPhoneNumber.setEnabled(true);
            isWrong = false;
        }
        return isWrong;
    }

    private boolean isWrongAddress() {
        boolean isWrong;
        if (txtAddress.getText().toString().length() <= 0) {
            txtAddress.setError((getString(R.string.main_invalid_data)));
            imgAddress.setEnabled(false);
            lblAddress.setTextColor(getResources().getColor(R.color.colorError));
            lblAddress.setEnabled(false);
            isWrong = true;
        } else {
            txtAddress.setError(null);
            imgAddress.setEnabled(true);
            lblAddress.setTextColor(getResources().getColor(R.color.colorBlack));
            lblAddress.setEnabled(true);
            isWrong = false;
        }
        return isWrong;
    }

    private boolean isWrongWeb() {
        boolean isWrong;
        if (!ValidationUtils.isValidUrl(txtWeb.getText().toString())) {
            txtWeb.setError((getString(R.string.main_invalid_data)));
            imgWeb.setEnabled(false);
            lblWeb.setTextColor(getResources().getColor(R.color.colorError));
            lblWeb.setEnabled(false);
            isWrong = true;
        } else {
            txtWeb.setError(null);
            imgWeb.setEnabled(true);
            lblWeb.setTextColor(getResources().getColor(R.color.colorBlack));
            lblWeb.setEnabled(true);
            isWrong = false;
        }
        return isWrong;
    }

    private void save() {
        boolean flag = true;
        KeyboardUtils.hideSoftKeyboard(this);

        if (isWrongName()) {
            flag = false;
        }
        if (isWrongEmail()) {
            flag = false;
        }
        if (isWrongPhonenumber()) {
            flag = false;
        }
        if (isWrongAddress()) {
            flag = false;
        }

        if (isWrongWeb()) {
            flag = false;
        }

        if (flag) {
            SnackbarUtils.snackbar(imgAvatar, getString(R.string.main_saved_succesfully), Snackbar.LENGTH_SHORT);
        } else {
            SnackbarUtils.snackbar(imgAvatar, getString(R.string.main_error_saving), Snackbar.LENGTH_SHORT);
        }
    }

    // DO NOT TOUCH
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // DO NOT TOUCH
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnuSave) {
            save();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
