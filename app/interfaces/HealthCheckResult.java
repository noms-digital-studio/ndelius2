package interfaces;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HealthCheckResult {
    private final boolean healthy;
    private final Object detail;

    public HealthCheckResult(boolean healthy) {
        this.healthy = healthy;
        this.detail = "none";
    }

    public static HealthCheckResult healthy() {
        return new HealthCheckResult(true);
    }
    public static HealthCheckResult healthy(Object details) {
        return new HealthCheckResult(true, details);
    }
    public static HealthCheckResult unhealthy() {
        return new HealthCheckResult(false);
    }
    public static HealthCheckResult unhealthy(Object details) {
        return new HealthCheckResult(false, details);
    }
}
