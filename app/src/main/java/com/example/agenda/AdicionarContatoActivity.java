package com.example.agenda;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agenda.controller.ContatoController;
import com.example.agenda.model.Contato;

public class AdicionarContatoActivity extends AppCompatActivity {
    private EditText editNome, editEmail, editTelefone;
    private Button btnSalvar;
    private ImageView imageViewFoto;
    private Contato contatoEditar;

    ContatoController contatoController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adicionar_contato);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        contatoController = new ContatoController(this);
        inicializarViews();

        //se veio um contato da lista, preenche o formulario (update)
        contatoEditar = (Contato) getIntent().getSerializableExtra("contato");
        if (contatoEditar != null){
            editNome.setText(contatoEditar.getNome());
            editEmail.setText(contatoEditar.getEmail());
            editTelefone.setText(contatoEditar.getTelefone());
        }
    }

    public void inicializarViews(){
        editNome = findViewById(R.id.editTextNome);
        editEmail = findViewById(R.id.editTextEmail);
        editTelefone = findViewById(R.id.editTextTelefone);
        btnSalvar = findViewById(R.id.btnSalvar);
        imageViewFoto = findViewById(R.id.imageView);

        btnSalvar.setOnClickListener(v -> salvarContato());
    }

    public void salvarContato(){
        //recuperar os valores dos edits
        String nome = editNome.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String telefone = editTelefone.getText().toString().trim();

        //validacao
        if (nome.isEmpty() || telefone.isEmpty()){
            Toast.makeText(this, "Nome e telefone são obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contatoEditar == null){
            contatoController.adicionarContato(nome, email, telefone, ""); //foto em branco
            Toast.makeText(this, "Contato adicionado com sucesso", Toast.LENGTH_SHORT).show();
        } else {
            contatoController.atualizarContato(contatoEditar.getId(), nome, email, telefone, contatoEditar.getFoto());
            Toast.makeText(this, "Contato atualizado com sucesso", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        contatoController.close();
    }
}