package com.sravan.quizadmin;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.Dialog;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView cat_recycler_view;
    private Button addCatB;
    public List<CategoryModel> catList = new ArrayList<>();
    private FirebaseFirestore firestore;
    private EditText dialogCatName;
    private Button dialogAddB;
    private Dialog addCatDialog;
    private CategoryAdapter adapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        toolbar = findViewById(R.id.toolbar);
       //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Categories");

        cat_recycler_view = findViewById(R.id.cat_recycler);
        addCatB = findViewById(R.id.addCatB);

        List<String> catList = new ArrayList<>();

        //catList.add("GK");
        //catList.add("Maths");

        dialogCatName = findViewById(R.id.ac_cat_name);
        dialogAddB = findViewById(R.id.addCatB);


            firestore = FirebaseFirestore.getInstance();

            addCatDialog = new Dialog(CategoryActivity.this);
            addCatDialog.setContentView(R.layout.add_category_dialog);
            addCatDialog.setCancelable(true);
            addCatDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            addCatB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogCatName.getText().clear();
                    addCatDialog.show();
                }
            });

            dialogAddB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogCatName.getText().toString().isEmpty()) {
                        dialogCatName.setError("Enter Category Name");
                    }
                    addNewCategory(dialogCatName.getText().toString());
                }
            });

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            cat_recycler_view.setLayoutManager(layoutManager);

            loadData2();


    }

  /*  public void loadData(){
        catList.clear();

        firestore.collection("QUIZ").document("Categories")
                .get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()){
                    long count = (long)doc.get("COUNT");
                    for(int i=1; i<=count; i++)
                    {
                        String catName = doc.getString("CAT" + String.valueOf(i)+"_NAME");
                        String catid = doc.getString("CAT" + String.valueOf(i)+"_ID");
                        catList.add(new CategoryModel(catid,catName,"0"));
                    }
                    adapter = new CategoryAdapter(catList);
                    cat_recycler_view.setAdapter(adapter);

                }else{
                    Toast.makeText(CategoryActivity.this, "No category document Found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }*/

    private void loadData2(){
        catList.clear();

        firestore.collection("QUIZ")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private static final String TAG = "" ;

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if(document.exists()) {

                                    //long count = (long) document.get("COUNT");



                                    for (int i = 1; i<= task.getResult().size(); i++) {
                                        String catName = document.getString("CAT" + String.valueOf(i) + "_NAME");
                                        String catid = document.getString("CAT" + String.valueOf(i) + "_ID");
                                        catList.add(new CategoryModel(catid, catName, "0"));
                                    }
                                    adapter = new CategoryAdapter(catList);
                                    cat_recycler_view.setAdapter(adapter);
                                }else {
                                    Toast.makeText(CategoryActivity.this, "No category document Found", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        } else {

                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    private void addNewCategory(String title){
        addCatDialog.dismiss();

        Map<String, Object> catData = new ArrayMap<>();
        catData.put("NAME", title);
        catData.put("SETS",0);

        String doc_id = firestore.collection("QUIZ").document().getId();

        firestore.collection("QUIZ").document(doc_id).set(catData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Map<String, Object> catDoc = new ArrayMap<>();
                catDoc.put("CAT" + String.valueOf(catList.size() +1) +"_NAME",title);
                catDoc.put("CAT" + String.valueOf(catList.size() +1) +"_NAME",doc_id);
                catDoc.put("COUNT",catList.size() + 1);

                firestore.collection("QUIZ").document("Categories")
                        .update(catDoc)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CategoryActivity.this, "Category added successfully", Toast.LENGTH_SHORT).show();
                                catList.add(new CategoryModel(doc_id,title,"0"));
                                adapter.notifyItemInserted(catList.size());

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CategoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CategoryActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}