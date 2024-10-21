#include <stdio.h>
#include <stdlib.h>
#include <time.h>

// #define DEBUG

int ROWS = 10; // size of grid
int COLS = 10;
int generations = 3; // number of simulated generations

int *grid; // the grid and next generation grid
int *new_grid;

void print_grid(void)
{
    for (int i = 0; i < ROWS; i++)
    {
        for (int j = 0; j < COLS; j++)
        {
            printf("%c ", grid[i * COLS + j] ? 'X' : '.');
        }
        printf("\n");
    }
}

void init_grid(void)
{
    grid = (int *)malloc(ROWS * COLS * sizeof(int));
    if (!grid)
    {
        printf("Memory allocation error for grid\n");
        exit(1);
    }
    new_grid = (int *)malloc(ROWS * COLS * sizeof(int));
    if (!new_grid)
    {
        printf("Memory allocation error for new grid\n");
        exit(1);
    }

    srand(time(NULL));

    for (int i = 0; i < ROWS; i++)
        for (int j = 0; j < COLS; j++)
        {
            if (rand() % 2)
                grid[i * COLS + j] = 1;
            else
                grid[i * COLS + j] = 0;
        }
}

void swap_ptr(int **p1, int **p2) // used to swap grids between generations
{
    int *tmp = *p1;
    *p1 = *p2;
    *p2 = tmp;
}

int count_neighbors(int x, int y) // count alive neighbours of (x,y) in grid
{
    int count = 0;
    for (int dx = -1; dx <= 1; dx++)
        for (int dy = -1; dy <= 1; dy++)
        {
            if (dx != 0 && dy != 0)
            { // count only neighbours, not current cell
                int nx = x + dx, ny = y + dy;
                if (nx >= 0 && nx < ROWS && ny >= 0 && ny < COLS)
                {
                    if (grid[nx * COLS + ny] == 1)
                        count++;
                }
            }
        }

    return count;
}

void simulate_bacteria(void)
{
    for (int gen = 0; gen < generations; gen++)
    {
        // Apply the rules of the simulation
        for (int i = 0; i < ROWS; i++)
            for (int j = 0; j < COLS; j++)
            {
                new_grid[i * COLS + j] = 0; // by default no bacterium

                int neighbors = count_neighbors(i, j);

                if (grid[i * COLS + j] == 1)
                { // If current cell has a bacterium
                    if (neighbors == 2 || neighbors == 3)
                        new_grid[i * COLS + j] = 1; // Survives in next generation
                }
                else
                { // If current cell is empty
                    if (neighbors == 3)
                        new_grid[i * COLS + j] = 1; // New bacterium appears here
                }
            }
        // Make new grid to old grid for the next generation
        swap_ptr(&grid, &new_grid);
#ifdef DEBUG
        printf("After generation %d:\n", gen);
        print_grid();
#endif
    }
}

int main()
{
    init_grid();

    printf("Initial configuration:\n");
    print_grid();

    simulate_bacteria();

    printf("Final configuration after %d generations:\n", generations);
    print_grid();

    free(grid);
    free(new_grid);
    return 0;
}
