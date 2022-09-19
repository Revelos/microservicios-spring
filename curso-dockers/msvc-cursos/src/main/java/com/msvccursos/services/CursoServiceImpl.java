package com.msvccursos.services;

import com.msvccursos.clients.UsuarioClientRest;
import com.msvccursos.models.Usuario;
import com.msvccursos.models.entity.CursoUsuario;
import com.msvccursos.models.entity.Cursos;
import com.msvccursos.repository.CursosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CursoServiceImpl implements CursoService{

    private CursosRepository repository;

    @Autowired
    private UsuarioClientRest client;

    @Autowired
    public CursoServiceImpl(CursosRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cursos> listar() {
        return (List<Cursos>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cursos> porId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Cursos guardar(Cursos usuario) {
        return repository.save(usuario);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void eliminarCursoUsuarioPorId(Long id) {
        repository.eliminarCursoUsuarioPorId(id);
    }

    @Override
    @Transactional
    public Optional<Usuario> asginarUsuario(Usuario usuario, Long cursoId) {
        Optional<Cursos> o = repository.findById(cursoId);
        if(o.isPresent()){
            Usuario usarioMsvc = client.detalle(usuario.getId());
            Cursos curso = o.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usarioMsvc.getId());

            curso.addCursoUsuario(cursoUsuario);
            repository.save(curso);
            return Optional.of(usarioMsvc);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId) {
        Optional<Cursos> o = repository.findById(cursoId);
        if(o.isPresent()){
            Usuario usarioNuevoMsvc = client.crear(usuario);
            Cursos curso = o.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usarioNuevoMsvc.getId());

            curso.addCursoUsuario(cursoUsuario);
            repository.save(curso);
            return Optional.of(usarioNuevoMsvc);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId) {
        Optional<Cursos> o = repository.findById(cursoId);
        if(o.isPresent()){
            Usuario usarioNuevoMsvc = client.detalle(usuario.getId());
            Cursos curso = o.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usarioNuevoMsvc.getId());

            curso.removeCursoUsuario(cursoUsuario);
            repository.save(curso);
            return Optional.of(usarioNuevoMsvc);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Cursos> porIdConUsuarios(Long id){
        Optional<Cursos> o = repository.findById(id);
        if(o.isPresent()){
            Cursos curso = o.get();
            if(!curso.getCursoUsuarios().isEmpty()){
                List<Long> ids = curso.getCursoUsuarios().stream().map(cu-> cu.getUsuarioId()).collect(Collectors.toList());
                List<Usuario> usuarios = client.obtenerAlumnosPorCurso(ids);
                curso.setUsuarios(usuarios);
            }
            return Optional.of(curso);
        }
        return Optional.empty();
    }
}
