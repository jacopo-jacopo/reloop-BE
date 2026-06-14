package it.unife.sample.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * Calcola la CO₂ risparmiata da uno scambio interrogando l'API Climatiq.
 * Per ogni categoria di annuncio viene cercato un emission factor "a spesa"
 * (unit_type Money) e poi usato per stimare il co2e corrispondente al
 * valore stimato dell'oggetto scambiato.
 */
@Slf4j
@Service
public class ClimatiqService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${climatiq.api-key:}")
    private String apiKey;

    private static final String SEARCH_URL   = "https://api.climatiq.io/data/v1/search";
    private static final String ESTIMATE_URL = "https://api.climatiq.io/data/v1/estimate";
    private static final String DATA_VERSION = "^26";

    // Termine di ricerca Climatiq (in inglese) per ciascuna categoria di annuncio reloop
    private static final Map<String, String> QUERY_PER_CATEGORIA = Map.of(
            "Arredamento",          "furniture",
            "Abbigliamento",        "clothing",
            "Libri & Cultura",      "books",
            "Sport & Tempo libero", "sports goods",
            "Elettronica",          "electronics",
            "Cucina",               "household appliances",
            "Musica",               "audio equipment"
    );

    /**
     * Stima la CO₂ (in kg) risparmiata riutilizzando un bene di seconda mano
     * della categoria e del valore stimato indicati.
     *
     * @return il valore di co2e stimato, oppure {@link Optional#empty()} se
     *         la chiamata a Climatiq fallisce o non è configurata una API key.
     */
    public Optional<BigDecimal> stimaCo2Risparmiata(String categoria, BigDecimal prezzoStimato) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Climatiq API key non configurata, salto la chiamata.");
            return Optional.empty();
        }

        try {
            String query = QUERY_PER_CATEGORIA.getOrDefault(categoria, "consumer goods");
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);

            // 1. Cerca un emission factor "a spesa" (Money) per la categoria
            String searchUrl = UriComponentsBuilder.fromHttpUrl(SEARCH_URL)
                    .queryParam("query", query)
                    .queryParam("unit_type", "Money")
                    .queryParam("region", "IT")
                    .queryParam("results_per_page", 1)
                    .queryParam("data_version", DATA_VERSION)
                    .toUriString();

            Map<String, Object> searchResponse = restTemplate.exchange(
                    searchUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class).getBody();

            if (searchResponse == null) return Optional.empty();
            var results = (java.util.List<Map<String, Object>>) searchResponse.get("results");
            if (results == null || results.isEmpty()) {
                log.warn("Nessun emission factor Climatiq trovato per la categoria '{}'", categoria);
                return Optional.empty();
            }
            String emissionFactorId = (String) results.get(0).get("id");

            // 2. Stima il co2e corrispondente al valore dell'oggetto
            HttpHeaders estimateHeaders = new HttpHeaders();
            estimateHeaders.setBearerAuth(apiKey);
            estimateHeaders.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Map.of(
                    "emission_factor", Map.of("id", emissionFactorId),
                    "parameters", Map.of(
                            "money", prezzoStimato,
                            "money_unit", "eur"
                    )
            );

            Map<String, Object> estimateResponse = restTemplate.exchange(
                    ESTIMATE_URL, HttpMethod.POST,
                    new HttpEntity<>(body, estimateHeaders), Map.class).getBody();

            if (estimateResponse == null || estimateResponse.get("co2e") == null) {
                return Optional.empty();
            }
            return Optional.of(new BigDecimal(estimateResponse.get("co2e").toString()));

        } catch (Exception e) {
            log.warn("Errore durante la chiamata a Climatiq, uso il calcolo di fallback: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
