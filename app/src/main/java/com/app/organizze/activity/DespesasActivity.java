package com.app.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.organizze.R;
import com.app.organizze.config.ConfiguracaoFirebase;
import com.app.organizze.helper.Base64Custom;
import com.app.organizze.helper.Datautil;
import com.app.organizze.model.Movimentacao;
import com.app.organizze.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText campoCategoria, campoData, campoDescricao;
    private EditText campoValor;
    private DatabaseReference firebaseref = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double despesaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        campoData = findViewById(R.id.editDatad);
        campoCategoria = findViewById(R.id.editCategoriaD);
        campoDescricao = findViewById(R.id.editDescricaoD);
        campoValor = findViewById(R.id.editValorD);

        campoData.setText(Datautil.dataAtual());
        recuperarDespesaTotal();
    }

    public void salvarDespesa(View view){

        if (validarCampoDespesa()) {
            Movimentacao m = new Movimentacao();
            String data = campoData.getText().toString();
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());
            m.setValor(valorRecuperado);
            m.setCategoria(campoCategoria.getText().toString());
            m.setDescricao(campoDescricao.getText().toString());
            m.setData(data);
            m.setTipo("d");
            Double despesaAtualizada = despesaTotal + valorRecuperado;
            atualizarDespesa(despesaAtualizada);
            m.salvar(data);

            finish();
        }
    }

    public boolean validarCampoDespesa(){

        String valor = campoValor.getText().toString();
        String data = campoData.getText().toString();
        String categoria = campoCategoria.getText().toString();
        String desc = campoDescricao.getText().toString();

        if (valor.isEmpty()){
            if (data.isEmpty()){
                if (categoria.isEmpty()){
                    if (desc.isEmpty()){
                        return true;
                    }else {
                        Toast.makeText(this, "Descrição não foi preenchido!", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                }else {
                    Toast.makeText(this, "Categoria não foi preenchido!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else {
                Toast.makeText(this, "Data não foi preenchido!", Toast.LENGTH_SHORT).show();
                return false;
            }

        }else {
            Toast.makeText(this, "Valor não foi preenchido!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void recuperarDespesaTotal(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);

        DatabaseReference usuarioRef = firebaseref.child("usuarios").child(idUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void atualizarDespesa(Double despesaAtualizada){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);

        DatabaseReference usuarioRef = firebaseref.child("usuarios").child(idUsuario);
        usuarioRef.child("despesaTotal").setValue(despesaAtualizada);
    }
}