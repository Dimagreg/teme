# Epidemic Simulation Model

This project implements and parallelizes a simplified epidemic simulation model to observe the spread of a contagious disease within a community. The model tracks how the infection propagates over time as individuals move within a defined area, allowing insights into spatial and temporal patterns of disease spread.

## Problem Description

Large-scale epidemic simulations are essential for public health planning and forecasting. However, realistic models require immense computing power, often utilizing parallel, cloud-based distributed solutions. In this project, we implement a simplified version of such a model.

### Model Overview

- **Community Layout**: 
  - Individuals inhabit a rectangular area, with each person’s location defined by discrete coordinates (x, y).
  - Coordinates represent small areas where contagion can occur if two individuals are at the same location simultaneously.

- **Movement and Contagion**:
  - Each individual moves in a fixed direction (North, South, East, or West) and changes direction when reaching the area’s boundaries.
  - Movement follows a fixed amplitude, but individuals can only infect others when they share the exact coordinates at the same time.

- **Health States**:
  - Individuals can be:
    - **Infected**: Currently contagious.
    - **Immune**: Recently recovered, temporarily resistant to reinfection.
    - **Susceptible**: Not immune or infected, can catch the disease if exposed.
  - Infection and immunity periods are defined by constants:
    - `INFECTION_DURATION`: Duration for which an individual remains contagious.
    - `IMMUNE_DURATION`: Duration for which an individual is immune post-recovery.

### Simulation Process

1. **Initialize Simulation**:
   - Set the starting location and health status for each individual.

2. **Each Time Step**:
   - Update each individual’s location based on their movement direction and amplitude.
   - Calculate each individual’s future health status based on proximity to infected individuals and time-based recovery/immunity changes.
   - Increment the simulation time and update all individuals’ future statuses to their current status.

3. **Simulation Termination**:
   - The simulation runs for a specified duration, tracking infection spread across the community over time.

## Requirements

The simulation requires certain initial parameters and data to be provided through command line arguments and input files:

### Command Line Arguments
- `TOTAL_SIMULATION_TIME`: The total time for which the simulation will run.
- `InputFileName`: Name of the input file containing initial data.
- `ThreadNumber`: Number of threads to use for parallelization.

### Input File Structure
The input file must contain the following information:

1. **Simulation Area Size**:
   - `MAX_X_COORD` and `MAX_Y_COORD`: Define the boundaries of the rectangular simulation area.

2. **Community Data**:
   - `N`: Number of individuals in the area. For each individual, the following data should be specified on a new line:
     - `PersonID`: Unique identifier for the person.
     - Initial coordinates `x`, `y`: Must be within `0..MAX_X_COORD` and `0..MAX_Y_COORD`.
     - Initial status:
       - `infected=0` for initially infected persons (who are infected at time zero).
       - `susceptible=1` for persons who are not yet immune or infected.
     - Movement pattern:
       - `Direction`: One of `N=0`, `S=1`, `E=2`, `W=3` (North, South, East, or West).
       - `Amplitude`: Integer defining the movement distance per step, smaller than the area’s dimension in the movement direction.

## Algorithms Implementation

### Sequential and Parallel Simulation

The simulation includes both a sequential and parallel implementation. The parallel version uses **OPENMP** and distributes individuals across the specified number of threads. 2 parallel versions are included: one uses **openmp parallel for** (**OpenMP V1**) and the other one **manual explicit data partitioning** (**OpenMP V2**)

- **Synchronization**: To ensure accuracy, synchronization points are enforced between threads:
  - All individuals’ locations must be updated before computing infection statuses.
  - All individuals’ infection statuses must be updated before moving to the next time step.

### Expected Results

The final output for each individual will include:
- **Final coordinates**: (x, y)
- **Final status**: (infected, immune, or susceptible)
- **Infection counter**: The number of times the individual was infected during the simulation.

Output files follow the naming convention `f_serial_out.txt` `f_omp1_out.txt` and `f_omp2_out.txt` based on the input file name (`f.txt`). Only final results are saved; intermediate statuses at each simulation step are not saved.

### Verification and Modes

- **Result Verification**: An automatic method compares the sequential and parallel outputs to ensure both versions yield the same results.
- **Running Modes**: 
  - **DEBUG mode**: Prints the evolution of individuals at each generation for inspection.
  - **Normal mode**: Suppresses print statements, optimized for performance measurement.

### Performance Measurement

- **Runtime Measurement**: Serial and parallel runtimes are measured, excluding file I/O operations, to calculate the speedup achieved by parallelization.
- **Parameter Variations**:
  - Population sizes: 20K, 200k, 1M individuals.
  - Simulation durations: 100, 500 time units.
  - Thread counts for parallel execution: 2, 4
  - Policy: static/dynamic
  - Chunk Sizes: 100, "population_size / (2 * threads_num)" (formula which seems to optimally distribute chunks)

## Results
The hardware used for this experiment is following:
* HP ProDesk 600 G3 DM
* Intel® Core™ i5-6500T × 4 Cores
* 16.0 GiB RAM

### Execution Time
After computing the input data for 20K, 200K, 1M individuals across 100, 500 time units following results can be seen:

- Optimized Chunk size is considered **population_size / (2 * threads_num).**
- Note that policy and chunk sizes influence only **OpenMP V1**!

- **20,000 People**

![20K, 100](img/20K,100,static,100.png)
![20K, 500](img/20K,500,static,100.png)
![20K, 500, static, optimized](img/20K,500,static,optimized.png)
![20K, 500, dynamic, optimized](img/20K,500,dynamic,optimized.png)


- **200,000 People**

![200K, 100](img/200K,100,static,100.png)
![200K, 500](img/200K,500,static,100.png)
![200K, 500, static, optimized](img/200K,500,static,optimized.png)
![200K, 500, dynamic, optimized](img/200K,500,dynamic,optimized.png)

- **1,000,000 People**

![1M, 100](img/1M,100,static,100.png)
![1M, 500](img/1M,500,static,100.png)
![1M, 500, static, optimized](img/1M,500,static,optimized.png)
![1M, 500, dynamic, optimized](img/1M,500,dynamic,optimized.png)

### Speedup
- An average speedup can be considered:
    - **1.40** for 2 threads, OpenMP V1 (max **2.40** for 1M data set, min **1.15** for 20K)
    - **1.80** for 4 threads, OpenMP V1 (max **2.70** for 1M data set, min **0.93!** for 20K)
    - For OpenMP V2, speedup is sligtly bigger than OmpV1 (around 0.1-0.2) with maximum value being 0.3 for 1M data set.
    - Using optimized chunk size did give a slight boost to the performance but it's only around 0.03-0.05.
    - For static/dynamic policy, difference is similar (0.05) which does not really influence on small data sets.

### Observations
- After executing this experiment, we can conclude that for small datasets (e.g., 20K individuals), the overhead of thread management diminishes speedup, and sub-optimal performance (e.g., speedup < 1) was observed in some configurations. For larger datasets (e.g., 1M individuals), the parallel implementation achieves significant performance gains, with a maximum speedup of **~2.7** for 4 threads in the OpenMP V1 implementation. 
- OpenMP Version 2 (explicit data partitioning) consistently performed slightly better than OpenMP Version 1 (parallel for) across all datasets, achieving an additional speedup of **0.1-0.3** in most cases. This indicates the benefit of explicit partitioning in improving cache locality and workload balance. Optimized chunk size, calculated as **population_size / (2 * threads_num)**, provided marginally better performance (speedup improvement of **~0.03-0.05**) compared to a fixed chunk size of 100.
- Each of the parallelization versions provide the same results compared to the sequential algorithm.