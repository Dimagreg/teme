// arguments frames1.bin out.txt
// afiseaza ce tip de fisier e - cu stat (regular file, link, dir)
// file size (stat)
// citim byte din byte - daca gasim caracter printabil -> scriem in out. (a-z, A-Z, 0-9) ASCII, daca nu e printabil il scriem codul lui ascii in out. sprintf(buff, temp, "0x%x", buff[i])

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <time.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/sysmacros.h>
#include <string.h>


int main(int argc, char* argv[])
{
    struct stat statbuf;
    int fd_input, fd_output;
    uint8_t buff[128];
    uint8_t buff_output[128];

    if (argc != 3)
    {
        printf("wrong arguments count\n");
        exit(1);
    }

    if ((fd_input = open(argv[1], O_RDONLY)) < 0)
    {
        perror(NULL);
        exit(1);
    }

    if ((fd_output = open(argv[2], O_WRONLY | O_CREAT | O_TRUNC, S_IRUSR, S_IWUSR)) < 0)
    {
        perror(NULL);
        exit(1);
    }

    if (stat(argv[1], &statbuf) != 0)
    {
        perror(NULL);
        exit(1);
    }

    printf("%s is a ", argv[1]);

    switch (statbuf.st_mode & __S_IFMT) {
    case __S_IFBLK:  printf("block device\n");            break;
    case __S_IFCHR:  printf("character device\n");        break;
    case __S_IFDIR:  printf("directory\n");               break;
    case __S_IFIFO:  printf("FIFO/pipe\n");               break;
    case __S_IFLNK:  printf("symlink\n");                 break;
    case __S_IFREG:  printf("regular file\n");            break;
    case __S_IFSOCK: printf("socket\n");                  break;
    default:       printf("unknown?\n");                break;
    }    

    int i;
    while ((i = read(fd_input, buff, 128)) > 0)
    {
        for (int j = 0; j < i; j++)
        {
            if (buff[j] > 32 && buff[j] < 127)
            {
                printf("%c ", buff[j]);
                sprintf(buff_output, "%c ", buff[j]);

                if (write(fd_output, buff_output, strlen(buff_output)) < 0)
                {
                    perror(NULL);
                    exit(1);
                }
            }

            else
            {
                printf("0x%02x ", buff[j]);
                sprintf(buff_output, "0x%02x ", buff[j]);
                
                if (write(fd_output, buff_output, strlen(buff_output)) < 0)
                {
                    perror(NULL);
                    exit(1);
                }
            }
        }
    }



    return 0;
}