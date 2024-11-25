/* File:       
 *    mpi_hello.c
 *
 * Purpose:    
 *    A "hello,world" program that uses MPI
 *
 * Compile:    
 *    mpicc -g -Wall -std=C99 -o mpi_hello mpi_hello.c
 * Usage:        
 *    mpiexec -n<number of processes> ./mpi_hello
 *
 * Input:      
 *    None
 * Output:     
 *    A greeting from each process
 *
 * Algorithm:  
 *    Each process sends a message to process 0, which prints 
 *    the messages it has received, as well as its own message.
 *
 * IPP2  Section 3.1 (pp. 90 and ff.)
 */
#include <stdio.h>
#include <string.h>  /* For strlen             */
#include "mpi.h"    /* For MPI functions, etc */ 

#define MAX_STRING   100

int main(void) {
   char       greeting[MAX_STRING];  /* String storing message */
   int        comm_sz;               /* Number of processes    */
   int        my_rank;               /* My process rank        */
   int a, b, sum;

   /* Start up MPI */
   MPI_Init(NULL, NULL); 

   /* Get the number of processes */
   MPI_Comm_size(MPI_COMM_WORLD, &comm_sz); 

   /* Get my rank among all the processes */
   MPI_Comm_rank(MPI_COMM_WORLD, &my_rank); 

   if (my_rank == 1) { 
      // Get message from process 0
      MPI_Recv(&a, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
      MPI_Recv(&b, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
      
      printf("a = %d from process %d\n", a, my_rank);
      printf("b = %d from process %d\n", b, my_rank);

      sum = a + b;

      MPI_Send(&sum, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
   } else {  
      a = 2;
      b = 4;

      /* Send message to process 1 */
      MPI_Send(&a, 1, MPI_INT, 1, 0, MPI_COMM_WORLD); 
      MPI_Send(&b, 1, MPI_INT, 1, 0, MPI_COMM_WORLD);

      MPI_Recv(&sum, 1, MPI_INT, 1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);

      printf("sum = %d from process %d\n", sum, my_rank);
   }

   /* Shut down MPI */
   MPI_Finalize(); 

   return 0;
}  /* main */
