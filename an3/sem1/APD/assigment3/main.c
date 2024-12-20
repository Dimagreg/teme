// GRIGORI DMITRII - ASSIGMENT 3
// 3TI 3.1
// v1

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <limits.h> // for INT_MIN and INT_MAX
#include <time.h>
#include "mpi.h"

#define DEBUG 1
#define INFECTED_DURATION 15 // simulation time a person is infected ( > 1)
#define IMMUNE_DURATION 10 // simulation time a person is immune to being infected ( > 1)
#define MASTER 0 // MPI master node

int TOTAL_SIMULATION_TIME;
char inputFileName[256];

struct timespec start, finish;
double elapsed;

int MAX_X_COORD;
int MAX_Y_COORD;
int PEOPLE_COUNT;

int **ZONE; // 2D simulation zone where we compute if a person gets infected or not. (infected=1, safe=0)
            // In present case we consider that person gets infected if he is in the same coord as infected one.

struct person
{
    int personId;
    int x, y; // coords 
    int current_status; // (infected=0, susceptible = 1, immune = 2)
    int next_status; // status that will define current_status on next simulation. To be honest there is no reason to use this field in current case.
    int pattern_direction; // (N=0, S=1, E=2, W=3)
    int pattern_amplitude; // step amplitude, doesn't get infected if steps across the infected zone in one simulation
    int effect_time_left; // (infected or immune)
    int infected_times; // how many times a person got infected
};

struct threadArgs
{
    struct person **st_person;
    int start;
    int end;
};

int isResetZoneCalled = 0;

void readArguments(char *argv[]);
void readInputFile(struct person **st_person);
void setZone(struct person *person);
void initPersonEffect(struct person *person);
void processSimulationSequential(struct person **st_person);
void resetZone();
void resetZoneParallel(int rank);
void decrementEffectTime(struct person **st_person, int start, int end);
void updateLocation(struct person **st_person, int start, int end);
void computeLocation(struct person *person, int *local_zone);
void computeZoneSequential(struct person *person);
void computeZoneParallel(struct person *person, int rank);
void computeNextStatus(struct person **st_person, int start, int end);
void computePersonNextStatus(struct person *person, int *local_zone);
void processSimulationParallel(struct person **st_person, int rank, int size);
void printDebug(int simulation_time, struct person **st_person);
char *writeOutputFile(char *filename, char *postfix, struct person *st_person);
int compareFiles(char *file1, char *file2);

int main(int argc, char* argv[])
{
    struct person *st_person = NULL;
    double elapsed_serial;
    char *outputFile_serial;
    char *outputFile_parallel;
    int rank, size;

    // Initialize MPI
    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    // MASTER initializes
    if (rank == MASTER)
    {
        if (argc != 3)
        {
            printf("Incorrect number of arguments\n");
            printf("Usage: ./app <TOTAL_SIMULATION_TIME> <inputFileName>\n");
            MPI_Abort(MPI_COMM_WORLD, 1);
        }

        readArguments(argv);
        readInputFile(&st_person);

        printf("Simulation for %d people, Simulation duration: %d\n",
               PEOPLE_COUNT, TOTAL_SIMULATION_TIME);

        // Serial simulation for comparison
        clock_gettime(CLOCK_MONOTONIC, &start);
        processSimulationSequential(&st_person, rank);
        clock_gettime(CLOCK_MONOTONIC, &finish);

        elapsed = (finish.tv_sec - start.tv_sec);
        elapsed += (finish.tv_nsec - start.tv_nsec) / 1000000000.0;
        elapsed_serial = elapsed;

        printf("Elapsed time for Sequential simulation: %fs\n", elapsed_serial);

        outputFile_serial = writeOutputFile(argv[2], "_serial_out.txt", st_person);

        readInputFile(&st_person); // Reset data for parallel simulation
        clock_gettime(CLOCK_MONOTONIC, &start);
    }

    MPI_Barrier(MPI_COMM_WORLD);

    MPI_Bcast(&PEOPLE_COUNT, 1, MPI_INT, MASTER, MPI_COMM_WORLD);
    MPI_Bcast(&MAX_X_COORD, 1, MPI_INT, MASTER, MPI_COMM_WORLD);
    MPI_Bcast(&MAX_Y_COORD, 1, MPI_INT, MASTER, MPI_COMM_WORLD);
    MPI_Bcast(&(ZONE[0][0]), MAX_X_COORD * MAX_Y_COORD, MPI_INT, MASTER, MPI_COMM_WORLD);

    printf("rank=%d, PEOPLE_COUNT=%d, MAX_X_COORD=%d, MAX_Y_COORD=%d\n", rank, PEOPLE_COUNT, MAX_X_COORD, MAX_Y_COORD);

    MPI_Barrier(MPI_COMM_WORLD);

    // Allocate memory for st_person (if necessary) or ensure it matches MASTER's initialization
    if (rank != MASTER)
    {
        st_person = malloc(PEOPLE_COUNT * sizeof(struct person));
        if (!st_person)
        {
            perror("Memory allocation failed for st_person");
            MPI_Abort(MPI_COMM_WORLD, 1);
        }
    }

    MPI_Barrier(MPI_COMM_WORLD);  // Ensure that all processes are at the same point
    
    // Synchronize and broadcast st_person to all processes
    MPI_Bcast(st_person, PEOPLE_COUNT * sizeof(struct person), MPI_BYTE, MASTER, MPI_COMM_WORLD);

    // Ensure synchronization before starting parallel simulation
    MPI_Barrier(MPI_COMM_WORLD);
    printf("main barrier 0 rank=%d\n", rank);

    // Execute parallel simulation
    printf("processSimulationParallel rank=%d\n", rank);
    processSimulationParallel(&st_person, rank, size);

    // Synchronize before finalizing
    MPI_Barrier(MPI_COMM_WORLD);
    printf("main barrier 1 rank=%d\n", rank);

    // MASTER gathers results and prints them
    if (rank == MASTER)
    {
        clock_gettime(CLOCK_MONOTONIC, &finish);

        elapsed = (finish.tv_sec - start.tv_sec);
        elapsed += (finish.tv_nsec - start.tv_nsec) / 1000000000.0;

        printf("Elapsed time for MPI simulation: %fs Speedup: %f\n", elapsed, elapsed_serial / elapsed);

        outputFile_parallel = writeOutputFile(argv[2], "_mpi_out.txt", st_person);

        int filesEqual = compareFiles(outputFile_serial, outputFile_parallel);

        if (filesEqual)
        {
            printf("Files have the same content\n");
        }
        else
        {
            printf("Files do not have the same content\n");
        }

        free(st_person);

        for (int i = 0; i < MAX_X_COORD; i++)
        {
            free(ZONE[i]);
        }
        free(ZONE);
    }

    printf("rank=%d, MPI_Finalize\n", rank);

    // Final synchronization and clean-up
    MPI_Barrier(MPI_COMM_WORLD);
    MPI_Finalize();

    return 0;
}

void readArguments(char *argv[])
{
    char* p;
    long arg;

    printf("argv[0] = %s\n", argv[0]);
    printf("argv[1] = %s\n", argv[1]);
    printf("argv[2] = %s\n", argv[2]);

    printf("strerror: %s\n", strerror(errno));

    errno = 0;

    arg = strtol(argv[1], &p, 10);
    if (*p != '\0' || errno != 0) {
        printf("strtol error argv[1]: %s\n", strerror(errno));
        exit(1);
    }

    if (arg < INT_MIN || arg > INT_MAX) {
        printf("arguments int limits exceeded\n");
        exit(1);
    }

    TOTAL_SIMULATION_TIME = arg;

    if (strlen(argv[2]) > 256)
    {
        printf("inputfile too long\n");
        exit(1);
    }

    strcpy(inputFileName, argv[2]);
}

void readInputFile(struct person **st_person)
{
    FILE *file = fopen(inputFileName, "r");

    if (!file)
    {
        perror(NULL);
        exit(1);
    }

    if (fscanf(file, "%d %d", &MAX_X_COORD, &MAX_Y_COORD) != 2)
    {
        printf("error reading MAX_X_COORD, MAX_Y_COORD\n");
        exit(1);
    }

    // Allocate size for 2D array ZONE
    if ((ZONE = (int**) malloc(MAX_X_COORD * sizeof(int *))) == NULL)
    {
        perror("ZONE malloc 2D");
        exit(1);
    }
    for (int i = 0; i < MAX_X_COORD; i++)
    {
        if ((ZONE[i] = (int*) malloc(MAX_Y_COORD * sizeof(int))) == NULL)
        {
            perror("ZONE malloc 1D");
            exit(1);
        }
    }

    if (fscanf(file, "%d", &PEOPLE_COUNT) != 1)
    {
        printf("error reading PEOPLE_COUNT\n");
        exit(1);
    }

    if ((*st_person = (struct person*) malloc(PEOPLE_COUNT * sizeof(struct person))) == NULL)
    { 
        perror("st_person malloc");
        exit(1);
    }

    #ifdef DEBUG
        printf("MAX_X_COORD: %d, MAX_Y_COORD: %d, PEOPLE_COUNT: %d\n", MAX_X_COORD, MAX_Y_COORD, PEOPLE_COUNT);
    #endif

    // read people from file, init spawning ZONE and effect duration
    for (int i = 0; i < PEOPLE_COUNT; i++)
    {
        if (fscanf(file, "%d %d %d %d %d %d", &(*st_person)[i].personId, &(*st_person)[i].x, &(*st_person)[i].y,
                &(*st_person)[i].current_status, &(*st_person)[i].pattern_direction, &(*st_person)[i].pattern_amplitude) != 6)
        {
            printf("error reading people. check if lines are formatted correctly.\n");
            exit(1);
        }
        
        setZone(&(*st_person)[i]);

        initPersonEffect(&(*st_person)[i]);
    }

    fclose(file);
}

void setZone(struct person *person)
{
    // mark zone if infected person steps on it and it's not already infected
    if (person->current_status == 0 && ZONE[person->x][person->y] != 1)
    {
        ZONE[person->x][person->y] = 1; // infected zone is only set once per simulation
    }
}

void setZoneParallel(struct person *person, int *local_zone)
{
    if (person->current_status == 0)
    {
        local_zone[person->x * MAX_Y_COORD + person->y] = 1; // Mark as infected in the local copy
    }
}

void updateGlobalZone(int *local_zone)
{
    MPI_Reduce(local_zone, &(ZONE[0][0]), MAX_X_COORD * MAX_Y_COORD, MPI_INT, MPI_LOR, MASTER, MPI_COMM_WORLD);

    // Broadcast the updated ZONE back to all processes
    MPI_Bcast(&(ZONE[0][0]), MAX_X_COORD * MAX_Y_COORD, MPI_INT, MASTER, MPI_COMM_WORLD);
}

void initPersonEffect(struct person *person)
{
    switch (person->current_status)
    {
        case 0: 
            person->effect_time_left = INFECTED_DURATION;
            person->infected_times = 1;
            break;

        case 1: 
            person->effect_time_left = 0;
            person->infected_times = 0;
            break;

        case 2: 
            person->effect_time_left = IMMUNE_DURATION;
            person->infected_times = 0;
            break;

        default:
            printf("invalid pattern direction\n");
            exit(1);
    }
}

void processSimulationSequential(struct person **st_person, int rank)
{
    // Simulation for sequential processing of each person.

    // at spawn time 0, if the person spawns with infected -> give him infection. (c'est la vie)
    computeNextStatus(st_person, 0, PEOPLE_COUNT);

    for (int i = 1; i <= TOTAL_SIMULATION_TIME; i++)
    {   
        #ifdef DEBUG
            printf("simulation time %d\n", i);
        #endif

        // reset ZONE for each simulation time
        resetZone(rank);

        // update location of each person
        updateLocation(st_person, 0, PEOPLE_COUNT);

        // decrement effect time for each person
        decrementEffectTime(st_person, 0, PEOPLE_COUNT);

        // compute status for next simulation time
        computeNextStatus(st_person, 0, PEOPLE_COUNT);

        // setCurrentStatusSequential(st_person);

        #ifdef DEBUG
            printf("debug for sequential simulation\n");
            printDebug(i, st_person);
        #endif
    }
}

void printDebug(int simulation_time, struct person **st_person)
{
    printf("simulation_time: %d\n", simulation_time);

    for (int i = 0; i < PEOPLE_COUNT; i++)
    {
        printf("personId: %d, x: %d, y: %d, current_status: %d, effect_time_left: %d, infected_times: %d, direction: %d, amplitude: %d\n",
            (*st_person)[i].personId, (*st_person)[i].x, (*st_person)[i].y, (*st_person)[i].current_status, (*st_person)[i].effect_time_left, (*st_person)[i].infected_times, (*st_person)[i].pattern_direction, (*st_person)[i].pattern_amplitude);
    }
}      

void resetZone(int rank)
{
    // MASTER resets the zone
    if (rank == MASTER)
    {
        for (int i = 0; i < MAX_X_COORD; i++)
        {
            for (int j = 0; j < MAX_Y_COORD; j++)
            {
                ZONE[i][j] = 0;
            }
        }
    }

    // Broadcast the reset ZONE to all processes
    MPI_Bcast(&(ZONE[0][0]), MAX_X_COORD * MAX_Y_COORD, MPI_INT, MASTER, MPI_COMM_WORLD);
}

void decrementEffectTime(struct person **st_person, int start, int end)
{
    for (int i = start; i < end; i++)
    {
        if ((*st_person)[i].effect_time_left > 0)
        {
            (*st_person)[i].effect_time_left--;
        }
    }
}

void updateLocation(struct person **st_person, int start, int end)
{
    // Local zone array for this process
    int *local_zone = calloc(MAX_X_COORD * MAX_Y_COORD, sizeof(int));
    if (!local_zone)
    {
        perror("local_zone calloc");
        MPI_Abort(MPI_COMM_WORLD, 1);
    }

    for (int i = start; i < end; i++)
    {
        computeLocation(&(*st_person)[i], local_zone);
    }

    // Update the global ZONE after processing locations
    updateGlobalZone(local_zone);

    free(local_zone);
}

void computeLocation(struct person *person, int *local_zone)
{
    // ROWS are X, COLS are Y
    switch (person->pattern_direction)
    {
    case 0: // N -> x--
        person->x -= person->pattern_amplitude;
        break;

    case 1: // S -> x++
        person->x += person->pattern_amplitude;
        break;

    case 2: // E -> y++
        person->y += person->pattern_amplitude;
        break;

    case 3: // W -> y--
        person->y -= person->pattern_amplitude;
        break;

    default:
        printf("invalid pattern direction\n");
        exit(1);
    }

    // Handle boundary conditions
    if (person->x < 0)
    {
        person->x = abs(person->x);
        person->pattern_direction = 1;
    }
    else if (person->x >= MAX_X_COORD)
    {
        person->x = MAX_X_COORD - (person->x - (MAX_X_COORD - 1)) - 1;
        person->pattern_direction = 0;
    }

    if (person->y < 0)
    {
        person->y = abs(person->y);
        person->pattern_direction = 2;
    }
    else if (person->y >= MAX_Y_COORD)
    {
        person->y = MAX_Y_COORD - (person->y - (MAX_Y_COORD - 1)) - 1;
        person->pattern_direction = 3;
    }

    // Update the local zone array
    if (person->current_status == 0) // Mark as infected in local zone
    {
        local_zone[person->x * MAX_Y_COORD + person->y] = 1;
    }
}


void computeNextStatus(struct person **st_person, int start, int end)
{
     // Create a local copy of ZONE for this rank
    int *local_zone = calloc(MAX_X_COORD * MAX_Y_COORD, sizeof(int));
    if (!local_zone)
    {
        perror("local_zone calloc");
        MPI_Abort(MPI_COMM_WORLD, 1);
    }

    for (int i = start; i < end; i++)
    {
        computePersonNextStatus(&(*st_person)[i], local_zone);
    }

    // Update the global ZONE after processing all persons
    updateGlobalZone(local_zone);

    free(local_zone);
}

void computePersonNextStatus(struct person *person, int *local_zone)
{
    if (person->effect_time_left == 0 && person->current_status != 1)
    {
        switch (person->current_status)
        {
            case 0:
                person->current_status = 2; // Infection expired -> immune
                person->effect_time_left = IMMUNE_DURATION;
                break;

            case 2:
                person->current_status = 1; // Immunity expired -> susceptible
                person->effect_time_left = 0;
                break;
        }
    }

    if (local_zone[person->x * MAX_Y_COORD + person->y] == 1)
    {
        if (person->current_status == 1) // Susceptible -> infected
        {
            person->current_status = 0;
            person->effect_time_left = INFECTED_DURATION;
            person->infected_times++;
        }
    }

    setZoneParallel(person, local_zone); // Update the local zone based on the person's status
}


void resetZoneParallel(int rank, int size) {
    // Each rank initializes its part of the ZONE
    int rows_per_rank = MAX_X_COORD / size;
    int start_row = rank * rows_per_rank;
    int end_row = (rank == size - 1) ? MAX_X_COORD : (rank + 1) * rows_per_rank;

    for (int i = start_row; i < end_row; i++) {
        for (int j = 0; j < MAX_Y_COORD; j++) {
            ZONE[i][j] = 0;
        }
    }

    // Synchronize ZONE across all ranks
    MPI_Allreduce(MPI_IN_PLACE, &ZONE[0][0], MAX_X_COORD * MAX_Y_COORD, MPI_INT, MPI_SUM, MPI_COMM_WORLD);
}

void processSimulationParallel(struct person **st_person, int rank, int size)
{
    // Simulation for parallel processing of each person.
    // Using MPI Scatter and Gather

    printf("rank=%d, PEOPLE_COUNT=%d\n", rank, PEOPLE_COUNT);

    // Calculate data partitioning for each rank
    int start = rank * (PEOPLE_COUNT / size);
    int end = (rank == size - 1) ? PEOPLE_COUNT : (rank + 1) * (PEOPLE_COUNT / size);

    printf("rank=%d, start=%d, end=%d\n", rank, start, end);

    // Allocate memory for local data
    int local_count = end - start;
    printf("rank=%d, local_count=%d\n", rank, local_count);

    struct person *local_person = malloc(local_count * sizeof(struct person));

    if (!local_person)
    {
        perror("local_person malloc");
        MPI_Abort(MPI_COMM_WORLD, 1);
    }

    // Scatter data to ranks: we need to calculate displacements and counts based on start and end for each rank
    int *sendcounts = malloc(size * sizeof(int));
    int *displs = malloc(size * sizeof(int));

    for (int i = 0; i < size; i++) {
        int rank_start = i * (PEOPLE_COUNT / size);
        int rank_end = (i == size - 1) ? PEOPLE_COUNT : (i + 1) * (PEOPLE_COUNT / size);
        
        // Each rank will get the same chunk size except for the last rank
        sendcounts[i] = rank_end - rank_start;
        displs[i] = rank_start;

        printf("rank=%d, sendcounts[%d]=%d, displs[%d]=%d\n", rank, i, sendcounts[i], i, displs[i]);
    }

    // Synchronize all processes before the scatter operation
    MPI_Barrier(MPI_COMM_WORLD);

    // Scatter the data
    MPI_Scatterv(*st_person, sendcounts, displs, MPI_BYTE,
                local_person, local_count * sizeof(struct person), MPI_BYTE,
                MASTER, MPI_COMM_WORLD);

    // Ensure that the process finishes receiving before moving on
    MPI_Barrier(MPI_COMM_WORLD);

    free(sendcounts);
    free(displs);

    // Perform initial status computation (at spawn time 0)
    computeNextStatus(&local_person, 0, local_count);

    MPI_Barrier(MPI_COMM_WORLD);
    printf("processSimulationParallel barrier 0 rank=%d\n", rank);

    // Simulation loop
    for (int i = 1; i <= TOTAL_SIMULATION_TIME; i++)
    {
        resetZoneParallel(rank, size);

        // Synchronize ranks before processing
        MPI_Barrier(MPI_COMM_WORLD);
        printf("processSimulationParallel barrier 1 rank=%d\n", rank);

        // Update location of each person
        updateLocation(&local_person, 0, local_count);
        MPI_Barrier(MPI_COMM_WORLD);
        printf("processSimulationParallel barrier 2 rank=%d\n", rank);

        // Decrement effect time for each person
        decrementEffectTime(&local_person, 0, local_count);
        MPI_Barrier(MPI_COMM_WORLD);
        printf("processSimulationParallel barrier 3 rank=%d\n", rank);

        // Compute status for next simulation time
        computeNextStatus(&local_person, 0, local_count);
        MPI_Barrier(MPI_COMM_WORLD);
        printf("processSimulationParallel barrier 4 rank=%d\n", rank);

        if (rank == MASTER)
        {
            #ifdef DEBUG
                printf("Debug information for parallel simulation:\n");
                printDebug(i, &local_person);
            #endif
        }
    }

    // Gather results back to the master
    sendcounts = malloc(size * sizeof(int));
    displs = malloc(size * sizeof(int));

    for (int i = 0; i < size; i++) {
        int rank_start = i * (PEOPLE_COUNT / size);
        int rank_end = (i == size - 1) ? PEOPLE_COUNT : (i + 1) * (PEOPLE_COUNT / size);
        
        // Each rank will get the same chunk size except for the last rank
        sendcounts[i] = rank_end - rank_start;
        displs[i] = rank_start;

        printf("rank=%d, sendcounts[%d]=%d, displs[%d]=%d\n", rank, i, sendcounts[i], i, displs[i]);
    }

    MPI_Gather(local_person, local_count * sizeof(struct person), MPI_BYTE,
                *st_person, *sendcounts, MPI_BYTE,
                MASTER, MPI_COMM_WORLD);

    free(sendcounts);
    free(displs);
    free(local_person);
}

 
char *writeOutputFile(char *filename, char *postfix, struct person *st_person)
{
    char *original_name = strtok(filename, ".");

    int len = strlen(original_name) + strlen(postfix) + 1;

    char *finalNameOutput = malloc(len);

    if (!finalNameOutput)
    {
        perror(NULL);
        exit(1);
    }

    snprintf(finalNameOutput, len, "%s%s", original_name, postfix);

    FILE *file = fopen(finalNameOutput, "w");

    if (!file)
    {
        perror(NULL);
        exit(1);
    }

    for (int i = 0; i < PEOPLE_COUNT; i++)
    {
        fprintf(file, "%d %d %d %d %d\n", st_person[i].personId, st_person[i].x, st_person[i].y,
            st_person[i].current_status, st_person[i].infected_times);
    }

    fclose(file);

    return finalNameOutput;
}

int compareFiles(char *file1, char *file2)
{
    FILE *f1 = fopen(file1, "rb");
    FILE *f2 = fopen(file2, "rb");

    if (!f1 || !f2)
    {
        perror("Cannot open files for comparison");
        exit(1);
    }

    char buff1[256];
    char buff2[256];

    // read files line by line and compare them. Return 1 on identical files
    while (fscanf(f1, "%s", buff1) != EOF)
    {
        if (fscanf(f2, "%s", buff2) == EOF)
        {
            perror("EOF on f2");
            exit(1);
        }

        if (strcmp(buff1, buff2) != 0)
        {
            fclose(f1);
            fclose(f2);

            return 0;
        }
    }

    fclose(f1);
    fclose(f2);

    return 1;
}
