package com.example.appcursos.bd;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;

import com.example.appcursos.modelos.Usuario;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static javax.crypto.Cipher.ENCRYPT_MODE;

public class UsuarioBD {

    private static final String DATABASE_NAME = "appcursos.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLA_USUARIOS = "usuarios";
    private static final String COL_USUARIO_ID = "usuario_id";
    private static final int NUM_COL_USUARIO_ID = 0;
    private static final String COL_USUARIO_NAME = "username";
    private static final int NUM_COL_USUARIO_NAME = 1;
    private static final String COL_USUARIO_EMAIL = "email";
    private static final int NUM_COL_USUARIO_EMAIL = 2;
    private static final String COL_USUARIO_PASSWORD = "password";
    private static final int NUM_COL_USUARIO_PASSWORD = 3;

    private static final String AES = "AES";
    private static final String SHA_256 = "SHA-256";
    private static final String UTF_8 = "UTF-8";
    private SQLiteDatabase bd;
    private AdminBD abd;

    public UsuarioBD(Context context) {
        abd = new AdminBD(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void leerBD() {
        bd = abd.getReadableDatabase();
    }
    public void escribirBD() {
        bd = abd.getWritableDatabase();
    }
    public void cerrarBD() {
        bd.close();
    }

    public void insertarUsuario(Usuario usuario) {
        bd = abd.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put(COL_USUARIO_NAME, usuario.getUsername());
        registro.put(COL_USUARIO_EMAIL, usuario.getEmail());
        registro.put(COL_USUARIO_PASSWORD, usuario.getPassword());
        bd.insert(TABLA_USUARIOS, null, registro);
        bd.close();
    }
    public int editarUsuario(int id, Usuario usuario) {
        bd = abd.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put(COL_USUARIO_NAME, usuario.getUsername());
        registro.put(COL_USUARIO_EMAIL, usuario.getEmail());
        registro.put(COL_USUARIO_PASSWORD, usuario.getPassword());
        int res = bd.update(TABLA_USUARIOS, registro, COL_USUARIO_ID + "=" + id,null);
        bd.close();
        return res;
    }

    public int eliminarUsuario(int id) {
        bd = abd.getReadableDatabase();
        int res = bd.delete(TABLA_USUARIOS, COL_USUARIO_ID + "=" + id, null);
        bd.close();
        return res;
    }

    public Usuario buscarUsuario(String nombre) {
        bd = abd.getReadableDatabase();
        Cursor cursor = bd.query(TABLA_USUARIOS, new String[] {COL_USUARIO_ID, COL_USUARIO_NAME, COL_USUARIO_EMAIL, COL_USUARIO_PASSWORD}, COL_USUARIO_NAME
                + " LIKE \"" + nombre + "\"", null, null, null, null, COL_USUARIO_NAME);
        bd.close();
        return seleccionarUsuario(cursor);
    }

    public Usuario seleccionarUsuario(Cursor cursor) {
        bd = abd.getReadableDatabase();
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        Usuario usuario = new Usuario();
        usuario.setUsuarioId(cursor.getInt(NUM_COL_USUARIO_ID));
        usuario.setUsername(cursor.getString(NUM_COL_USUARIO_NAME));
        usuario.setEmail(cursor.getString(NUM_COL_USUARIO_EMAIL));
        usuario.setPassword(cursor.getString(NUM_COL_USUARIO_PASSWORD));
        cursor.close();
        bd.close();
        return usuario;
    }
    public ArrayList<Usuario> listarUsuarios() {
        bd = abd.getReadableDatabase();
        Cursor cursor = bd.query(TABLA_USUARIOS, new String[] {
                COL_USUARIO_ID, COL_USUARIO_NAME, COL_USUARIO_EMAIL, COL_USUARIO_PASSWORD
        }, null, null, null, null, COL_USUARIO_NAME);

        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        ArrayList<Usuario> listaUsuario = new ArrayList<>();
        while (cursor.moveToNext()) {
            Usuario usuario = new Usuario();
            usuario.setUsuarioId(cursor.getInt(NUM_COL_USUARIO_ID));
            usuario.setUsername(cursor.getString(NUM_COL_USUARIO_NAME));
            usuario.setEmail(cursor.getString(NUM_COL_USUARIO_EMAIL));
            usuario.setPassword(cursor.getString(NUM_COL_USUARIO_PASSWORD));
            listaUsuario.add(usuario);
        }
        cursor.close();
        bd.close();
        return listaUsuario;
    }

    public Usuario Authenticate(Usuario usuario) {

        // array of columns to fetch
        String[] columns = {
                COL_USUARIO_ID,
                COL_USUARIO_NAME,
                COL_USUARIO_EMAIL,
                COL_USUARIO_PASSWORD
        };
        bd = abd.getReadableDatabase();

        Cursor cursor = bd.query(TABLA_USUARIOS, //Table to query
                columns,                    //columns to return
                null,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order
        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            do {

                Usuario user = new Usuario(cursor.getInt(cursor.getColumnIndex(COL_USUARIO_ID)),
                        cursor.getString(cursor.getColumnIndex(COL_USUARIO_NAME)),
                        cursor.getString(cursor.getColumnIndex(COL_USUARIO_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(COL_USUARIO_PASSWORD)));
                try {
                    String descifrado = decrypt(user.getPassword(), usuario.getPassword());
                    if (descifrado.equalsIgnoreCase(usuario.getPassword()) && user.getEmail().equals(usuario.getEmail())) {
                        return user;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());

        }
        cursor.close();
        bd.close();
        return null;
    }

    private SecretKeySpec generateKey(String pass) throws Exception {
        MessageDigest sha = MessageDigest.getInstance(SHA_256);
        @SuppressLint({"NewApi", "LocalSuppress"}) byte[] key = pass.getBytes(StandardCharsets.UTF_8);
        key = sha.digest(key);
        SecretKeySpec secKey = new SecretKeySpec(key, AES);
        return secKey;
    }

    public String encrypt(String pass) throws Exception {
        SecretKeySpec secretKey = generateKey(pass);
        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] datosEncriptadosBytes = cipher.doFinal(pass.getBytes());
        String datosEncriptadosString = Base64.encodeToString(datosEncriptadosBytes, Base64.DEFAULT);
        return datosEncriptadosString;

        /*String encriptado = "";
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
            keyGenerator.init(128);
            SecretKey secretKey = generateKey(pass);
            byte[] bytesSecretKey = secretKey.getEncoded();
            SecretKeySpec secretKeySpec = new SecretKeySpec(bytesSecretKey, AES);
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] cifrado = cipher.doFinal(pass.getBytes());
            encriptado = Base64.encodeToString(cifrado, Base64.DEFAULT);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return encriptado;*/
    }

    private String decrypt(String datosEncriptadosString, String pass) throws Exception {
        SecretKeySpec secretKey = generateKey(pass);
        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] datosDescoficados = Base64.decode(datosEncriptadosString, Base64.DEFAULT);
        byte[] datosDesencriptadosByte = cipher.doFinal(datosDescoficados);
        String datosDesencriptadosString = new String(datosDesencriptadosByte);
        return datosDesencriptadosString;

        /*String desencriptado = "";
        try {
            SecretKey secretKey = generateKey(encriptado);
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] descifrado = cipher.doFinal(encriptado);
            desencriptado = new;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return desencriptado;*/
    }
    public boolean isEmailExists(String email) {
        // array of columns to fetch
        String[] columns = {COL_USUARIO_EMAIL};

        //Selection
        String selection = COL_USUARIO_EMAIL + " = ? ";

        //Selection Args
        String[] selection_Args = {email};

        bd = abd.getReadableDatabase();
        //Query
        Cursor cursor = bd.query(TABLA_USUARIOS,
                columns,
                selection,
                selection_Args,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            return true;
        }
        cursor.close();
        bd.close();
        return false;
    }

    public boolean isUsuarioExists(String username) {
        // array of columns to fetch
        String[] columns = {COL_USUARIO_NAME};

        //Selection
        String selection = COL_USUARIO_NAME + " = ? ";

        //Selection Args
        String[] selection_Args = {username};

        bd = abd.getReadableDatabase();
        //Query
        Cursor cursor = bd.query(TABLA_USUARIOS,
                columns,
                selection,
                selection_Args,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            return true;
        }
        cursor.close();
        bd.close();
        return false;
    }
}
