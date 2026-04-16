package task2.registro_movimentacoes.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmailNotificacao(String assunto, String texto) {
        SimpleMailMessage message = new SimpleMailMessage();
        // Coloque o e-mail do "cliente" ou administrador que vai receber os avisos
        message.setTo("barbara.leidemer@univates.br"); 
        message.setSubject(assunto);
        message.setText(texto);
        
        try {
            mailSender.send(message);
            System.out.println("E-mail enviado com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail: " + e.getMessage());
        }
    }
}