// 7. Să se scrie un program C, care constă în 2 procese înrudite:

//     procesul copil: va invoca comanda "ls -l" și se va asigura că informația generată de această comandă este transmisă părintelui folosind un pipe
//     procesul părinte: va număra câți octeți a primit prin pipe, va afișa acest număr și după va prelua starea procesului copil.

#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <fcntl.h>
#include <stdio.h>

#define ARRAY_SIZE(arr) (sizeof(arr) / sizeof((arr)[0]))

int isVocal(char letter)
{
    switch (tolower(letter))
    {
        case 'a':
        case 'e':
        case 'i':
        case 'o':
        case 'u':
        case 'y':
            return 1;
    }

    return 0;
}

int main()
{
    int pid;
    int status;
    int pfd[2];

    if (pipe(pfd) < 0)
    {
        perror(NULL);
        exit(1);
    }

    if ((pid = fork()) < 0)
    {
        perror(NULL);
        exit(1);
    }

    if (pid == 0)
    {
        //child
        dup2(pfd[1], STDOUT_FILENO);

        close(pfd[0]); //close read pipe
        close(pfd[1]);

        execl("/bin/ls", "ls", "-l", NULL);
    }
    //parent
    close(pfd[1]); //close write pipe

    int bytes_count = 0;
    int bytes;
    char buff[256];

    while ((bytes = read(pfd[0], buff, 256)) > 0)
    {
        bytes_count+= bytes;
    }

    printf("Total bytes read: %d\n", bytes_count);

    waitpid(pid, &status, 0);

    if (WIFEXITED(status))
    {
        int exit_status = WEXITSTATUS(status);
        printf("child exit status: %d\n", exit_status);
    }

    close(pfd[0]);

    return 0;
}