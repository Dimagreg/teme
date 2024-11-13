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

        char *ext = strrchr(newpath, '.');
        
        if (!ext) {
            /* no extension */
        } else {

            if (strcmp(ext, ".c") == 0)
            {
                if ((statbuf.st_mode & __S_IFMT) == __S_IFREG)
                {  
                    if ((pid[pid_count] = fork()) < 0)
                    {
                        perror(NULL);
                        exit(-1);
                    }

                    if (pid[pid_count++] == 0)
                    {
                        printf("i execute\n");
                        
                        char arg[1500];

                        sprintf(arg, "-Wall -o %s.x %s", newpath, newpath);

                        execlp("gcc", "gcc", arg, NULL);
                    }
                }
            }
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