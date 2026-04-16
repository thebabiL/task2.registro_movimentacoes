package task2.registro_movimentacoes.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class LancamentoTest {

    // Teste 1
    @Test
    public void deveCriarLancamentoComValoresCorretos() {
        Lancamento lancamento = new Lancamento();
        lancamento.setDescricao("Conta de Luz");
        lancamento.setValor(new BigDecimal("150.00"));
        lancamento.setTipo(TipoLancamento.DEBITO);
        
        assertEquals("Conta de Luz", lancamento.getDescricao());
        assertEquals(new BigDecimal("150.00"), lancamento.getValor());
        assertEquals(TipoLancamento.DEBITO, lancamento.getTipo());
    }

    // Teste 2
    @Test
    public void deveAlterarSituacaoDoLancamento() {
        Lancamento lancamento = new Lancamento();
        lancamento.setSituacao(SituacaoLancamento.PENDENTE);
        
        lancamento.setSituacao(SituacaoLancamento.PAGO);
        
        assertEquals(SituacaoLancamento.PAGO, lancamento.getSituacao());
    }

    // Teste 3
    @Test
    public void deveVerificarSeDataLancamentoNaoENulaAposSetar() {
        Lancamento lancamento = new Lancamento();
        LocalDate hoje = LocalDate.now();
        
        lancamento.setDataLancamento(hoje);
        
        assertNotNull(lancamento.getDataLancamento());
        assertEquals(hoje, lancamento.getDataLancamento());
    }
}