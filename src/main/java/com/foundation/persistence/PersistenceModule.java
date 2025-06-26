package com.foundation.persistence;

import com.foundation.config.Config;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import javax.inject.Named;

/**
 * Dagger module responsible for binding persistence-related components.
 * <p>
 * This module binds the {@link DocumentSink} and {@link ErrorSink} interfaces
 * to their default implementations, and provides any necessary configuration values
 * such as file system paths or Kafka topics for error reporting.
 * </p>
 *
 * @see DiskDocumentSink
 * @see KafkaErrorSink
 */
@Module
public abstract class PersistenceModule {

    /**
     * Binds the default {@link DocumentSink} implementation ({@link DiskDocumentSink}).
     * <p>
     * The {@link DiskDocumentSink} writes OCR-processed documents to the file system
     * under the directory bound to {@code document.sink.path}.
     * </p>
     *
     * @param impl the DiskDocumentSink implementation
     * @return the bound DocumentSink interface
     */
    @Binds
    @Singleton
    public abstract DocumentSink bindDocumentSink(DiskDocumentSink impl);

    /**
     * Binds the default {@link ErrorSink} implementation ({@link KafkaErrorSink}).
     * <p>
     * The {@link KafkaErrorSink} publishes error envelopes to the configured Kafka topic.
     * </p>
     *
     * @param impl the KafkaErrorSink implementation
     * @return the bound ErrorSink interface
     */
    @Binds
    @Singleton
    public abstract ErrorSink bindErrorSink(KafkaErrorSink impl);

    /**
     * Provides the base file system path where documents should be written.
     * <p>
     * This value is read from {@link Config#getDocumentSinkPath()}.
     * </p>
     *
     * @param config the application configuration
     * @return the document sink base directory path
     */
    @Provides
    @Singleton
    @Named("document.sink.path")
    public static String provideDocumentSinkPath(Config config) {
        return config.getDocumentSinkPath();
    }

    /**
     * Provides the Kafka topic name for error reporting.
     * <p>
     * This value is read from {@link Config#getErrorTopic()}.
     * </p>
     *
     * @param config the application configuration
     * @return the error sink Kafka topic name
     */
    @Provides
    @Singleton
    @Named("error.sink.topic")
    public static String provideErrorSinkTopic(Config config) {
        return config.getErrorTopic();
    }
}
