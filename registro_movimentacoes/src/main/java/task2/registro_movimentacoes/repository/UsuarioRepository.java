package task2.registro_movimentacoes.repository;

import task2.registro_movimentacoes.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> 
{
    Optional<Usuario> findByLoginAndSenha(String login, String senha);
}