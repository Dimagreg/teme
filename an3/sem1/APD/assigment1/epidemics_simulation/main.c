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
    for (int i = 0; i < TOTAL_SIMULATION_TIME; i++)
    {
        
    }
}