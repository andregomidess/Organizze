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

public class ReceitasActivity extends AppCompatActivity {

    private TextInputEditText campoCategoria, campoData, campoDescricao;
    private EditText campoValor;
    private DatabaseReference firebaseref = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double receitaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        campoData = findViewById(R.id.editDataR);
        campoCategoria = findViewById(R.id.editCategoriaR);
        campoDescricao = findViewById(R.id.editDescricaoR);
        campoValor = findViewById(R.id.editValorR);

        campoData.setText(Datautil.dataAtual());
        recuperarReceitaTotal();
    }

    public void salvarReceita(View view){

        if (validarCampoReceita()) {
            Movimentacao m = new Movimentacao();
            String data = campoData.getText().toString();
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());
            m.setValor(valorRecuperado);
            m.setCategoria(campoCategoria.getText().toString());
            m.setDescricao(campoDescricao.getText().toString());
            m.setData(data);
            m.setTipo("r");
            Double receitaAtualizada = receitaTotal + valorRecuperado;
            atualizarReceita(receitaAtualizada);
            m.salvar(data);

            finish();
        }
    }

    public boolean validarCampoReceita(){

        String valor = campoValor.getText().toString();
        String data = campoData.getText().toString();
        String categoria = campoCategoria.getText().toString();
        String desc = campoDescricao.getText().toString();

        if (!valor.isEmpty()){
            if (!data.isEmpty()){
                if (!categoria.isEmpty()){
                    if (!desc.isEmpty()){
                        return true;
                    }else {
                        Toast.makeText(this, "Descri????o n??o foi preenchido!", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                }else {
                    Toast.makeText(this, "Categoria n??o foi preenchido!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else {
                Toast.makeText(this, "Data n??o foi preenchido!", Toast.LENGTH_SHORT).show();
                return false;
            }

        }else {
            Toast.makeText(this, "Valor n??o foi preenchido!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void recuperarReceitaTotal(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);

        DatabaseReference usuarioRef = firebaseref.child("usuarios").child(idUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                receitaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void atualizarReceita(Double receitaAtualizada){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);

        DatabaseReference usuarioRef = firebaseref.child("usuarios").child(idUsuario);
        usuarioRef.child("receitaTotal").setValue(receitaAtualizada);
    }
}