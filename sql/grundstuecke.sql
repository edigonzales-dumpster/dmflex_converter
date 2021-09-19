

-- TODO: in separates SQL-File auslagern
/*
INSERT INTO 
    dmflex.t_ili2db_dataset 
    (
        t_id,
        datasetname
    )
    VALUES
    (
        nextval('dmflex.t_ili2db_seq'),
        'fubar.xtf-1'
    )
;
INSERT INTO
    dmflex.t_ili2db_basket 
    (
        t_id,
        dataset,
        topic,
        t_ili_tid,
        attachmentkey
    )
SELECT 
    nextval('dmflex.t_ili2db_seq'),
    t_id,
    'DM_Flex_AV_CH_Grundstuecke_V1_0.Grundstuecke',
    uuid_generate_v4(),
    'fubar.xtf-2'
FROM 
    dmflex.t_ili2db_dataset 
;   
*/



-- TOOD: 
-- * Testen, ob 'hoheitsgrenzpunkt' funktioniert. Ist das überhaupt notwendig?
-- Entweder ist er in den DM01-HGP oder er ist kein Hoheitsgrenzpunk!?

DELETE FROM 
    dmflex.grundstuecke_grenzpunkt 
;
DELETE FROM 
    dmflex.grundstuecke_gsnachfuehrung 
;


INSERT INTO
    dmflex.grundstuecke_gsnachfuehrung 
    (
        t_id,
        nbident,
        identifikator,
        beschreibung,
        perimeter,
        astatus,
        gueltigereintrag,
        grundbucheintrag
    )
SELECT 
    t_id,
    nbident,
    identifikator,
    beschreibung,
    perimeter,
    gueltigkeit,
    gueltigereintrag,
    gbeintrag
FROM 
    dm01.liegenschaften_lsnachfuehrung   
;

INSERT INTO
    dmflex.grundstuecke_grenzpunkt
    (
        t_id,
        nbident,
        identifikator,
        geometrie,
        lagegen,
        lagezuv,
        punktzeichen,
        hoheitsgrenzpunkt,
        exaktdefiniert,
        symbolori,
        entstehung
    )
SELECT 
    gp.t_id,
    nf.nbident,
    gp.identifikator,
    gp.geometrie,
    lagegen,
    lagezuv,
    punktzeichen,
    CASE 
        WHEN ST_Intersects(gp.geometrie, ST_Boundary(gemgre.geometrie)) THEN 'ja'
        ELSE 'nein'
    END AS hoheitsgrenzpunkt,
    lower(exaktdefiniert),
    CASE 
        WHEN symbol.ori IS NULL THEN 0.0
        ELSE symbol.ori 
    END AS symbolori,
    gp.entstehung
FROM 
    dm01.liegenschaften_grenzpunkt AS gp
    LEFT JOIN dm01.liegenschaften_lsnachfuehrung AS nf 
    ON gp.entstehung = nf.t_id
    LEFT JOIN dm01.liegenschaften_grenzpunktsymbol AS symbol 
    ON gp.t_id = symbol.grenzpunktsymbol_von,
    dm01.gemeindegrenzen_gemeindegrenze AS gemgre
;
-- Alle Hoheitsgrenzpunkte und ihre dazugehörigen NF-Einträge.
-- Achtung: 'nbident' und 'identifikator' müssen eindeutig sein. Somit
-- kann es zu Konflikten kommen, weil diese Kombination bereits in bei den 
-- Grundstücken vorkommt. In der Annahme, dass es sich bei einem Konflikt, um
-- die gleiche Nachführung handelt, inserten wir nur neue Kombinationen.
-- Das Inserten der HGP wird damit leicht komplizierter, weil man 
-- zwischen einer möglichen neuen t_id und der originalen t_id mappen
-- muss.
-- H2: Ginge wohl irgendwie auch ohne UPSERT, dafür mit einer CTE. Die 
-- Mapping-Tabelle als CTE und diese dann als "Gedächtnis" verwenden.
INSERT INTO
    dmflex.grundstuecke_gsnachfuehrung 
    (
        t_id,
        nbident,
        identifikator,
        beschreibung,
        perimeter,
        astatus,
        gueltigereintrag,
        grundbucheintrag
    )
SELECT 
    t_id,
    nbident,
    identifikator,
    beschreibung,
    perimeter,
    gueltigkeit,
    gueltigereintrag,
    datum1
FROM 
    dm01.gemeindegrenzen_gemnachfuehrung AS nf
WHERE 
    t_id IN 
(
    SELECT DISTINCT 
        entstehung 
    FROM 
        dm01.gemeindegrenzen_hoheitsgrenzpunkt AS hgp 
)   
ON CONFLICT (nbident, identifikator)
DO NOTHING
;

INSERT INTO
    dmflex.grundstuecke_grenzpunkt
    (
        t_id,
        nbident,
        identifikator,
        geometrie,
        lagegen,
        lagezuv,
        punktzeichen,
        hoheitsgrenzpunkt,
        exaktdefiniert,
        symbolori,
        entstehung
    )
SELECT
    hgp.t_id,
    gsnf.nbident,
    hgp.identifikator,
    hgp.geometrie,
    hgp.lagegen,
    hgp.lagezuv,
    hgp.punktzeichen,
    'ja',
    lower(hgp.exaktdefiniert),
    0.0::NUMERIC AS symbolori, --todo
    gsnf.t_id AS entstehung
FROM 
(
    SELECT DISTINCT
        nf.t_id,
        nf.nbident,
        nf.identifikator
    FROM 
        dm01.gemeindegrenzen_hoheitsgrenzpunkt AS hgp 
        LEFT JOIN dm01.gemeindegrenzen_gemnachfuehrung AS nf 
        ON nf.t_id = hgp.entstehung 
) AS foo
LEFT JOIN dmflex.grundstuecke_gsnachfuehrung AS gsnf 
ON gsnf.nbident = foo.nbident AND gsnf.identifikator = foo.identifikator
LEFT JOIN dm01.gemeindegrenzen_hoheitsgrenzpunkt AS hgp 
ON hgp.entstehung = foo.t_id
;
    