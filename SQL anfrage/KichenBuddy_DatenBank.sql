#### Zu Erzeug 'KichtenbuddyDB' by Jack Behringer ###


### PASS AUF!!!###
#DROP DATABASE IF EXISTS kitchenbuddy;


### Datenbank erzeugen
CREATE DATABASE IF NOT EXISTS kitchenbuddy;


### sicher dass man mit die richtige DB verbundet ist
USE kitchenbuddy;



#### Tabellen erzeugen mit beziehungen

###Erstmal die Tabellen ohne FK
CREATE TABLE IF NOT EXISTS Rezepte
(
    RezeptID     INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    Titel        VARCHAR(50)      NOT NULL,
    Kueche       VARCHAR(50)      NULL,
    Schwerigkeit INT(1) UNSIGNED  NOT NULL DEFAULT 2,   #DEFAULT 2 sind die 'Schwerigkeit'
    Zeit         INT(1) UNSIGNED  NOT NULL DEFAULT 2,   #DEFAULT 2 sind die 'Normalzeit'
    Methode      TEXT             NULL,
    PRIMARY KEY (RezeptID)
);


CREATE TABLE IF NOT EXISTS Zutaten
(
    ZutatID             INT(10) UNSIGNED        NOT NULL AUTO_INCREMENT,
    Zutat               VARCHAR(50)     UNIQUE  NULL,
    Ingredient          VARCHAR(50)     UNIQUE  NULL,
    Lebensmittelgruppe  VARCHAR(20)             NOT NULL,
    Season              VARCHAR(20)             NULL,
    PRIMARY KEY (ZutatID)
);


CREATE TABLE IF NOT EXISTS Einkaufstyp
(
    EinkaufstypID   INT(10) UNSIGNED    NOT NULL AUTO_INCREMENT,
    Typ             VARCHAR(50)         NOT NULL,
    Kostenranking   INT(1) UNSIGNED     NOT NULL,
    Beschreibung    TEXT                NULL,
    PRIMARY KEY (EinkaufstypID)
);


CREATE TABLE IF NOT EXISTS Mealarten
(
    MealartID    INT(10) UNSIGNED   NOT NULL AUTO_INCREMENT,
    Mealart      VARCHAR(50)        NOT NULL,
    Beschreibung TEXT               NULL,
    PRIMARY KEY (MealartID)
);

## Jetzt die Beziehungstabellen
CREATE TABLE IF NOT EXISTS Rezept_Zutaten
(
    RezeptID    INT(10) UNSIGNED     NOT NULL,
    ZutatID     INT(10) UNSIGNED     NOT NULL,
    Menge       VARCHAR(100)         NULL,
    PRIMARY KEY (RezeptID, ZutatID),
    CONSTRAINT Rezepte_Rezept FOREIGN KEY (RezeptID) REFERENCES Rezepte (RezeptID)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT Zutaten_Rezept FOREIGN KEY (ZutatID) REFERENCES Zutaten (ZutatID)
        ON UPDATE CASCADE ON DELETE RESTRICT
);


CREATE TABLE IF NOT EXISTS Rezeptarten
(
    RezeptID    INT(10) UNSIGNED   NOT NULL,
    MealartID   INT(10) UNSIGNED   NOT NULL,
    PRIMARY KEY (RezeptID, MealartID),
    CONSTRAINT Rezepte_mealart FOREIGN KEY (RezeptID) REFERENCES rezepte (RezeptID)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT Mealarten_mealart FOREIGN KEY (MealartID) REFERENCES mealarten (MealartID)
        ON UPDATE CASCADE ON DELETE RESTRICT
);


CREATE TABLE IF NOT EXISTS Einkaufen
(
    EinkaufstypID   INT(10) UNSIGNED    NOT NULL,
    ZutatID         INT(10) UNSIGNED    NOT NULL,
    PRIMARY KEY (EinkaufstypID, ZutatID),
    CONSTRAINT Einkaufstyp_einkauf FOREIGN KEY (EinkaufstypID) REFERENCES einkaufstyp (EinkaufstypID)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT Zutaten_einauf FOREIGN KEY (ZutatID) REFERENCES zutaten (ZutatID)
        ON UPDATE CASCADE ON DELETE RESTRICT
);


#### TRIGGERS / FUNCTIONEN & PROZEDUREN ####


#Prozedure um die richtiger Zutaten und einkaufstpy beim haufitgsten benutzten zutaten
DELIMITER {}
CREATE PROCEDURE InsertAmhaufigstenEinkaufstypfür1und2()
BEGIN
    DECLARE i INT DEFAULT 1;

    ##Normal Lebensmittelläden
    WHILE i <= 258 DO
        IF i IN (13, 45, 68, 72, 83, 84, 197, 243, 254, 257) THEN     ##Nehmen die zutaten raus dass nicht beim laden sind
            SET i = i + 1;
        END IF;
        INSERT INTO Einkaufen(EinkaufstypID, ZutatID)
            VALUES (1, i);
        SET i = i + 1;
        IF i IN (13, 45, 68, 72, 83, 84, 197, 243, 254, 257) THEN  ##ausnahme für 83...
            SET i = i + 1;
        END IF;
    end while;

    SET i = 1;  ##wieder i auf 1

    ##BIOladen
    WHILE i <= 258 DO
            IF i IN (13, 45, 68, 72, 83, 84, 197, 243, 254, 257) THEN     ##Nehmen die zutaten raus dass nicht beim laden sind
                SET i = i + 1;
            END IF;
            INSERT INTO Einkaufen(EinkaufstypID, ZutatID)
            VALUES (2, i);
            SET i = i + 1;
            IF i IN (13, 45, 68, 72, 83, 84, 197, 243, 254, 257) THEN  ##ausnahme für 83...
                SET i = i + 1;
            END IF;
    end while;

end {}


#### AUTO rezeptart vegan wenn gerischte auch veggie ist
#DELIMITER {}
#CREATE PROCEDURE autoVeggie()
#BEGIN
#
#    DECLARE i INT DEFAULT 0;
#
#
#
#    INSERT INTO rezeptarten (RezeptID, MealartID)
#    VALUES (input, 2);
#
#end {}


#funktion dass prüft ein eingeben ob es zwischen 1 und 5 ist, wenn nicht, dann setzt es aud 1 oder 5, jenachdem die wert großer und kleiner ist
DELIMITER {}
CREATE FUNCTION zwischen1_5(input INT)
RETURNS INT(1)
BEGIN

    DECLARE i INT(1);
    SET i = input;

    IF i > 5 THEN
        SET i = 5;
    ELSEIF i < 1 THEN
        SET i = 1;
    end if;

    RETURN i;
end {}

#### Triggers

### Kostenranking bei Einkauftstup
DELIMITER {}
CREATE TRIGGER insertEinkaufstpy_1zu5
BEFORE INSERT ON einkaufstyp
FOR EACH ROW
BEGIN

    ### Prüft ob die eingeben zwischen 1 - 5 ist
    SET NEW.Kostenranking = zwischen1_5(NEW.Kostenranking);

end {}

DELIMITER {}
CREATE TRIGGER updateEinkaufstpy_1zu5
    BEFORE INSERT ON einkaufstyp
    FOR EACH ROW
BEGIN

    ### Prüft ob die änderung zwischen 1 - 5 ist
    SET NEW.Kostenranking = zwischen1_5(NEW.Kostenranking);

end {}


### Zeit bei rezepte
DELIMITER {}
CREATE TRIGGER insertZeit_1zu5
    BEFORE INSERT ON rezepte
    FOR EACH ROW
BEGIN

    ### Prüft ob die eingeben zwischen 1 - 5 ist
    SET NEW.Zeit = zwischen1_5(NEW.Zeit);

end {}

DELIMITER {}
CREATE TRIGGER updateZeit_1zu5
    BEFORE INSERT ON rezepte
    FOR EACH ROW
BEGIN

    ### Prüft ob die änderung zwischen 1 - 5 ist
    SET NEW.Zeit = zwischen1_5(NEW.Zeit);

end {}


### Schwerigkeit bei rezepte
DELIMITER {}
CREATE TRIGGER insertSchwerigkeit_1zu5
    BEFORE INSERT ON rezepte
    FOR EACH ROW
BEGIN

    ### Prüft ob die eingeben zwischen 1 - 5 ist
    SET NEW.Schwerigkeit = zwischen1_5(NEW.Schwerigkeit);

end {}

DELIMITER {}
CREATE TRIGGER updateSchwerigkeit_1zu5
    BEFORE INSERT ON rezepte
    FOR EACH ROW
BEGIN

    ### Prüft ob die änderung zwischen 1 - 5 ist
    SET NEW.Schwerigkeit = zwischen1_5(NEW.Schwerigkeit);

end {}


#### Zutaten auto Ganzjährig, wenn NULL eingegeben ist
DELIMITER {}
CREATE TRIGGER insertAutoGanzjaehrig
    BEFORE INSERT ON zutaten
    FOR EACH ROW
BEGIN

    IF NEW.Season IS NULL THEN
        SET NEW.Season = 'Ganzjährig';
    END IF;

end {}

DELIMITER {}
CREATE TRIGGER updateAutoGanzjaehrig
    BEFORE UPDATE ON zutaten
    FOR EACH ROW
BEGIN

    IF NEW.Season IS NULL THEN
        SET NEW.Season = 'Ganzjährig';
    END IF;

end {}




#### VIEWS ####

#### Erzeugt die beziehung zwischen Rezepte und Einkaufstyp
CREATE VIEW rezepte_Einkaufstyp AS
    SELECT rezepte.RezeptID, rezepte.Titel, Zutaten.Zutat, rezepte.Kueche, rezepte.Schwerigkeit, rezepte.Zeit, rezepte.Methode,
           Einkaufstyp.Typ from rezepte
    JOIN rezept_zutaten ON rezepte.RezeptID = rezept_zutaten.RezeptID
    JOIN zutaten ON rezept_zutaten.ZutatID = zutaten.ZutatID
    JOIN einkaufen ON zutaten.ZutatID = einkaufen.ZutatID
    JOIN einkaufstyp ON einkaufen.EinkaufstypID = einkaufstyp.EinkaufstypID;

### Erzeut die beziehung zwischen Rezepte und Mealarten
CREATE VIEW rezepte_Mealarten AS
SELECT rezepte.RezeptID, rezepte.Titel, Mealarten.Mealart, rezepte.Kueche, rezepte.Schwerigkeit, rezepte.Zeit, rezepte.Methode  from rezepte
    JOIN rezeptarten ON Rezeptarten.RezeptID = Rezepte.RezeptID
    JOIN Mealarten ON Mealarten.MealartID = Rezeptarten.MealartID
    GROUP BY Rezepte.RezeptID;


### Alle beziehungen von Rezepte zu Mealart und Einkaufstyp
CREATE VIEW Mealart_Rezepte_Einkaufstpy AS
    SELECT Rezepte.RezeptID, rezepte.Titel, rezepte.Kueche, rezepte.Schwerigkeit, rezepte.Zeit, GROUP_CONCAT(DISTINCT Mealarten.Mealart SEPARATOR ' | ') AS Mealarten,
           GROUP_CONCAT(DISTINCT Einkaufstyp.Typ SEPARATOR ' | ') AS Einkauftstyp, GROUP_CONCAT(DISTINCT Zutaten.Zutat SEPARATOR ' | ') AS Zutanten, rezepte.Methode FROM Rezepte
    JOIN Rezept_Zutaten ON Rezepte.RezeptID = Rezept_Zutaten.RezeptID
    JOIN zutaten ON Rezept_Zutaten.ZutatID = zutaten.ZutatID
    JOIN einkaufen ON Einkaufen.ZutatID = Zutaten.ZutatID
    JOIN einkaufstyp ON Einkaufstyp.EinkaufstypID = Einkaufen.EinkaufstypID
    JOIN rezeptarten ON Rezeptarten.RezeptID = Rezepte.RezeptID
    JOIN Mealarten ON Mealarten.MealartID = Rezeptarten.MealartID
    GROUP BY rezepte.RezeptID;


#### COOKING VIEW
CREATE VIEW CookingTime AS
    SELECT rezepte.RezeptID, rezepte.Titel, GROUP_CONCAT(DISTINCT Zutaten.Zutat SEPARATOR ' | ') AS Zutaten, rezepte.Methode FROM Rezepte
    JOIN rezept_zutaten ON rezepte.RezeptID = rezept_zutaten.RezeptID
    JOIN zutaten ON rezept_zutaten.ZutatID = zutaten.ZutatID
    GROUP BY Rezepte.RezeptID;

