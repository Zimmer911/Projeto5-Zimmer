package br.com.aula.text;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class postagem extends AppCompatActivity {

    private EditText inputNome;
    private EditText inputDescricao;
    private EditText inputNota;
    private Button buttonPublicar;
    private Button buttonSelecionarImagem;
    private ImageView imageViewImagem;

    private static final String URL = "https://ludis.onrender.com/api/publicacao";
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_postagem);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        TextInputLayout inputLayoutNome = findViewById(R.id.inputNome);
        TextInputLayout inputLayoutDescricao = findViewById(R.id.inputDescricao);
        TextInputLayout inputLayoutNota = findViewById(R.id.inputNota);

        inputNome = inputLayoutNome.getEditText();
        inputDescricao = inputLayoutDescricao.getEditText();
        inputNota = inputLayoutNota.getEditText();
        buttonPublicar = findViewById(R.id.buttonpublicar);
        buttonSelecionarImagem = findViewById(R.id.buttonSelecionarImagem);
        imageViewImagem = findViewById(R.id.imageViewImagem);
    }

    private void setupListeners() {
        buttonSelecionarImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        buttonPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicarPost();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imageViewImagem.setImageURI(selectedImageUri);
        }
    }

    private void publicarPost() {
        String nome = inputNome.getText().toString();
        String descricao = inputDescricao.getText().toString();
        String nota = inputNota.getText().toString();

        if (nome.isEmpty() || descricao.isEmpty() || nota.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Por favor, preencha todos os campos e selecione uma imagem", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criptografar os dados
        String nomeCriptografado = cifraCesar(nome, 3);
        String descricaoCriptografada = cifraCesar(descricao, 3);
        String notaCriptografada = cifraCesar(nota, 3);

        File imageFile = new File(getRealPathFromURI(selectedImageUri));

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("nome", nomeCriptografado)
                .addFormDataPart("descricao", descricaoCriptografada)
                .addFormDataPart("nota", notaCriptografada)
                .addFormDataPart("imagem", imageFile.getName(),
                        RequestBody.create(MediaType.parse("image/*"), imageFile))
                .build();

        Request request = new Request.Builder()
                .url(URL)
                .post(requestBody)
                .build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Erro", "Falha na requisição: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(postagem.this, "Erro ao criar publicação: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(postagem.this, "Publicação criada com sucesso!", Toast.LENGTH_SHORT).show();
                        limparCampos();
                    } else {
                        Toast.makeText(postagem.this, "Erro ao criar publicação: " + responseBody, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    private void limparCampos() {
        inputNome.setText("");
        inputDescricao.setText("");
        inputNota.setText("");
        imageViewImagem.setImageResource(android.R.color.transparent);
        selectedImageUri = null;
    }

    // Método de criptografia de César
    private String cifraCesar(String texto, int deslocamento) {
        StringBuilder resultado = new StringBuilder();
        for (char caractere : texto.toCharArray()) {
            if (Character.isLetter(caractere)) {
                char base = Character.isUpperCase(caractere) ? 'A' : 'a';
                resultado.append((char) (((caractere - base + deslocamento) % 26) + base));
            } else {
                resultado.append(caractere);
            }
        }
        return resultado.toString();
    }
}