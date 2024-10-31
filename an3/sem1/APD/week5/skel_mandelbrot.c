/**
 * Compute the Area of the Mandelbrot Set
 *
 */
// SERIAL: 5.095072 

// PARALLEL:
// POLICY static, CHUNK_SIZE 1: 1.419553 
// POLICY dynamic, CHUNK_SIZE 1: 1.331046  
// POLICY static, CHUNK_SIZE 20: 1.351624 
// POLICY dynamic, CHUNK_SIZE 20: 1.307675 best!

// same error, same area for parallel and serial.


#include <stdio.h>
#include <omp.h>

#define NPOINTS 1600
#define MAXITER 1000
#define THREADS 4
#define POLICY dynamic
#define CHUNK_SIZE 20

struct complex
{
    double r;
    double i;
};

void compute_serial(double *area, double *error)
{
    int numinside = 0, numoutside = 0;
    for (int i = 0; i < NPOINTS; i++)
        for (int j = 0; j < NPOINTS; j++)
        {
            // generate grid of points C in the rectangle
            // C.r in [-2  .. 0.5]
            // C.i in [0 .. 1.125 ] - will be taken also symmetric Ox
            struct complex c;
            c.r = -2.0 + 2.5 * (double)(i) / (double)(NPOINTS);
            c.i = 1.125 * (double)(j) / (double)(NPOINTS);
            struct complex z;
            z = c; // start computing series z for c
            for (int iter = 0; iter < MAXITER; iter++)
            {
                double temp = (z.r * z.r) - (z.i * z.i) + c.r;
                z.i = z.r * z.i * 2 + c.i;
                z.r = temp;
                if ((z.r * z.r + z.i * z.i) > 4.0)
                { // z diverges
                    numoutside++;
                    break;
                }
            }
        }
    numinside = NPOINTS * NPOINTS - numoutside;
    *area = 2.0 * 2.5 * 1.125 * (double)(numinside) / (double)(NPOINTS * NPOINTS);
    *error = *area / (double)NPOINTS;
}

void compute_parallel(double *area, double *error)
{
    int numinside = 0, numoutside = 0;

    #pragma omp parallel for num_threads(THREADS) schedule(POLICY, CHUNK_SIZE) reduction(+:numoutside)
    for (int i = 0; i < NPOINTS; i++)
        for (int j = 0; j < NPOINTS; j++)
        {
            // generate grid of points C in the rectangle
            // C.r in [-2  .. 0.5]
            // C.i in [0 .. 1.125 ] - will be taken also symmetric Ox
            struct complex c;
            c.r = -2.0 + 2.5 * (double)(i) / (double)(NPOINTS);
            c.i = 1.125 * (double)(j) / (double)(NPOINTS);
            struct complex z;
            z = c; // start computing series z for c
            for (int iter = 0; iter < MAXITER; iter++)
            {
                double temp = (z.r * z.r) - (z.i * z.i) + c.r;
                z.i = z.r * z.i * 2 + c.i;
                z.r = temp;
                if ((z.r * z.r + z.i * z.i) > 4.0)
                { // z diverges
                    numoutside++;
                    break;
                }
            }
        }
    numinside = NPOINTS * NPOINTS - numoutside;
    *area = 2.0 * 2.5 * 1.125 * (double)(numinside) / (double)(NPOINTS * NPOINTS);
    *error = *area / (double)NPOINTS;
}

int main()
{
    double area, error;
    double start, time;

    printf("Serial version:...");
    start = omp_get_wtime();
    compute_serial(&area, &error);
    time = omp_get_wtime() - start;
    printf("Serial :  area=%f  error=%f   time=%f \n", area, error, time);

    area = 0; error = 0;

    printf("Parallel version:...");
    start = omp_get_wtime();
    compute_parallel(&area, &error);
    time = omp_get_wtime() - start;
    printf("Parallel :  area=%f  error=%f   time=%f \n", area, error, time);
}
