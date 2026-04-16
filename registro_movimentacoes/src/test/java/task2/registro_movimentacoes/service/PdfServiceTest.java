package task2.registro_movimentacoes.service;

import org.junit.jupiter.api.Test;
import task2.registro_movimentacoes.model.Lancamento;
import task2.registro_movimentacoes.model.SituacaoLancamento;
import task2.registro_movimentacoes.model.TipoLancamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PdfServiceTest {

    // Teste 10
    @Test
    public void deveGerarPdfComListaVazia() {
        PdfService pdfService = new PdfService();
        byte[] pdf = pdfService.gerarRelatorioLancamentos(Collections.emptyList());
        
        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    // Teste 11
    @Test
    public void deveGerarPdfComDados() {
        PdfService pdfService = new PdfService();
        Lancamento l = new Lancamento("1", "Teste", LocalDate.now(), BigDecimal.TEN, TipoLancamento.CREDITO, SituacaoLancamento.PAGO);
        
        byte[] pdf = pdfService.gerarRelatorioLancamentos(Arrays.asList(l));
        
        assertNotNull(pdf);
        assertTrue(pdf.length > 100); // O PDF deve ter um tamanho razoável
    }
}