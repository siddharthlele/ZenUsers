package com.zenpets.users.creator.pet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.zenpets.users.R;
import com.zenpets.users.utils.TypefaceSpan;
import com.zenpets.users.utils.adapters.pet.BreedsSpinnerAdapter;
import com.zenpets.users.utils.adapters.pet.PetTypeSpinnerAdapter;
import com.zenpets.users.utils.models.pet.BreedTypesData;
import com.zenpets.users.utils.models.pet.PetTypesData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PetCreator extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    /** THE URI'S **/
    private Uri imageUri;

    /** REQUEST CODE FOR SELECTING AN IMAGE **/
    private final int PICK_GALLERY_REQUEST = 1;
    private final int PICK_CAMERA_REQUEST = 2;

    /** A FIREBASE DATABASE REFERENCE INSTANCE **/
    private DatabaseReference reference;

    /** A FIREBASE STORAGE REFERENCE **/
    private StorageReference storageReference;

    /** THE USER ID AND USER KEY **/
    private String USER_ID;
    private String USER_KEY;

    /****** DATA TYPES FOR CATEGORY DETAILS *****/
    private String FILE_NAME = null;
    private String PET_TYPE_NAME = null;
    private String PET_BREED_NAME = null;
    private String PET_NAME = null;
    private String PET_GENDER = "Male";
    private String PET_DOB = null;
    private Uri PET_URI = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaHeaderProgress) LinearLayout linlaHeaderProgress;
    @BindView(R.id.spnPetTypes) AppCompatSpinner spnPetTypes;
    @BindView(R.id.spnBreeds) AppCompatSpinner spnBreeds;
    @BindView(R.id.inputPetName) TextInputLayout inputPetName;
    @BindView(R.id.edtPetName) AppCompatEditText edtPetName;
    @BindView(R.id.groupGender) SegmentedButtonGroup groupGender;
    @BindView(R.id.txtPetDOB) AppCompatTextView txtPetDOB;
    @BindView(R.id.imgvwPetThumb) AppCompatImageView imgvwPetThumb;

    /** THE PET TYPES AND BREED NAMES ARRAY LISTS **/
    private final ArrayList<PetTypesData> arrPetTypes = new ArrayList<>();
    private final ArrayList<BreedTypesData> arrBreeds = new ArrayList<>();

    /** A PROGRESS DIALOG **/
    private ProgressDialog dialog;

    /** SELECT AN IMAGE OF THE MEDICINE **/
    @OnClick(R.id.imgvwPetThumb) void selectImage()    {
        AlertDialog.Builder builder = new AlertDialog.Builder(PetCreator.this, R.style.ZenPetsDialog);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Gallery", "Camera"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which)  {
                    case 0:
                        getGalleryImage();
                        break;
                    case 1:
                        getCameraImage();
                        break;
                    default:
                        break;
                }
            }
        }); builder.show();
    }

    /** SELECT THE PET'S DATE OF BIRTH **/
    @OnClick(R.id.btnDOBSelector) void selectDOB()   {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = DatePickerDialog.newInstance(
                PetCreator.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show(getFragmentManager(), "DatePickerDialog");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pet_creator);
        ButterKnife.bind(this);

        /** GET THE USER DETAILS **/
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            USER_ID = user.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
            Query query = reference.orderByChild("userID").equalTo(USER_ID);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        USER_KEY = postSnapshot.getKey();
//                        Log.e("USER KEY", USER_KEY);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            finish();
        }

        /** SET THE CURRENT DATE **/
        setCurrentDate();

        /** FETCH ALL PET TYPES **/
        fetchPetTypes();

        /***** CONFIGURE THE TOOLBAR *****/
        configTB();

        /***** SELECT THE USER'S GENDER *****/
        groupGender.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
            @Override
            public void onClickedButtonPosition(int position) {
                Toast.makeText(getApplicationContext(), "Clicked: " + position, Toast.LENGTH_SHORT).show();
                if (position == 0)  {
                    PET_GENDER = "Male";
                } else if (position == 1)   {
                    PET_GENDER = "Female";
                }
            }
        });

        /** SELECT A PET TYPE **/
        spnPetTypes.setOnItemSelectedListener(selectPetType);

        /** SELECT THE BREED **/
        spnBreeds.setOnItemSelectedListener(selectBreed);

        /** INSTANTIATE THE STORAGE REFERENCE **/
        storageReference = FirebaseStorage.getInstance().getReference();

        /** INSTANTIATE THE PROGRESS DIALOG INSTANCE **/
        dialog = new ProgressDialog(this);
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = getString(R.string.add_a_new_pet);
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getApplicationContext()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(PetCreator.this);
        inflater.inflate(R.menu.activity_save_cancel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuSave:
                /***** CHECK FOR ALL PET DETAILS  *****/
                checkPetDetails();
                break;
            case R.id.menuCancel:
                /** CANCEL CATEGORY CREATION **/
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    /***** CHECK FOR ALL PET DETAILS  *****/
    private void checkPetDetails() {

        /** HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtPetName.getWindowToken(), 0);

        /** GET THE REQUIRED TEXTS **/
        PET_NAME = edtPetName.getText().toString().trim();
        if (!TextUtils.isEmpty(PET_NAME))   {
            FILE_NAME = PET_NAME.replaceAll(" ", "_").toLowerCase().trim();
        }

        if (TextUtils.isEmpty(PET_NAME))   {
            inputPetName.setError("Enter the Pet's name");
        } else if (PET_URI == null)   {
            Toast.makeText(getApplicationContext(), "Please select your Pet's image", Toast.LENGTH_LONG).show();
        } else {
            /** POST THE PET DETAILS TO FIREBASE **/
            startPosting();
        }
    }

    /** POST THE PET DETAILS TO FIREBASE **/
    private void startPosting() {

        /** SHOW THE PROGRESS DIALOG WHILE UPLOADING THE IMAGE **/
        dialog.setMessage("Please wait while we add the new Pet to your account..");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        StorageReference refStorage = storageReference.child("Pets").child(FILE_NAME);
        refStorage.putFile(PET_URI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadURL = taskSnapshot.getDownloadUrl();
                if (downloadURL != null) {
                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(USER_KEY).child("Pets").push();
                    reference.child("petTypeName").setValue(PET_TYPE_NAME);
                    reference.child("petBreedName").setValue(PET_BREED_NAME);
                    reference.child("petName").setValue(PET_NAME);
                    reference.child("petGender").setValue(PET_GENDER);
                    reference.child("petDOB").setValue(PET_DOB);
                    reference.child("petPicture").setValue(downloadURL.toString());

                    /** FINISH THE ACTIVITY **/
                    Toast.makeText(getApplicationContext(), "Successfully added your Pet", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Image upload failed. Please try again", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
    }

    /** SET THE CURRENT DATE **/
    private void setCurrentDate() {
        /** SET THE CURRENT DATE (DISPLAY ONLY !!!!) **/
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String formattedDate = dateFormat.format(new Date());
        txtPetDOB.setText(formattedDate);

        /** SET THE CURRENT DATE (DATABASE ONLY !!!!)**/
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        PET_DOB = sdf.format(cal.getTime());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int month, int date) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        /** FOR THE DATABASE ONLY !!!!**/
        PET_DOB = sdf.format(cal.getTime());
//        Log.e("INITIAL PET DOB", PET_DOB);

        /** FOR DISPLAY ONLY !!!! **/
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String selectedDate = dateFormat.format(cal.getTime());
        txtPetDOB.setText(selectedDate);
    }

    /** FETCH AN IMAGE FROM THE GALLERY **/
    private void getGalleryImage() {
        Intent getGalleryImage = new Intent();
        getGalleryImage.setType("image/*");
        getGalleryImage.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(getGalleryImage, "Choose a picture"), PICK_GALLERY_REQUEST);
    }

    /** FETCH AN IMAGE FROM THE CAMERA **/
    private void getCameraImage() {
        Intent getCameraImage = new Intent("android.media.action.IMAGE_CAPTURE");
        File cameraFolder;
        if (Environment.getExternalStorageState().equals (Environment.MEDIA_MOUNTED))
            cameraFolder = new File(android.os.Environment.getExternalStorageDirectory(), "ZenPets/");
        else
            cameraFolder = getCacheDir();
        if(!cameraFolder.exists())
            cameraFolder.mkdirs();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault());
        String timeStamp = dateFormat.format(new Date());
        String imageFileName = "picture_" + timeStamp + ".jpg";

        File photo = new File(Environment.getExternalStorageDirectory(), "ZenPets/" + imageFileName);
        getCameraImage.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);

        startActivityForResult(getCameraImage, PICK_CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap BMP_IMAGE;
        Uri targetURI;
        if (resultCode == RESULT_OK && requestCode == PICK_CAMERA_REQUEST)  {
            targetURI = imageUri;
            Bitmap bitmap = BitmapFactory.decodeFile(targetURI.getPath());
            BMP_IMAGE = resizeBitmap(bitmap);
            imgvwPetThumb.setImageBitmap(BMP_IMAGE);
            imgvwPetThumb.setScaleType(ImageView.ScaleType.CENTER_CROP);

            /** STORE THE BITMAP AS A FILE AND USE THE FILE'S URI **/
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/ZenPets");
            myDir.mkdirs();
            String fName = "photo.jpg";
            File file = new File(myDir, fName);
            if (file.exists()) file.delete();

            try {
                FileOutputStream out = new FileOutputStream(file);
                BMP_IMAGE.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                /** GET THE FINAL PET URI **/
                PET_URI = Uri.fromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == PICK_GALLERY_REQUEST) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                BMP_IMAGE = resizeBitmap(bitmap);
                imgvwPetThumb.setImageBitmap(BMP_IMAGE);
                imgvwPetThumb.setScaleType(ImageView.ScaleType.CENTER_CROP);

                /** STORE THE BITMAP AS A FILE AND USE THE FILE'S URI **/
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/ZenPets");
                myDir.mkdirs();
                String fName = "photo.jpg";
                File file = new File(myDir, fName);
                if (file.exists()) file.delete();

                try {
                    FileOutputStream out = new FileOutputStream(file);
                    BMP_IMAGE.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

                    /** GET THE FINAL PET URI **/
                    PET_URI = Uri.fromFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** RESIZE THE BITMAP **/
    private Bitmap resizeBitmap(Bitmap image)   {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = 1024;
            height = (int) (width / bitmapRatio);
        } else {
            height = 768;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    /** FETCH ALL PET TYPES **/
    private void fetchPetTypes() {
        PetTypesData pet1 = new PetTypesData();
        pet1.setPetTypeID("1");
        pet1.setPetTypeName("Dog");
        arrPetTypes.add(pet1);

        PetTypesData pet2 = new PetTypesData();
        pet2.setPetTypeID("2");
        pet2.setPetTypeName("Cat");
        arrPetTypes.add(pet2);

        PetTypesData pet3 = new PetTypesData();
        pet3.setPetTypeID("3");
        pet3.setPetTypeName("Bird");
        arrPetTypes.add(pet3);

        PetTypesData pet4 = new PetTypesData();
        pet4.setPetTypeID("4");
        pet4.setPetTypeName("Rabbit");
        arrPetTypes.add(pet4);

        PetTypesData pet5 = new PetTypesData();
        pet5.setPetTypeID("5");
        pet5.setPetTypeName("Small & Furry");
        arrPetTypes.add(pet5);

        PetTypesData pet6 = new PetTypesData();
        pet6.setPetTypeID("6");
        pet6.setPetTypeName("Horse");
        arrPetTypes.add(pet6);

        PetTypesData pet7 = new PetTypesData();
        pet7.setPetTypeID("7");
        pet7.setPetTypeName("Reptile");
        arrPetTypes.add(pet7);

        PetTypesData pet8 = new PetTypesData();
        pet8.setPetTypeID("8");
        pet8.setPetTypeName("Barn Yard");
        arrPetTypes.add(pet8);

        /** SET THE PET TYPES ADAPTER TO THE SPINNER DROPDOWN **/
        spnPetTypes.setAdapter(new PetTypeSpinnerAdapter(
                PetCreator.this,
                arrPetTypes));
    }

    /** SELECT A PET TYPE **/
    private final AdapterView.OnItemSelectedListener selectPetType = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            /** CHANGE THE SELECTED PET TYPE **/
            PET_TYPE_NAME = arrPetTypes.get(position).getPetTypeName();
            DatabaseReference refBreed = FirebaseDatabase.getInstance().getReference().child("Breeds").child(PET_TYPE_NAME);
//            Log.e("REFERENCE", String.valueOf(refBreed));

            /** CLEAR THE BREEDS ARRAY LIST **/
            arrBreeds.clear();

            /** GET THE LIST OF BREEDS **/
            refBreed.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    getBreedsSpinner(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /** SELECT A BREED **/
    private final AdapterView.OnItemSelectedListener selectBreed = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            PET_BREED_NAME = arrBreeds.get(position).getBreedName();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void getBreedsSpinner(DataSnapshot dataSnapshot) {
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            BreedTypesData types = postSnapshot.getValue(BreedTypesData.class);
            arrBreeds.add(types);
        }

        /** SET THE BREEDS ADAPTER TO THE BREEDS SPINNER **/
        spnBreeds.setAdapter(new BreedsSpinnerAdapter(this, arrBreeds));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}