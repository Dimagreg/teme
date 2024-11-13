#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>

int main() {
    int fd = open("integers.bin", O_WRONLY | O_CREAT | O_TRUNC, 0644);
    if (fd == -1) {
        perror("Error opening file");
        return 1;
    }

    int numbers[] = {1234, 1235, 1236};
    ssize_t bytes_written;

    // Write each integer to the file in binary format
    for (int i = 0; i < 3; i++) {
        bytes_written = write(fd, &numbers[i], sizeof(int));
        if (bytes_written != sizeof(int)) {
            perror("Error writing to file");
            close(fd);
            return 1;
        }
    }

    printf("Binary file 'file.bin' created with integers 1234, 1235, 1236\n");

    close(fd);
    return 0;
}