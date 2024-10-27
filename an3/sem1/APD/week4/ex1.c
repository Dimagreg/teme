#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>


#define REPEAT 10
#define N_THREADS 2

int theFork;
pthread_mutex_t fork_lock;
int knife;
pthread_mutex_t knife_lock;

struct timespec timeoutTime;

void *eat_fct1(void *var)
{
        int id = *(int *)var; // thread id

        for (int i = 0; i < REPEAT; i++)
        {
            clock_gettime(CLOCK_REALTIME, &timeoutTime);
            timeoutTime.tv_sec += 0.5;

            printf("Thread %d wants fork \n",id);
            pthread_mutex_timedlock(&fork_lock, &timeoutTime);
            printf("Thread %d has fork wants knife \n",id);
            pthread_mutex_timedlock(&knife_lock, &timeoutTime);
            // uses fork and knife
            sleep(1);
            printf("Thread %d has fork and knife \n",id);
            pthread_mutex_unlock(&knife_lock);
            pthread_mutex_unlock(&fork_lock);
        }

        return NULL;
}

void *eat_fct2(void *var)
{
        int id = *(int *)var; // thread id
        for (int i = 0; i < REPEAT; i++)
        {
            clock_gettime(CLOCK_REALTIME, &timeoutTime);
            timeoutTime.tv_sec += 0.5;
            
            printf("Thread %d wants knife \n",id);
            pthread_mutex_timedlock(&knife_lock, &timeoutTime);
            printf("Thread %d has knife wants fork \n",id);
            pthread_mutex_timedlock(&fork_lock, &timeoutTime);
            // uses fork and knife
            sleep(1);
            printf("Thread %d has fork and knife \n",id);
            pthread_mutex_unlock(&fork_lock);
            pthread_mutex_unlock(&knife_lock);
        }
        return NULL;
}

int main(int argc, char **argv)
{

        int tid[N_THREADS];
        pthread_t thread[N_THREADS];

        int rc;

        printf("start\n");

        for (int i = 0; i < N_THREADS; i++)
        {
                tid[i] = i;
                if (i % 2 == 0)
                        rc = pthread_create(&thread[i], NULL, eat_fct1, (void *)&tid[i]);
                else
                        rc = pthread_create(&thread[i], NULL, eat_fct2, (void *)&tid[i]);
                if (rc)
                {
                        printf("ERROR; pthread_create() return code is %d\n", rc);
                        exit(-1);
                }
        }

        for (int i = 0; i < N_THREADS; i++)
        {
                // the main thread waits for thread[i] to finish
                rc = pthread_join(thread[i], NULL);
                if (rc)
                {
                        printf("ERROR; pthread_join() return code is %d\n", rc);
                        exit(-1);
                }
        }

        return 0;
}