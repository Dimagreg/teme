// 4. Să se scrie un program C, care își definește un comportament nou la recepția semnalului SIGUSR1.
// Programul va afișa un mesaj de fiecare dată când acest semnal este primit.
// Testarea programului se va face prin trimiterea semnalului SIGUSR1 dintr-un alt terminal, folosind comanda ps aux pentru a afla PIDul și comanda kill pentru a trimite semnalul.

#include <stdio.h>
#include <signal.h>
#include <unistd.h>

void signalHandler(int signalNum)
{
    if (signalNum == SIGUSR1)
    {
        printf("caught SIGUSR1\n");
    }
}


int main()
{
    signal(SIGUSR1, signalHandler);
    
    // /bin/bash kill -10 {pid}

    while(1)
    {
        sleep(1);
    }
    return 0;
}