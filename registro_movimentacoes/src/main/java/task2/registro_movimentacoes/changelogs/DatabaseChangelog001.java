package task2.registro_movimentacoes.changelogs;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id = "001-criar-colecoes-iniciais", order = "001", author = "barbara")
public class DatabaseChangelog001 {

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        if (!mongoTemplate.collectionExists("lancamentos")) {
            mongoTemplate.createCollection("lancamentos");
        }

        if (!mongoTemplate.collectionExists("usuarios")) {
            mongoTemplate.createCollection("usuarios");
        }
    }

    @RollbackExecution
    public void rollbackExecution(MongoTemplate mongoTemplate) {
        if (mongoTemplate.collectionExists("lancamentos")) {
            mongoTemplate.dropCollection("lancamentos");
        }

        if (mongoTemplate.collectionExists("usuarios")) {
            mongoTemplate.dropCollection("usuarios");
        }
    }
}
