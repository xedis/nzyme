/*
 * This file is part of nzyme.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program. If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */

package app.nzyme.core.configuration.node;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import app.nzyme.core.configuration.*;
import jakarta.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Optional;

public class NodeConfigurationLoader {

    private static final Logger LOG = LogManager.getLogger(NodeConfigurationLoader.class);

    private final Config root;
    private final Config general;
    private final Config interfaces;
    private final Config performance;

    @Nullable
    private final Config misc;

    public NodeConfigurationLoader(File configFile, boolean skipValidation) throws InvalidConfigurationException, IncompleteConfigurationException, FileNotFoundException {
        if (!Files.isReadable(configFile.toPath())) {
            throw new FileNotFoundException("File at [" + configFile.getPath() + "] does not exist or is not readable. Check path and permissions.");
        }

        this.root = ConfigFactory.parseFile(configFile).resolve();

        try {
            this.general = root.getConfig(ConfigurationKeys.GENERAL);
            this.interfaces = root.getConfig(ConfigurationKeys.INTERFACES);
            this.performance = root.getConfig(ConfigurationKeys.PERFORMANCE);

            this.misc = root.hasPath(ConfigurationKeys.MISC) ? root.getConfig(ConfigurationKeys.MISC) : null;
        } catch(ConfigException e) {
            throw new IncompleteConfigurationException("Incomplete configuration.", e);
        }

        if (!skipValidation) {
            validate();
        }
    }

    public NodeConfiguration get() {
        return NodeConfiguration.create(
                parseVersionchecksEnabled(),
                parseDatabasePath(),
                parseRestListenUri(),
                parseHttpExternalUri(),
                parsePluginDirectory(),
                parseCryptoDirectory(),
                parseSlowQueryLogThreshold(),
                parseNtpServer(),
                parseConnectUri(),
                parseConnectSkip(),
                parsePerformance(),
                parseMisc()
        );
    }

    private Optional<Integer> parseSlowQueryLogThreshold() {
        try {
            return Optional.of(general.getInt(ConfigurationKeys.SLOW_QUERY_LOG_THRESHOLD));
        } catch(ConfigException.Missing ignored) {
            return Optional.empty();
        }
    }

    private String parseDatabasePath() {
        return general.getString(ConfigurationKeys.DATABASE_PATH);
    }

    private boolean parseVersionchecksEnabled() {
        return general.getBoolean(ConfigurationKeys.VERSIONCHECKS);
    }

    private String parsePluginDirectory() {
        return general.getString(ConfigurationKeys.PLUGIN_DIRECTORY);
    }

    private String parseCryptoDirectory() {
        return general.getString(ConfigurationKeys.CRYPTO_DIRECTORY);
    }

    @Nullable
    private String parseConnectUri() {
        if (general.hasPath(ConfigurationKeys.CONNECT_SERVER)) {
            return general.getString(ConfigurationKeys.CONNECT_SERVER);
        } else {
            return null;
        }
    }

    private boolean parseConnectSkip() {
        if (general.hasPath(ConfigurationKeys.CONNECT_SKIP)) {
            return general.getBoolean(ConfigurationKeys.CONNECT_SKIP);
        } else {
            return false;
        }
    }

    private String parseNtpServer() {
        return general.getString(ConfigurationKeys.NTP_SERVER);
    }

    private URI parseRestListenUri() {
        return URI.create(interfaces.getString(ConfigurationKeys.REST_LISTEN_URI));
    }

    private URI parseHttpExternalUri() {
        return URI.create(interfaces.getString(ConfigurationKeys.HTTP_EXTERNAL_URI));
    }

    private PerformanceConfiguration parsePerformance() {
        return PerformanceConfiguration.create(performance.getInt(ConfigurationKeys.REPORT_PROCESSOR_POOL_SIZE));
    }

    private MiscConfiguration parseMisc() {
        if (misc == null) {
            return MiscConfiguration.create(null, null);
        }

        String customTitle = misc.hasPath(ConfigurationKeys.CUSTOM_TITLE) ?
                misc.getString(ConfigurationKeys.CUSTOM_TITLE) : null;

        String customFaviconUrl = misc.hasPath(ConfigurationKeys.CUSTOM_FAVICON_URL) ?
                misc.getString(ConfigurationKeys.CUSTOM_FAVICON_URL) : null;

        return MiscConfiguration.create(customTitle, customFaviconUrl);
    }

    private void validate() throws IncompleteConfigurationException, InvalidConfigurationException {
        // Completeness and type validity.
        ConfigurationValidator.expect(general, ConfigurationKeys.DATABASE_PATH, ConfigurationKeys.GENERAL, String.class);
        ConfigurationValidator.expect(general, ConfigurationKeys.VERSIONCHECKS, ConfigurationKeys.GENERAL, Boolean.class);
        ConfigurationValidator.expect(general, ConfigurationKeys.PLUGIN_DIRECTORY, ConfigurationKeys.GENERAL, String.class);
        ConfigurationValidator.expect(general, ConfigurationKeys.CRYPTO_DIRECTORY, ConfigurationKeys.GENERAL, String.class);
        ConfigurationValidator.expect(general, ConfigurationKeys.NTP_SERVER, ConfigurationKeys.GENERAL, String.class);
        ConfigurationValidator.expect(performance, ConfigurationKeys.REPORT_PROCESSOR_POOL_SIZE, ConfigurationKeys.PERFORMANCE, Integer.class);

        // Plugin directory exists and is readable?
        File pluginDirectory = new File(parsePluginDirectory());
        if (!pluginDirectory.exists()) {
            throw new InvalidConfigurationException("Plugin directory [" + parsePluginDirectory() + "] does not exist.");
        }

        if (!pluginDirectory.isDirectory()) {
            throw new InvalidConfigurationException("Plugin directory [" + parsePluginDirectory() + "] is not a directory.");
        }

        if (!pluginDirectory.canRead()) {
            throw new InvalidConfigurationException("Plugin directory [" + parsePluginDirectory() + "] is not readable.");
        }

        // Crypto directory exists and is readable?
        File cryptoKeyDirectory = new File(parseCryptoDirectory());
        if (!cryptoKeyDirectory.exists()) {
            throw new InvalidConfigurationException("Crypto directory [" + parseCryptoDirectory() + "] does not exist.");
        }

        if (!cryptoKeyDirectory.isDirectory()) {
            throw new InvalidConfigurationException("Crypto directory [" + parseCryptoDirectory() + "] is not a directory.");
        }

        if (!cryptoKeyDirectory.canRead()) {
            throw new InvalidConfigurationException("Crypto directory [" + parseCryptoDirectory() + "] is not readable.");
        }

        if (!cryptoKeyDirectory.canWrite()) {
            throw new InvalidConfigurationException("Crypto directory [" + parseCryptoDirectory() + "] is not writable.");
        }

        // REST listen URI can be parsed into a URI and is TLS.
        try {
            URI uri = parseRestListenUri();
            if (!uri.getScheme().equals("https")) {
                throw new IncompleteConfigurationException("Parameter [interfaces." + ConfigurationKeys.REST_LISTEN_URI + "] must be using HTTPS/TLS.");
            }
        } catch(IllegalArgumentException e) {
            LOG.error(e);
            throw new InvalidConfigurationException("Parameter [interfaces." + ConfigurationKeys.REST_LISTEN_URI + "] cannot be parsed into a URI. Make sure it is correct.");
        }

        // HTTP external URI can be parsed into a URI and is TLS.
        try {
            URI uri = parseHttpExternalUri();
            if (!uri.getScheme().equals("https")) {
                throw new IncompleteConfigurationException("Parameter [interfaces." + ConfigurationKeys.HTTP_EXTERNAL_URI + "] must be using HTTPS/TLS.");
            }
        } catch(IllegalArgumentException e) {
            LOG.error(e);
            throw new InvalidConfigurationException("Parameter [interfaces." + ConfigurationKeys.HTTP_EXTERNAL_URI + "] cannot be parsed into a URI. Make sure it is correct.");
        }
    }

}
