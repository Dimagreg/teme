echo "100, 4"
./prog_parallel 100 epidemics10M.txt 4
echo "100, 8"
./prog_parallel 100 epidemics10M.txt 8


echo "1000, 4"; 
./prog_parallel 1000 epidemics10M.txt 4; 
echo "1000, 8"; 
./prog_parallel 1000 epidemics10M.txt 8; 

echo "2000, 2"; 
./prog 2000 epidemics10M.txt 2; 
echo "2000, 4"; 
./prog_parallel 2000 epidemics10M.txt 4; 
echo "2000, 8"; 
./prog_parallel 2000 epidemics10M.txt 8;

echo "5000, 2";
./prog 5000 epidemics10M.txt 2;
echo "5000, 4";
./prog_parallel 5000 epidemics10M.txt 4;
echo "5000, 8";
./prog_parallel 5000 epidemics10M.txt 8;