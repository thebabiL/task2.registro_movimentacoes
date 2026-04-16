package task2.registro_movimentacoes.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    // Teste 12
    @Test
    public void deveDispararEnvioDeEmail() {
        emailService.enviarEmailNotificacao("Assunto", "Texto");
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
    
    // Teste 13
    @Test
    public void naoDeveQuebrarSeDerErroNoEnvio() {
        // Simulando que o envio dá erro
        org.mockito.Mockito.doThrow(new RuntimeException("Erro SMTP")).when(mailSender).send(any(SimpleMailMessage.class));
        
        // O método no EmailService tem um try-catch, então não deve jogar a exception para cima
        emailService.enviarEmailNotificacao("Assunto", "Texto");
        
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}