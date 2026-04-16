package task2.registro_movimentacoes.service;

import task2.registro_movimentacoes.model.Lancamento;
import task2.registro_movimentacoes.repository.LancamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LancamentoService 
{

    private final LancamentoRepository repository;
    private final EmailService emailService;

    public LancamentoService(LancamentoRepository repository, EmailService emailService) 
    {
        this.repository = repository;
        this.emailService = emailService;
    }

    public Lancamento salvar(Lancamento lancamento) 
    {
        Lancamento salvo = repository.save(lancamento);
        emailService.enviarEmailNotificacao("Novo Lançamento Criado", 
                "Um novo lançamento de " + lancamento.getTipo() + " no valor de R$ " + lancamento.getValor() + " foi registrado.");
        return salvo;
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
        Lancamento atualizado = repository.save(lancamento);
        emailService.enviarEmailNotificacao("Lançamento Atualizado", 
                "O lançamento '" + lancamento.getDescricao() + "' foi atualizado com sucesso.");
        return atualizado;
    }
}