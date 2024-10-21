dosseg

; First 6 elements of the Fibonacci sequence
; 0 1 1 2 3 5
; Xn = Xn-1 + Xn-2

.model small

.stack 100h; 100h = 256 bytes of stack space

.data
    result_array DB 0, 1; store in array [0, 1] -> [Xn-2, Xn-1]
    next_element DB 0
    counter DB 0

    space DB ' $'
.code

new_line proc
    MOV AH, 2
    MOV DL, 10; ASCII code for new line
    INT 21h
    ret
new_line endp

print_digit proc
    ADD AL, 30h          ; Convert digit to ASCII
    MOV DL, AL           ; Move to DL for printing
    MOV AH, 2            ; DOS interrupt to print character
    INT 21h
    RET
print_digit endp

main proc
    MOV AX, @data; initialize data segment
    MOV DS, AX; DS points to data segment

    MOV CX, 7; number of elements to display
    MOV counter, 2; initialize counter -> we print first two elements

    ; Print first two elements
    MOV AL, [result_array] ; load Xn-2
    CALL print_digit
    LEA DX, space; print space
    MOV AH, 9
    INT 21h

    MOV AL, [result_array + 1]; load Xn-1
    CALL print_digit
    LEA DX, space
    MOV AH, 9
    INT 21h

loop_start:
    ; Calculate next element
    MOV AL, [result_array]; load Xn-2
    ADD AL, [result_array + 1]; Xn = Xn-1 + Xn-2
    MOV [next_element], AL; save Xn 

    ; Print next element
    MOV AL, [next_element]
    CALL print_digit
    LEA DX, space
    MOV AH, 9
    INT 21h

    ; Update result_array
    MOV AL, [result_array + 1]; load Xn-1
    MOV [result_array], AL; Xn-2 = Xn-1
    MOV AL, [next_element]; load Xn
    MOV [result_array + 1], AL; Xn-1 = Xn

    ; Increment the counter
    INC counter
    MOV AL, counter
    CMP AL, CL
    JL loop_start; jump if less

    ; Print newline
    CALL new_line; new line

    MOV AH, 4Ch; terminate program
    INT 21h;

main endp

end main