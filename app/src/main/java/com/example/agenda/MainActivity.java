package com.example.agenda;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agenda.controller.ContatoController;
import com.example.agenda.model.Contato;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    //propriedades referentes aos componentes View no layout
    private Toolbar toolbar;
    private ListView listView;
    private FloatingActionButton fabAdicionar;
    private List<Contato> contatos;
    private ArrayAdapter<Contato> adapter;
    private ContatoController controller;
    private static final String ARQUIVO_PREFERENCIA = "ArquivoPreferencia";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inicializarViews();
        carregarContatos();
        configurarListeners();
        carregarNomeUsuario();
    }

    private void inicializarViews(){
        //inicializar os componentes
        toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.listView);
        fabAdicionar = findViewById(R.id.fabAdicionar);

        fabAdicionar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdicionarContatoActivity.class);
            startActivity(intent);
        });

        //clique na toolbar - trocar o nome do usuario
        toolbar.setOnClickListener(v -> pedirNomeUsuario());
    }

    //SharedPreferences - nome do usuario na toolbar
    private void carregarNomeUsuario(){
        SharedPreferences preferences = getSharedPreferences(ARQUIVO_PREFERENCIA, 0);
        if (preferences.contains("nome")){
            String nome = preferences.getString("nome", "");
            toolbar.setTitle("Contatos de " + nome);
        } else {
            pedirNomeUsuario();
        }
    }

    private void pedirNomeUsuario(){
        EditText editNomeUsuario = new EditText(this);
        editNomeUsuario.setHint("Digite o seu nome");

        new AlertDialog.Builder(this)
                .setTitle("Qual o seu nome?")
                .setView(editNomeUsuario)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String nome = editNomeUsuario.getText().toString().trim();
                    if (nome.isEmpty()){
                        Toast.makeText(this, "Preencha o nome", Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences preferences = getSharedPreferences(ARQUIVO_PREFERENCIA, 0);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("nome", nome);
                        editor.commit();
                        toolbar.setTitle("Contatos de " + nome);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void carregarContatos(){
        controller = new ContatoController(this);
        contatos = controller.listarContatos();
        //precisamos levar os dados para a Activity - Adapter
        adapter = new ArrayAdapter<>(this, R.layout.list_item, contatos){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                }
                Contato contato = getItem(position);
                //escrever nos TextViews do layout
                TextView textNome = convertView.findViewById(R.id.textNomeLista);
                TextView textTelefone = convertView.findViewById(R.id.textTelLista);

                textNome.setText(contato.getNome());
                textTelefone.setText(contato.getTelefone());
                return convertView;
            }
        };
        listView.setAdapter(adapter);
    }

    private void configurarListeners(){
        //clique curto em item do listView - alterar contato
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Contato contato = contatos.get(position);
            Intent intent = new Intent(MainActivity.this, AdicionarContatoActivity.class);
            intent.putExtra("contato", contato);
            startActivity(intent);
        });

        //clique longo em item do listView - excluir contato
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Contato contato = contatos.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("Confirmação de Exclusão")
                    .setMessage("Deseja realmente excluir o contato " + contato.getNome() + "?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        controller.apagarContato(contato.getId());
                        carregarContatos(); //atualizar a listView
                        Toast.makeText(this, "Contato excluído", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Não", null)
                    .show();
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarContatos(); //ao voltar para esta activity, recarrega
    }
}