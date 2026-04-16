package task2.registro_movimentacoes.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import task2.registro_movimentacoes.model.Usuario;
import task2.registro_movimentacoes.repository.UsuarioRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository repository;

    // Teste 14
    @Test
    public void deveRetornarSucessoNoLoginQuandoUsuarioExiste() throws Exception {
        Usuario mockUser = new Usuario();
        mockUser.setLogin("admin");
        mockUser.setSenha("admin");
        mockUser.setNome("Administrador");

        when(repository.findByLoginAndSenha("admin", "admin")).thenReturn(Optional.of(mockUser));

        String jsonRequest = "{\"login\":\"admin\", \"senha\":\"admin\"}";

        mockMvc.perform(post("/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Administrador"));
    }

    // Teste 15
    @Test
    public void deveRetornarNaoAutorizadoQuandoLoginFalha() throws Exception {
        when(repository.findByLoginAndSenha(anyString(), anyString())).thenReturn(Optional.empty());

        String jsonRequest = "{\"login\":\"errado\", \"senha\":\"errado\"}";

        mockMvc.perform(post("/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isUnauthorized());
    }

    // Teste 16
    @Test
    public void deveRetornarBadRequestSeEnviarJsonVazio() throws Exception {
        mockMvc.perform(post("/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }
}