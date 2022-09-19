package com.msvcusuario.controllers;

import com.msvcusuario.models.entity.Usuario;
import com.msvcusuario.services.contratos.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
//@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping
    public ResponseEntity<?> listar(){
        List<Usuario> usuarioList = service.listar();
        return ResponseEntity.status(HttpStatus.OK).body(usuarioList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> listar(@PathVariable Long id){
        Optional<Usuario> usuario = service.porId(id);
        if(usuario.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(usuario);
        }
       return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid@RequestBody Usuario usuario, BindingResult result){
        ResponseEntity<?> errores = validar(result);
        if (errores != null) return errores;
        if(!usuario.getEmail().isEmpty() && service.existsByEmail(usuario.getEmail())){
            return ResponseEntity.badRequest().body(Collections.singletonMap("mensaje","Ya existe un usuario con ese correo"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
    }

    public ResponseEntity<?> validar(BindingResult result) {
        if(result.hasErrors()){
            Map<String,String> errores = new HashMap<>();
            result.getFieldErrors().forEach(e->errores.put(e.getField(),"El campo "+e.getField()+" "+e.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
        }
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid@RequestBody Usuario usuario, @PathVariable Long id,BindingResult result){
        ResponseEntity<?> errores = validar(result);
        if (errores != null) return errores;
        Optional<Usuario> o = service.porId(id);
        if(o.isPresent()){
            Usuario usuarioDb = o.get();
            if(!usuario.getEmail().isEmpty() && !usuario.getEmail().equalsIgnoreCase(usuarioDb.getEmail()) && service.porEmail(usuario.getEmail()).isPresent()){
                return ResponseEntity.badRequest().body(Collections.singletonMap("mensaje","Ya existe un usuario con ese correo"));
            }
            usuarioDb.setNombre(usuario.getNombre());
            usuarioDb.setEmail(usuario.getEmail());
            usuarioDb.setPassword(usuario.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuarioDb));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        Optional<Usuario> o = service.porId(id);
        if(o.isPresent()){
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/usuarios-por-curso")
    public ResponseEntity<?> obtenerAlumnosPorCurso(@RequestParam List<Long> ids){
        return ResponseEntity.ok(service.listarPorIds(ids));
    }

}
