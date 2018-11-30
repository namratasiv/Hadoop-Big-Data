M = LOAD 'M-matrix-large.txt' USING PigStorage(',') AS (row, column, value);
N = LOAD 'N-matrix-large.txt' USING PigStorage(',') AS (row, column, value);
J = JOIN M BY column, N BY row;
O = FOREACH J GENERATE M::row AS Mrow, N::column AS Ncolumn, (M::value)*(N::value) AS value;
RESULT = GROUP O BY (Mrow, Ncolumn);
MATRIX = FOREACH RESULT GENERATE group.$0 as row, group.$1 as column, SUM(O.value) AS value;
STORE MATRIX INTO 'output' USING PigStorage (',');
