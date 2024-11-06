dosseg

; First n elements of Fibonnaci (3 <= n <= 9)
; Xn = Xn-1 + Xn-2

.model small

.stack 100h; 100h = 256 bytes of stack space

.data
    N DB 0
    n1 DB "Introducet n: $"
    result_array DB 0, 1; store in array [0, 1] -> [Xn-2, Xn-1]
    next_element DB 0
    counter DB 0
    buffer DB '00', 0

    space DB ' $'
.code

new_line proc
    MOV AH, 2
    MOV DL, 10; ASCII code for new line
    INT 21h; DOS interrupt
    
    RET; RETurn
new_line endp

print_number proc
    ; Use stack to save AX and following digits
    PUSH AX ; save AX to stack

    MOV CX, 10 ; dividing by 10
    MOV BX, 0 ; reset BX, use for storing digits

print_digit:
    XOR DX, DX ; clear DX before DIV
    DIV CX ; AX / 10, remainder in DX

    ADD DL, '0' ; convert remainder to ASCII
    
    PUSH DX ; push remainder onto stack
    
    INC BX ; increment BX to count digits
    
    CMP AX, 0 ; check if AX is 0
    JNE print_digit ; if not zero, continue to next digit

print_stack_digits:
    POP DX ; pop digits from stack (in reverse order)
    
    MOV AH, 2
    INT 21H
    
    DEC BX ; decrement BX (to check if we've displayed all digits)
    JNZ print_stack_digits ; repeat until all digits displayed

    POP AX ; restore AX
    
    ret
print_number endp

main proc
    MOV AX, @data; initialize data segment
    MOV DS, AX; DS points to data segment

    ; read N
    MOV AH, 9
    MOV DX, OFFSET n1; display message
    INT 21h; DOS INTerrupt
    MOV AH, 1; read character from keyboard
    INT 21h; DOS interrupt
    SUB AL, 30h; convert ASCII to digit
    MOV N, AL; save character in A
    CALL new_line; new line

    MOV CL, N; number of elements to display
    MOV counter, 2; initialize counter -> we print first two elements

    ; Print first two elements
    MOV AH, 0
    MOV AL, [result_array] ; load Xn-2
    CALL display_number
    LEA DX, space; print space
    MOV AH, 9
    INT 21h

    MOV AH, 0
    MOV AL, [result_array + 1]; load Xn-1
    CALL display_number
    LEA DX, space
    MOV AH, 9
    INT 21h

loop_start:
    ; Calculate next element
    MOV AL, [result_array]; load Xn-2
    ADD AL, [result_array + 1]; Xn = Xn-1 + Xn-2
    MOV [next_element], AL; save Xn 

    ; Print next element
    MOV AH, 0
    MOV AL, [next_element]
    CALL display_number
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