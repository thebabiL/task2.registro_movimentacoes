package task2.registro_movimentacoes.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import task2.registro_movimentacoes.model.Lancamento;
import task2.registro_movimentacoes.service.LancamentoService;
import task2.registro_movimentacoes.service.PdfService;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest(LancamentoController.class)
class LancamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LancamentoService service;

    @MockBean
    private PdfService pdfService;

    // Teste 17
    @Test
    void deveListarLancamentos() throws Exception 
    {
        when(service.listarTodos()).thenReturn(Arrays.asList(new Lancamento(), new Lancamento()));

        mockMvc.perform(get("/lancamentos"))
                .andExpect(status().isOk());
    }

    // Teste 18
    @Test
    void deveDeletarLancamentoERetornarOk() throws Exception 
    {
        mockMvc.perform(delete("/lancamentos/123"))
                .andExpect(status().isOk());
    }

    // Teste 19
    @Test
    void deveExportarPdf() throws Exception 
    {
        when(service.listarTodos()).thenReturn(Arrays.asList(new Lancamento()));
        when(pdfService.gerarRelatorioLancamentos(org.mockito.ArgumentMatchers.anyList()))
                .thenReturn(new byte[]{1, 2, 3});

        mockMvc.perform(get("/lancamentos/exportar-pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=relatorio_financeiro.pdf"));
    }

    // Teste 20 - ATUALIZADO COM O FILTRO 'TIPO'
    @Test
    void deveAceitarParametrosDeFiltroNaListagem() throws Exception 
    {
        mockMvc.perform(get("/lancamentos?dataInicio=2026-05-10&situacao=PAGO&tipo=RECEITA"))
                .andExpect(status().isOk());
    }
}