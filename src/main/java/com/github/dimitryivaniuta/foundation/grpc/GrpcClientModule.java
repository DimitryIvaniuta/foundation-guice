package com.github.dimitryivaniuta.foundation.grpc;

import com.github.dimitryivaniuta.foundation.config.Config;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Dagger module responsible for providing gRPC-based clients.
 * <p>
 * This module configures and constructs external gRPC clients required
 * by the application, such as the Google Cloud Vision API client.
 * Configuration values are sourced from the provided {@link Config}.
 * </p>
 *
 * @see ImageAnnotatorClient
 * @see GoogleCredentials
 */
@Module
public class GrpcClientModule {

    /**
     * Provides a singleton {@link ImageAnnotatorClient} for interacting with
     * Google Cloud's Vision API.
     * <p>
     * The client is configured with service account credentials loaded
     * from the JSON key file path specified in {@link Config#getGoogleCredentialsPath()}.
     * Credentials are scoped to the Cloud Platform to allow full Vision API access.
     * </p>
     *
     * @param config the application configuration containing credential path
     * @return a fully configured, singleton ImageAnnotatorClient
     * @throws IllegalStateException if the credentials file cannot be read or
     *                               client initialization fails
     */
    @Provides
    @Singleton
    public ImageAnnotatorClient provideImageAnnotatorClient(final Config config) {
        try (FileInputStream fis = new FileInputStream(config.getGoogleCredentialsPath())) {
            // Load service account credentials from JSON key file
            GoogleCredentials credentials = GoogleCredentials.fromStream(fis)
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

            // Build Vision API settings with credentials provider
            ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();

            // Create and return the Vision API client
            return ImageAnnotatorClient.create(settings);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to create ImageAnnotatorClient with credentials at '"
                            + config.getGoogleCredentialsPath() + "'", e
            );
        }
    }
}
