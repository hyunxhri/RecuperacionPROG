CREATE TABLE ALUMNOS (
    id INT PRIMARY KEY,
    inicialesm VARCHAR(3),
    nombrem VARCHAR(100),
    modulom VARCHAR(3),
    notam VARCHAR(5)
    );

CREATE TABLE NOTAS (
    id INT PRIMARY KEY,
    inicialesn VARCHAR(3),
    modulon VARCHAR(100),
    idRAn VARCHAR(5),
    notaRAn VARCHAR(5),
    notasCEn VARCHAR(200)
    );