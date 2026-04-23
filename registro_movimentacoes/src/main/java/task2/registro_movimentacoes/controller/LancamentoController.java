package task2.registro_movimentacoes.controller;

import task2.registro_movimentacoes.model.Lancamento;
import task2.registro_movimentacoes.service.LancamentoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import task2.registro_movimentacoes.service.PdfService;

import java.util.List;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoController 
{

    private final LancamentoService service;
    private final PdfService pdfService;

    public LancamentoController(LancamentoService service, PdfService pdfService) 
    {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public Lancamento salvar(@RequestBody Lancamento lancamento) 
    {
        return service.salvar(lancamento);
    }

    @GetMapping
    public List<Lancamento> listar(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String situacao,
            @RequestParam(required = false) String tipo) 
    { 

        List<Lancamento> lancamentos = service.listarTodos();

        if (dataInicio != null && !dataInicio.isEmpty() && dataFim != null && !dataFim.isEmpty()) 
        {
            java.time.LocalDate inicio = java.time.LocalDate.parse(dataInicio);
            java.time.LocalDate fim = java.time.LocalDate.parse(dataFim);

            lancamentos = lancamentos.stream()
                    .filter(l -> l.getDataLancamento() != null && !l.getDataLancamento().isBefore(inicio) && !l.getDataLancamento().isAfter(fim))
                    .toList();
        }

        if (situacao != null && !situacao.isEmpty()) 
        {
            lancamentos = lancamentos.stream()
                    .filter(l -> l.getSituacao().name().equals(situacao))
                    .toList();
        }

        if (tipo != null && !tipo.isEmpty()) 
        {
            lancamentos = lancamentos.stream()
                    .filter(l -> l.getTipo().name().equals(tipo))
                    .toList();
        }

        return lancamentos;
    }

    @PutMapping("/{id}")
    public Lancamento atualizar(@PathVariable String id, @RequestBody Lancamento lancamento) 
    {
        return service.atualizar(id, lancamento);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable String id) 
    {
        service.deletar(id);
    }

    @GetMapping("/exportar-pdf")
    public ResponseEntity<byte[]> exportarPDF(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String situacao,
            @RequestParam(required = false) String tipo) 
        { 

        List<Lancamento> lancamentos = service.listarTodos();

        if (dataInicio != null && !dataInicio.isEmpty() && dataFim != null && !dataFim.isEmpty()) 
        {
            java.time.LocalDate inicio = java.time.LocalDate.parse(dataInicio);
            java.time.LocalDate fim = java.time.LocalDate.parse(dataFim);

            lancamentos = lancamentos.stream()
                    .filter(l -> l.getDataLancamento() != null && !l.getDataLancamento().isBefore(inicio) && !l.getDataLancamento().isAfter(fim))
                    .toList();
        }

        if (situacao != null && !situacao.isEmpty()) 
        {
            lancamentos = lancamentos.stream()
                    .filter(l -> l.getSituacao().name().equals(situacao))
                    .toList();
        }

        if (tipo != null && !tipo.isEmpty()) 
        {
            lancamentos = lancamentos.stream()
                    .filter(l -> l.getTipo().name().equals(tipo))
                    .toList();
        }

        byte[] pdf = pdfService.gerarRelatorioLancamentos(lancamentos);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_financeiro.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}