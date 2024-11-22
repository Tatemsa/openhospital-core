
CREATE TABLE OH_MORTUARYSTAYS (
   MRTST_CODE VARCHAR(11) NOT NULL,
   MRTST_NAME VARCHAR(100) NOT NULL,
   MRTST_DESC VARCHAR(255) NOT NULL,
   MRTST_MIN_DAYS INT NOT NULL,
   MRTST_MAX_DAYS INT NOT NULL,
   MRTST_CREATED_BY VARCHAR(50) NULL DEFAULT NULL,
   MRTST_CREATED_DATE datetime NULL DEFAULT NULL,
   MRTST_LAST_MODIFIED_BY VARCHAR(50) NULL DEFAULT NULL,
   MRTST_LAST_MODIFIED_DATE datetime NULL DEFAULT NULL,
   MRTST_ACTIVE TINYINT(1) NOT NULL DEFAULT 1,
   PRIMARY KEY (MRTST_CODE)
);
