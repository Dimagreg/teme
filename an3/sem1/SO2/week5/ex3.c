// 6. Să se scrie un program C, care constă în 2 procese înrudite:

//     procesul copil: va citi în întregime un fișier text primit ca și argument în linie de comandă și va trimite doar vocalele procesului părinte folosind un pipe
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

int main(int argc, char* argv[])
{
    int pid;
    int status;
    int pfd[2];

    if (argc != 2)
    {
        printf("invalid number of arguments\n");
        exit(1);
    }

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
        close(pfd[0]); //close read pipe

        int fd;

        if ((fd = open(argv[1], O_RDONLY)) < 0)
        {
            perror(NULL);
            exit(-1);
        }

        char buff[2056];
        int bytes;

        bytes = read(fd, buff, sizeof(char[2056]));

        for (int i = 0; i < bytes; i++)
        {
            if (isVocal(buff[i]))
            {
                printf("%c is vocal\n", buff[i]);

                write(pfd[1], &buff[i], 1);
            }
        }

        close(pfd[1]);

        return 0;
    }
    //parent
    close(pfd[1]); //close write pipe

    int vocals_count = 0;
    char letter[1];

    while (read(pfd[0], letter, 1) > 0)
    {
        vocals_count+= 1;
    }

    printf("Total vocals: %d\n", vocals_count);

    waitpid(pid, &status, 0);

    if (WIFEXITED(status))
    {
        int exit_status = WEXITSTATUS(status);
        printf("child exit status: %d\n", exit_status);
    }

    close(pfd[0]);

    return 0;
}