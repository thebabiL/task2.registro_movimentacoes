package task2.registro_movimentacoes.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "lancamentos")
public class Lancamento 
{

    @Id
    private String id;

    private String descricao;
    private LocalDate dataLancamento;
    private BigDecimal valor;

    private TipoLancamento tipo;
    private SituacaoLancamento situacao;
}