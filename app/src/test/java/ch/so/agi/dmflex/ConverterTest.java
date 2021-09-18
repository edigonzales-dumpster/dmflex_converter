package ch.so.agi.dmflex;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.ehi.ili2db.base.Ili2dbException;

public class ConverterTest {
    @Test
    public void convert_Ok(@TempDir Path directory) throws IOException, Ili2dbException {
                
        String outputFileName = Paths.get(directory.toFile().getAbsolutePath(), "252400.xtf").toFile().getAbsolutePath();
        
        
        Converter converter = new Converter();
        converter.run("src/test/data/252400.itf", outputFileName);

    }
    
}
