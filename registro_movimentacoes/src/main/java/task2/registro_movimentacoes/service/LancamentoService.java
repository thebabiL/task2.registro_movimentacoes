package task2.registro_movimentacoes.service;

import task2.registro_movimentacoes.model.Lancamento;
import task2.registro_movimentacoes.repository.LancamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        
        String conteudo = "<p style='color: #4b5563;'>Um novo lançamento foi registrado no seu sistema:</p>" +
                "<ul style='list-style: none; padding: 0; color: #1f2937;'>" +
                "<li style='margin-bottom: 8px;'><b>Descrição:</b> " + salvo.getDescricao() + "</li>" +
                "<li style='margin-bottom: 8px;'><b>Valor:</b> R$ " + salvo.getValor() + "</li>" +
                "<li style='margin-bottom: 8px;'><b>Tipo:</b> " + salvo.getTipo() + "</li>" +
                "<li style='margin-bottom: 8px;'><b>Situação:</b> " + salvo.getSituacao() + "</li>" +
                "<li style='margin-bottom: 8px;'><b>Data:</b> " + salvo.getDataLancamento() + "</li>" +
                "</ul>";

        String htmlFinal = gerarTemplateHtml("Novo Lançamento Criado", conteudo);
        emailService.enviarEmailNotificacaoHtml(salvo.getEmailDestino(), "Novo Lançamento Registrado", htmlFinal);
        
        return salvo;
    }

    public Lancamento atualizar(String id, Lancamento lancamentoAtualizado) 
    {
        Optional<Lancamento> lancamentoAntigoOpt = repository.findById(id);
        
        if (lancamentoAntigoOpt.isPresent()) {
            Lancamento antigo = lancamentoAntigoOpt.get();
           
            StringBuilder mudancas = new StringBuilder("<p style='color: #4b5563;'>O lançamento <b>")
                    .append(antigo.getDescricao())
                    .append("</b> sofreu as seguintes alterações:</p>")
                    .append("<ul style='color: #1f2937;'>");

            if (!antigo.getDescricao().equals(lancamentoAtualizado.getDescricao())) 
                mudancas.append("<li><b>Descrição:</b> <del style='color: #ef4444;'>").append(antigo.getDescricao()).append("</del> ➔ <span style='color: #10b981;'>").append(lancamentoAtualizado.getDescricao()).append("</span></li>");
            if (!antigo.getValor().equals(lancamentoAtualizado.getValor())) 
                mudancas.append("<li><b>Valor:</b> <del style='color: #ef4444;'>R$ ").append(antigo.getValor()).append("</del> ➔ <span style='color: #10b981;'>R$ ").append(lancamentoAtualizado.getValor()).append("</span></li>");
            if (!antigo.getTipo().equals(lancamentoAtualizado.getTipo())) 
                mudancas.append("<li><b>Tipo:</b> <del style='color: #ef4444;'>").append(antigo.getTipo()).append("</del> ➔ <span style='color: #10b981;'>").append(lancamentoAtualizado.getTipo()).append("</span></li>");
            if (!antigo.getSituacao().equals(lancamentoAtualizado.getSituacao())) 
                mudancas.append("<li><b>Situação:</b> <del style='color: #ef4444;'>").append(antigo.getSituacao()).append("</del> ➔ <span style='color: #10b981;'>").append(lancamentoAtualizado.getSituacao()).append("</span></li>");
            if (antigo.getDataLancamento() != null && !antigo.getDataLancamento().equals(lancamentoAtualizado.getDataLancamento())) 
                mudancas.append("<li><b>Data:</b> <del style='color: #ef4444;'>").append(antigo.getDataLancamento()).append("</del> ➔ <span style='color: #10b981;'>").append(lancamentoAtualizado.getDataLancamento()).append("</span></li>");

            mudancas.append("</ul>");

            lancamentoAtualizado.setId(id);
            Lancamento salvo = repository.save(lancamentoAtualizado);

            if (mudancas.toString().contains("<li>")) 
            {
                String htmlFinal = gerarTemplateHtml("Lançamento Atualizado", mudancas.toString());
                emailService.enviarEmailNotificacaoHtml(salvo.getEmailDestino(), "Alteração de Lançamento: " + salvo.getDescricao(), htmlFinal);
            }
            return salvo;
        }
        
        throw new RuntimeException("Lançamento não encontrado para edição.");
    }

    public List<Lancamento> listarTodos() 
    {
        return repository.findAll();
    }

    public void deletar(String id) 
    {
        repository.deleteById(id);
    }

    private String gerarTemplateHtml(String titulo, String conteudoDinamico) {
        return "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f3f4f6; padding: 30px;\">" +
               "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.05);\">" +
               "<h2 style=\"color: #4f46e5; margin-top: 0; border-bottom: 2px solid #f3f4f6; padding-bottom: 15px;\">" + titulo + "</h2>" +
               "<div style=\"font-size: 15px; line-height: 1.6;\">" +
               conteudoDinamico +
               "</div>" +
               "<div style=\"margin-top: 30px; padding-top: 20px; border-top: 1px solid #e5e7eb; font-size: 12px; color: #9ca3af; text-align: center;\">" +
               "Este é um e-mail automático do seu Dashboard Financeiro. Por favor, não responda." +
               "</div>" +
               "</div>" +
               "</div>";
    }
}