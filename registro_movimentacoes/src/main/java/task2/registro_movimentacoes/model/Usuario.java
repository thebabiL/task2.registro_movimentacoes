package task2.registro_movimentacoes.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "usuarios")
public class Usuario 
{

    @Id
    private String id;

    private String nome;
    private String login;
    private String senha;

    private SituacaoUsuario situacao;
}
