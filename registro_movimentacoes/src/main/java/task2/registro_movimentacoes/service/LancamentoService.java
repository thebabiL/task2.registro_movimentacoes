package task2.registro_movimentacoes.service;

import task2.registro_movimentacoes.model.Lancamento;
import task2.registro_movimentacoes.repository.LancamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LancamentoService 
{

    private final LancamentoRepository repository;

    public LancamentoService(LancamentoRepository repository) 
    {
        this.repository = repository;
    }

    public Lancamento salvar(Lancamento lancamento) 
    {
        return repository.save(lancamento);
    }

    public List<Lancamento> listarTodos() 
    {
        return repository.findAll();
    }

    public void deletar(String id) 
    {
        repository.deleteById(id);
    }

    public Lancamento atualizar(String id, Lancamento lancamento)
    {
        lancamento.setId(id);
        return repository.save(lancamento);
    }
}