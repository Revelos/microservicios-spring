package com.msvccursos.controllers;

import com.msvccursos.models.Usuario;
import com.msvccursos.models.entity.Cursos;
import com.msvccursos.services.CursoService;
import feign.FeignException;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/cursos")
public class CursoController {

    @Autowired
    private CursoService service;

    @GetMapping
    public ResponseEntity<?> listar(){
        List<Cursos> usuarioCursos = service.listar();
        return ResponseEntity.status(HttpStatus.OK).body(usuarioCursos);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> listar(@PathVariable Long id){
        Optional<Cursos> cursos = service.porIdConUsuarios(id);
        if(cursos.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(cursos);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Cursos cursos,BindingResult result){
        ResponseEntity<?> errores = validar(result);
        if (errores != null) return errores;
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(cursos));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid@RequestBody Cursos cursos, BindingResult result, @PathVariable Long id){
        Optional<Cursos> o = service.porId(id);
        if(o.isPresent()){
            Cursos cursoDb = o.get();
            cursoDb.setNombre(cursos.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(cursoDb));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        Optional<Cursos> o = service.porId(id);
        if(o.isPresent()){
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/asignar-usuario/{cursoId}")
    public ResponseEntity asignarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> o = null;
        try {
            o = service.asginarUsuario(usuario, cursoId);
            if(o.isPresent()){
                return ResponseEntity.status(HttpStatus.CREATED).body(o.get());
            }
        }catch (FeignException e ){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("mensaje","no existe el usuario por el Id - Error en la comunicacion "+ e.getMessage()));
        }catch ( DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("mensaje","llave duplicada viola restricción de unicidad - Error en la comunicacion "+ ex.getCause()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping ("/crear-usuario/{cursoId}")
    public ResponseEntity crearUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> o = null;
        try {
            o = service.crearUsuario(usuario, cursoId);
            if(o.isPresent()){
                return ResponseEntity.status(HttpStatus.CREATED).body(o.get());
            }
        }catch (FeignException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("mensaje","no se creo el usuario - Error en la comunicacion "+ e.getMessage()));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar-usuario/{cursoId}")
    public ResponseEntity eliminarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> o = null;
        try {
            o = service.eliminarUsuario(usuario, cursoId);

        }catch (FeignException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("mensaje","no existe el usuario por el Id - Error en la comunicacion "+ e.getMessage()));
        }
        if(o.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar-curso-usuario/{id}")
    public ResponseEntity<?> eliminarCursoUsuarioPorId(@PathVariable Long id){
        service.eliminarCursoUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> validar(BindingResult result) {
        if(result.hasErrors()){
            Map<String,String> errores = new HashMap<>();
            result.getFieldErrors().forEach(e->errores.put(e.getField(),"El campo "+e.getField()+" "+e.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
        }
        return null;
    }
}
