package com.zenpets.users.editor.pet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
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
import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PetEditor extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    /** THE INCOMING PET ID **/
    private String PET_ID;

    /** THE LOGGED IN USER ID **/
    private String USER_ID;

    /** THE PET TYPES AND BREED NAMES ARRAY LISTS **/
    private final ArrayList<PetTypesData> arrPetTypes = new ArrayList<>();
    private final ArrayList<BreedTypesData> arrBreeds = new ArrayList<>();

    /****** DATA TYPES FOR CATEGORY DETAILS *****/
    private String FILE_NAME = null;
    private String PET_TYPE_NAME = null;
    private String PET_BREED_NAME = null;
    private String PET_NAME = null;
    private String PET_GENDER = "Male";
    private String PET_DOB = null;
    private String PET_PICTURE_LINK = null;
    private Bitmap BMP_IMAGE = null;
    private Uri PET_URI = null;

    /** A PROGRESS DIALOG **/
    private ProgressDialog dialog;

    /** THE URI'S **/
    private Uri imageUri;

    /** REQUEST CODE FOR SELECTING AN IMAGE **/
    private final int PICK_GALLERY_REQUEST = 1;
    private final int PICK_CAMERA_REQUEST = 2;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaHeaderProgress) LinearLayout linlaHeaderProgress;
    @BindView(R.id.spnPetTypes) AppCompatSpinner spnPetTypes;
    @BindView(R.id.spnBreeds) AppCompatSpinner spnBreeds;
    @BindView(R.id.inputPetName) TextInputLayout inputPetName;
    @BindView(R.id.edtPetName) AppCompatEditText edtPetName;
    @BindView(R.id.groupGender) SegmentedButtonGroup groupGender;
    @BindView(R.id.txtPetDOB) AppCompatTextView txtPetDOB;
    @BindView(R.id.imgvwPetThumb) AppCompatImageView imgvwPetThumb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pet_editor);
        ButterKnife.bind(this);

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /** FETCH ALL PET TYPES **/
        fetchPetTypes();

        /** SELECT A PET TYPE **/
        spnPetTypes.setOnItemSelectedListener(selectPetType);

        /** SELECT THE BREED **/
        spnBreeds.setOnItemSelectedListener(selectBreed);
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
    }

    /** GET THE INCOMING DATA **/
    private void getIncomingData() {

        /** GET THE CATEGORY ID **/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("PET_ID_KEY")) {
            PET_ID = bundle.getString("PET_ID_KEY");
            if (PET_ID == null) {
                Toast.makeText(getApplicationContext(), "failed to get required information", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                /** GET THE FIREBASE USER ID **/
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    USER_ID = user.getUid();

                    /** SHOW THE PET DETAILS **/
                    showPetDetails();

                } else {
                    Toast.makeText(getApplicationContext(), "failed to get required information", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    /** SHOW THE PET DETAILS **/
    private void showPetDetails() {
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.hasChildren()) {
//                    pet = dataSnapshot.getValue(PetData.class);
//
//                    /** SET THE VALUES **/
//                    PET_TYPE_NAME = pet.getPetTypeName();
//                    PET_BREED_NAME = pet.getPetBreedName();
//
//                    /** SET THE APPROPRIATE PET TYPE IN THE SPINNER **/
//                    if (PET_TYPE_NAME != null)   {
//                        int posPetType = getTypeIndex(arrPetTypes, PET_TYPE_NAME);
//                        spnPetTypes.setSelection(posPetType);
//                    }
//
//                    /** SET THE APPROPRIATE BREED IN THE SPINNER **/
//                    if (PET_BREED_NAME != null)   {
//                        int posBreed = getBreedIndex(arrBreeds, PET_BREED_NAME);
//                        spnBreeds.setSelection(posBreed);
//                    }
//
//                    PET_NAME = pet.getPetName();
//                    edtPetName.setText(PET_NAME);
//
//                    PET_GENDER = pet.getPetGender();
//                    if (PET_GENDER.equalsIgnoreCase("male")) {
//                        groupGender.setPosition(0, true);
//                    } else if (PET_GENDER.equalsIgnoreCase("female")){
//                        groupGender.setPosition(1, true);
//                    }
//
//                    PET_PICTURE_LINK = pet.getPetPicture();
//                    if (pet.getPetPicture() != null)   {
//                        Picasso.with(PetEditor.this)
//                                .load(pet.getPetPicture())
//                                .resize(800, 600)
//                                .centerInside()
//                                .into(target);
//                    }
//
//                    /** CONVERT THE DOB TO YEAR, MONTH AND DATE**/
//                    try {
//                        PET_DOB = pet.getPetDOB();
//                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//                        Date dtDOB = format.parse(PET_DOB);
//                        Calendar calDOB = Calendar.getInstance();
//                        calDOB.setTime(dtDOB);
//
//                        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
//                        String selectedDate = dateFormat.format(calDOB.getTime());
//                        txtPetDOB.setText(selectedDate);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    Toast.makeText(getApplicationContext(), "failed to get required information", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
    }

    /** GET THE PET TYPE POSITION **/
    private int getTypeIndex(ArrayList<PetTypesData> array, String name) {
        int index = 0;
        for (int i =0; i < array.size(); i++) {
            if (array.get(i).getPetTypeName().equalsIgnoreCase(name))   {
                index = i;
                break;
            }
        }
        return index;
    }

    /** GET THE BREED POSITION **/
    private int getBreedIndex(ArrayList<BreedTypesData> array, String name) {
        int index = 0;
        for (int i =0; i < array.size(); i++) {
            if (array.get(i).getBreedName().equalsIgnoreCase(name))   {
                index = i;
                break;
            }
        }
        return index;
    }

    /** THE PICASSO TARGET **/
    private final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            BMP_IMAGE = bitmap;
            imgvwPetThumb.setImageBitmap(BMP_IMAGE);

            /** STORE THE BITMAP AS A FILE AND USE THE FILE'S URI **/
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/PetStore");
            myDir.mkdirs();
            String fName = "photo.jpg";
            File file = new File(myDir, fName);
            if (file.exists()) file.delete();

            try {
                FileOutputStream out = new FileOutputStream(file);
                BMP_IMAGE.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                /** GET THE FINAL MENU URI **/
                PET_URI = Uri.fromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

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

    /** SELECT A PET TYPE **/
    private final AdapterView.OnItemSelectedListener selectPetType = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            /** CHANGE THE SELECTED PET TYPE **/
            PET_TYPE_NAME = arrPetTypes.get(position).getPetTypeName();
            DatabaseReference refBreed = FirebaseDatabase.getInstance().getReference().child(PET_TYPE_NAME);

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
        spnBreeds.setAdapter(new BreedsSpinnerAdapter(
                PetEditor.this,
                arrBreeds));

        /** GET THE INCOMING DATA **/
        getIncomingData();
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
            cameraFolder = new File(android.os.Environment.getExternalStorageDirectory(), "PetStore/");
        else
            cameraFolder = getCacheDir();
        if(!cameraFolder.exists())
            cameraFolder.mkdirs();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault());
        String timeStamp = dateFormat.format(new Date());
        String imageFileName = "picture_" + timeStamp + ".jpg";

        File photo = new File(Environment.getExternalStorageDirectory(), "PetStore/" + imageFileName);
        getCameraImage.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);

        startActivityForResult(getCameraImage, PICK_CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri targetURI;
        if (resultCode == RESULT_OK && requestCode == PICK_CAMERA_REQUEST)  {
            targetURI = imageUri;
            Bitmap bitmap = BitmapFactory.decodeFile(targetURI.getPath());
            Bitmap bmpSquared = getSquareBitmap(bitmap);
            BMP_IMAGE = resizeBitmap(bmpSquared, 600);
            imgvwPetThumb.setImageBitmap(BMP_IMAGE);
            imgvwPetThumb.setScaleType(ImageView.ScaleType.CENTER_CROP);

            /** STORE THE BITMAP AS A FILE AND USE THE FILE'S URI **/
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/PetStore");
            myDir.mkdirs();
            String fName = "photo.jpg";
            File file = new File(myDir, fName);
            if (file.exists()) file.delete();

            try {
                FileOutputStream out = new FileOutputStream(file);
                BMP_IMAGE.compress(Bitmap.CompressFormat.JPEG, 90, out);
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
                Bitmap bmpSquared = getSquareBitmap(bitmap);
                BMP_IMAGE = resizeBitmap(bmpSquared, 600);
                imgvwPetThumb.setImageBitmap(BMP_IMAGE);
                imgvwPetThumb.setScaleType(ImageView.ScaleType.CENTER_CROP);

                /** STORE THE BITMAP AS A FILE AND USE THE FILE'S URI **/
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/PetStore");
                myDir.mkdirs();
                String fName = "photo.jpg";
                File file = new File(myDir, fName);
                if (file.exists()) file.delete();

                try {
                    FileOutputStream out = new FileOutputStream(file);
                    BMP_IMAGE.compress(Bitmap.CompressFormat.JPEG, 90, out);
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

    /** CONVERT BITMAP IMAGE TO A SQUARE BITMAP **/
    private Bitmap getSquareBitmap(Bitmap bitmap) {
        Bitmap dstBmp;
        /** convert rectangle to square **/
        if (bitmap.getWidth() >= bitmap.getHeight()) {
            dstBmp = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 2 - bitmap.getHeight() / 2, 0, bitmap.getHeight(), bitmap.getHeight());
        } else {
            dstBmp = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight()/2 - bitmap.getWidth()/2, bitmap.getWidth(), bitmap.getWidth());
        }
        return dstBmp;
    }

    private Bitmap resizeBitmap(Bitmap image, int maxSize)   {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
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
                PetEditor.this,
                arrPetTypes));
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    @SuppressWarnings("ConstantConditions")
    private void configAB() {

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
        MenuInflater inflater = new MenuInflater(PetEditor.this);
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}