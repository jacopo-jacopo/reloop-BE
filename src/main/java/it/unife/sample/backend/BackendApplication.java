package it.unife.sample.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// classe principale dell'applicazione Spring Boot: avvia l'applicazione e configura il contesto Spring

@SpringBootApplication // indica che questa classe è la classe principale dell'applicazione Spring Boot, 
                       // abilitando la configurazione automatica e la scansione dei componenti
public class BackendApplication {
    public static void main(String[] args) {
        // avvia l'applicazione Spring Boot, creando il contesto dell'applicazione e avviando il server web incorporato
        SpringApplication.run(BackendApplication.class, args); 
    }
}