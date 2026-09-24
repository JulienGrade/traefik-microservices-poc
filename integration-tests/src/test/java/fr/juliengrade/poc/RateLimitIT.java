package fr.juliengrade.poc;

import java.util.ArrayList;
import java.util.concurrent.*;
import org.junit.jupiter.api.Test;
import static fr.juliengrade.poc.StackSupport.*;
import static org.assertj.core.api.Assertions.assertThat;

class RateLimitIT {
    @Test void rejectsExcessTrafficAndRefillsTheBucket() throws Exception {
        await("Catalogue prêt", () -> get("/api/products").status() == 200);
        pause(3000);
        ExecutorService pool = Executors.newFixedThreadPool(32);
        var tasks = new ArrayList<Future<Integer>>();
        try {
            for (int i = 0; i < 160; i++) tasks.add(pool.submit(() -> get("/api/products").status()));
            var statuses = new ArrayList<Integer>();
            for (var task : tasks) statuses.add(task.get(30, TimeUnit.SECONDS));
            assertThat(statuses).contains(429).allMatch(code -> code == 200 || code == 429);
        } finally {
            pool.shutdownNow();
            pause(3000);
        }
        assertThat(get("/api/products").status()).isEqualTo(200);
    }
}
