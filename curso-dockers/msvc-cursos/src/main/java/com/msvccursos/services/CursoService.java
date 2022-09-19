package com.msvccursos.services;

import com.msvccursos.models.Usuario;
import com.msvccursos.models.entity.Cursos;

import java.util.List;
import java.util.Optional;

public interface CursoService {

    List<Cursos> listar();

    Optional<Cursos> porId(Long id);

    Cursos guardar(Cursos usuario);

    void eliminar(Long id);

    void eliminarCursoUsuarioPorId(Long id);

    Optional<Usuario> asginarUsuario(Usuario usuario, Long cursoId);

    Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId);
    Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId);
    Optional<Cursos> porIdConUsuarios(Long id);
}
