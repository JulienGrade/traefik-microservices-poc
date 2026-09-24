package fr.juliengrade.poc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import static org.assertj.core.api.Assertions.assertThat;

final class StackSupport {
    static final Path ROOT = Path.of(System.getProperty("poc.root")).toAbsolutePath();
    static final String BASE = System.getProperty("poc.url", "http://localhost:8080");
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();

    private StackSupport() { }

    record Reply(int status, HttpHeaders headers, JsonNode body) { }

    static Reply get(String path) { return request(path, null); }
    static Reply post(String path, String payload) { return request(path, payload); }

    private static Reply request(String path, String payload) {
        try {
            var builder = HttpRequest.newBuilder(URI.create(BASE + path)).timeout(Duration.ofSeconds(5));
            if (payload != null) builder.header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload));
            var response = HTTP.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            JsonNode body;
            try { body = JSON.readTree(response.body()); }
            catch (IOException ex) { body = JSON.getNodeFactory().textNode(response.body()); }
            return new Reply(response.statusCode(), response.headers(), body);
        } catch (IOException ex) {
            throw new IllegalStateException("Appel HTTP impossible : " + path, ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
    }

    static void await(String message, BooleanSupplier condition) {
        long deadline = System.nanoTime() + Duration.ofSeconds(90).toNanos();
        Throwable last = null;
        while (System.nanoTime() < deadline) {
            try { if (condition.getAsBoolean()) return; }
            catch (RuntimeException | AssertionError ex) { last = ex; }
            pause(500);
        }
        throw new AssertionError(message, last);
    }

    static void pause(long milliseconds) {
        try { Thread.sleep(milliseconds); }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
    }

    static String docker(String... args) {
        Path output = null;
        Process process = null;
        try {
            output = Files.createTempFile("poc-docker-", ".log");
            var command = new ArrayList<String>();
            command.add("docker"); command.addAll(List.of(args));
            process = new ProcessBuilder(command).directory(ROOT.toFile()).redirectErrorStream(true)
                    .redirectOutput(output.toFile()).start();
            if (!process.waitFor(5, TimeUnit.MINUTES)) {
                throw new AssertionError("Délai Docker dépassé : " + command);
            }
            String text = Files.readString(output).strip();
            assertThat(process.exitValue()).as("%s%n%s", command, text).isZero();
            return text;
        } catch (IOException ex) {
            throw new IllegalStateException("Docker doit être installé et démarré", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        } finally {
            if (process != null && process.isAlive()) process.destroyForcibly();
            if (output != null) {
                try { Files.deleteIfExists(output); } catch (IOException ignored) { }
            }
        }
    }

    static String compose(String... args) {
        var command = new ArrayList<String>(); command.add("compose"); command.addAll(List.of(args));
        return docker(command.toArray(String[]::new));
    }

    static String gateway() {
        String id = compose("ps", "-q", "traefik");
        assertThat(id).as("Traefik doit être démarré").isNotBlank();
        return id;
    }

    static void scale(int replicas) {
        compose("up", "-d", "--wait", "--wait-timeout", "180", "--scale", "product-service=" + replicas, "product-service");
        await("Catalogue disponible", () -> get("/api/products/instance").status() == 200);
    }
}
