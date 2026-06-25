package task2.registro_movimentacoes.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService 
{

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) 
    {
        this.mailSender = mailSender;
    }

    public void enviarEmailNotificacaoHtml(String destinatario, String assunto, String htmlTexto) 
    {
        if (destinatario == null || destinatario.trim().isEmpty()) 
        {
            System.out.println("Nenhum e-mail de destino informado. Notificação ignorada.");
            return;
        }

        try 
        {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(htmlTexto, true); 
            
            mailSender.send(message);
            System.out.println("E-mail HTML enviado com sucesso para: " + destinatario);
        } 
        catch (Exception e) 
        {
            System.err.println("Erro ao enviar e-mail HTML: " + e.getMessage());
        }
    }
}