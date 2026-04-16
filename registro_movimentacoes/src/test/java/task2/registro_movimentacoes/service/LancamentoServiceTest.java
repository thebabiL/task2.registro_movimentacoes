package task2.registro_movimentacoes.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import task2.registro_movimentacoes.model.Lancamento;
import task2.registro_movimentacoes.repository.LancamentoRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LancamentoServiceTest {

    @Mock
    private LancamentoRepository repository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private LancamentoService service;

    // Teste 6
    @Test
    public void deveSalvarLancamentoEEnviarEmail() {
        Lancamento lancamento = new Lancamento();
        lancamento.setDescricao("Venda");
        lancamento.setValor(new BigDecimal("500.00"));

        when(repository.save(any(Lancamento.class))).thenReturn(lancamento);

        Lancamento salvo = service.salvar(lancamento);

        assertNotNull(salvo);
        verify(repository, times(1)).save(lancamento);
        verify(emailService, times(1)).enviarEmailNotificacao(anyString(), anyString());
    }

    // Teste 7
    @Test
    public void deveListarTodosOsLancamentos() {
        List<Lancamento> listaMock = Arrays.asList(new Lancamento(), new Lancamento());
        when(repository.findAll()).thenReturn(listaMock);

        List<Lancamento> resultado = service.listarTodos();

        assertEquals(2, resultado.size());
        verify(repository, times(1)).findAll();
    }

    // Teste 8
    @Test
    public void deveDeletarLancamentoComSucesso() {
        String idParaDeletar = "123";
        service.deletar(idParaDeletar);
        verify(repository, times(1)).deleteById(idParaDeletar);
    }

    // Teste 9
    @Test
    public void deveAtualizarLancamento() {
        Lancamento lancamento = new Lancamento();
        lancamento.setDescricao("Luz");
        
        when(repository.save(any(Lancamento.class))).thenReturn(lancamento);
        
        Lancamento atualizado = service.atualizar("123", lancamento);
        
        assertEquals("123", lancamento.getId());
        assertNotNull(atualizado);
        verify(emailService, times(1)).enviarEmailNotificacao(anyString(), anyString());
    }
}