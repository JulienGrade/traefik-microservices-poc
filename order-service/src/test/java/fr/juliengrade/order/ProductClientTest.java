package fr.juliengrade.order;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import static org.assertj.core.api.Assertions.*;

class ProductClientTest {
    private HttpServer server;
    private ProductClient client;
    private int status;
    private String body;

    @BeforeEach void start() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/products/1", exchange -> {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        client = new ProductClient(RestClient.builder(), "http://127.0.0.1:" + server.getAddress().getPort());
    }

    @AfterEach void stop() { server.stop(0); }
    @Test void readsRemoteProduct() {
        status = 200; body = "{\"id\":1,\"name\":\"Clavier\",\"price\":89.90}";
        assertThat(client.find(1).price()).isEqualByComparingTo("89.90");
    }
    @Test void translatesNotFound() {
        status = 404; body = "{}";
        assertThatThrownBy(() -> client.find(1)).isInstanceOfSatisfying(ResponseStatusException.class,
                e -> assertThat(e.getStatusCode().value()).isEqualTo(404));
    }
    @Test void translatesServerFailure() {
        status = 500; body = "{}";
        assertThatThrownBy(() -> client.find(1)).isInstanceOfSatisfying(ResponseStatusException.class,
                e -> assertThat(e.getStatusCode().value()).isEqualTo(503));
    }
    @Test void translatesConnectionFailure() {
        server.stop(0);
        assertThatThrownBy(() -> client.find(1)).isInstanceOfSatisfying(ResponseStatusException.class,
                e -> assertThat(e.getStatusCode().value()).isEqualTo(503));
    }
}
