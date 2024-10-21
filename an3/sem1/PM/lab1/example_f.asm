; Program to print the first 6 elements of the Fibonacci sequence in TASM
.model small
.stack 100h
.data
    fibArray dw 0, 1, 0, 0, 0, 0     ; Array to store the first 6 Fibonacci numbers
    msg db 'Fibonacci sequence: $'   ; Message to display before the numbers
    newline db 0Dh, 0Ah, '$'         ; New line characters

.code
main proc
    ; Set up the data segment
    mov ax, @data
    mov ds, ax

    ; Print the message
    lea dx, msg
    mov ah, 9                        ; DOS interrupt to print string
    int 21h

    ; Step 1: Calculate the Fibonacci numbers
    mov cx, 4                        ; We already have the first two numbers, calculate the next 4
    lea si, fibArray                 ; SI points to the first element of the array
    add si, 4                        ; Move SI to the third element (offset 4 bytes)

calculate_fib:
    ; Load the two previous Fibonacci numbers
    mov ax, [si - 4]                 ; Load the previous number
    add ax, [si - 2]                 ; Add the number before the previous

    ; Store the result in the current position
    mov [si], ax

    ; Move to the next position in the array
    add si, 2

    ; Decrease the counter and repeat if not zero
    loop calculate_fib

    ; Step 2: Print each Fibonacci number
    lea si, fibArray                 ; SI points to the start of the array
    mov cx, 6                        ; We want to print 6 numbers

print_fib:
    ; Convert the number to a string
    mov ax, [si]                     ; Load the current Fibonacci number
    call print_number                ; Convert and print the number

    ; Print a new line
    lea dx, newline
    mov ah, 9
    int 21h

    ; Move to the next number in the array
    add si, 2

    ; Decrease the counter and repeat if not zero
    loop print_fib

    ; Exit the program
    mov ah, 4Ch                      ; DOS interrupt to terminate the program
    int 21h

main endp

; Subroutine to print a number in AX as a decimal string
print_number proc
    ; AX contains the number to print
    push ax                          ; Save AX
    mov cx, 0                        ; Clear the digit count

    mov bx, 10                       ; Divisor for converting to decimal

convert_loop:
    xor dx, dx                       ; Clear DX for division
    div bx                           ; AX = AX / 10, DX = remainder

    add dl, '0'                      ; Convert remainder to ASCII
    push dx                          ; Save the ASCII character on the stack
    inc cx                           ; Increase digit count

    test ax, ax                      ; Check if AX is zero
    jnz convert_loop                 ; Repeat if not zero

print_digits:
    pop dx                           ; Get the next digit from the stack
    mov ah, 2                        ; DOS interrupt to print a character
    int 21h

    loop print_digits                ; Repeat for all digits

    pop ax                           ; Restore AX
    ret
print_number endp

end main
