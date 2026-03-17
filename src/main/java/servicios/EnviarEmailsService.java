package servicios;

import interfaces.InterfazEnviarEmails;
import modelo.Destinatario;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class EnviarEmailsService implements InterfazEnviarEmails {
    private final Logger logger;

    public EnviarEmailsService(Logger logger) {
        this.logger = logger;
    }
    @Override
    public boolean enviarEmail(Destinatario dest, String email) {
        this.logger.info("Enviar Email: " + email);
        return true;
    }
}
