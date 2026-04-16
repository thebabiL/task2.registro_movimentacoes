package task2.registro_movimentacoes.repository;

import task2.registro_movimentacoes.model.Lancamento;
import task2.registro_movimentacoes.model.SituacaoLancamento;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.List;

public interface LancamentoRepository extends MongoRepository<Lancamento, String> 
{
    List<Lancamento> findByDataLancamento(LocalDate dataLancamento);
    List<Lancamento> findBySituacao(SituacaoLancamento situacao);
    List<Lancamento> findByDataLancamentoAndSituacao(LocalDate dataLancamento, SituacaoLancamento situacao);
}