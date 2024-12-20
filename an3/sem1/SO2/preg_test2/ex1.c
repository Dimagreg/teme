// 1. Să se scrie un program C ce se va apela în linie de comandă astfel:

// ./prog <directory> <output_file> <threshold> <ch1> <ch2> .. <chn>
// Programul va parcurge recursiv directorul dat ca și argument.
// Pentru fiecare fișier obișnuit (regular) găsit se va crea un thread ce va număra de câte ori caracterele date ca și argumente apar în conținutul acestuia. 
// Dacă numărul total de caractere numărate pentru fiecare fișier depășește numărul <threshold>, programul va crea o legătură simbolică în același director cu fișierul procesat, cu același nume, dar cu terminația "_th".
// Se va crea de asemenea și un fisier de statistică referit prin <output_file>. Fișierul va avea o formă tabelară, iar capul de tabel va fi generat dinamic în funcție de argumente, astfel:

// forma generala: 
// <file_path>;<nr_ch1>;<nr_ch2>;...;<nr_chn> <total>
// Pentru un caz de test, se poate descărca o arhivă rulând în terminal:

// wget https://staff.cs.upt.ro/~valy/so/test_so_3_1.tar.gz
// Pentru dezarhivare se poate folosi următoarea comandă:

// tar xf test_so_3_1.tar.gz

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <dirent.h>
#include <sys/stat.h>
#include <unistd.h>
#include <sys/sysmacros.h>
#include <pthread.h>
#include <fcntl.h>

pthread_t *pthread_handles = NULL;
int THREADS = 0;

typedef struct 
{
    char newpath[500];
    char characters[128];
    int occurence[128];
    int total;
    int size;
} Characters_data;

Characters_data data[128];

char characters[128];
int ch_count = 0;
int threshold = 0;
int fd_output = 0;

void *Pthread_readFile(void *args)
{
    Characters_data *data = (Characters_data *) args;

    //init characters occurence
    data->size = ch_count;

    for (int i = 0; i < data->size; i++)
    {
        data->characters[i] = characters[i];
        data->occurence[i] = 0;
        data->total = 0;
    }

    int fd;

    if ((fd = open(data->newpath, O_RDONLY)) < 0)
    {
        perror("open");
        exit(1);
    }

    int bytes;
    char character;

    while ((bytes = read(fd, &character, sizeof(char))) > 0)
    {
        for (int i = 0; i < data->size; i++)
        {
            if (character == data->characters[i])
            {
                data->occurence[i]++;
                data->total++;

                // printf("data->occurence[%d] = %d\n", i, data->occurence[i]);
                // printf("data->total = %d\n", data->total);

                break; // only one occurence per character
            }
        }
    }

    // create symlink
    if (data->total > threshold)
    {
        printf("Creating symlink for %s...\n", data->newpath);

        char sympath[510];

        char *token1 = strtok(data->newpath, ".");
        char *token2 = strtok(NULL, ".");

        sprintf(sympath, "%s_th.%s", token1, token2);

        printf("%s\n", sympath);

        if (symlink(data->newpath, sympath) == -1)
        {
            perror("symlink");
            exit(1);
        }
    }

    char buffer[1000];

    sprintf(buffer, "%s;", data->newpath);

    for(int i = 0; i < data->size; i++)
    {
        char occurence[12]; sprintf(occurence, "%d", data->occurence[i]);
        strcat(buffer, occurence);
        strcat(buffer, ";");
    }

    strcat(buffer, " ");

    char total[12]; sprintf(total, "%d", data->total);
    strcat(buffer, total);

    strcat(buffer, "\n");

    printf("buffer: %s\n", buffer);

    int bytes_buffer;

    for (int i = 0; i < sizeof(buffer); i++)
    {
        if (buffer[i] != '\0')
        {
            bytes_buffer++;
        }
        else
        {
            break;
        }
    }

    if (write(fd_output, buffer, bytes_buffer) < 0)
    {
        perror(NULL);
        exit(1);
    }

    printf("output newpath = %s\n", data->newpath);

    return NULL;
}

void readDirRecursive(char *input_dir)
{
    struct dirent *st_dir;
    struct stat st_stat;
    char newpath[500];

    DIR *dir = opendir(input_dir);

    if (!dir)
    {
        perror("opendir");
        exit(1);
    }

    while ((st_dir = readdir(dir)) != NULL)
    {
        // skip . and ..
        if (strcmp(st_dir->d_name, ".") == 0 || strcmp(st_dir->d_name, "..") == 0)
        {
            continue;
        }

        // get current entry path
        sprintf(newpath, "%s/%s", input_dir, st_dir->d_name);

        if (stat(newpath, &st_stat) == -1)
        {
            printf("error stat: %s\n", newpath);
            perror("stat");
            exit(1);
        }

        // check if regular file
        if ((st_stat.st_mode & __S_IFMT) == __S_IFREG)
        {
            printf("reached this path = %s\n", newpath);

            strcpy(data[THREADS].newpath, newpath);

            pthread_handles = realloc(pthread_handles, (THREADS + 1) * sizeof(pthread_t));

            if (!pthread_handles)
            {
                perror("realloc");
                exit(1);
            }

            pthread_create(&pthread_handles[THREADS], NULL, Pthread_readFile, &data[THREADS]);

            THREADS++;
        }

        // rec
        if ((st_stat.st_mode & __S_IFMT) == __S_IFDIR)
        {
            readDirRecursive(newpath);
        }
    }

    closedir(dir);
    free(st_dir);
}

int main(int argc, char *argv[])
{
    if (argc < 5)
    {
        printf("not enough arguments\n");
        exit(1);
    }

    threshold = atoi(argv[3]);

    if (threshold <= 0)
    {
        printf("atoi error\n");
        exit(1);
    } 

    for (int i = 4; i < argc; i++)
    {
        if (strlen(argv[i]) > 1)
        {
            printf("\"%s\" is not a char\n", argv[i]);
            exit(1);
        }

        strcpy(&characters[ch_count], argv[i]);
        ch_count++;
    }

    for (int i = 0; i < ch_count; i++)
    {
        printf("%c ", characters[i]);
    }

    printf("\n");

    fd_output = open(argv[2], O_WRONLY | O_CREAT | O_TRUNC, S_IRUSR | S_IWUSR);

    if (fd_output < 0)
    {
        perror("open");
        exit(1);
    }

    readDirRecursive(argv[1]);

    for (int i = 0; i < THREADS; i++)
    {
        pthread_join(pthread_handles[i], NULL);
    }

    close(fd_output);

    return 0;
}
