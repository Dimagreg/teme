#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <limits.h> // for INT_MIN and INT_MAX

long TOTAL_SIMULATION_TIME;
char inputFileName[256];
long ThreadNumber;

int MAX_X_COORD;
int MAX_Y_COORD;
int PEOPLE_COUNT;

int ZONE[][]; // simulation zone where we compute if a person gets infected or not

struct person
{
    int personId;
    int x, y; // coords 
    int current_status; // (infected=0, susceptible = 1)
    int next_status; // status that will define current_status on next simulation
    int pattern_direction; // (N=0, S=1, E=2, W=3)
    int pattern_amplitude; // step amplitude, doesn't get infected if steps across the infected zone in one simulation
};

void readArguments(char* argv[]);
void readInputFile(struct person **st_person);
void processSimulationSequential(struct person **st_person);
void updateLocationSequential(struct person **st_person);
void computeNextStatusSequential(struct person **st_person);
void setCurrentStatusSequential(struct person **st_person);

int main(int argc, char* argv[])
{
    struct person *st_person = NULL;

    if (argc < 4)
    {
        printf("incorrect number of arguments\n");
        printf("use: ./app <TOTAL_SIMULATION_TIME> <inputFileName> <ThreadNumber>\n");
        exit(1);
    }

    readArguments(argv);

    readInputFile(&st_person);

    processSimulationSequential(&st_person);

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

    ThreadNumber = arg;

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

    if (fscanf(file, "%d", &PEOPLE_COUNT) != 1)
    {
        printf("error reading PEOPLE_COUNT\n");
        exit(1);
    }

    if ((*st_person = malloc(PEOPLE_COUNT * sizeof(struct person))) == NULL)
    {
        perror(NULL);
        exit(1);
    }

    for (int i = 0; i < PEOPLE_COUNT; i++)
    {
        if (fscanf(file, "%d %d %d %d %d %d", &(*st_person)[i].personId, &(*st_person)[i].x, &(*st_person)[i].y,
                &(*st_person)[i].current_status, &(*st_person)[i].pattern_direction, &(*st_person)[i].pattern_amplitude) != 6)
        {
            printf("error reading people. check if lines are formatted correctly.\n");
            exit(1);
        }
    }
}

void processSimulationSequential(struct person **st_person)
{
    // we consider on simulation time 0 that everyone "spawns" and no one gets infected by it's neighbour.
    // update location, compute next status, set current status and increment simulation time.

    for (int i = 0; i < TOTAL_SIMULATION_TIME; i++)
    {
        updateLocationSequential(st_person);

        computeNextStatusSequential(st_person);

        setCurrentStatusSequential(st_person);

        i++;
    }
}

void updateLocationSequential(struct person **st_person)
{
    for (int i = 0; i < PEOPLE_COUNT; i++)
    {
        computeLocation(&(*st_person)[i]);
    }
}

void computeLocation(struct person *person)
{
    switch (person->pattern_direction)
    {
    case 0: // N -> y++
        person->y += person->pattern_amplitude;
        break;
    case 1: // S -> y--
        person->y -= person->pattern_amplitude;
        break;
    case 2: // E -> x++
        person->x += person->pattern_amplitude;
        break;
    case 3: // W -> x--
        person->x -= person->pattern_amplitude;
        break;
    default:
        printf("invalid pattern direction\n");
        exit(1);
    }

    if (person->x < 0)
    {
        // x out of bounds because of W direction, switch to E
        person->x = 0;
        person->pattern_direction = 2;
    }
    else if (person->x > MAX_X_COORD)
    {
        // x out of bounds because of E direction, switch to W
        person->x = MAX_X_COORD;
        person->pattern_direction = 3;
    }

    if (person->y < 0)
    {
        // y out of bounds because of S direction, switch to N
        person->y = 0;
        person->pattern_direction = 0;
    }
    else if (person->y > MAX_Y_COORD)
    {
        // y out of bounds because of N direction, switch to S
        person->y = MAX_Y_COORD;
        person->pattern_direction = 1;
    }
}

void computeNextStatusSequential(struct person **st_person)
{
    
}
void setCurrentStatusSequential(struct person **st_person);