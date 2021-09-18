package ch.so.agi.dmflex;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2h2gis.H2gisMain;
import net.lingala.zip4j.ZipFile;

public class Converter {
    Logger log = LoggerFactory.getLogger(Converter.class);
    
    private final static String MODELS_ZIP_FILENAME = "models.zip";
    private final static String DM01AVCH24LV95D = "DM01AVCH24LV95D";
    private final static String DMFLEX_MODELNAME = "DM_Flex_AV_CH_Grundstuecke_V1_0";
    private final static String DMFLEX_DB_FILENAME = "dmflex";
    
    public void run(String inputFileName, String outputFileName) throws IOException, Ili2dbException {
//        String tmpdir = Files.createTempDirectory("dmflex").toFile().getAbsolutePath();
        String tmpdir = "/Users/stefan/tmp/";
        
        // Datenmodelle auf das Filesystem kopieren
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(MODELS_ZIP_FILENAME);
        Path modelsZipPath = Paths.get(tmpdir, MODELS_ZIP_FILENAME);
        Files.copy(inputStream, modelsZipPath, StandardCopyOption.REPLACE_EXISTING);
        new ZipFile(modelsZipPath.toFile()).extractAll(tmpdir);
        
        importDM01(inputFileName, tmpdir);
        initDMflex(tmpdir);
//        initDatabase(tmpdir, DM01_MODELNAME);
//        initDatabase(tmpdir, DMFLEX_MODELNAME);

    }
    
    private void importDM01(String intputFileName, String tmpdir) throws Ili2dbException {
        String h2FileName = Paths.get(tmpdir, DMFLEX_DB_FILENAME).toFile().getAbsolutePath();
        String dbUrl = "jdbc:h2:file:" + h2FileName;

        Config settings = new Config();
        new H2gisMain().initConfig(settings);
        settings.setFunction(Config.FC_IMPORT);
        settings.setModels(DM01AVCH24LV95D);
        settings.setModeldir(tmpdir + ";http://models.geo.admin.ch");
        settings.setDbfile(h2FileName);
        settings.setValidation(false);
        settings.setItfTransferfile(true);
        settings.setDburl(dbUrl);
        settings.setXtffile(intputFileName);
        settings.setDoImplicitSchemaImport(true);
        settings.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
        settings.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
        settings.setDefaultSrsCode("2056");
        settings.setDbschema("dm01");
        settings.setCreateMetaInfo(true);

        Ili2db.run(settings, null);
    }
    
    private void initDMflex(String tmpdir) throws Ili2dbException {
        String h2FileName = Paths.get(tmpdir, DMFLEX_DB_FILENAME).toFile().getAbsolutePath();
        String dbUrl = "jdbc:h2:file:" + h2FileName;

        Config settings = new Config();
        new H2gisMain().initConfig(settings);
        settings.setFunction(Config.FC_SCHEMAIMPORT);
        settings.setModels(DMFLEX_MODELNAME);
        settings.setModeldir(tmpdir + ";http://models.geo.admin.ch");
        settings.setDbfile(h2FileName);
        settings.setValidation(false);
        settings.setItfTransferfile(false);
        settings.setDburl(dbUrl);
        //settings.setXtffile(intputFileName);
        settings.setDoImplicitSchemaImport(true);
        settings.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
        settings.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
        settings.setDefaultSrsCode("2056");
        settings.setDbschema("dmflex");
        settings.setCreateMetaInfo(true);

        Ili2db.run(settings, null);
    }
    
    
    
//    private void initDatabase(String tmpdir, String modelName) throws Ili2dbException {
//        String gpkgFileName = Paths.get(tmpdir, this.DMFLEX_DB_FILENAME).toFile().getAbsolutePath();
//        String dbUrl = "jdbc:sqlite:" + gpkgFileName;
//        Config settings = new Config();
//        new GpkgMain().initConfig(settings);
//        settings.setFunction(Config.FC_SCHEMAIMPORT);
//        settings.setModels(modelName);
//        settings.setModeldir(tmpdir + ";http://models.geo.admin.ch");
//        settings.setDbfile(gpkgFileName);
//        settings.setValidation(false);
//        //settings.setItfTransferfile(true);
//        settings.setDburl(dbUrl);
//        //settings.setXtffile(inputFileName);
//        //settings.setDoImplicitSchemaImport(true);
//        settings.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
//        //Config.setStrokeArcs(settings, Config.STROKE_ARCS_ENABLE);
//        settings.setDefaultSrsCode("2056");
//        //settings.setCreateMetaInfo(true);
//        Ili2db.run(settings, null);
//    }
//    
//    private void importData(String inputFileName, String tmpdir) throws Ili2dbException {
//        String gpkgFileName = Paths.get(tmpdir, this.DMFLEX_DB_FILENAME).toFile().getAbsolutePath();
//        String dbUrl = "jdbc:sqlite:" + gpkgFileName;
//        Config settings = new Config();
//        new GpkgMain().initConfig(settings);
//        settings.setFunction(Config.FC_IMPORT);
//        settings.setModels(DM01_MODELNAME);
//        settings.setDbfile(gpkgFileName);
//        settings.setValidation(false);
//        settings.setItfTransferfile(true);
//        settings.setDburl(dbUrl);
//        settings.setXtffile(inputFileName);
//        settings.setDoImplicitSchemaImport(true);
//        settings.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
//        //Config.setStrokeArcs(settings, Config.STROKE_ARCS_ENABLE);
//        settings.setDefaultSrsCode("2056");
//        settings.setCreateMetaInfo(true);
//        Ili2db.run(settings, null);
//    }

}
