package br.com.aula.text;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class postagem extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText nomeEditText;
    private EditText descricaoEditText;
    private EditText notaEditText;
    private ImageView imageView;
    private Uri imageUri;
    private Button postarButton;
    private Button escolherImagemButton;
    private static final int SHIFT = 3;
    public static final String CHAVE_SECRETA = "MinhaChaveSecreta123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_postagem);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        nomeEditText = ((TextInputLayout) findViewById(R.id.inputNome)).getEditText();
        descricaoEditText = ((TextInputLayout) findViewById(R.id.inputDescricao)).getEditText();
        notaEditText = ((TextInputLayout) findViewById(R.id.inputNota)).getEditText();
        imageView = findViewById(R.id.imageView);
        postarButton = findViewById(R.id.buttonpublicar);
        escolherImagemButton = findViewById(R.id.buttonEscolherImagem);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupListeners() {
        postarButton.setOnClickListener(v -> validateAndPost());
        escolherImagemButton.setOnClickListener(v -> openImageChooser());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private byte[] criptografarImagem(byte[] imagemBytes) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        byte[] chave = CHAVE_SECRETA.getBytes();
        byte[] imagemCriptografada = new byte[imagemBytes.length + salt.length];

        System.arraycopy(salt, 0, imagemCriptografada, 0, salt.length);

        for (int i = 0; i < imagemBytes.length; i++) {
            imagemCriptografada[i + salt.length] = (byte) (imagemBytes[i] ^
                    chave[i % chave.length] ^
                    salt[i % salt.length]);
        }

        return imagemCriptografada;
    }

    private void validateAndPost() {
        String nome = nomeEditText.getText().toString().trim();
        String descricao = descricaoEditText.getText().toString().trim();
        String nota = notaEditText.getText().toString().trim();

        if (!validateFields(nome, descricao, nota)) {
            return;
        }

        String nomeCriptografado = cifraCesar(nome, SHIFT);
        String descricaoCriptografada = cifraCesar(descricao, SHIFT);

        System.out.println("Nome original: " + nome);
        System.out.println("Nome criptografado: " + nomeCriptografado);
        System.out.println("Descrição original: " + descricao);
        System.out.println("Descrição criptografada: " + descricaoCriptografada);

        criarPostagem(nomeCriptografado, descricaoCriptografada, nota);
    }

    private boolean validateFields(String nome, String descricao, String nota) {
        if (nome.isEmpty() || descricao.isEmpty() || nota.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            int notaValue = Integer.parseInt(nota);
            if (notaValue < 0 || notaValue > 10) {
                Toast.makeText(this, "A nota deve estar entre 0 e 10", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Por favor, insira uma nota válida", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void criarPostagem(String nome, String descricao, String nota) {
        CustomTrustManager customTrustManager = new CustomTrustManager();
        OkHttpClient client = customTrustManager.getOkHttpClient();

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("nome", nome)
                .addFormDataPart("descricao", descricao)
                .addFormDataPart("nota", nota);

        if (imageUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                inputStream.close();

                // Remova a criptografia da imagem por enquanto para garantir compatibilidade
                RequestBody imageBody = RequestBody.create(
                        MediaType.parse(getContentResolver().getType(imageUri)),
                        bytes
                );

                builder.addFormDataPart("image", "imagem.jpg", imageBody);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao processar imagem", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url("https://ludis.onrender.com/api/publicacao")
                .post(requestBody)
                .build();

        System.out.println("Enviando para o servidor:");
        System.out.println("Nome: " + nome);
        System.out.println("Descrição: " + descricao);
        System.out.println("Nota: " + nota);
        System.out.println("Imagem incluída: " + (imageUri != null));

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(postagem.this,
                            "Erro ao criar postagem: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response)
                    throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(postagem.this,
                                "Postagem criada com sucesso!",
                                Toast.LENGTH_SHORT).show();
                        navigateToFeed();
                    });
                } else {
                    handleErrorResponse(response);
                }
            }
        });
    }

    private void navigateToFeed() {
        Intent intent = new Intent(postagem.this, Telafeed.class);
        startActivity(intent);
        finish();
    }

    private void handleErrorResponse(Response response) throws IOException {
        final String errorBody = response.body().string();
        runOnUiThread(() -> {
            Toast.makeText(postagem.this,
                    "Erro ao criar postagem: " + errorBody,
                    Toast.LENGTH_SHORT).show();
        });
    }

    private String cifraCesar(String texto, int deslocamento) {
        StringBuilder resultado = new StringBuilder();
        for (char caractere : texto.toCharArray()) {
            if (Character.isLetter(caractere)) {
                int base = Character.isUpperCase(caractere) ? 'A' : 'a';
                resultado.append((char) (((caractere - base + deslocamento) % 26) + base));
            } else {
                resultado.append(caractere);
            }
        }
        return resultado.toString();
    }
}