package task2.registro_movimentacoes.repository;

import task2.registro_movimentacoes.model.Lancamento;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LancamentoRepository extends MongoRepository<Lancamento, String> 
{
  
}