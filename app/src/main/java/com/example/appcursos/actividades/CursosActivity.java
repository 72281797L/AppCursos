package com.example.appcursos.actividades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.appcursos.R;
import com.example.appcursos.adaptadores.CursoAdaptador;
import com.example.appcursos.bd.CursoBD;
import com.example.appcursos.modelos.Curso;
import java.util.ArrayList;

public class CursosActivity extends AppCompatActivity {

    ArrayList<Curso> listaCursos;
    CursoAdaptador cursoAdaptador;
    ListView lvCursos;
    CursoBD cbd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_cursos);

        cbd = new CursoBD(this);
        listaCursos = new ArrayList<>();
        lvCursos = findViewById(R.id.lvCursos);
        listaCursos = cbd.listarCursos();
        cbd.cerrarBD();
        cursoAdaptador = new CursoAdaptador(this, R.layout.activity_cursos, listaCursos);
        lvCursos.setAdapter(cursoAdaptador);
        registerForContextMenu(lvCursos);
        cursoAdaptador.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu2, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.searchView).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                consultar(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                consultar(newText);
                return false;
            }
        });
        return true;
    }

    private void consultar(String keyword) {
        Curso c = cbd.buscarCurso(keyword);
        if (listaCursos.contains(c)) {
            lvCursos.setAdapter(new CursoAdaptador(getApplicationContext(), R.layout.activity_cursos, listaCursos));
        } else {
            Dialog d;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.title_informacion)
                    .setIcon(R.drawable.ic_done_black_24dp)
                    .setMessage(R.string.msg_informacion)
                    .setNegativeButton(R.string.lb_cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            dialog.dismiss();
                        }
                    });
            d = builder.create();
            d.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_nuevo:
                Intent intent = new Intent(CursosActivity.this, NuevoCursoActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_salir:
                finish();
                Toast.makeText(getApplicationContext(), "Ha salido correctamente", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(CursosActivity.this, MainActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_contextual, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int posicion = info.position;

        switch (item.getItemId()) {
            case R.id.action_editar:
                //showDialog(0);
                Dialog dialogo1;
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle(R.string.titulo_editar)
                        .setMessage(R.string.msg_editar)
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setPositiveButton(R.string.lb_si,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Qué hacer si el usuario pulsa "Si"
                                        Intent editar = new Intent(CursosActivity.this, EditarCursoActivity.class);
                                        editar.putExtra("nombreCurso", String.valueOf(listaCursos.get(posicion).getNombreCurso()));
                                        startActivity(editar);
                                    }
                                })
                        .setNegativeButton(R.string.lb_no,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Qué hacer si el usuario pulsa "No"
                                        // En este caso se cierra directamente el diálogo y no se hace nada más
                                        dialog.dismiss();
                                    }
                                });
                dialogo1 = builder1.create();
                dialogo1.show();
                return true;
            case R.id.action_eliminar:
                //showDialog(1);
                Dialog dialogo2;
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle(R.string.titulo_eliminar)
                        .setMessage(R.string.msg_eliminar)
                        .setIcon(R.drawable.ic_info_black_24dp)
                        .setPositiveButton(R.string.lb_si,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Qué hacer si el usuario pulsa "Si"

                                        listaCursos.remove(posicion);
                                        cursoAdaptador.notifyDataSetChanged();
                                        String textoLista = String.valueOf(lvCursos.getItemAtPosition(posicion));
                                        String nombre = textoLista.substring(textoLista.indexOf("<>") + 2, textoLista.lastIndexOf("<>")).trim();
                                        cbd.eliminarCurso(nombre);
                                        showDialog(0);

                                    }
                                })
                        .setNegativeButton(R.string.lb_no,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Qué hacer si el usuario pulsa "No"
                                        // En este caso se cierra directamente el diálogo y no se hace nada más
                                        dialog.dismiss();
                                    }
                                });
                dialogo2 = builder2.create();
                dialogo2.show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public Dialog onCreateDialog(int id) {
        Dialog dialogo;
        AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
        builder3.setTitle(R.string.title_confirmar)
                .setIcon(R.drawable.ic_done_black_24dp)
                .setMessage(R.string.msg_confirmEliminar)
                .setNegativeButton(R.string.lb_cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        dialogo = builder3.create();
        return dialogo;
    }
}
