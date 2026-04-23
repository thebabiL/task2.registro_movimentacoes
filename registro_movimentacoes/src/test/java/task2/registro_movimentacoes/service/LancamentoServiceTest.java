package task2.registro_movimentacoes.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import task2.registro_movimentacoes.model.Lancamento;
import task2.registro_movimentacoes.model.SituacaoLancamento;
import task2.registro_movimentacoes.model.TipoLancamento;
import task2.registro_movimentacoes.repository.LancamentoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LancamentoServiceTest 
{

    @Mock
    private LancamentoRepository repository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private LancamentoService service;


    @Test
    void deveSalvarLancamentoEEnviarEmail() 
    {
        Lancamento lancamento = new Lancamento();
        lancamento.setDescricao("Venda de Produto");
        lancamento.setValor(new BigDecimal("500.00"));
        lancamento.setTipo(TipoLancamento.RECEITA);
        lancamento.setSituacao(SituacaoLancamento.PAGO);
        lancamento.setDataLancamento(LocalDate.of(2026, 4, 23));
        lancamento.setEmailDestino("usuario@exemplo.com");

        when(repository.save(any(Lancamento.class))).thenReturn(lancamento);

        Lancamento salvo = service.salvar(lancamento);

        assertNotNull(salvo);
        verify(repository, times(1)).save(lancamento);
        verify(emailService, times(1)).enviarEmailNotificacaoHtml(
                eq("usuario@exemplo.com"),
                eq("Novo Lançamento Registrado"),
                anyString() 
        );
    }


    @Test
    void deveAtualizarLancamentoComMudancasEEnviarEmail() 
    {
        Lancamento antigo = new Lancamento();
        antigo.setDescricao("Conta de Luz");
        antigo.setValor(new BigDecimal("100.00"));
        antigo.setTipo(TipoLancamento.DESPESA);
        antigo.setSituacao(SituacaoLancamento.PENDENTE);
        antigo.setDataLancamento(LocalDate.of(2026, 4, 20));

        Lancamento atualizado = new Lancamento();
        atualizado.setDescricao("Conta de Luz");
        atualizado.setValor(new BigDecimal("115.00")); 
        atualizado.setTipo(TipoLancamento.DESPESA);
        atualizado.setSituacao(SituacaoLancamento.PAGO); // Situação mudou
        atualizado.setDataLancamento(LocalDate.of(2026, 4, 20));
        atualizado.setEmailDestino("usuario@exemplo.com");

        when(repository.findById("123")).thenReturn(Optional.of(antigo));
        when(repository.save(any(Lancamento.class))).thenReturn(atualizado);

        Lancamento resultado = service.atualizar("123", atualizado);

        assertNotNull(resultado);
        assertEquals("123", resultado.getId());
        verify(repository, times(1)).save(atualizado);
        verify(emailService, times(1)).enviarEmailNotificacaoHtml(
                eq("usuario@exemplo.com"),
                eq("Alteração de Lançamento: Conta de Luz"),
                anyString()
        );
    }

    @Test
    void deveAtualizarLancamentoSemMudancasENaoEnviarEmail() 
    {
        Lancamento antigo = new Lancamento();
        antigo.setDescricao("Internet");
        antigo.setValor(new BigDecimal("99.90"));
        antigo.setTipo(TipoLancamento.DESPESA);
        antigo.setSituacao(SituacaoLancamento.PAGO);
        antigo.setDataLancamento(LocalDate.of(2026, 4, 10));

        Lancamento atualizado = new Lancamento();
        atualizado.setDescricao("Internet");
        atualizado.setValor(new BigDecimal("99.90"));
        atualizado.setTipo(TipoLancamento.DESPESA);
        atualizado.setSituacao(SituacaoLancamento.PAGO);
        atualizado.setDataLancamento(LocalDate.of(2026, 4, 10));

        when(repository.findById("123")).thenReturn(Optional.of(antigo));
        when(repository.save(any(Lancamento.class))).thenReturn(atualizado);

        service.atualizar("123", atualizado);

        verify(repository, times(1)).save(atualizado);
        verify(emailService, never()).enviarEmailNotificacaoHtml(anyString(), anyString(), anyString());
    }

    @Test
    void deveLancarExcecaoAoTentarAtualizarLancamentoInexistente() 
    {
        when(repository.findById("999")).thenReturn(Optional.empty());

        Lancamento atualizado = new Lancamento();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.atualizar("999", atualizado);
        });

        assertEquals("Lançamento não encontrado para edição.", exception.getMessage());
        
        verify(repository, never()).save(any());
        verify(emailService, never()).enviarEmailNotificacaoHtml(anyString(), anyString(), anyString());
    }


    @Test
    void deveListarTodosOsLancamentos() 
    {
        List<Lancamento> listaMock = Arrays.asList(new Lancamento(), new Lancamento());
        when(repository.findAll()).thenReturn(listaMock);

        List<Lancamento> resultado = service.listarTodos();

        assertEquals(2, resultado.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void deveDeletarLancamentoComSucesso() 
    {
        String idParaDeletar = "123";
        service.deletar(idParaDeletar);
        verify(repository, times(1)).deleteById(idParaDeletar);
    }
}