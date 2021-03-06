package com.example.appcursos.actividades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
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
        return true;
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

        switch (item.getItemId()) {
            case R.id.action_editar:
                showDialog(0);
                break;
            case R.id.action_eliminar:
                cbd.eliminarCurso(lvCursos.getId());
                listaCursos.remove(info.position);
                showDialog(1);
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    @Override
    public Dialog onCreateDialog(int id) {
        Dialog dialogo;
        switch (id) {
            case 0:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.titulo_editar)
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setMessage(R.string.msg_editar)
                        .setPositiveButton(R.string.lb_si,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Qué hacer si el usuario pulsa "Si"
                                        Intent editar = new Intent(CursosActivity.this, EditarCursoActivity.class);
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
                dialogo = builder.create();
                break;
            case 1:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle(R.string.titulo_eliminar)
                        .setIcon(R.drawable.ic_info_black_24dp)
                        .setMessage(R.string.msg_eliminar)
                        .setPositiveButton(R.string.lb_si,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Qué hacer si el usuario pulsa "Si"

                                        showDialog(2);
                                        cursoAdaptador.notifyDataSetChanged();
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
                dialogo = builder2.create();
                break;
            case 2:
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
                break;
            default:
                return super.onCreateDialog(id);

        }
        return dialogo;

    }
}
