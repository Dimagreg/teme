#include <iostream>
#include <cstdio>
#include <unistd.h>
#include <netdb.h>
#include <netinet/in.h>
#include <arpa/inet.h>


int main() {
    char hostbuffer[256];

    gethostname(hostbuffer, sizeof(hostbuffer));

    const hostent *host_entry = gethostbyname(hostbuffer);

    char *IPbuffer = inet_ntoa(*reinterpret_cast<struct in_addr *>(host_entry->h_addr_list[0]));

    printf("gcc version: %d.%d.%d\n",__GNUC__,__GNUC_MINOR__,__GNUC_PATCHLEVEL__);
    printf("Hostname: %s\n", hostbuffer);
    printf("Host IP: %s\n", IPbuffer);
    std::cout << "Hello, World!" << std::endl;
    return 0;
}
