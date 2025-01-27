package com.example.mobile.activity;

import static javax.microedition.khronos.opengles.GL10.GL_MAX_TEXTURE_SIZE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.mobile.ImageHelper;
import com.example.mobile.R;
import com.example.mobile.model.Category;
import com.example.mobile.model.Expense;
import com.example.mobile.model.ExpenseHolder;
import com.example.mobile.model.Member;
import com.example.mobile.service.ExpenseService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CreateExpenseActivity extends BaseActivity {

    EditText expenseNameEt, expenseCostEt;
    RadioGroup categoryRg;
    Button createExpenseBtn, cancelBtn, chooseImageBtn, deletePhotoBtn;
    ImageView receiptIv;
    LinearLayout membersCb;

    ExpenseService expenseService;
    int walletId;
    String accessToken, imagePath;
    List<Member> members;
    List<Category> categoriesExpense;
    Category selectedCategory;
    List<String> selectedMembers;
    Uri selectedImage;
    Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_expense);

        walletId = getIntent().getIntExtra("walletId", 0);
        accessToken = getIntent().getStringExtra("accessToken");
        members = getIntent().getParcelableArrayListExtra("members");

        expenseService = new ExpenseService(this);

        expenseNameEt = findViewById(R.id.name_expense_et);
        expenseCostEt = findViewById(R.id.cost_et);
        categoryRg = findViewById(R.id.category_expense_RG);
        createExpenseBtn = findViewById(R.id.create_expense_btn);
        cancelBtn = findViewById(R.id.cancel_expense_btn);
        membersCb = findViewById(R.id.members_cb);
        receiptIv = findViewById(R.id.receipt_iv);
        chooseImageBtn = findViewById(R.id.choose_image_btn);
        deletePhotoBtn = findViewById(R.id.delete_photo_btn);

        cancelBtn.setOnClickListener(v -> finish());

        categoriesExpense = MainActivity.getCategoriesExpense();
        selectedMembers = new ArrayList<>();

        deletePhotoBtn.setOnClickListener(v -> {
            imageBitmap = null;
            imagePath = null;
            receiptIv.setVisibility(View.GONE);
            deletePhotoBtn.setVisibility(View.GONE);
        });

        chooseImageBtn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                finish();
                startActivity(intent);
                return;
            }
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 100);
        });

        for (int i = 0; i < categoriesExpense.size(); i++) {
            RadioButton rdbtn = new RadioButton(CreateExpenseActivity.this);
            rdbtn.setId(categoriesExpense.get(i).getId());
            rdbtn.setText(categoriesExpense.get(i).getName());
            rdbtn.setTextAppearance(R.style.simple_label);
            rdbtn.setTextSize(18);
            rdbtn.setButtonDrawable(R.drawable.rb_radio_button);
            categoryRg.addView(rdbtn);
            if (i == 0) {
                rdbtn.setChecked(true);
                selectedCategory = new Category(rdbtn.getId(), categoriesExpense.get(i).getName());
            }
        }

        categoryRg.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = findViewById(checkedId);
            String radioText = rb.getText().toString();
            selectedCategory = new Category(checkedId, radioText);
        });

        for (int i = 0; i < members.size(); i++) {
            CheckBox cb = new CheckBox(CreateExpenseActivity.this);
            cb.setId(members.get(i).getUserId());
            cb.setText(members.get(i).getLogin());
            cb.setTextAppearance(R.style.simple_label);
            cb.setTextSize(18);
            cb.setButtonDrawable(R.drawable.cb_simple);
            membersCb.addView(cb);

            cb.setOnClickListener(v -> {
                if (cb.isChecked()) selectedMembers.add(cb.getText().toString());
                else selectedMembers.remove(cb.getText().toString());
            });
        }

        createExpenseBtn.setOnClickListener(v -> {
            Double costD = validateCost(expenseCostEt.getText().toString());

            if (expenseNameEt.getText().toString().length() == 0)
                expenseNameEt.setError("Wpisz nazwe wydatku!");
            else if (costD == 0)
                expenseCostEt.setError("Wpisz poprawną kwote wydatku !");
            else if (selectedMembers.size() == 0)
                Toast.makeText(CreateExpenseActivity.this, "Wybierz osoby dla których zrobiony jest wydatek", Toast.LENGTH_LONG).show();
            else if (imageBitmap != null) {
                expenseService.uploadReceiptImage(imageBitmap, accessToken, expenseNameEt.getText().toString(), path -> {
                    Expense expense = new Expense(expenseNameEt.getText().toString(), path, costD, selectedCategory);
                    ExpenseHolder expenseHolder = new ExpenseHolder(expense, selectedMembers);
                    expenseService.createExpense(accessToken, walletId, expenseHolder, walletId);
                });
            } else {
                Expense expense = new Expense(expenseNameEt.getText().toString(), null, costD, selectedCategory);
                ExpenseHolder expenseHolder = new ExpenseHolder(expense, selectedMembers);
                expenseService.createExpense(accessToken, walletId, expenseHolder, walletId);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();

            BitmapFactory.Options bitMapOption = new BitmapFactory.Options();
            bitMapOption.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(ImageHelper.getRealPathFromURI(this, selectedImage), bitMapOption);

            if (bitMapOption.outWidth < GL_MAX_TEXTURE_SIZE && bitMapOption.outHeight < GL_MAX_TEXTURE_SIZE)
                try {
                    imageBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), selectedImage));
                    receiptIv.setImageBitmap(imageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            else
                Toast.makeText(this, "Wybierz zdjęcie o mniejszej rozdzielczości", Toast.LENGTH_SHORT).show();

            receiptIv.setVisibility(View.VISIBLE);
            deletePhotoBtn.setVisibility(View.VISIBLE);
        }
    }

    public double validateCost(String cost) {
        if (Pattern.compile("^\\d{0,8}(\\.\\d{1,2})?$").matcher(cost).matches()) {
            try {
                return Double.parseDouble(cost);
            } catch (NumberFormatException e) {
                return 0;
            }
        } else return 0;
    }
}