package com.github.dimitryivaniuta.foundation.grpc;

import com.github.dimitryivaniuta.foundation.config.ConfigModule;
import com.github.dimitryivaniuta.foundation.grpc.GrpcClientModule;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import dagger.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.inject.Singleton;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link VisionClientProvider}, verifying that it
 * correctly provides a singleton {@link ImageAnnotatorClient}
 * and propagates errors appropriately.
 */
class VisionClientProviderTest {

    /**
     * Dagger test component that installs the Config and gRPC client modules,
     * including {@link VisionClientProvider}.
     */
    @Singleton
    @Component(modules = {
            ConfigModule.class,
            GrpcClientModule.class
    })
    interface TestComponent {
        /**
         * Exposes the provided ImageAnnotatorClient from the graph.
         *
         * @return the ImageAnnotatorClient instance
         */
        ImageAnnotatorClient visionClient();
    }

    private TestComponent component;

    /**
     * Builds the Dagger test component before each test.
     */
    @BeforeEach
    void setUp() {
        // Provide required environment settings via system properties for tests
        System.setProperty("APPLICATION_ID", "test-app");
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
        System.setProperty("SCHEMA_REGISTRY_URL", "http://localhost:8081");
        System.setProperty("INPUT_TOPIC", "test-input-topic");
        System.setProperty("OUTPUT_TOPIC", "test-output-topic");
        System.setProperty("GV_THRESHOLD", "5");
        System.setProperty("RUN_GV_IN_PARALLEL", "false");
        System.setProperty("GOOGLE_CREDENTIALS_PATH", "/tmp/creds.json");
        System.setProperty("KAFKA_SECURITY_PROTOCOL", "PLAINTEXT");
        System.setProperty("KAFKA_TRUSTSTORE_PATH", "/tmp/truststore.jks");
        System.setProperty("KAFKA_TRUSTSTORE_PASSWORD", "trustpass");
        System.setProperty("KAFKA_KEYSTORE_PATH", "/tmp/keystore.jks");
        System.setProperty("KAFKA_KEYSTORE_PASSWORD", "keypass");
        System.setProperty("VISION_API_TIMEOUT_MS", "10000");
        System.setProperty("HEALTH_CHECK_INTERVAL_SEC", "30");

        component = DaggerVisionClientProviderTest_TestComponent.create();
    }

    /**
     * Verifies that the provider returns a non-null, singleton client when
     * {@link ImageAnnotatorClient#create()} succeeds.
     *
     * @throws IOException if the static mocking fails
     */
    @Test
    void testVisionClientIsSingleton() throws IOException {
        // 1) Create a real, empty temp file so FileInputStream succeeds
        Path tempCreds = Files.createTempFile("vision-creds", ".json");
        // (leave it empty; we'll stub parsing next)

        // 2) Point the provider at our temp file
        System.setProperty("GOOGLE_CREDENTIALS_PATH", tempCreds.toString());

        // 3) Stub GoogleCredentials.fromStream(...) → return a mock that returns itself on createScoped()
        GoogleCredentials mockCreds = Mockito.mock(GoogleCredentials.class);
        Mockito.when(mockCreds.createScoped(Mockito.anyList()))
                .thenReturn(mockCreds);

        try (MockedStatic<GoogleCredentials> credsStatic = Mockito.mockStatic(GoogleCredentials.class)) {
            credsStatic.when(() -> GoogleCredentials.fromStream(Mockito.any(FileInputStream.class)))
                    .thenReturn(mockCreds);

            // 4) Stub ImageAnnotatorClient.create(ImageAnnotatorSettings) → our mock client
            ImageAnnotatorClient mockClient = Mockito.mock(ImageAnnotatorClient.class);
            try (MockedStatic<ImageAnnotatorClient> clientStatic = Mockito.mockStatic(ImageAnnotatorClient.class)) {
                clientStatic.when(() -> ImageAnnotatorClient.create(Mockito.any(ImageAnnotatorSettings.class)))
                        .thenReturn(mockClient);

                // 5) Now exercise the provider
                ImageAnnotatorClient first = component.visionClient();
                ImageAnnotatorClient second = component.visionClient();

                assertNotNull(first, "Should never return null");
                assertSame(mockClient, first, "Should return our stubbed client");
                assertSame(first, second, "Should be the same singleton instance");
            }
        } finally {
            Files.deleteIfExists(tempCreds);
        }
    }
    /**
     * Verifies that if {@link ImageAnnotatorClient#create()} throws an IOException,
     * the error is propagated as a runtime exception with the original cause.
     */
    @Test
    void testVisionClientCreationFailure() throws IOException {
        // 1) Prepare credentials file again
        Path tempCreds = Files.createTempFile("vision-creds", ".json");
        System.setProperty("GOOGLE_CREDENTIALS_PATH", tempCreds.toString());

        // 2) Stub credential parsing to succeed
        GoogleCredentials mockCreds = Mockito.mock(GoogleCredentials.class);
        Mockito.when(mockCreds.createScoped(Mockito.anyList()))
                .thenReturn(mockCreds);

        try (MockedStatic<GoogleCredentials> credsStatic = Mockito.mockStatic(GoogleCredentials.class)) {
            credsStatic.when(() -> GoogleCredentials.fromStream(Mockito.any(FileInputStream.class)))
                    .thenReturn(mockCreds);

            // 3) Stub the create(settings) call to throw IOException
            IOException ioEx = new IOException("creation failed");
            try (MockedStatic<ImageAnnotatorClient> clientStatic = Mockito.mockStatic(ImageAnnotatorClient.class)) {
                clientStatic.when(() -> ImageAnnotatorClient.create(Mockito.any(ImageAnnotatorSettings.class)))
                        .thenThrow(ioEx);

                // 4) Calling visionClient() should now wrap that in our dedicated exception
                IllegalStateException ex = assertThrows(
                        IllegalStateException.class,
                        () -> component.visionClient()
                );
                assertSame(ioEx, ex.getCause(), "Should preserve the original IOException");
            }
        } finally {
            Files.deleteIfExists(tempCreds);
        }
    }
}
