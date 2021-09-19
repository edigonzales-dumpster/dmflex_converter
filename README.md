# Converter

## Develop mit PostgreSQL
Annahme: SQL bleibt identisch. 

Achtung: UUID in H2?

```
mkdir -m 0777 ~/pgdata_dmflex
docker run -p 54321:5432 -v ~/pgdata_dmflex:/var/lib/postgresql/data:delegated -e POSTGRES_DB=edit -e POSTGRES_PASSWORD=mysecretpassword postgis/postgis:13-3.1
```

```
java -jar /Users/stefan/apps/ili2pg-4.6.0/ili2pg-4.6.0.jar --dbhost localhost --dbport 54321 --dbdatabase edit --dbusr postgres --dbpwd mysecretpassword --defaultSrsCode 2056 --disableValidation --nameByTopic --createGeomIdx --createFk --createFkIdx --createEnumTabs --models DM01AVCH24LV95D --modeldir "models;http://models.geo.admin.ch" --dbschema dm01  --setupPgExt --doSchemaImport --import data/252400.itf
```

```
java -jar /Users/stefan/apps/ili2pg-4.6.0/ili2pg-4.6.0.jar --dbhost localhost --dbport 54321 --dbdatabase edit --dbusr postgres --dbpwd mysecretpassword --defaultSrsCode 2056 --disableValidation --nameByTopic --createGeomIdx --createFk --createFkIdx --createEnumTabs --models DM_Flex_AV_CH_Grundstuecke_V1_0 --modeldir "models;http://models.geo.admin.ch" --dbschema dmflex --createUnique --createNumChecks --createTextChecks --createDateTimeChecks --idSeqMin 1000000000000 --setupPgExt --schemaimport
```

```
java -jar /Users/stefan/apps/ili2pg-4.6.0/ili2pg-4.6.0.jar --dbhost localhost --dbport 54321 --dbdatabase edit --dbusr postgres --dbpwd mysecretpassword --defaultSrsCode 2056 --disableValidation --models DM_Flex_AV_CH_Grundstuecke_V1_0 --modeldir "models;http://models.geo.admin.ch" --dbschema dmflex  --export fubar.xtf

xmllint --format fubar.xtf -o fubar.xtf
```

```
java -jar /Users/stefan/apps/ilivalidator-1.11.10/ilivalidator-1.11.10.jar --modeldir "models;http://models.geo.admin.ch" fubar.xtf
```