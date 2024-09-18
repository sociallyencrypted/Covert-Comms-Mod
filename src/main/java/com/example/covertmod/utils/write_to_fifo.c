// File: write_to_fifo.c
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>

// Main function to write data to a FIFO (named pipe)
int main(int argc, char *argv[]) {
    // Check for correct number of arguments
    if (argc != 2) {
        fprintf(stderr, "Usage: %s <fifo_path>\n", argv[0]);
        return EXIT_FAILURE;
    }

    const char *fifo_path = argv[1];
    char input_buffer[256];

    // Open the FIFO for non-blocking write
    int fifo_fd = open(fifo_path, O_WRONLY | O_NONBLOCK);
    if (fifo_fd == -1) {
        perror("open");
        return EXIT_FAILURE;
    }

    // Continuously read from stdin and write to the FIFO
    while (1) {
        if (fgets(input_buffer, sizeof(input_buffer), stdin) == NULL) {
            break;
        }

        ssize_t bytes_written = write(fifo_fd, input_buffer, strlen(input_buffer));
        if (bytes_written == -1) {
            perror("write");
            close(fifo_fd);
            return EXIT_FAILURE;
        }

        printf("Wrote %zd bytes to FIFO\n", bytes_written);
    }

    // Close the FIFO file descriptor
    close(fifo_fd);
    return EXIT_SUCCESS;
}