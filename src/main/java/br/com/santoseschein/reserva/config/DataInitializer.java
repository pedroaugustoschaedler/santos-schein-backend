package br.com.santoseschein.reserva.config;

import br.com.santoseschein.reserva.model.Court;
import br.com.santoseschein.reserva.model.User;
import br.com.santoseschein.reserva.repository.CourtRepository;
import br.com.santoseschein.reserva.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CourtRepository courtRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Seed default Admin User if not exists
        if (!userRepository.existsByEmail("admin@santos.com")) {
            User admin = new User(
                "Administrador Santos",
                "admin@santos.com",
                "123456", // Em produção usaria hashing de senha
                "ADMIN"
            );
            userRepository.save(admin);
        }

        // Seed 4 Courts if empty
        if (courtRepository.count() == 0) {
            courtRepository.save(new Court("Quadra 1", "Tênis", true));
            courtRepository.save(new Court("Quadra 2", "Beach Tennis", true));
            courtRepository.save(new Court("Quadra 3", "Futsal", true));
            courtRepository.save(new Court("Quadra 4", "Vôlei de Areia", true));
        }
    }
}
