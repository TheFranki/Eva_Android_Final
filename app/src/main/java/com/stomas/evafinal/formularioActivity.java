package com.stomas.evafinal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class formularioActivity extends AppCompatActivity{
    private EditText txtNombre, txtAcompañante;
    private ListView lista;
    private Spinner spClase;
    private FirebaseFirestore db;
    String[] Clase={"Guerrero", "Mago", "Invocador", "Arquero", "Chamán", "Asesino"};
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_formulario);

        CargarLisaFirestore();
        db=FirebaseFirestore.getInstance();
        txtNombre=findViewById(R.id.txtNombre);
        txtAcompañante=findViewById(R.id.txtAcompañante);
        spClase=findViewById(R.id.spClase);
        lista=findViewById(R.id.lista);

        ArrayAdapter<String> adapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Clase);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spClase.setAdapter(adapter);
    }
    public void enviarDatosFirestore(View view){
        String nombre = txtNombre.getText().toString();
        String acompañante = txtAcompañante.getText().toString();
        String tipoClase=spClase.getSelectedItem().toString();

        Map<String, Object> clase = new HashMap<>();
        clase.put("nombre", nombre);
        clase.put("acompañante", acompañante);
        clase.put("clase", clase);

        db.collection("clase")
                .document(nombre)
                .set(tipoClase)
                .addOnSuccessListener(aVoid ->{
                    Toast.makeText(formularioActivity.this, "Datos enviados a la Firestore correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e->{
                    Toast.makeText(formularioActivity.this, "Error al enviar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    public void CargarLista(View view){
        CargarLisaFirestore();
    }
    public void CargarLisaFirestore(){
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        db.collection("clase")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<String> listaClase = new ArrayList<>();
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String linea = "|| " + document.getString("nombre") + " || " +
                                        document.getString("acompañante") + " || " +
                                        document.getString("clase");
                                listaClase.add(linea);
                            }
                            ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                                    formularioActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    listaClase
                            );
                            lista.setAdapter(adaptador);
                        }else{
                            Log.e("TAG", "Error al obtener los datos de Firestore", task.getException());
                        }
                    }
                });
    }
}
