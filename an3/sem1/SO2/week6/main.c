#include <stdio.h>
#include <stdlib.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>

// ./prog dir output_file.txt

pid_t pid;
int pfd[2];
int status;

void readDirRec(char *path)
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

        // sha256sum
        if ((statbuf.st_mode & __S_IFMT) == __S_IFREG)
        {  
            if ((pid = fork()) < 0)
            {
                perror(NULL);
                exit(-1);
            }

            if (pid == 0)
            {
                //child
                close(pfd[0]); //close read pipe

                dup2(pfd[1], STDOUT_FILENO);

                if (execlp("/bin/sha256sum", "sha256sum", newpath, NULL) < 0)
                {
                    printf("execlp error\n");
                }
            }
        }
        else {
            // no-op
        }

        //verify if its directory and read it recursively
        if ((statbuf.st_mode & __S_IFMT) == __S_IFDIR)
        {
            readDirRec(newpath);
        }
    }

    closedir(dir);
}

int main(int argc, char *argv[])
{   
    if (argc != 3)  
    {
        printf("incorrect amount of arguments\n");
        exit(1);
    }

    FILE *foutput = NULL;

    if ((foutput = fopen(argv[2], "w")) == NULL)
    {
        perror(NULL);
        exit(1);
    }

    if (pipe(pfd) < 0)
    {
        perror(NULL);
        exit(1);
    }

    readDirRec(argv[1]);

    // parent
    close(pfd[1]); //close write pipe

    char buff[128];
    char buff1[128];

    FILE *file = NULL;
    
    if ((file = fdopen(pfd[0], "r")) == NULL)
    {
        perror(NULL);
        exit(1);
    }

    while ((fscanf(file, "%s %s\n", buff, buff1)) == 2)
    {
        printf("%s %s\n", buff, buff1);
        
        if (fprintf(foutput, "%s %s\n", buff, buff1) < 0)
        {
            perror(NULL);
            exit(1);
        }
    }

    waitpid(pid, &status, 0);

    if (WIFEXITED(status))
    {
        int exit_status = WEXITSTATUS(status);
        printf("child exit status: %d\n", exit_status);
    }

    close(pfd[0]);

    fclose(foutput);
    
    return 0;
}