// GRIGORI DMITRII - ASSIGMENT 2
// 3TI 3.1
// v1

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <limits.h> // for INT_MIN and INT_MAX
#include <time.h>
#include <pthread.h>
#include <bits/pthreadtypes.h>
#include <omp.h>

// #define DEBUG 1
#define INFECTED_DURATION 15 // simulation time a person is infected ( > 1)
#define IMMUNE_DURATION 10 // simulation time a person is immune to being infected ( > 1)

#define POLICY dynamic
#define CHUNK_SIZE 100

int TOTAL_SIMULATION_TIME;
char inputFileName[256];
int Thread_count;

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
void resetZoneParallel();
void decrementEffectTime(struct person **st_person, int start, int end);
void updateLocation(struct person **st_person, int start, int end);
void computeLocation(struct person *person);
void computeZoneSequential(struct person *person);
void computeNextStatus(struct person **st_person, int start, int end);
void computePersonNextStatus(struct person *person);
void processSimulationParallel_V1(struct person **st_person);
void processSimulationParallel_V2(struct person **st_person);
void threadProcessSimulationParallel_V2(struct person **st_person, int start, int end);
void printDebug(int simulation_time, struct person **st_person);
char *writeOutputFile(char *filename, char *postfix, struct person *st_person);
int compareFiles(char *file1, char *file2, char *file3);

int main(int argc, char* argv[])
{
    struct person *st_person = NULL;

    if (argc < 4)
    {
        printf("incorrect number of arguments\n");
        printf("use: ./app <TOTAL_SIMULATION_TIME> <inputFileName> <Thread_count>\n");
        exit(1);
    }

    readArguments(argv);

    // Serial
    readInputFile(&st_person);

    printf("Simulation for %d people, Simulation duration: %d, Threads: %d, argv[0] = %s\n", PEOPLE_COUNT, TOTAL_SIMULATION_TIME, Thread_count, argv[0]);

    clock_gettime(CLOCK_MONOTONIC, &start); 
    processSimulationSequential(&st_person);
    clock_gettime(CLOCK_MONOTONIC, &finish);

    elapsed = (finish.tv_sec - start.tv_sec);
    elapsed += (finish.tv_nsec - start.tv_nsec) / 1000000000.0;
    
    double elapsed_serial = elapsed;

    printf("Elapsed time for Sequential simulation: %fs\n", elapsed_serial);

    char *outputFile_serial = writeOutputFile(argv[2], "_serial_out.txt", st_person);

    // Parallel V1
    readInputFile(&st_person);

    clock_gettime(CLOCK_MONOTONIC, &start);
    processSimulationParallel_V1(&st_person);
    clock_gettime(CLOCK_MONOTONIC, &finish);

    elapsed = (finish.tv_sec - start.tv_sec);
    elapsed += (finish.tv_nsec - start.tv_nsec) / 1000000000.0;

    printf("Elapsed time for Omp1 simulation: %fs Speedup: %f\n", elapsed, elapsed_serial / elapsed);

    char *outputFile_parallel_v1 = writeOutputFile(argv[2], "_omp1_out.txt", st_person);

    // Parallel V2
    readInputFile(&st_person);

    clock_gettime(CLOCK_MONOTONIC, &start);
    processSimulationParallel_V2(&st_person);
    clock_gettime(CLOCK_MONOTONIC, &finish);

    elapsed = (finish.tv_sec - start.tv_sec);
    elapsed += (finish.tv_nsec - start.tv_nsec) / 1000000000.0;

    printf("Elapsed time for Omp2 simulation: %fs Speedup: %f\n", elapsed, elapsed_serial / elapsed);

    char *outputFile_parallel_v2 = writeOutputFile(argv[2], "_omp2_out.txt", st_person);

    int filesEqual = compareFiles(outputFile_serial, outputFile_parallel_v1, outputFile_parallel_v2);

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

    return 0;
}

void readArguments(char *argv[])
{
    char* p;
    long arg;

    arg = strtol(argv[1], &p, 10);
    if (*p != '\0' || errno != 0) {
        printf("strtol error\n");
        exit(1);
    }

    if (arg < INT_MIN || arg > INT_MAX) {
        printf("arguments int limits exceeded\n");
        exit(1);
    }

    TOTAL_SIMULATION_TIME = arg;

    arg = strtol(argv[3], &p, 10);
    if (*p != '\0' || errno != 0) {
        printf("strtol error\n");
        exit(1);
    }

    if (arg < INT_MIN || arg > INT_MAX) {
        printf("arguments int limits exceeded\n");
        exit(1);
    }

    Thread_count = arg;

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

void processSimulationSequential(struct person **st_person)
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
        resetZone();

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

void resetZone()
{
    for (int i = 0; i < MAX_X_COORD; i++)
    {
        for (int j = 0; j < MAX_Y_COORD; j++)
        {
            ZONE[i][j] = 0;
        }
    }
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
    for (int i = start; i < end; i++)
    {
        computeLocation(&(*st_person)[i]);
    }
}
void computeLocation(struct person *person)
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

    if (person->x < 0)
    {
        // x out of bounds because of N direction, switch to S
        person->x = abs(person->x); // using abs if amplitude made him take multiple steps and get him back in bounds keeping steps
        person->pattern_direction = 1;
    }
    else if (person->x >= MAX_X_COORD)
    {
        // x out of bounds because of S direction, switch to N
        person->x = MAX_X_COORD - (person->x - (MAX_X_COORD - 1)) - 1;
        person->pattern_direction = 0;
    }

    if (person->y < 0)
    {
        // y out of bounds because of W direction, switch to E
        person->y = abs(person->y);
        person->pattern_direction = 2;
    }
    else if (person->y >= MAX_Y_COORD)
    {
        // y out of bounds because of E direction, switch to W
        person->y = MAX_Y_COORD - (person->y - (MAX_Y_COORD - 1)) - 1;
        person->pattern_direction = 3;
    }

    // mark current zone based on status
    setZone(person);
}


void computeNextStatus(struct person **st_person, int start, int end)
{
    for (int i = start; i < end; i++)
    {
        computePersonNextStatus(&(*st_person)[i]);
    }
}

void computePersonNextStatus(struct person *person)
{
    //compute if effects and person is not susceptible (no effects)
    if (person->effect_time_left == 0 && person->current_status != 1)
    {
        switch (person->current_status)
        {
            case 0: //infection expired -> make immune
                person->current_status = 2;
                person->effect_time_left = IMMUNE_DURATION;
                break;
            
            case 2: //immunity expired -> make susceptible
                person->current_status = 1;
                person->effect_time_left = 0;
                break;
        }
    }

    if (ZONE[person->x][person->y] == 1)
    {
        // person is susceptible -> set next status to infected
        if (person->current_status == 1)
        {
            person->current_status = 0;
            person->effect_time_left = INFECTED_DURATION;
            person->infected_times++;
        }
    }
    else
    {
        // noop
    }
}

void processSimulationParallel_V1(struct person **st_person)
{
    // Simulation for parallel processing of each person.
    // V1 - Parallel for with scheduling policy and chunksizes
    // This method will execute each action with omp parallel for.
    // It's surprisingly faster rather than sequential version tbh.

    #pragma omp parallel for num_threads(Thread_count) schedule(POLICY, CHUNK_SIZE)
    for (int i = 0; i < PEOPLE_COUNT; i++)
    {
        // at spawn time 0, if the person spawns with infected -> give him infection. (c'est la vie)
        computeNextStatus(st_person, i, i + 1);
    }

    for (int i = 1; i <= TOTAL_SIMULATION_TIME; i++)
    {   
        #ifdef DEBUG
                printf("simulation time %d\n", i);
        #endif

        // reset ZONE for each simulation time
        resetZone();

        // update location of each person
        #pragma omp parallel for num_threads(Thread_count) schedule(POLICY, CHUNK_SIZE)
        for (int i = 0; i < PEOPLE_COUNT; i++)
        {
            updateLocation(st_person, i, i + 1);
        }

        // decrement effect time for each person
        #pragma omp parallel for num_threads(Thread_count) schedule(POLICY, CHUNK_SIZE)
        for (int i = 0; i < PEOPLE_COUNT; i++)
        {
            decrementEffectTime(st_person, i, i + 1);
        }

        // compute status for next simulation time
        #pragma omp parallel for num_threads(Thread_count) schedule(POLICY, CHUNK_SIZE)
        for (int i = 0; i < PEOPLE_COUNT; i++)
        {
            computeNextStatus(st_person, i, i + 1);
        }

        #ifdef DEBUG
            printf("debug for parallel simulation\n");
            printDebug(i, st_person);
        #endif
    }
}

void processSimulationParallel_V2(struct person **st_person)
{
    // Simulation for parallel processing of each person. People are divided by number of threads.
    // V2 - Manual explicit data partitioning
    #pragma omp parallel num_threads(Thread_count)
    {
        // fixed bug where I exit program if I can't divide people by threads :)
        int thread_id = omp_get_thread_num();
        int start = thread_id * (PEOPLE_COUNT / Thread_count);
        int end = (thread_id == Thread_count - 1) ? PEOPLE_COUNT : (thread_id + 1) * (PEOPLE_COUNT / Thread_count); // gets the rest of people on last thread

        threadProcessSimulationParallel_V2(st_person, start, end);
    }
}

void threadProcessSimulationParallel_V2(struct person **st_person, int start, int end)
{
    // Simulation for parallel processing of each person.
    // at spawn time 0, if the person spawns with infected -> give him infection. (c'est la vie)
    computeNextStatus(st_person, start, end);

    #pragma omp barrier

    for (int i = 1; i <= TOTAL_SIMULATION_TIME; i++)
    {   
        #ifdef DEBUG
            #pragma omp single // make sure prints only one thread
                printf("simulation time %d\n", i);
        #endif

        // reset ZONE for each simulation time
        resetZone();
        #pragma omp barrier

        // update location of each person
        updateLocation(st_person, start, end);
        #pragma omp barrier

        // decrement effect time for each person
        decrementEffectTime(st_person, start, end);
        #pragma omp barrier

        // compute status for next simulation time
        computeNextStatus(st_person, start, end);
        #pragma omp barrier

        #ifdef DEBUG
            #pragma omp single
            {
                printf("debug for parallel simulation\n");
                printDebug(i, st_person);
            }
        #endif

        // set flag to reset ZONE for next simulation time
        #pragma omp single
        {
            isResetZoneCalled = 0;
        }
    }
}

void resetZoneParallel()
{
    // make sure it is called by one thread only per simulation time
    #pragma omp single
    {
        if (!isResetZoneCalled)
        {
            resetZone();
            isResetZoneCalled = 1;
        }
    }
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

int compareFiles(char *file1, char *file2, char *file3)
{
    FILE *f1 = fopen(file1, "rb");
    FILE *f2 = fopen(file2, "rb");
    FILE *f3 = fopen(file3, "rb");

    if (!f1 || !f2 || !f3)
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
            fclose(f3);

            return 0;
        }
    }

    // if not returned 0, f1 equals to f2 => Compare f1 to f3
    while (fscanf(f1, "%s", buff1) != EOF)
    {
        if (fscanf(f3, "%s", buff2) == EOF)
        {
            perror("EOF on f3");
            exit(1);
        }

        if (strcmp(buff1, buff2) != 0)
        {
            fclose(f1);
            fclose(f2);
            fclose(f3);

            return 0;
        }
    }

    fclose(f1);
    fclose(f2);
    fclose(f3);

    return 1;
}
