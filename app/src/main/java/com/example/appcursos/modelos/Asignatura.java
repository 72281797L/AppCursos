package com.example.appcursos.modelos;

public class Asignatura {

    private int asignaturaId;
    private String nombreAsignatura;
    private String descripcionAsignatura;
    private Curso curso;

    public Asignatura() {
    }

    public Asignatura(String nombreAsignatura, String descripcionAsignatura, Curso curso) {
        this.nombreAsignatura = nombreAsignatura;
        this.descripcionAsignatura = descripcionAsignatura;
        this.curso = curso;
    }

    public int getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(int asignaturaId) {
        this.asignaturaId = asignaturaId;
    }

    public String getNombreAsignatura() {
        return nombreAsignatura;
    }

    public void setNombreAsignatura(String nombreAsignatura) {
        this.nombreAsignatura = nombreAsignatura;
    }

    public String getDescripcionAsignatura() {
        return descripcionAsignatura;
    }

    public void setDescripcionAsignatura(String descripcionAsignatura) {
        this.descripcionAsignatura = descripcionAsignatura;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }
}
