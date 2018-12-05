SET GLOBAL TRANSACTION ISOLATION LEVEL SERIALIZABLE;
SET GLOBAL sql_mode = 'ANSI';
CREATE DATABASE testdatabase;
USE testdatabase;
CREATE TABLE PHARMA_PRODUCTS (
    No_AMM VARCHAR(255),
    Date_Premiere_Autorisation VARCHAR(255),
    Date_Echeance_Autorisation VARCHAR(255),
    Date_Retrait_Definitif VARCHAR(255),
    Statut VARCHAR(255),
    Nom_Commercial VARCHAR(255),
    Type_Commercial VARCHAR(255),
    Entreprise_Detentrice VARCHAR(255),
    Phrases VARCHAR(255),
    Date_Comite VARCHAR(255),
    Teneur VARCHAR(255),
    Unite VARCHAR(255),
    Substance VARCHAR(255),
    No_CAS_Substance VARCHAR(255)
);
