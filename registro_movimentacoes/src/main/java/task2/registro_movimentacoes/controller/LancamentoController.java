package task2.registro_movimentacoes.controller;

import task2.registro_movimentacoes.model.Lancamento;
import task2.registro_movimentacoes.service.LancamentoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoController 
{

    private final LancamentoService service;

    public LancamentoController(LancamentoService service) 
    {
        this.service = service;
    }

    @PostMapping
    public Lancamento salvar(@RequestBody Lancamento lancamento) 
    {
        return service.salvar(lancamento);
    }

    @GetMapping
    public List<Lancamento> listar() 
    {
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
}