package task2.registro_movimentacoes.changelogs;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id = "002-criar-colecao-categorias", order = "002", author = "barbara")
public class DatabaseChangelog002 
{
    @Execution
    public void execution(MongoTemplate mongoTemplate) 
    {
        if (!mongoTemplate.collectionExists("categorias")) 
        {
            mongoTemplate.createCollection("categorias");
        }

        mongoTemplate.getCollection("categorias")
                .insertOne(new Document("descricao", "Categoria criada pelo Mongock"));
    }

    @RollbackExecution
    public void rollbackExecution(MongoTemplate mongoTemplate) 
    {
        if (mongoTemplate.collectionExists("categorias")) 
        {
            mongoTemplate.dropCollection("categorias");
        }
    }
}