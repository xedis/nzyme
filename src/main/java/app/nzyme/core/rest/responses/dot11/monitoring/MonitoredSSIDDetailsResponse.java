package app.nzyme.core.rest.responses.dot11.monitoring;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@AutoValue
public abstract class MonitoredSSIDDetailsResponse {

    @JsonProperty("uuid")
    public abstract UUID uuid();

    @JsonProperty("is_enabled")
    public abstract boolean isEnabled();

    @JsonProperty("ssid")
    public abstract String ssid();

    @JsonProperty("organization_id")
    @Nullable
    public abstract UUID organizationId();

    @JsonProperty("tenant_id")
    @Nullable
    public abstract UUID tenantId();

    @JsonProperty("bssids")
    @Nullable
    public abstract List<MonitoredBSSIDDetailsResponse> bssids();

    @JsonProperty("channels")
    @Nullable
    public abstract List<MonitoredChannelResponse> channels();

    @JsonProperty("security_suites")
    @Nullable
    public abstract List<MonitoredSecuritySuiteResponse> securitySuites();

    @JsonProperty("created_at")
    public abstract DateTime createdAt();

    @JsonProperty("updated_at")
    public abstract DateTime updatedAt();

    @JsonProperty("is_alerted")
    public abstract boolean isAlerted();

    @JsonProperty("status_unexpected_bssid")
    @Nullable
    public abstract MonitoredAttributeResult statusUnexpectedBSSID();

    @JsonProperty("status_unexpected_channel")
    @Nullable
    public abstract MonitoredAttributeResult statusUnexpectedChannel();

    @JsonProperty("status_unexpected_security")
    @Nullable
    public abstract MonitoredAttributeResult statusUnexpectedSecurity();

    @JsonProperty("status_unexpected_fingerprint")
    @Nullable
    public abstract MonitoredAttributeResult statusUnexpectedFingerprint();

    @JsonProperty("status_unexpected_signal_tracks")
    @Nullable
    public abstract MonitoredAttributeResult statusUnexpectedSignalTracks();

    public static MonitoredSSIDDetailsResponse create(UUID uuid, boolean isEnabled, String ssid, UUID organizationId, UUID tenantId, List<MonitoredBSSIDDetailsResponse> bssids, List<MonitoredChannelResponse> channels, List<MonitoredSecuritySuiteResponse> securitySuites, DateTime createdAt, DateTime updatedAt, boolean isAlerted, MonitoredAttributeResult statusUnexpectedBSSID, MonitoredAttributeResult statusUnexpectedChannel, MonitoredAttributeResult statusUnexpectedSecurity, MonitoredAttributeResult statusUnexpectedFingerprint, MonitoredAttributeResult statusUnexpectedSignalTracks) {
        return builder()
                .uuid(uuid)
                .isEnabled(isEnabled)
                .ssid(ssid)
                .organizationId(organizationId)
                .tenantId(tenantId)
                .bssids(bssids)
                .channels(channels)
                .securitySuites(securitySuites)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .isAlerted(isAlerted)
                .statusUnexpectedBSSID(statusUnexpectedBSSID)
                .statusUnexpectedChannel(statusUnexpectedChannel)
                .statusUnexpectedSecurity(statusUnexpectedSecurity)
                .statusUnexpectedFingerprint(statusUnexpectedFingerprint)
                .statusUnexpectedSignalTracks(statusUnexpectedSignalTracks)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_MonitoredSSIDDetailsResponse.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder uuid(UUID uuid);

        public abstract Builder isEnabled(boolean isEnabled);

        public abstract Builder ssid(String ssid);

        public abstract Builder organizationId(UUID organizationId);

        public abstract Builder tenantId(UUID tenantId);

        public abstract Builder bssids(List<MonitoredBSSIDDetailsResponse> bssids);

        public abstract Builder channels(List<MonitoredChannelResponse> channels);

        public abstract Builder securitySuites(List<MonitoredSecuritySuiteResponse> securitySuites);

        public abstract Builder createdAt(DateTime createdAt);

        public abstract Builder updatedAt(DateTime updatedAt);

        public abstract Builder isAlerted(boolean isAlerted);

        public abstract Builder statusUnexpectedBSSID(MonitoredAttributeResult statusUnexpectedBSSID);

        public abstract Builder statusUnexpectedChannel(MonitoredAttributeResult statusUnexpectedChannel);

        public abstract Builder statusUnexpectedSecurity(MonitoredAttributeResult statusUnexpectedSecurity);

        public abstract Builder statusUnexpectedFingerprint(MonitoredAttributeResult statusUnexpectedFingerprint);

        public abstract Builder statusUnexpectedSignalTracks(MonitoredAttributeResult statusUnexpectedSignalTracks);

        public abstract MonitoredSSIDDetailsResponse build();
    }
}
