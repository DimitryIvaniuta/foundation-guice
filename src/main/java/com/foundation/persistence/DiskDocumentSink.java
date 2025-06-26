package com.foundation.persistence;

import com.datafactory.model.Document;
import com.datafactory.model.Page;
import com.datafactory.model.Line;
import com.datafactory.model.Word;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Disk-based implementation of {@link DocumentSink}.
 * <p>
 * This sink writes each {@link Document} to the local file system under a
 * configured base directory. For each document, it creates a subdirectory named
 * by the document's key, where it writes:
 * <ul>
 *   <li>A JSON file ('document.json') containing the full document model.</li>
 *   <li>Plain-text files ('page-{n}.txt') for each page, with each line's text.</li>
 * </ul>
 * </p>
 * <p>
 * Requires a binding of a {@code @Named("document.sink.path")} String that
 * specifies the base output directory. The directory will be created if it does not exist.
 * </p>
 *
 * @see DocumentSink
 */
@Singleton
public class DiskDocumentSink implements DocumentSink {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Base directory under which documents are written.
     */
    private final Path baseDirectory;

    /**
     * Constructs the sink with the given base output path.
     *
     * @param outputPath the file-system path to the base output directory
     */
    @Inject
    public DiskDocumentSink(@Named("document.sink.path") String outputPath) {
        this.baseDirectory = Paths.get(outputPath);
    }

    /**
     * Writes the given {@link Document} to disk.
     * <p>
     * Creates a subdirectory under the base directory named for the document key,
     * then writes the document JSON and per-page text files.
     * </p>
     *
     * @param document the document to write
     * @throws IOException if any file operations fail
     */
    @Override
    public void write(final Document document) throws IOException {
        // Prepare document directory
        Path docDir = baseDirectory.resolve(document.getKey());
        Files.createDirectories(docDir);

        // Write JSON representation
        Path jsonFile = docDir.resolve("document.json");
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(jsonFile.toFile(), document);

        // Write plain-text for each page
        List<Page> pages = document.getPages();
        for (Page page : pages) {
            String text = page.getLines().stream()
                    .map(Line::getWords)
                    .flatMap(List::stream)
                    .map(Word::getText)
                    .collect(Collectors.joining(" ", "", System.lineSeparator()));

            Path txtFile = docDir.resolve(String.format("page-%d.txt", page.getPageNumber()));
            Files.writeString(txtFile, text);
        }
    }
}
