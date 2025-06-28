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
    void testVisionClientIsSingleton() throws Exception {
        // 1) Create a temp creds file and point the provider at it
        Path credsFile = Files.createTempFile("vision-creds", ".json");
        System.setProperty("GOOGLE_CREDENTIALS_PATH", credsFile.toString());

        // 2) Stub GoogleCredentials.fromStream(...) to avoid JSON parsing
        try (
                MockedStatic<GoogleCredentials> credsStatic =
                        Mockito.mockStatic(GoogleCredentials.class);
                // 3) Stub exactly the overload create(ImageAnnotatorSettings):
                MockedStatic<ImageAnnotatorClient> clientStatic =
                        Mockito.mockStatic(ImageAnnotatorClient.class)
        ) {
            // Arrange credential stub
            GoogleCredentials dummyCreds = Mockito.mock(GoogleCredentials.class);
            Mockito.when(dummyCreds.createScoped(Mockito.anyList()))
                    .thenReturn(dummyCreds);
            credsStatic.when(() ->
                    GoogleCredentials.fromStream(Mockito.any(FileInputStream.class))
            ).thenReturn(dummyCreds);

            // Arrange client stub (disambiguate by matching ImageAnnotatorSettings)
            ImageAnnotatorClient mockClient = Mockito.mock(ImageAnnotatorClient.class);
            clientStatic.when(() ->
                    ImageAnnotatorClient.create(Mockito.any(ImageAnnotatorSettings.class))
            ).thenReturn(mockClient); // NOSONAR

            // 4) Both calls to visionClient() in the same try‐with‐resources header:
            try (
                    ImageAnnotatorClient first = component.visionClient();
                    ImageAnnotatorClient second = component.visionClient()
            ) {
                assertNotNull(first, "Client must not be null");
                assertSame(mockClient, first, "Should return the stubbed instance");
                assertSame(first, second, "Should be the same singleton");
            }
        } finally {
            Files.deleteIfExists(credsFile);
            System.clearProperty("GOOGLE_CREDENTIALS_PATH");
        }
    }

    /**
     * Verifies that if {@link ImageAnnotatorClient#create()} throws an IOException,
     * the error is propagated as a runtime exception with the original cause.
     */
    @Test
    void testVisionClientCreationFailure() throws IOException {
        Path credsFile = Files.createTempFile("vision-creds", ".json");
        System.setProperty("GOOGLE_CREDENTIALS_PATH", credsFile.toString());

        try (
                MockedStatic<GoogleCredentials> credsStatic =
                        Mockito.mockStatic(GoogleCredentials.class);
                // NOSONAR: static mocking of AutoCloseable factory
                MockedStatic<ImageAnnotatorClient> clientStatic =
                        Mockito.mockStatic(ImageAnnotatorClient.class)
        ) {
            // Stub credentials
            GoogleCredentials dummyCreds = Mockito.mock(GoogleCredentials.class);
            Mockito.when(dummyCreds.createScoped(Mockito.anyList()))
                    .thenReturn(dummyCreds);
            credsStatic.when(() ->
                    GoogleCredentials.fromStream(Mockito.any(FileInputStream.class))
            ).thenReturn(dummyCreds);

            // Stub create(settings) to throw
            IOException ioEx = new IOException("creation failed");
            clientStatic.when(() ->
                    ImageAnnotatorClient.create(Mockito.any(ImageAnnotatorSettings.class))
            ).thenThrow(ioEx);

            // Use try-with-resources to satisfy resource rules, but we expect exception
            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> {
                        try (ImageAnnotatorClient ignored = component.visionClient()) {
                            // no-op
                        }
                    },
                    "Expected VisionClientInitializationException on failure"
            );
            assertSame(ioEx, ex.getCause(),
                    "Should preserve original IOException as cause");
        } finally {
            Files.deleteIfExists(credsFile);
            System.clearProperty("GOOGLE_CREDENTIALS_PATH");
        }
    }
}
