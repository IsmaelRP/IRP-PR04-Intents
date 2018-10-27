package es.iessaladillo.pedrojoya.pr04.ui.main;

import android.app.SearchManager;
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

import androidx.annotation.AnyRes;
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

    // YO NO DEFINIRÍA ESTÁ CONSTANTE AQUÍ. YA LA TIENES DEFINIDA EN AvatarActivity , A DONDE
    // PERTENECE.
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
        if (savedInstanceState == null) {
            txtName.requestFocus();
        }
    }

    private void initViews() {
        // MEJOR USA database.getDefaultAvatar().
        avatar = database.queryAvatar(1);

        imgAvatar = ActivityCompat.requireViewById(this, R.id.imgAvatar);
        showAvatar(database.getDefaultAvatar().getImageResId());
        imgAvatar.setOnClickListener(v -> startAvatarActivity());

        lblAvatar = ActivityCompat.requireViewById(this, R.id.lblAvatar);
        showLabel(database.getDefaultAvatar().getName());
        lblAvatar.setOnClickListener(v -> startAvatarActivity());

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

    private void showLabel(String text) {
        lblAvatar.setText(text);
    }

    private void startAvatarActivity() {
        AvatarActivity.startForResult(MainActivity.this, RC_AVATAR, avatar);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == RC_AVATAR) {
            if (data != null && data.hasExtra(AvatarActivity.EXTRA_AVATAR)) {
                // YO USARÍA AvatarActivity.EXTRA_AVATAR Y ASÍ ME AHORRO DEFINIR LA CONSTANTE
                // EN ESTA CLASE Y MANTENER EL MISMO VALOR EN AMBAS.
                avatar = data.getParcelableExtra(EXTRA_AVATAR);
                // YO HABRÍA HECHO AMBAS COSAS EN EL MISMO MÉTODO. AL FIN Y AL CABO MOSTRAR
                // UN AVATAR ES MOSTRAR TANTO SU FOTO COMO SU NOMBRE.
                showAvatar(avatar.getImageResId());
                showLabel(avatar.getName());
            }
        }
    }

    private void showAvatar(@AnyRes int resId) {
        imgAvatar.setImageResource(resId);
        imgAvatar.setTag(resId);
    }

    private void sendEmail() {
        Intent intent;
        String address = txtEmail.getText().toString();

        if (!isWrongEmail()) {
            // YO CREARÍA UN MÉTODO DE UTILIDAD ESTÁTICO EN ALGUNA CLASE DE UTILIDAD QUE PUDIERA
            // REUTILIZAR EN OTROS PROYECTOS. ALGO ASÍ COMO newSendEMailIntent(...)
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
        } else {
            // MUY BIEN. ME GUSTA COMO LO HAS GESTIONADO.
            setErrorEmail(isWrongEmail());
        }
    }

    private void dial() {
        Intent intent;
        String phoneNumber = txtPhoneNumber.getText().toString();

        if (!isWrongPhonenumber()) {
            intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                KeyboardUtils.hideSoftKeyboard(this);
                SnackbarUtils.snackbar(imgPhonenumber, getString(R.string.error_phonenumber), Snackbar.LENGTH_SHORT);
            }
        } else {
            setErrorPhonenumber(isWrongPhonenumber());
        }
    }

    private void maps() {
        Intent intent;
        String address = txtAddress.getText().toString();

        if (!isWrongAddress()) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("geo:0,0?q=" + address));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                KeyboardUtils.hideSoftKeyboard(this);
                SnackbarUtils.snackbar(imgAddress, getString(R.string.error_address), Snackbar.LENGTH_SHORT);
            }
        } else {
            setErrorAddress(isWrongAddress());
        }
    }

    private void searchWeb() {
        Intent intent;
        String web = txtWeb.getText().toString();

        if (!isWrongWeb()) {
            // USA MEJOR startsWith()
            if (web.substring(0, 8).matches("https://") || web.substring(0, 7).matches("http://")) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(web));
            } else {
                // O TAMBIÉN PUEDES SIMPLEMENTE AÑADIRLE TU EL http
                intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, web);
            }

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                KeyboardUtils.hideSoftKeyboard(this);
                SnackbarUtils.snackbar(imgWeb, getString(R.string.error_web), Snackbar.LENGTH_SHORT);
            }
        } else {
            setErrorWeb(isWrongWeb());
        }
    }


    // YO LO HABRÍA LLAMADO txtToggleBold() . SWAP SIGNIFICA INTERCAMBIAR Y DA
    // LA IMPRESIÓN DE QUE VA A RECIBIR DOS VALORES A INTERCAMBIAR. ¿?
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
                    setErrorName(isWrongName());
                    break;
                case R.id.txtEmail:
                    setErrorEmail(isWrongEmail());
                    break;
                case R.id.txtPhonenumber:
                    setErrorPhonenumber(isWrongPhonenumber());
                    break;
                case R.id.txtAddress:
                    setErrorAddress(isWrongAddress());
                    break;
                case R.id.txtWeb:
                    setErrorWeb(isWrongWeb());
                    break;
            }
        }
    }

    private boolean isWrongName() {
        boolean isWrong = false;
        if (txtName.getText().toString().length() <= 0) {
            isWrong = true;
        }
        return isWrong;
    }

    private void setErrorName(boolean wrong) {
        if (wrong) {
            txtName.setError((getString(R.string.main_invalid_data)));
            lblName.setEnabled(false);
        } else {
            txtName.setError(null);
            lblName.setEnabled(true);
        }
    }

    private boolean isWrongEmail() {
        boolean isWrong = false;
        if (!ValidationUtils.isValidEmail(txtEmail.getText().toString())) {
            isWrong = true;
        }
        return isWrong;
    }

    // ¿POR QUÉ NO HACES UN MÉTODO ÚNICO QUE RECIBA EL txt, EL img, EL lbl Y EL wrong ?
    // FÍJATE EN QUE TIENES VARIOS MÉTODOS PRÁCTICAMENTE IGUALES.
    private void setErrorEmail(boolean wrong) {
        if (wrong) {
            txtEmail.setError((getString(R.string.main_invalid_data)));
            imgEmail.setEnabled(false);
            lblEmail.setEnabled(false);
        } else {
            txtAddress.setError(null);
            imgEmail.setEnabled(true);
            lblEmail.setEnabled(true);
        }
    }

    private boolean isWrongPhonenumber() {
        boolean isWrong = false;
        if (!ValidationUtils.isValidPhone(txtPhoneNumber.getText().toString())) {
            isWrong = true;
        }
        return isWrong;
    }

    private void setErrorPhonenumber(boolean wrong) {
        if (wrong) {
            txtPhoneNumber.setError((getString(R.string.main_invalid_data)));
            imgPhonenumber.setEnabled(false);
            lblPhoneNumber.setEnabled(false);
        } else {
            txtPhoneNumber.setError(null);
            imgPhonenumber.setEnabled(true);
            lblPhoneNumber.setEnabled(true);
        }
    }

    private boolean isWrongAddress() {
        boolean isWrong = false;
        if (txtAddress.getText().toString().length() <= 0) {
            isWrong = true;
        }
        return isWrong;
    }

    private void setErrorAddress(boolean wrong) {
        if (wrong) {
            txtAddress.setError((getString(R.string.main_invalid_data)));
            imgAddress.setEnabled(false);
            lblAddress.setEnabled(false);
        } else {
            txtAddress.setError(null);
            imgAddress.setEnabled(true);
            lblAddress.setEnabled(true);
        }
    }

    private boolean isWrongWeb() {
        boolean isWrong = false;
        if (!ValidationUtils.isValidUrl(txtWeb.getText().toString())) {
            isWrong = true;
        }
        return isWrong;
    }

    private void setErrorWeb(boolean wrong) {
        if (wrong) {
            txtWeb.setError((getString(R.string.main_invalid_data)));
            imgWeb.setEnabled(false);
            lblWeb.setEnabled(false);
        } else {
            txtWeb.setError(null);
            imgWeb.setEnabled(true);
            lblWeb.setEnabled(true);
        }
    }

    private void save() {
        boolean valid;
        KeyboardUtils.hideSoftKeyboard(this);

        valid = isFormValid();

        if (valid) {
            SnackbarUtils.snackbar(imgAvatar, getString(R.string.main_saved_succesfully), Snackbar.LENGTH_SHORT);
        } else {
            SnackbarUtils.snackbar(imgAvatar, getString(R.string.main_error_saving), Snackbar.LENGTH_SHORT);
        }
    }

    private boolean isFormValid() {
        boolean valid = true;

        if (isWrongName()) {
            valid = false;
            setErrorName(isWrongName());
        }
        if (isWrongEmail()) {
            valid = false;
            setErrorEmail(isWrongEmail());
        }
        if (isWrongPhonenumber()) {
            valid = false;
            setErrorPhonenumber(isWrongPhonenumber());
        }
        if (isWrongAddress()) {
            valid = false;
            setErrorAddress(isWrongAddress());
        }

        if (isWrongWeb()) {
            valid = false;
            setErrorWeb(isWrongWeb());
        }

        return valid;
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
