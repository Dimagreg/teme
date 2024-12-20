#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

//4. Sa se scrie un program care primeste ca si argumente doua numere ce reprezinta captele unui interval A si B si un alt treilea numar N ce va reprezinta un numar de thread-uri pe care programul le va crea. Programul va imparti intervalul [A, B] in N intervale egale. Apoi programul va crea N threaduri. Fiecare thread va procesa cate un subinterval din intervalul [A,B] si va identifica numere prime din subinerval. In momentul in care un thread va identifica cate un numar prim il va printa la iesirea standard

typedef struct interval
{
    int start;
    int end;
    int tid;
} INTERVAL;

void *Pthreads_calc(void *arg)
{
    INTERVAL interval = *(INTERVAL*) arg;

    for (int i = interval.start; i <= interval.end; i++)
    {
        int potential_prime = i;

        int j;
        for (j = 2; j < potential_prime / 2; j++)
        {
            if (potential_prime % j == 0) // not prime
                break;
        }

        if (j == (potential_prime / 2))
            printf("%d, %d, %d, %d\n", potential_prime, interval.tid, interval.start, interval.end);
    }

    return NULL;
}

int main(int argc, char* argv[])
{
    if (argc != 4)
    {
        printf("incorrect amount of arguments\n");
        exit(1);
    }

    int A = atoi(argv[1]);
    int B = atoi(argv[2]);
    int thread_count = atoi(argv[3]);

    printf("A = %d\n", A);
    printf("B = %d\n", B);
    printf("thread_count = %d\n", thread_count);

    INTERVAL interval[thread_count];

    int step = (B - A + 1) / thread_count;

    printf("step = %d\n", step);

    for (int i = 0; i < thread_count; i++)
    {
        interval[i].start = A + i * step;
        interval[i].end = A + (i + 1) * step - 1;
        interval[i].tid = i;

        if ((i + 1) == thread_count)
        {
            interval[i].end = B;
        }
    
        printf("interval[%d].start = %d\n", i, interval[i].start);
        printf("interval[%d].end = %d\n", i, interval[i].end);
    }

    pthread_t *thread_handles;

    if ((thread_handles = malloc(thread_count * sizeof(pthread_t))) == NULL)
    {
        perror("malloc");
        exit(1);
    }

    for (int i = 0; i < thread_count; i++)
    {
        pthread_create(&thread_handles[i], NULL,
                     Pthreads_calc, &interval[i]);
    }
        
    for (int i = 0; i < thread_count; i++)
        pthread_join(thread_handles[i], NULL);

    free(thread_handles);
}