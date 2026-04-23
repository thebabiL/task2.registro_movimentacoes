package task2.registro_movimentacoes.config;

import task2.registro_movimentacoes.model.Lancamento;
import task2.registro_movimentacoes.model.SituacaoLancamento;
import task2.registro_movimentacoes.model.SituacaoUsuario;
import task2.registro_movimentacoes.model.TipoLancamento;
import task2.registro_movimentacoes.model.Usuario;
import task2.registro_movimentacoes.repository.LancamentoRepository;
import task2.registro_movimentacoes.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataLoader 
{

    @Bean
    CommandLineRunner init(LancamentoRepository lancamentoRepo, UsuarioRepository usuarioRepo) 
    {
        return args ->
        {

            if (usuarioRepo.count() == 0) 
            {
                usuarioRepo.save(Usuario.builder()
                        .nome("Administrador")
                        .login("admin")
                        .senha("admin")
                        .situacao(SituacaoUsuario.ATIVO)
                        .build());
            }

            if (lancamentoRepo.count() == 0) 
            {
                for (int i = 1; i <= 10; i++) 
                {
                    lancamentoRepo.save(Lancamento.builder()
                            .descricao("Lançamento " + i)
                            .dataLancamento(LocalDate.now())
                            .valor(BigDecimal.valueOf(100 + i))
                            .tipo(i % 2 == 0 ? TipoLancamento.RECEITA : TipoLancamento.DESPESA)
                            .situacao(SituacaoLancamento.PENDENTE)
                            .build());
                }
            }
        };
    }
}