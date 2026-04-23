package task2.registro_movimentacoes.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest 
{

    // Teste 4
    @Test
    void deveCriarUsuarioAtivoComBuilder() 
    {
        Usuario usuario = Usuario.builder()
                .nome("Teste")
                .login("teste123")
                .situacao(SituacaoUsuario.ATIVO)
                .build();

        assertEquals("Teste", usuario.getNome());
        assertEquals(SituacaoUsuario.ATIVO, usuario.getSituacao());
    }

    // Teste 5
    @Test
    void deveAlterarSenhaDoUsuario() 
    {
        Usuario usuario = new Usuario();
        usuario.setSenha("senhaVelha");
        
        usuario.setSenha("senhaNova123");
        
        assertEquals("senhaNova123", usuario.getSenha());
    }
}