/* File:    omp_trapezoids.c
Computes definite integral (or area under curve) using trapezoidal  rule.

Serial version and 3 parallel versions

*/

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <omp.h>

void Usage(char *prog_name);

double f(double x); /* Function we are integrating */

/**
 * Serial version
 * Interval [a,b], n subintervals(n trapezoids)
 */
double serial_integral(double a, double b, int n)
{
   double integral;
   double h = (b - a) / n;
   int i;
   integral = (f(a) + f(b)) / 2.0;
   for (i = 1; i <= n - 1; i++)
   {
      integral += f(a + i * h);
   }
   integral = integral * h;
   return integral;
}

/**
 * Parallel V1 - explicit data partitioning and critical section
 * Interval [a,b], n subintervals(n trapezoids)
 */
double parallel_integral_v1(double a, double b, int n, int thread_count)
{
   double global_result;

#pragma omp parallel num_threads(thread_count)
   {
      double h, x, my_result;
      double local_a, local_b;
      int i, local_n, rest;
      int my_rank = omp_get_thread_num();

      h = (b - a) / n;
      local_n = n / thread_count;

      if (my_rank == thread_count - 1)
         rest = n % thread_count;
      else
         rest = 0;

      local_a = a + my_rank * local_n * h;
      local_b = local_a + (local_n + rest) * h;

      my_result = (f(local_a) + f(local_b)) / 2.0;
      for (i = 1; i <= local_n + rest - 1; i++)
      {
         x = local_a + i * h;
         my_result += f(x);
      }
      my_result = my_result * h;

#pragma omp critical
      global_result += my_result;
   }

   return global_result;
}

/**
 * Parallel V2 - explicit data partitioning and reduction
 * Interval [a,b], n subintervals(n trapezoids)
 */
double parallel_integral_v2(double a, double b, int n, int thread_count)
{
   double result;

#pragma omp parallel num_threads(thread_count) reduction(+ : result)
   {
      double h, x;
      double local_a, local_b;
      int i, local_n, rest;
      int my_rank = omp_get_thread_num();

      h = (b - a) / n;
      local_n = n / thread_count;
      if (my_rank == thread_count - 1)
         rest = n % thread_count;
      else
         rest = 0;

      local_a = a + my_rank * local_n * h;
      local_b = local_a + (local_n + rest) * h;
      result = (f(local_a) + f(local_b)) / 2.0;
      for (i = 1; i <= local_n + rest - 1; i++)
      {
         x = local_a + i * h;
         result += f(x);
      }
      result = result * h;
   }
   return result;
}

/**
 * Parallel V3 - loop parallelism
 * Interval [a,b], n subintervals(n trapezoids)
 */
double parallel_integral_v3(double a, double b, int n, int thread_count)
{
   double integral;
   double h = (b - a) / n;
   int i;
   integral = (f(a) + f(b)) / 2.0;

#pragma omp parallel for num_threads(thread_count) reduction(+ : integral)
   for (i = 1; i <= n - 1; i++)
   {
      integral += f(a + i * h);
   }
   integral = integral * h;
   return integral;
}

int main(int argc, char *argv[])
{
   double result = 0.0; /* Result is the approximation of the integral */
   double a, b;         /* Left and right endpoints      */
   int n;               /* Total number of trapezoids    */
   int thread_count;    /* Number of threads */
   double start, duration_s, duration;

   thread_count = 8; // number of threads
   a = 15;
   b = 90;       // interval
   n = 100000000; // number of trapezoids

   printf("Start numerical integration with n=%d trapezoids \n", n);
   printf("Parallel versions use %d threads\n", thread_count);

   start = omp_get_wtime();
   result = serial_integral(a, b, n);
   duration_s = omp_get_wtime() - start;
   printf("Serial vers: %.10e time=%f\n", result, duration_s);

   start = omp_get_wtime();
   result = parallel_integral_v1(a, b, n, thread_count);
   duration = omp_get_wtime() - start;
   printf("Parallel v1: %.10e time=%f speedup=%f \n", result, duration, duration_s/duration);

   start = omp_get_wtime();
   result = parallel_integral_v2(a, b, n, thread_count);
   duration = omp_get_wtime() - start;
   printf("Parallel v2: %.10e time=%f speedup=%f\n", result, duration, duration_s/duration);

   start = omp_get_wtime();
   result = parallel_integral_v3(a, b, n, thread_count);
   duration = omp_get_wtime() - start;
   printf("Parallel v3: %.10e time=%f speedup=%f \n", result, duration, duration_s/duration);

   return 0;
}

/**
 * function to compute integral
 */
double f(double x)
{
   double return_val;

   return_val = x * x;
   return return_val;
}
