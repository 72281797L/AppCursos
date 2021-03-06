package com.example.appcursos.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.appcursos.modelos.Rol;
import java.util.ArrayList;

public class RolBD {

    private static final String DATABASE_NAME = "appcursos.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLA_ROLES = "roles";
    private static final String COL_ROL_ID = "rol_id";
    private static final int NUM_COL_ROL_ID = 0;
    private static final String COL_NOMBRE_ROL = "nombre_rol";
    private static final int NUM_COL_NOMBRE_ROL = 1;
    private static final String COL_DESCRIPCION_ROL = "descripcion_rol";
    private static final int NUM_COL_DESCRIPCION_ROL = 2;

    private SQLiteDatabase bd;
    private AdminBD abd;

    public RolBD(Context context) {
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

    public SQLiteDatabase getBD() {
        return bd;
    }

    public void insertarRol(Rol rol) {
        bd = abd.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put(COL_NOMBRE_ROL, rol.getNombreRol());
        registro.put(COL_DESCRIPCION_ROL, rol.getDescripcionRol());
        bd.insert(TABLA_ROLES, null, registro);
        bd.close();
    }

    public int editarRol(int id, Rol rol) {
        bd = abd.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put(COL_NOMBRE_ROL, rol.getNombreRol());
        registro.put(COL_DESCRIPCION_ROL, rol.getDescripcionRol());
        int res = bd.update(TABLA_ROLES, registro, COL_ROL_ID + "=" + id,null);
        bd.close();
        return res;
    }

    public int eliminarRol(int id) {
        bd = abd.getReadableDatabase();
        int res = bd.delete(TABLA_ROLES, COL_ROL_ID + "=" + id, null);
        bd.close();
        return res;
    }

    public Rol buscarRol(String nombre) {
        bd = abd.getReadableDatabase();
        Cursor cursor = bd.query(TABLA_ROLES, new String[] {COL_ROL_ID, COL_NOMBRE_ROL, COL_DESCRIPCION_ROL}, COL_NOMBRE_ROL
                + " LIKE \"" + nombre + "\"", null, null, null, null, COL_NOMBRE_ROL);
        bd.close();
        return seleccionarRol(cursor);
    }

    public Rol seleccionarRol(Cursor cursor) {
        bd = abd.getReadableDatabase();
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        Rol rol = new Rol();
        rol.setRolId(cursor.getInt(NUM_COL_ROL_ID));
        rol.setNombreRol(cursor.getString(NUM_COL_NOMBRE_ROL));
        rol.setDescripcionRol(cursor.getString(NUM_COL_DESCRIPCION_ROL));
        cursor.close();
        bd.close();
        return rol;
    }

    public ArrayList<Rol> listarRoles() {
        bd = abd.getReadableDatabase();
        Cursor cursor = bd.query(TABLA_ROLES, new String[] {
                COL_ROL_ID, COL_NOMBRE_ROL, COL_DESCRIPCION_ROL
        }, null, null, null, null, COL_ROL_ID);

        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        ArrayList<Rol> listaRoles = new ArrayList<>();
        while (cursor.moveToNext()) {
            Rol rol = new Rol();
            rol.setRolId(cursor.getInt(NUM_COL_ROL_ID));
            rol.setNombreRol(cursor.getString(NUM_COL_NOMBRE_ROL));
            rol.setDescripcionRol(cursor.getString(NUM_COL_DESCRIPCION_ROL));
            listaRoles.add(rol);
        }
        cursor.close();
        bd.close();
        return listaRoles;
    }

    public boolean isExistsRol(String nombreRol) {
        String[] columns = {COL_NOMBRE_ROL};

        //Selection
        String selection = COL_NOMBRE_ROL + " = ? ";

        //Selection Args
        String[] selection_Args = {nombreRol};

        bd = abd.getReadableDatabase();
        //Query
        Cursor cursor = bd.query(TABLA_ROLES,
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
