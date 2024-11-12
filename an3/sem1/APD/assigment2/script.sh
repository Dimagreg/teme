
./prog_static_100 100 epidemics20K.txt 2 >> RESULTS.txt
./prog_static_100 500 epidemics20K.txt 2 >> RESULTS.txt

./prog_static_100 100 epidemics20K.txt 4 >> RESULTS.txt
./prog_static_100 500 epidemics20K.txt 4 >> RESULTS.txt

./prog_static_5000 100 epidemics20K.txt 2 >> RESULTS.txt
./prog_static_5000 500 epidemics20K.txt 2 >> RESULTS.txt

./prog_dynamic_5000 100 epidemics20K.txt 2 >> RESULTS.txt
./prog_dynamic_5000 500 epidemics20K.txt 2 >> RESULTS.txt

./prog_static_2500 100 epidemics20K.txt 4 >> RESULTS.txt
./prog_static_2500 500 epidemics20K.txt 4 >> RESULTS.txt

./prog_dynamic_2500 100 epidemics20K.txt 4 >> RESULTS.txt
./prog_dynamic_2500 500 epidemics20K.txt 4 >> RESULTS.txt

echo "done 20K"

./prog_static_100 100 epidemics200K.txt 2 >> RESULTS.txt
./prog_static_100 500 epidemics200K.txt 2 >> RESULTS.txt

./prog_static_100 100 epidemics200K.txt 4 >> RESULTS.txt
./prog_static_100 500 epidemics200K.txt 4 >> RESULTS.txt

./prog_static_50000 100 epidemics200K.txt 2 >> RESULTS.txt
./prog_static_50000 500 epidemics200K.txt 2 >> RESULTS.txt

./prog_dynamic_50000 100 epidemics200K.txt 2 >> RESULTS.txt
./prog_dynamic_50000 500 epidemics200K.txt 2 >> RESULTS.txt

./prog_static_25000 100 epidemics200K.txt 4 >> RESULTS.txt
./prog_static_25000 500 epidemics200K.txt 4 >> RESULTS.txt

./prog_dynamic_25000 100 epidemics200K.txt 4 >> RESULTS.txt
./prog_dynamic_25000 500 epidemics200K.txt 4 >> RESULTS.txt

echo "done 200K"

./prog_static_100 100 epidemics1M.txt 2 >> RESULTS.txt
./prog_static_100 500 epidemics1M.txt 2 >> RESULTS.txt

./prog_static_100 100 epidemics1M.txt 4 >> RESULTS.txt
./prog_static_100 500 epidemics1M.txt 4 >> RESULTS.txt

./prog_static_250000 100 epidemics1M.txt 2 >> RESULTS.txt
./prog_static_250000 500 epidemics1M.txt 2 >> RESULTS.txt

./prog_dynamic_250000 100 epidemics1M.txt 2 >> RESULTS.txt
./prog_dynamic_250000 500 epidemics1M.txt 2 >> RESULTS.txt

./prog_static_125000 100 epidemics1M.txt 4 >> RESULTS.txt
./prog_static_125000 500 epidemics1M.txt 4 >> RESULTS.txt

./prog_dynamic_125000 100 epidemics1M.txt 4 >> RESULTS.txt
./prog_dynamic_125000 500 epidemics1M.txt 4 >> RESULTS.txt

echo "done 1M"