package com.foundation.grpc;

import com.foundation.config.Config;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Provider for a thread-safe, lazily initialized singleton
 * {@link ImageAnnotatorClient} instance configured with
 * Google Cloud Vision credentials.
 * <p>
 * Uses double-checked locking with an {@link AtomicReference}
 * to ensure the client is only created once in a thread-safe manner.
 * </p>
 */
@Singleton
public class VisionClientProvider implements Provider<ImageAnnotatorClient> {

    /**
     * Configuration object containing the path to the service account key.
     */
    private final Config config;

    /**
     * Holds the singleton ImageAnnotatorClient once initialized.
     */
    private final AtomicReference<ImageAnnotatorClient> clientRef = new AtomicReference<>();

    /**
     * Constructs the provider with injected configuration.
     *
     * @param config application configuration
     */
    @Inject
    public VisionClientProvider(final Config config) {
        this.config = config;
    }

    /**
     * Returns the singleton {@link ImageAnnotatorClient}, creating and configuring it
     * on first access in a thread-safe manner.
     *
     * @return initialized ImageAnnotatorClient
     */
    @Override
    public ImageAnnotatorClient get() {
        ImageAnnotatorClient client = clientRef.get();
        if (client == null) {
            synchronized (clientRef) {
                client = clientRef.get();
                if (client == null) {
                    client = createClient();
                    clientRef.set(client);
                }
            }
        }
        return client;
    }

    /**
     * Creates a new {@link ImageAnnotatorClient} using the service account
     * key specified in the configuration.
     *
     * @return newly created ImageAnnotatorClient
     * @throws IllegalStateException if credentials cannot be loaded or client creation fails
     */
    private ImageAnnotatorClient createClient() {
        try (FileInputStream fis = new FileInputStream(config.getGoogleCredentialsPath())) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(fis)
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

            ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();

            ImageAnnotatorClient client = ImageAnnotatorClient.create(settings);
            // Add shutdown hook to close the client on JVM exit
            Runtime.getRuntime().addShutdownHook(new Thread(client::close));
            return client;
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to create ImageAnnotatorClient with credentials at '"
                            + config.getGoogleCredentialsPath() + "'", e
            );
        }
    }
}
