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
            @RequestParam(required = false) String data,
            @RequestParam(required = false) String situacao) {
        
        // Se mandou os dois filtros
        if (data != null && !data.isEmpty() && situacao != null && !situacao.isEmpty()) {
            return service.listarTodos().stream()
                    .filter(l -> l.getDataLancamento().toString().equals(data) && l.getSituacao().name().equals(situacao))
                    .toList();
        }
        // Se mandou só a data
        else if (data != null && !data.isEmpty()) {
            return service.listarTodos().stream()
                    .filter(l -> l.getDataLancamento().toString().equals(data))
                    .toList();
        }
        // Se mandou só a situação
        else if (situacao != null && !situacao.isEmpty()) {
            return service.listarTodos().stream()
                    .filter(l -> l.getSituacao().name().equals(situacao))
                    .toList();
        }
        
        // Se não mandou nada, lista tudo
        return service.listarTodos();
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
    public ResponseEntity<byte[]> exportarPDF() {
        List<Lancamento> lancamentos = service.listarTodos();
        byte[] pdf = pdfService.gerarRelatorioLancamentos(lancamentos);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_financeiro.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}