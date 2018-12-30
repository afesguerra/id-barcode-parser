package com.afesguerra.colombia.id.barcode;

import com.afesguerra.colombia.id.barcode.model.IDData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class IDReaderTest {

    private static final String TEST_DATA_FOLDER = "/examples";

    private IDReader idReader;

    @BeforeEach
    void setUp() {
        idReader = new IDReader();
    }

    static List<Path> getData() throws IOException, URISyntaxException {
        final URI resource = IDReaderTest.class.getResource(TEST_DATA_FOLDER).toURI();
        try (Stream<Path> walk = Files.walk(Paths.get(resource))) {
            return walk.filter(Files::isRegularFile).collect(Collectors.toList());
        }
    }

    @ParameterizedTest
    @MethodSource("getData")
    void testParsing(final Path filePath) throws IOException {
        final String data = Files.lines(filePath).collect(Collectors.joining());
        final IDData idData = idReader.readIDData(data);
        log.info("{}", idData);
        assertNotNull(idData);
    }
}