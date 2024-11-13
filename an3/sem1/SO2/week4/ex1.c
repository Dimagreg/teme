#include <stdio.h>
#include <stdlib.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>

int total_chars_count = 0;

void readDirRec(char *path, int fd_output)
{
    struct stat statbuf;
    DIR *dir;
    struct dirent *st_dir;
    char newpath[500];

    if ((dir = opendir(path)) == NULL)
    {
        perror(NULL);
        exit(1);
    }

    int pid_count = 0;
    pid_t pid[100]; //100 processes max

    while ((st_dir = readdir(dir)) != NULL)
    {
        //verify its not . and ..
        if (strcmp(st_dir->d_name, ".") == 0 || strcmp(st_dir->d_name, "..") == 0)
        {
            continue;
        }

        sprintf(newpath, "%s/%s", path, st_dir->d_name);

        if (lstat(newpath, &statbuf) != 0)
        {
            perror(NULL);
            exit(1);
        }

        //word count
        if ((statbuf.st_mode & __S_IFMT) == __S_IFREG)
        {  
            if ((pid[pid_count] = fork()) < 0)
            {
                perror(NULL);
                exit(-1);
            }

            if (pid[pid_count++] == 0)
            {
                int fd;

                if ((fd = open(newpath, O_RDONLY)) < 0)
                {
                    perror(NULL);
                    exit(-1);
                }

                int i;
                char buff[128];
                int word_count = 0;

                while ((i = read(fd, buff, 128)) > 0)
                {
                    word_count = word_count + i;
                }

                if (close(fd) < 0)
                {
                    perror(NULL);
                    exit(-1);
                }
                exit(word_count);
            }
        }
        else {
            // printf("%s - %s\n", newpath, filetype);

            // sprintf(buff_output, "%s - %s\n", newpath, filetype);

            // if (write(fd_output, buff_output, strlen(buff_output)) < 0)
            // {
            //     perror(NULL);
            //     exit(1);
            // }
        }

        //verify if its directory and read it recursively
        if ((statbuf.st_mode & __S_IFMT) == __S_IFDIR)
        {
            readDirRec(newpath, fd_output);
        }
    }

    closedir(dir);

    for (int i = 0; i < pid_count; i++)
    {
        int status;

        if ( waitpid(pid[i], &status, 0) == -1 ) {
            perror("waitpid failed");
            exit(-1);
        }

        if ( WIFEXITED(status) ) {
            int es = WEXITSTATUS(status);

            if (es == -1)
            {
                printf("es error\n");
                exit(1);
            }
            else
            {
                printf("Word count for pid[%d] is %d\n", i, es);
                total_chars_count += es;
            }
        }   
    }
}

int main(int argc, char *argv[])
{
    int fd_output = 0;
    
    if (argc != 2)  
    {
        printf("incorrect amount of arguments\n");
        exit(1);
    }

    // if ((fd_output = open(argv[2], O_WRONLY | O_CREAT | O_TRUNC, S_IRUSR | S_IWUSR)) < 0)
    // {
    //     perror(NULL);
    //     exit(1);
    // }

    readDirRec(argv[1], fd_output);

    printf("total characters count: %d\n", total_chars_count);

    // close(fd_output);
    return 0;
}