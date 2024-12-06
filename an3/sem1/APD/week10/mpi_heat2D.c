/****************************************************************************
 * FILE: mpi_heat2D.c
 * 
 * adaptation of example from https://hpc-tutorials.llnl.gov/mpi/examples/mpi_heat2D.c
 * 
 * DESCRIPTIONS:  
 *   HEAT2D Example - Parallelized C Version
 *   This example is based on a simplified two-dimensional heat 
 *   equation domain decomposition.  The initial temperature is computed to be 
 *   high in the middle of the domain and zero at the boundaries.  The 
 *   boundaries are held at zero throughout the simulation.  During the 
 *   time-stepping, an array containing two domains is used; these domains 
 *   alternate between old data and new data.
 *
 *   In this parallelized version, the grid is decomposed by the master
 *   process and then distributed by rows to the worker processes.  At each 
 *   time step, worker processes must exchange border data with neighbors, 
 *   because a grid point's current temperature depends upon it's previous
 *   time step value plus the values of the neighboring grid points.  Upon
 *   completion of all time steps, the worker processes return their results
 *   to the master process.
 *
 *   Two data files are produced: an initial data set and a final data set.
 * AUTHOR: Blaise Barney - adapted from D. Turner's serial C version. Converted
 *   to MPI: George L. Gusciora (1/95)
 * LAST REVISED: 06/12/13 Blaise Barney
 ****************************************************************************/
#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define NXPROB      500                /* x dimension of problem grid */
#define NYPROB      500                /* y dimension of problem grid */
#define STEPS       300                /* number of time steps */
#define MAXWORKER   8                  /* maximum number of worker tasks */
#define MINWORKER   3                  /* minimum number of worker tasks */
#define BEGIN       1                  /* message tag */
#define LTAG        2                  /* message tag */
#define RTAG        3                  /* message tag */
#define NONE        0                  /* indicates no neighbor */
#define DONE        4                  /* message tag */
#define MASTER      0                  /* taskid of first process */

struct Parms { 
  float cx;
  float cy;
} parms = {0.1, 0.1};

void inidat(), prtdat(), update();

int main(int argc, char *argv[]) {
    float u[2][NXPROB][NYPROB]; /* array for grid */
    int taskid, numtasks, numworkers;
    int averow, extra, offset;
    int i, it;

    MPI_Status status;
    MPI_Request reqs[4]; /* Requests for non-blocking communication */
    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
    MPI_Comm_rank(MPI_COMM_WORLD, &taskid);

    numworkers = numtasks - 1;
    if (taskid == MASTER) {
        /* Initialize grid */
        printf("Grid size: X= %d  Y= %d  Time steps= %d\n", NXPROB, NYPROB, STEPS);
        printf("Initializing grid and writing initial.dat file...\n");
        inidat(NXPROB, NYPROB, u[0]);
        prtdat(NXPROB, NYPROB, u[0], "initial.dat");
    }

    /* Determine row distribution */
    averow = (NXPROB - 2) / numtasks;
    extra = (NXPROB - 2) % numtasks;

    int sendcounts[numtasks];
    int displs[numtasks];
    offset = 1;

    for (i = 0; i < numtasks; i++) {
        int rows = (i < extra) ? averow + 1 : averow;
        sendcounts[i] = rows * NYPROB;
        displs[i] = offset * NYPROB;
        offset += rows;
    }

    /* Scatter data to all processes */
    float recvbuf[(averow + 1) * NYPROB];
    MPI_Scatterv(&u[0][0][0], sendcounts, displs, MPI_FLOAT,
                 recvbuf, (averow + 1) * NYPROB, MPI_FLOAT,
                 MASTER, MPI_COMM_WORLD);

    /* Compute row start and end for this process */
    int rows = sendcounts[taskid] / NYPROB;
    int start = 1;
    int end = rows;

    /* Allocate space for temporary computation */
    float local_u[2][rows + 2][NYPROB];
    memset(local_u, 0, sizeof(local_u));
    memcpy(&local_u[0][1][0], recvbuf, rows * NYPROB * sizeof(float));

    for (it = 1; it <= STEPS; it++) {
        /* Non-blocking communication for boundary exchange */
        if (taskid > 0) {
            MPI_Isend(&local_u[0][1][0], NYPROB, MPI_FLOAT, taskid - 1, 0, MPI_COMM_WORLD, &reqs[0]);
            MPI_Irecv(&local_u[0][0][0], NYPROB, MPI_FLOAT, taskid - 1, 0, MPI_COMM_WORLD, &reqs[1]);
        }
        if (taskid < numtasks - 1) {
            MPI_Isend(&local_u[0][rows][0], NYPROB, MPI_FLOAT, taskid + 1, 0, MPI_COMM_WORLD, &reqs[2]);
            MPI_Irecv(&local_u[0][rows + 1][0], NYPROB, MPI_FLOAT, taskid + 1, 0, MPI_COMM_WORLD, &reqs[3]);
        }

        /* Perform computation on inner rows while waiting for communication */
        update(start + 1, end - 1, NYPROB, local_u[0], local_u[1]);

        /* Wait for communication to complete */
        if (taskid > 0) {
            MPI_Wait(&reqs[0], &status);
            MPI_Wait(&reqs[1], &status);
        }
        if (taskid < numtasks - 1) {
            MPI_Wait(&reqs[2], &status);
            MPI_Wait(&reqs[3], &status);
        }

        /* Perform computation on boundary rows */
        if (taskid > 0) {
            update(start, start, NYPROB, local_u[0], local_u[1]);
        }
        if (taskid < numtasks - 1) {
            update(end, end, NYPROB, local_u[0], local_u[1]);
        }

        /* Swap arrays */
        memcpy(local_u[0], local_u[1], (rows + 2) * NYPROB * sizeof(float));
    }

    /* Gather updated data back to the master process */
    MPI_Gatherv(&local_u[0][1][0], rows * NYPROB, MPI_FLOAT,
                &u[0][0][0], sendcounts, displs, MPI_FLOAT,
                MASTER, MPI_COMM_WORLD);

    if (taskid == MASTER) {
        /* Write final output */
        printf("Writing final.dat file ...\n");
        prtdat(NXPROB, NYPROB, u[0], "final.dat");
    }

    MPI_Finalize();
    return 0;
}


/**************************************************************************
 *  subroutine update
 ****************************************************************************/
void update(int start, int end, int ny, float u1[][NYPROB], float u2[][NYPROB])
{
    int ix, iy;
    for (ix = start; ix <= end; ix++) 
        for (iy = 1; iy <= ny-2; iy++) 
            u2[ix][iy] = u1[ix][iy]  + 
                         parms.cx * (u1[ix+1][iy] +
                                     u1[ix-1][iy] - 
                                     2.0 * u1[ix][iy]) +
                         parms.cy * (u1[ix][iy+1] +
                                     u1[ix][iy-1] - 
                                     2.0 * u1[ix][iy]);
}

/**************************************************************************
 *  subroutine inidat
 ****************************************************************************/
void inidat(int nx, int ny, float u[][NYPROB])
{
    int ix, iy;
    for (ix = 0; ix <= nx-1; ix++) 
        for (iy = 0; iy <= ny-1; iy++) 
            u[ix][iy] = (float)(ix * (nx - ix - 1) * iy * (ny - iy - 1));
}

/**************************************************************************
 *  subroutine prtdat
 ****************************************************************************/
void prtdat(int nx, int ny, float u[][NYPROB], char *fnam)
{
    int ix, iy;
    FILE *fp;
    fp = fopen(fnam, "w");
    for (ix = 0; ix <= nx-1; ix++) {
        for (iy = 0; iy <= ny-1; iy++) {
            fprintf(fp, "%6.1f", u[ix][iy]);
            if (iy != ny-1) 
                fprintf(fp, " ");
            else
                fprintf(fp, "\n");
        }
    }
    fclose(fp);
}