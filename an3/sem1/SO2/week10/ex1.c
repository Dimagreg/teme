#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>

// ./prog input_file

typedef struct 
{
    u_int32_t *buff;
    u_int32_t size;
} Data;

#define N 20

u_int32_t checksum_calculate = 0;

pthread_mutex_t my_mutex = PTHREAD_MUTEX_INITIALIZER;

void *Pthreads_calc(void *arg)
{
    Data D = *(Data *) arg;

    for (int i = 0; i < D.size; i++)
    {
        pthread_mutex_lock(&my_mutex);

        checksum_calculate += D.buff[i];

        pthread_mutex_unlock(&my_mutex);
    }

    return NULL;
}

int main(int argc, char* argv[])
{
    Data D[N];

    if (argc != 2)
    {
        printf("incorrect amount of arguments\n");
        exit(1);
    }

    int fd = 0;

    if ((fd = open(argv[1], O_RDONLY)) < 0)
    {
        perror("open");
        exit(1);
    }

    struct stat st_stat;

    if (stat(argv[1], &st_stat) < 0)
    {
        perror("stat");
        exit(1);
    }

    int file_size = st_stat.st_size;

    printf("file size: %d\n", file_size);

    int buff_size = (file_size - 4) / (N * sizeof(u_int32_t));

    printf("buff size: %d\n", buff_size);

    for (int i = 0; i < N; i++)
    {
        if ((D[i].buff = (u_int32_t *) malloc(buff_size * sizeof(u_int32_t))) == NULL)
        {
            perror("malloc D");
            exit(1);
        }

        D[i].size = 0;
    }

    int bytes_read;
    u_int32_t buff;
    int counter = 0;
    int i = 0;
    u_int32_t checksum = 0;

    while ((bytes_read = read(fd, &buff, sizeof(u_int32_t))) > 0)
    {
        if (counter == (buff_size * N))
        {
            checksum = buff;
            break; //reached checksum
        }

        if (counter == (buff_size * (i + 1)))
        {
            i++;
        }

        D[i].buff[D[i].size] = buff;
        D[i].size++;

        counter++;
    }

    printf("checksum = %u\n", checksum);
    printf("counter = %d\n", counter);
    printf("i = %d\n", i);  
    printf("D[0].size = %d\n", D[0].size);

    pthread_t *thread_handles;

    if ((thread_handles = malloc(N * sizeof(pthread_t))) == NULL)
    {
        perror("malloc");
        exit(1);
    }

    pthread_mutex_init(&my_mutex, NULL);

    for (int i = 0; i < N; i++)
    {
        pthread_create(&thread_handles[i], NULL,
                     Pthreads_calc, &D[i]);
    }
        
    for (int i = 0; i < N; i++)
        pthread_join(thread_handles[i], NULL);


    printf("checksum calculated: %u\n", checksum_calculate);
    printf("checksum original: %u\n", checksum);

    pthread_mutex_destroy(&my_mutex);
    
    for (int i = 0; i < N; i++)
    {
        free(D[i].buff);
    }

    free(thread_handles);

    close(fd);
}