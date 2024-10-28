// GRIGORI DMITRII
// 3TI 3.1

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <limits.h> // for INT_MIN and INT_MAX
#include <time.h>
#include <pthread.h>
#include <bits/pthreadtypes.h>

// #define DEBUG 1
#define INFECTED_DURATION 15 // simulation time a person is infected ( > 1)
#define IMMUNE_DURATION 10 // simulation time a person is immune to being infected ( > 1)

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

// Barrier for parallel simulation
pthread_barrier_t barrier;

// Mutex and condition variable for ZONE reset
pthread_mutex_t resetZoneMutex = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t resetZoneCond = PTHREAD_COND_INITIALIZER;
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
void processSimulationParallel(struct person **st_person);
void *threadProcessSimulationParallel(void *args);
void printDebug(int simulation_time, struct person **st_person);
char *writeOutputFile(char *filename, char *postfix, struct person *st_person);
void compareFiles(char *file1, char *file2);

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

    readInputFile(&st_person);

    // avoid threads conflict with diving the people
    if (PEOPLE_COUNT % Thread_count != 0)
    {
        printf("PEOPLE_COUNT must be divisible by Thread_count\n");
        exit(1);
    }

    clock_gettime(CLOCK_MONOTONIC, &start); 
    processSimulationSequential(&st_person);
    clock_gettime(CLOCK_MONOTONIC, &finish);

    elapsed = (finish.tv_sec - start.tv_sec);
    elapsed += (finish.tv_nsec - start.tv_nsec) / 1000000000.0;

    printf("Elapsed time for Sequential simulation: %fs\n", elapsed);

    char *outputFile_serial = writeOutputFile(argv[2], "_serial_out.txt", st_person);

    readInputFile(&st_person);

    clock_gettime(CLOCK_MONOTONIC, &start);
    processSimulationParallel(&st_person);
    clock_gettime(CLOCK_MONOTONIC, &finish);

    elapsed = (finish.tv_sec - start.tv_sec);
    elapsed += (finish.tv_nsec - start.tv_nsec) / 1000000000.0;

    printf("Elapsed time for Parallel simulation: %fs\n", elapsed);

    char *outputFile_parallel = writeOutputFile(argv[2], "_parallel_out.txt", st_person);

    compareFiles(outputFile_serial, outputFile_parallel);

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
        ZONE[person->x][person->y] = 1;
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

void processSimulationParallel(struct person **st_person)
{
    // Simulation for parallel processing of each person. People are divided by number of threads.
    pthread_t threads[Thread_count];

    pthread_barrier_init(&barrier, NULL, Thread_count);
    pthread_mutex_init(&resetZoneMutex, NULL);
    pthread_cond_init(&resetZoneCond, NULL);

     struct threadArgs threadData[Thread_count];

    for (int i = 0; i < Thread_count; i++)
    {
        threadData[i].st_person = st_person;
        threadData[i].start = i * (PEOPLE_COUNT / Thread_count);
        threadData[i].end = (i + 1) * (PEOPLE_COUNT / Thread_count);

        pthread_create(&threads[i], NULL, threadProcessSimulationParallel, (void *)&threadData[i]);
    }

    for (int i = 0; i < Thread_count; i++)
    {
        pthread_join(threads[i], NULL);
    }

    pthread_mutex_destroy(&resetZoneMutex);
    pthread_cond_destroy(&resetZoneCond);
    pthread_barrier_destroy(&barrier);
}

void *threadProcessSimulationParallel(void *args)
{
    // Simulation for parallel processing of each person.
    struct threadArgs *threadArgs = (struct threadArgs *)args;

    struct person **st_person = threadArgs->st_person;
    int start = threadArgs->start;
    int end = threadArgs->end;

    // at spawn time 0, if the person spawns with infected -> give him infection. (c'est la vie)
    computeNextStatus(st_person, start, end);
    pthread_barrier_wait(&barrier);

    for (int i = 1; i <= TOTAL_SIMULATION_TIME; i++)
    {   
        #ifdef DEBUG
            printf("simulation time %d\n", i);
        #endif

        // reset ZONE for each simulation time
        resetZoneParallel();
        pthread_barrier_wait(&barrier);

        // update location of each person
        updateLocation(st_person, start, end);
        pthread_barrier_wait(&barrier);

        // decrement effect time for each person
        decrementEffectTime(st_person, start, end);
        pthread_barrier_wait(&barrier);

        // compute status for next simulation time
        computeNextStatus(st_person, start, end);
        pthread_barrier_wait(&barrier);

        // setCurrentStatusSequential(st_person);

        #ifdef DEBUG
            printf("debug for parallel simulation\n");
            printDebug(i, st_person);
        #endif

        // set flag to reset ZONE for next simulation time
        pthread_mutex_lock(&resetZoneMutex);
        isResetZoneCalled = 0;
        pthread_mutex_unlock(&resetZoneMutex);
    }

    pthread_exit(NULL);
}

void resetZoneParallel()
{
    // make sure it is called by one thread only per simulation time
    pthread_mutex_lock(&resetZoneMutex);

    if (!isResetZoneCalled)
    {
        resetZone();

        isResetZoneCalled = 1;

        pthread_cond_broadcast(&resetZoneCond);
    }
    else
    {
        while (!isResetZoneCalled)
        {
            pthread_cond_wait(&resetZoneCond, &resetZoneMutex);
        }
    }

    pthread_mutex_unlock(&resetZoneMutex);
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

void compareFiles(char *file1, char *file2)
{
    FILE *f1 = fopen(file1, "rb");
    FILE *f2 = fopen(file2, "rb");

    if (!f1 || !f2)
    {
        perror("Cannot open files for comparison");
        exit(1);
    }

    fseek(f1, 0, SEEK_END);
    fseek(f2, 0, SEEK_END);

    long size1 = ftell(f1);
    long size2 = ftell(f2);
    
    fclose(f1);
    fclose(f2);

    if (size1 != size2)
    {
        printf("Serial and Parallel results are not same\n");
    }
    else
    {
        printf("Serial and Parallel results are same\n");
    }
}
