--Add the new column EXA_FOR
ALTER TABLE oh_exam 
ADD COLUMN EXA_FOR ENUM('no', 'prenatal', 'postnatal', 'both') DEFAULT 'no';



