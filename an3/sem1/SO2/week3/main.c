//./prog dir output_file
//1. parcurgere recursiva la dir -> stdout
//2. sa identifice tipuri fisier -> regular, link, dir
//3. 1 + 2 in output_file
//symbolic link: ln -s lnfile test_file1
//4. for each regular -> count letters -> in output_file

// opendir / closedir

#include <stdio.h>
#include <stdlib.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>

void readDirRec(char *path, int fd_output)
{
    struct stat statbuf;
    DIR *dir;
    struct dirent *st_dir;
    char newpath[500];
    char buff_output[1000];
    char *filetype;
    int word_count;
    int fd;

    if ((dir = opendir(path)) == NULL)
    {
        perror(NULL);
        exit(1);
    }

    while ((st_dir = readdir(dir)) != NULL)
    {
        // reset 
        word_count = 0;
        fd = 0;

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

        switch (statbuf.st_mode & __S_IFMT) {
        case __S_IFDIR:  filetype = "directory";             break;
        case __S_IFLNK:  filetype = "symlink";               break;
        case __S_IFREG:  filetype = "regular file";          break;
        default:         filetype = "unknown?";              break;
        }    

        //word count
        if ((statbuf.st_mode & __S_IFMT) == __S_IFREG)
        {
            if ((fd = open(newpath, O_RDONLY)) < 0)
            {
                perror(NULL);
                exit(1);
            }

            int i;
            char buff[128];

            while ((i = read(fd, buff, 128)) > 0)
            {
                word_count = word_count + i;
            }

            printf("%s - %s: %d characters\n", newpath, filetype, word_count);

            sprintf(buff_output, "%s - %s: %d characters\n", newpath, filetype, word_count);

            if (write(fd_output, buff_output, strlen(buff_output)) < 0)
            {
                perror(NULL);
                exit(1);
            }

            if (close(fd) < 0)
            {
                perror(NULL);
                exit(1);
            }
        }
        else {
            printf("%s - %s\n", newpath, filetype);

            sprintf(buff_output, "%s - %s\n", newpath, filetype);

            if (write(fd_output, buff_output, strlen(buff_output)) < 0)
            {
                perror(NULL);
                exit(1);
            }
        }

        //verify if its directory and read it recursively
        if ((statbuf.st_mode & __S_IFMT) == __S_IFDIR)
        {
            readDirRec(newpath, fd_output);
        }
    }

    closedir(dir);
}

int main(int argc, char* argv[])
{
    int fd_output;
    
    if (argc != 3)  
    {
        printf("incorrect amount of arguments\n");
        exit(1);
    }

    if ((fd_output = open(argv[2], O_WRONLY | O_CREAT | O_TRUNC, S_IRUSR | S_IWUSR)) < 0)
    {
        perror(NULL);
        exit(1);
    }

    readDirRec(argv[1], fd_output);

    close(fd_output);

    return 0;
}