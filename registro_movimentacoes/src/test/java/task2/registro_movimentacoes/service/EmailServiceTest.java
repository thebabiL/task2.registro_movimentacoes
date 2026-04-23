package task2.registro_movimentacoes.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest 
{

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    // Teste 12 - 
    @Test
    void deveDispararEnvioDeEmail() 
    {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.enviarEmailNotificacaoHtml("teste@teste.com", "Assunto", "<h1>Texto</h1>");
        
        verify(mailSender, times(1)).send(mimeMessage);
    }
    
    // Teste 13 
    void naoDeveQuebrarSeDerErroNoEnvio() 
    {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doThrow(new RuntimeException("Erro SMTP")).when(mailSender).send(any(MimeMessage.class));
        
        emailService.enviarEmailNotificacaoHtml("teste@teste.com", "Assunto", "<h1>Texto</h1>");
        
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}