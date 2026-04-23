package task2.registro_movimentacoes.controller;

import task2.registro_movimentacoes.model.Usuario;
import task2.registro_movimentacoes.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController 
{

    private final UsuarioRepository repository;

    public UsuarioController(UsuarioRepository repository) 
    {
        this.repository = repository;
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Usuario usuario) 
    {

        Optional<Usuario> user = repository.findByLoginAndSenha(
                usuario.getLogin(),
                usuario.getSenha()
        );

        if (user.isPresent()) 
        {
            return ResponseEntity.ok(user.get());
        } 
        else 
        {
            return ResponseEntity.status(401).build();
        }
    }
}