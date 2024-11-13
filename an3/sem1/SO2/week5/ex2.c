// 5. Să se scrie un program C care constă în 2 procese înrudite:

//     procesul copil: își va seta un comportament nou la recepția semnalului SIGUSR1: va afișa un mesaj și își va termina execuția.
//     procesul părinte: va trimite semnalul SIGUSR1 procesului copil și după va prelua starea acestuia.

#include <stdio.h>
#include <signal.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>

void signalHandler(int signalNum)
{
    if (signalNum == SIGUSR1)
    {
        printf("caught SIGUSR1, exiting\n");
        exit(10);
    }
}

int main()
{
    int pid;
    int status;

    if ((pid = fork()) < 0)
    {
        perror(NULL);
        exit(1);
    }

    if (pid == 0)
    {
        //child
        signal(SIGUSR1, signalHandler);

        while(1)
        {
            sleep(1);
        }
    }
    //parent
    sleep(0.1); //sleep in order to child register signal 

    kill(pid, SIGUSR1);

    waitpid(pid, &status, 0);

    if (WIFEXITED(status))
    {
        int exit_status = WEXITSTATUS(status);
        printf("child exit status: %d\n", exit_status);
    }

    return 0;
}