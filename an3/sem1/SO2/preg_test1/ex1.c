// 3. Să se scrie un program care primește ca și argumente în linie de comandă calea către 2 fișiere:
//           <program> <fișier-intrare> <fișier-ieșire>
// Fișierul de intrare va conține un număr necunoscut de numere întregi pe 4 octeți.
// Programul va citi fișierul de intrare în întregime și va scrie în fișierul de ieșire, în format text, numărul minim, numărul maxim și media numerelor citite din fișierul de intrare binar.

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

int main(int argc, char* argv[])
{
    if (argc != 3)
    {
        printf("invalid number of arguments\n");
        exit(1);
    }

    int fd_in, fd_out;

    if ((fd_in = open(argv[1], O_RDONLY)) == -1)
    {
        perror(NULL);
        exit(1);
    }

    int max = 0;
    int min = 0;
    float average = 0;
    int arr[1000];
    int n = 0;
    int buff;
    int bytes_read;
    
    while ((bytes_read = read(fd_in, &buff, sizeof(int))) == sizeof(int))
    {
        arr[n] = buff;
        n++;
    }

    if (bytes_read < 0)
    {
        printf("error reading file\n");
        exit(1);
    }

    min = arr[0];
    max = arr[0];
    average = arr[0];

    for (int i = 1; i < n; i++)
    {
        if (min > arr[i])
            min = arr[i];

        if (max < arr[i])
            max = arr[i];

        average += arr[i];
    }

    if (n != 0)
        average = average / n;

    if ((fd_out = open(argv[2],  O_WRONLY | O_CREAT | O_TRUNC, 0644)) < 0)
    {
        perror(NULL);
        exit(1);
    }

    char buff1[2056];
    int offset = 0;

    int written = sprintf(buff1 + offset, "max=%d\n", max);
        offset += written;
    written = sprintf(buff1 + offset, "min=%d\n", min);
        offset += written;
    written = sprintf(buff1 + offset, "average=%f\n", average);
        offset += written;

    if (write(fd_out, buff1, offset) < 0)
    {
        perror(NULL);
        exit(1);
    }

    printf("max = %d\n", max);
    printf("min = %d\n", min);
    printf("average = %f\n", average);

    close(fd_in);
    close(fd_out);
}